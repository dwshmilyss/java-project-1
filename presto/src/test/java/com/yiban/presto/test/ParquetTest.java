package com.yiban.presto.test;

import com.yiban.presto.dev.ParquetMetadataReader;
import org.apache.hadoop.conf.Configuration;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hudi.org.apache.parquet.hadoop.Footer;
import org.apache.hudi.org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.hudi.org.apache.parquet.hadoop.metadata.BlockMetaData;
import org.apache.hudi.org.apache.parquet.hadoop.metadata.ParquetMetadata;
import org.junit.Test;
import parquet.hadoop.metadata.FileMetaData;
import parquet.schema.MessageType;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ParquetTest {
    @Test
    public void test() throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
//        Path path = new Path("hdfs://hdp1-pre1.leadswarp.com:8020/hudi/tenant362/contact/default/ad29c9c8-a99c-4a77-bbc8-c8f56d7199ab-0_0-1851-95543_20220106171258.parquet");
        Path path = new Path("hdfs://hdp1-pre1.leadswarp.com:8020/hudi/tenant362/contact/default/a553f7e2-7f3e-4136-a186-750ec1a395d5-0_1-2423-124720_20220106174659.parquet");
        FSDataInputStream inputStream = fs.open(path);

        FileStatus fileStatus = fs.getFileStatus(path);
//        System.out.println(fileStatus.getBlockSize());
        System.out.println(fileStatus.getLen());
//        System.out.println(fileStatus.getPath());
//        System.out.println(fileStatus.getOwner());
//        List<Footer> footers = ParquetFileReader.readFooters(conf, path);
//        System.out.println("footers.size() = " + footers.size());
//        Footer footer = footers.get(0);
//        System.out.println("footer.getParquetMetadata().getBlocks().size() = " + footer.getParquetMetadata().getBlocks().size());
////        for (Footer footer : footers) {
////            System.out.println(footer.toString());
////        }
////        System.out.println(footer.toString());
//        ParquetMetadata parquetMetadata = footer.getParquetMetadata();
////        System.out.println("parquetMetadata.toString() = " + parquetMetadata.toString());
//        Map<String, String> keyValueMetaData = parquetMetadata.getFileMetaData().getKeyValueMetaData();
//        for (String key : keyValueMetaData.keySet()) {
//            System.out.println("key = " + key + ",value = " + keyValueMetaData.get(key));
//        }
//        List<BlockMetaData> blocks = footer.getParquetMetadata().getBlocks();
//        BlockMetaData blockMetaData = blocks.get(0);
//        System.out.println("blockMetaData.getRowCount() = " + blockMetaData.getRowCount());
//        System.out.println("blockMetaData.getPath() = " + blockMetaData.getPath());
//        System.out.println("blockMetaData.getStartingPos() = " + blockMetaData.getStartingPos());
//        System.out.println("blockMetaData.getTotalByteSize() = " + blockMetaData.getTotalByteSize());


        parquet.hadoop.metadata.ParquetMetadata parquetMetadata = ParquetMetadataReader.readFooter(inputStream, path, fileStatus.getLen());
        FileMetaData fileMetaData = parquetMetadata.getFileMetaData();
        MessageType fileSchema = fileMetaData.getSchema();
        System.out.println(fileSchema.getName());
        System.out.println(fileSchema.getFieldCount());
        System.out.println(fileSchema.getId());
    }
}
