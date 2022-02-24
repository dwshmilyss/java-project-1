package com.linfflow.flink.dev;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.core.fs.Path;
import org.apache.flink.formats.json.JsonRowDataDeserializationSchema;
import org.apache.flink.formats.json.TimestampFormat;
import org.apache.flink.runtime.operators.coordination.OperatorEvent;
import org.apache.flink.runtime.state.CheckpointListener;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.operators.KeyedProcessOperator;
import org.apache.flink.streaming.api.operators.ProcessOperator;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.data.StringData;
import org.apache.flink.table.data.TimestampData;
import org.apache.flink.table.data.binary.BinaryRowData;
import org.apache.flink.table.data.writer.BinaryRowWriter;
import org.apache.flink.table.data.writer.BinaryWriter;
import org.apache.flink.table.runtime.typeutils.InternalSerializers;
import org.apache.flink.table.runtime.typeutils.InternalTypeInfo;
import org.apache.flink.table.types.logical.LogicalType;
import org.apache.flink.table.types.logical.RowType;
import org.apache.hudi.common.model.HoodieRecord;
import org.apache.hudi.configuration.FlinkOptions;
import org.apache.hudi.sink.StreamWriteOperatorFactory;
import org.apache.hudi.sink.bootstrap.BootstrapFunction;
import org.apache.hudi.sink.partitioner.BucketAssignFunction;
import org.apache.hudi.sink.partitioner.BucketAssignOperator;
import org.apache.hudi.sink.transform.RowDataToHoodieFunction;
import org.apache.hudi.util.AvroSchemaConverter;
import org.apache.hudi.util.StreamerUtil;
import org.apache.hudi.utils.TestConfigurations;
import org.apache.hudi.utils.TestData;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

public class HudiDemo {

    public static List<RowData> DATA_SET_INSERT = Arrays.asList(
            insertRow(StringData.fromString("id1"), StringData.fromString("Danny"), 230,
                    TimestampData.fromEpochMillis(1), StringData.fromString("par1")),
            insertRow(StringData.fromString("id3"), StringData.fromString("Julian"), 530,
                    TimestampData.fromEpochMillis(3), StringData.fromString("par2"))
    );

