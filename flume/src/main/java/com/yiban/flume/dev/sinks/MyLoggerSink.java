package com.yiban.flume.dev.sinks;

import com.google.common.base.Strings;
import org.apache.flume.*;
import org.apache.flume.conf.Configurable;
import org.apache.flume.event.EventHelper;
import org.apache.flume.sink.AbstractSink;
import org.apache.flume.sink.LoggerSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自定义LoggerSink 以实现输出日志时最大只有16 byte的限制
 */
public class MyLoggerSink extends AbstractSink implements Configurable {
    private static final Logger logger = LoggerFactory
            .getLogger(LoggerSink.class);

    // Default Max bytes to dump
    public static final int DEFAULT_MAX_BYTE_DUMP = 16;

    // Max number of bytes to be dumped
    private int maxBytesToLog = DEFAULT_MAX_BYTE_DUMP;

    public static final String MAX_BYTES_DUMP_KEY = "maxBytesToLog";

    @Override
    public void configure(Context context) {
        logger.info("========= configure =========");
        //从配置文件中读取maxBytesToLog属性的值
        String strMaxBytes = context.getString(MAX_BYTES_DUMP_KEY);
        logger.info("strMaxBytes -> " + strMaxBytes);
        if (!Strings.isNullOrEmpty(strMaxBytes)) {
            try {
                maxBytesToLog = Integer.parseInt(strMaxBytes);
            } catch (NumberFormatException e) {
                logger.warn(String.format(
                        "Unable to convert %s to integer, using default value(%d) for maxByteToDump",
                        strMaxBytes, DEFAULT_MAX_BYTE_DUMP));
                maxBytesToLog = DEFAULT_MAX_BYTE_DUMP;
            }
        }
        logger.info("maxBytesToLog -> " + maxBytesToLog);
    }

    //这个函数是用来让sink消费从关联的channel中得到的数据，SinkRunner不断调用这个方法
    @Override
    public Status process() throws EventDeliveryException {
        logger.info("========= process =========");
        Status result = Status.READY;
        Channel channel = getChannel();
        Transaction transaction = channel.getTransaction();
        Event event = null;

        try {
            transaction.begin();
            //从channel中获得下一个event
            event = channel.take();

            if (event != null) {
                if (logger.isInfoEnabled()) {
                    logger.info("Event: " + EventHelper.dumpEvent(event, maxBytesToLog));
                }
            } else {
                // No event found, request back-off semantics from the sink runner
                result = Status.BACKOFF;
            }
            transaction.commit();
        } catch (Exception ex) {
            transaction.rollback();
            throw new EventDeliveryException("Failed to log event: " + event, ex);
        } finally {
            transaction.close();
        }

        return result;
    }
}
