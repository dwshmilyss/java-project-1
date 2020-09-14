/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.yiban.flume.dev.sources.taildir;

import static com.yiban.flume.dev.sources.taildir.TaildirSourceConfigurationConstants.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.flume.ChannelException;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.FlumeException;
import org.apache.flume.PollableSource;
import org.apache.flume.conf.Configurable;
import org.apache.flume.instrumentation.SourceCounter;
import org.apache.flume.source.AbstractSource;
import org.apache.flume.source.PollableSourceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;

/**
 * 实现递归监控目录的功能
 * PollableSource通过一个外部驱动去查看有没有需要消费的event，从而拉取event，说白了就是定时拉取。所以也不是真正的实时
 * 定时拉取调用的是process()
 */
public class TaildirSource extends AbstractSource implements
        PollableSource, Configurable {

    private static final Logger logger = LoggerFactory.getLogger(TaildirSource.class);

    private Map<String, String> filePaths;
    private Table<String, String, String> headerTable;
    private int batchSize;
    private String positionFilePath;
    private boolean skipToEnd;
    private boolean byteOffsetHeader;

    private SourceCounter sourceCounter;
    private ReliableTaildirEventReader reader;
    private ScheduledExecutorService idleFileChecker;
    private ScheduledExecutorService positionWriter;
    private int retryInterval = 1000;
    private int maxRetryInterval = 5000;
    private int idleTimeout;
    private int checkIdleInterval = 5000;
    private int writePosInitDelay = 5000;
    private int writePosInterval;
    private boolean cachePatternMatching;

    private List<Long> existingInodes = new CopyOnWriteArrayList<Long>();
    private List<Long> idleInodes = new CopyOnWriteArrayList<Long>();
    private Long backoffSleepIncrement;
    private Long maxBackOffSleepInterval;
    private boolean fileHeader;
    private String fileHeaderKey;

    /**
     * Flume的taildir source启动会调用<code>start()</code>方法作初始化，
     * 里面创建一个<code>ReliableTaildirEventReader</code>
     * 比如 a1.sources.r1.filegroups.f1 = /opt/flume/taildir1/.*
     * 默认只能监控 taildir1 目录下的文件
     * 修改后可以监控 taildir1/dir1 , taildir1/dir2/dir3 等目录下的文件
     */
    @Override
    public synchronized void start() {
        logger.info("{} TaildirSource source starting with directory: {}", getName(), filePaths);
        try {
            //这里用到了建造者模式
            reader = new ReliableTaildirEventReader.Builder()
                    .filePaths(filePaths)
                    .headerTable(headerTable)
                    .positionFilePath(positionFilePath)
                    .skipToEnd(skipToEnd)
                    .addByteOffset(byteOffsetHeader)
                    .cachePatternMatching(cachePatternMatching)
                    .annotateFileName(fileHeader)
                    .fileNameHeader(fileHeaderKey)
                    .recursive(isRecursive)
                    .build();
        } catch (IOException e) {
            throw new FlumeException("Error instantiating ReliableTaildirEventReader", e);
        }
        idleFileChecker = Executors.newSingleThreadScheduledExecutor(
                new ThreadFactoryBuilder().setNameFormat("idleFileChecker").build());
        idleFileChecker.scheduleWithFixedDelay(new idleFileCheckerRunnable(),
                idleTimeout, checkIdleInterval, TimeUnit.MILLISECONDS);

        positionWriter = Executors.newSingleThreadScheduledExecutor(
                new ThreadFactoryBuilder().setNameFormat("positionWriter").build());
        positionWriter.scheduleWithFixedDelay(new PositionWriterRunnable(),
                writePosInitDelay, writePosInterval, TimeUnit.MILLISECONDS);

        super.start();
        logger.debug("TaildirSource started");
        sourceCounter.start();
    }

    @Override
    public synchronized void stop() {
        try {
            super.stop();
            ExecutorService[] services = {idleFileChecker, positionWriter};
            for (ExecutorService service : services) {
                service.shutdown();
                if (!service.awaitTermination(1, TimeUnit.SECONDS)) {
                    service.shutdownNow();
                }
            }
            // write the last position
            writePosition();
            reader.close();
        } catch (InterruptedException e) {
            logger.info("Interrupted while awaiting termination", e);
        } catch (IOException e) {
            logger.info("Failed: " + e.getMessage(), e);
        }
        sourceCounter.stop();
        logger.info("Taildir source {} stopped. Metrics: {}", getName(), sourceCounter);
    }

    @Override
    public String toString() {
        return String.format("Taildir source: { positionFile: %s, skipToEnd: %s, "
                        + "byteOffsetHeader: %s, idleTimeout: %s, writePosInterval: %s }",
                positionFilePath, skipToEnd, byteOffsetHeader, idleTimeout, writePosInterval);
    }

    //whether to support recursive
    private boolean isRecursive;

    @Override
    public synchronized void configure(Context context) {
        //从配置中读取是否要递归监控文件夹
        isRecursive = context.getBoolean(RECURSIVE, DEFAULT_RECURSIVE);
        logger.info("isRecursive = " + isRecursive);
        String fileGroups = context.getString(FILE_GROUPS);
        Preconditions.checkState(fileGroups != null, "Missing param: " + FILE_GROUPS);

        filePaths = selectByKeys(context.getSubProperties(FILE_GROUPS_PREFIX),
                fileGroups.split("\\s+"));
        Preconditions.checkState(!filePaths.isEmpty(),
                "Mapping for tailing files is empty or invalid: '" + FILE_GROUPS_PREFIX + "'");

        String homePath = System.getProperty("user.home").replace('\\', '/');
        positionFilePath = context.getString(POSITION_FILE, homePath + DEFAULT_POSITION_FILE);
        Path positionFile = Paths.get(positionFilePath);
        try {
            Files.createDirectories(positionFile.getParent());
        } catch (IOException e) {
            throw new FlumeException("Error creating positionFile parent directories", e);
        }
        headerTable = getTable(context, HEADERS_PREFIX);
        batchSize = context.getInteger(BATCH_SIZE, DEFAULT_BATCH_SIZE);
        skipToEnd = context.getBoolean(SKIP_TO_END, DEFAULT_SKIP_TO_END);
        byteOffsetHeader = context.getBoolean(BYTE_OFFSET_HEADER, DEFAULT_BYTE_OFFSET_HEADER);
        idleTimeout = context.getInteger(IDLE_TIMEOUT, DEFAULT_IDLE_TIMEOUT);
        writePosInterval = context.getInteger(WRITE_POS_INTERVAL, DEFAULT_WRITE_POS_INTERVAL);
        cachePatternMatching = context.getBoolean(CACHE_PATTERN_MATCHING,
                DEFAULT_CACHE_PATTERN_MATCHING);

        backoffSleepIncrement = context.getLong(PollableSourceConstants.BACKOFF_SLEEP_INCREMENT,
                PollableSourceConstants.DEFAULT_BACKOFF_SLEEP_INCREMENT);
        maxBackOffSleepInterval = context.getLong(PollableSourceConstants.MAX_BACKOFF_SLEEP,
                PollableSourceConstants.DEFAULT_MAX_BACKOFF_SLEEP);
        fileHeader = context.getBoolean(FILENAME_HEADER,
                DEFAULT_FILE_HEADER);
        fileHeaderKey = context.getString(FILENAME_HEADER_KEY,
                DEFAULT_FILENAME_HEADER_KEY);

        if (sourceCounter == null) {
            sourceCounter = new SourceCounter(getName());
        }
    }

    private Map<String, String> selectByKeys(Map<String, String> map, String[] keys) {
        Map<String, String> result = Maps.newHashMap();
        for (String key : keys) {
            if (map.containsKey(key)) {
                result.put(key, map.get(key));
            }
        }
        return result;
    }

    private Table<String, String, String> getTable(Context context, String prefix) {
        Table<String, String, String> table = HashBasedTable.create();
        for (Entry<String, String> e : context.getSubProperties(prefix).entrySet()) {
            String[] parts = e.getKey().split("\\.", 2);
            table.put(parts[0], parts[1], e.getValue());
        }
        return table;
    }

    @VisibleForTesting
    protected SourceCounter getSourceCounter() {
        return sourceCounter;
    }


    /**
     * taildir source是一种定时拉取策略
     * 每隔大概1s调用一次process()，这里应该是和MyLoggerSink的process()类似。
     *
     * @return
     */
    @Override
    public Status process() {
        Status status = Status.READY;
        try {
            existingInodes.clear();
            //reader.updateTailFiles()获取需要监控的文件，然后对每一个进行处理，查看最后修改时间，判断是否需要tail，需要tail就tail
            existingInodes.addAll(reader.updateTailFiles(isRecursive));
            for (long inode : existingInodes) {
                TailFile tf = reader.getTailFiles().get(inode);
                if (tf.needTail()) {
                    tailFileProcess(tf, true);
                }
            }
            closeTailFiles();
            try {
                TimeUnit.MILLISECONDS.sleep(retryInterval);
            } catch (InterruptedException e) {
                logger.info("Interrupted while sleeping");
            }
        } catch (Throwable t) {
            logger.error("Unable to tail files", t);
            status = Status.BACKOFF;
        }
        return status;
    }

    @Override
    public long getBackOffSleepIncrement() {
        return backoffSleepIncrement;
    }

    @Override
    public long getMaxBackOffSleepInterval() {
        return maxBackOffSleepInterval;
    }

    private void tailFileProcess(TailFile tf, boolean backoffWithoutNL)
            throws IOException, InterruptedException {
        while (true) {
            reader.setCurrentFile(tf);
            List<Event> events = reader.readEvents(batchSize, backoffWithoutNL);
            if (events.isEmpty()) {
                break;
            }
            sourceCounter.addToEventReceivedCount(events.size());
            sourceCounter.incrementAppendBatchReceivedCount();
            try {
                getChannelProcessor().processEventBatch(events);
                reader.commit();
            } catch (ChannelException ex) {
                logger.warn("The channel is full or unexpected failure. " +
                        "The source will try again after " + retryInterval + " ms");
                TimeUnit.MILLISECONDS.sleep(retryInterval);
                retryInterval = retryInterval << 1;
                retryInterval = Math.min(retryInterval, maxRetryInterval);
                continue;
            }
            retryInterval = 1000;
            sourceCounter.addToEventAcceptedCount(events.size());
            sourceCounter.incrementAppendBatchAcceptedCount();
            if (events.size() < batchSize) {
                break;
            }
        }
    }

    private void closeTailFiles() throws IOException, InterruptedException {
        for (long inode : idleInodes) {
            TailFile tf = reader.getTailFiles().get(inode);
            if (tf.getRaf() != null) { // when file has not closed yet
                tailFileProcess(tf, false);
                tf.close();
                logger.info("Closed file: " + tf.getPath() + ", inode: " + inode + ", pos: " + tf.getPos());
            }
        }
        idleInodes.clear();
    }

    /**
     * Runnable class that checks whether there are files which should be closed.
     */
    private class idleFileCheckerRunnable implements Runnable {
        @Override
        public void run() {
            try {
                long now = System.currentTimeMillis();
                for (TailFile tf : reader.getTailFiles().values()) {
                    if (tf.getLastUpdated() + idleTimeout < now && tf.getRaf() != null) {
                        idleInodes.add(tf.getInode());
                    }
                }
            } catch (Throwable t) {
                logger.error("Uncaught exception in IdleFileChecker thread", t);
            }
        }
    }

    /**
     * Runnable class that writes a position file which has the last read position
     * of each file.
     */
    private class PositionWriterRunnable implements Runnable {
        @Override
        public void run() {
            writePosition();
        }
    }

    private void writePosition() {
        File file = new File(positionFilePath);
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            if (!existingInodes.isEmpty()) {
                String json = toPosInfoJson();
                writer.write(json);
            }
        } catch (Throwable t) {
            logger.error("Failed writing positionFile", t);
        } finally {
            try {
                if (writer != null) writer.close();
            } catch (IOException e) {
                logger.error("Error: " + e.getMessage(), e);
            }
        }
    }

    private String toPosInfoJson() {
        @SuppressWarnings("rawtypes")
        List<Map> posInfos = Lists.newArrayList();
        for (Long inode : existingInodes) {
            TailFile tf = reader.getTailFiles().get(inode);
            posInfos.add(ImmutableMap.of("inode", inode, "pos", tf.getPos(), "file", tf.getPath()));
        }
        return new Gson().toJson(posInfos);
    }
}