    public static void main(String[] args) {
        try {
            String hudiTablePath = "file:///Users/edz/Desktop/hudi/flink/t2";
            Configuration conf = getConf(hudiTablePath);
            testInsert2Hudi(conf);
            testUpsert2Hudi(hudiTablePath,conf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testInsert2Hudi(Configuration conf) throws Exception {
        StreamExecutionEnvironment execEnv = createExecEnv();
//        use BoundedSourceFunction to create dataSource
        String sourcePath = Objects.requireNonNull(Thread.currentThread()
                .getContextClassLoader().getResource("test_source.data")).toString();
        DataStreamSource<String> dataStreamSource = execEnv
                .addSource(new BoundedSourceFunction(new Path(sourcePath), 2));
//        use List to create dataSource
//        DataStreamSource<String> dataStreamSource = execEnv.fromCollection(Arrays.asList("{\"uuid\": \"id1\", \"name\": \"Danny\", \"age\": 25, \"ts\": \"1970-01-01T00:00:01\", \"partition\": \"par1\"}", "{\"uuid\": \"id3\", \"name\": \"Julian\", \"age\": 55, \"ts\": \"1970-01-01T00:00:03\", \"partition\": \"par2\"}"));
        write2Hudi(conf, execEnv, dataStreamSource);
    }

    /**
     * test upsert
     * @param hudiTablePath
     * @throws Exception
     */
    public static void testUpsert2Hudi(String hudiTablePath,Configuration conf) throws Exception {
        StreamWriteFunctionWrapper<RowData> funcWrapper = new StreamWriteFunctionWrapper<>(hudiTablePath, conf);
        funcWrapper.openFunction();
        for (RowData rowData : DATA_SET_INSERT) {
            funcWrapper.invoke(rowData);
        }
        funcWrapper.checkpointFunction(1);
        OperatorEvent nextEvent = funcWrapper.getNextEvent();
        funcWrapper.getCoordinator().handleEventFromOperator(0, nextEvent);
        funcWrapper.checkpointComplete(1);
        funcWrapper.close();
    }

    private static BinaryRowData insertRow(Object... fields) {
        LogicalType[] types = TestConfigurations.ROW_TYPE.getFields().stream().map(RowType.RowField::getType)
                .toArray(LogicalType[]::new);
        assertEquals(
                "Filed count inconsistent with type information",
                fields.length,
                types.length);
        BinaryRowData row = new BinaryRowData(fields.length);
        BinaryRowWriter writer = new BinaryRowWriter(row);
        writer.reset();
        for (int i = 0; i < fields.length; i++) {
            Object field = fields[i];
            if (field == null) {
                writer.setNullAt(i);
            } else {
                BinaryWriter.write(writer, i, field, types[i], InternalSerializers.create(types[i]));
            }
        }
        writer.complete();
        return row;
    }

    private static StreamExecutionEnvironment createExecEnv() {
        StreamExecutionEnvironment execEnv = StreamExecutionEnvironment.getExecutionEnvironment();
        execEnv.getConfig().disableObjectReuse();
        execEnv.setParallelism(4);
        // set up checkpoint interval
        execEnv.enableCheckpointing(4000, CheckpointingMode.EXACTLY_ONCE);
        execEnv.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
        return execEnv;
    }

    private static void write2Hudi(Configuration conf,StreamExecutionEnvironment execEnv,DataStreamSource<String> dataStreamSource) throws Exception {
        // Read from file source
        RowType rowType =
                (RowType) AvroSchemaConverter.convertToDataType(StreamerUtil.getSourceSchema(conf))
                        .getLogicalType();
        StreamWriteOperatorFactory<HoodieRecord> operatorFactory =
                new StreamWriteOperatorFactory<>(conf);

        JsonRowDataDeserializationSchema deserializationSchema = new JsonRowDataDeserializationSchema(
                rowType,
                InternalTypeInfo.of(rowType),
                false,
                true,
                TimestampFormat.ISO_8601
        );
        String sourcePath = Objects.requireNonNull(Thread.currentThread()
                .getContextClassLoader().getResource("test_source.data")).toString();

        DataStream<HoodieRecord> hoodieDataStream = dataStreamSource
                .name("continuous_file_source")
                .setParallelism(1)
                .map(record -> deserializationSchema.deserialize(record.getBytes(StandardCharsets.UTF_8)))
                .setParallelism(4)
                .map(new RowDataToHoodieFunction<>(rowType, conf), TypeInformation.of(HoodieRecord.class));

        if (conf.getBoolean(FlinkOptions.INDEX_BOOTSTRAP_ENABLED)) {
            hoodieDataStream = hoodieDataStream.transform("index_bootstrap",
                    TypeInformation.of(HoodieRecord.class),
                    new ProcessOperator<>(new BootstrapFunction<>(conf)));
        }

        DataStream<Object> pipeline = hoodieDataStream
                // Key-by record key, to avoid multiple subtasks write to a bucket at the same time
                .keyBy(HoodieRecord::getRecordKey)
                .transform(
                        "bucket_assigner",
                        TypeInformation.of(HoodieRecord.class),
                        new BucketAssignOperator<>(new BucketAssignFunction<>(conf)))
                .uid("uid_bucket_assigner")
                // shuffle by fileId(bucket id)
                .keyBy(record -> record.getCurrentLocation().getFileId())
                .transform("hoodie_stream_write", TypeInformation.of(Object.class), operatorFactory)
                .uid("uid_hoodie_stream_write");
        execEnv.addOperator(pipeline.getTransformation());

        JobClient client = execEnv.executeAsync(execEnv.getStreamGraph(conf.getString(FlinkOptions.TABLE_NAME)));
        // wait for the streaming job to finish
        client.getJobExecutionResult().get();
    }

    private static Configuration getConf(String tablePath) {
        String schemaStr = "{        \"type\": \"record\",        \"name\": \"record\",        \"fields\": [{                \"name\": \"uuid\",                \"type\": [\"null\", \"string\"],                \"default\": null        }, {                \"name\": \"name\",                \"type\": [\"null\", \"string\"],                \"default\": null        }, {                \"name\": \"age\",                \"type\": [\"null\", \"int\"],                \"default\": null        }, {                \"name\": \"ts\",                \"type\": [\"null\", {                        \"type\": \"long\",                        \"logicalType\": \"timestamp-millis\"                }],                \"default\": null        }, {                \"name\": \"partition\",                \"type\": [\"null\", \"string\"],                \"default\": null        }]}";
        Configuration conf = new Configuration();
        conf.setString(FlinkOptions.PATH, tablePath);
//        conf.setString(FlinkOptions.SOURCE_AVRO_SCHEMA_PATH,
//                Objects.requireNonNull(Thread.currentThread()
//                        .getContextClassLoader().getResource("test_read_schema.avsc")).toString());
        conf.setString(FlinkOptions.SOURCE_AVRO_SCHEMA,schemaStr);
        conf.setString(FlinkOptions.TABLE_NAME, "TestHoodieTable");
        conf.setString(FlinkOptions.PARTITION_PATH_FIELD, "partition");
        return conf;
    }

    public static class BoundedSourceFunction implements SourceFunction<String>, CheckpointListener {
        private final Path path;
        private List<String> dataBuffer;

        private final int checkpoints;
        private final AtomicInteger currentCP = new AtomicInteger(0);

        private volatile boolean isRunning = true;

        public BoundedSourceFunction(Path path, int checkpoints) {
            this.path = path;
            this.checkpoints = checkpoints;
        }

        @Override
        public void run(SourceContext<String> context) throws Exception {
            if (this.dataBuffer == null) {
                loadDataBuffer();
            }
            int oldCP = this.currentCP.get();
            boolean finish = false;
            while (isRunning) {
                int batchSize = this.dataBuffer.size() / this.checkpoints;
                int start = batchSize * oldCP;
                synchronized (context.getCheckpointLock()) {
                    for (int i = start; i < start + batchSize; i++) {
                        if (i >= this.dataBuffer.size()) {
                            finish = true;
                            break;
                            // wait for the next checkpoint and exit
                        }
                        context.collect(this.dataBuffer.get(i));
                    }
                }
                oldCP++;
                while (this.currentCP.get() < oldCP) {
                    synchronized (context.getCheckpointLock()) {
                        context.getCheckpointLock().wait(10);
                    }
                }
                if (finish || !isRunning) {
                    return;
                }
            }
        }

        @Override
        public void cancel() {
            this.isRunning = false;
        }

        private void loadDataBuffer() {
            try {
                this.dataBuffer = Files.readAllLines(Paths.get(this.path.toUri()));
            } catch (IOException e) {
                throw new RuntimeException("Read file " + this.path + " error", e);
            }
        }

        @Override
        public void notifyCheckpointComplete(long l) throws Exception {
            this.currentCP.incrementAndGet();
        }
    }

}
