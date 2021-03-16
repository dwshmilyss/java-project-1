package com.yiban.hbase.coprocessor.endpoint;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.google.protobuf.Service;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.Coprocessor;
import org.apache.hadoop.hbase.CoprocessorEnvironment;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.coprocessor.CoprocessorException;
import org.apache.hadoop.hbase.coprocessor.CoprocessorService;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.protobuf.ResponseConverter;
import org.apache.hadoop.hbase.regionserver.InternalScanner;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 部署到hbase服务端的endpoint
 * 注意 部署这个类的时候打包不要把依赖打进来，只要包含这个类和maxProtocol类就可以了 不然会报和google.proto.getSerivce冲突
 * 解决了半天  最后还是重启hbase服务解决了问题 这里特别记录下
 * 另外这里的logger打印的输出 因为只有一个region region在哪个regionserver上  就去哪个服务器上查看log
 * 因为这里测试的Student表，这个表的region在cdhslave3上，所以log路径就在
 * /var/log/hbase/hbase-cmf-hbase-REGIONSERVER-cdhslave3.log.out
 */
public class MaxServer extends maxProtocol.maxService implements Coprocessor, CoprocessorService {
    private  static Logger logger= LoggerFactory.getLogger(MaxServer.class);
    private RegionCoprocessorEnvironment env;//定义环境

    static {
        logger.info("找到MaxServer了");
    }

    @Override
    public void start(CoprocessorEnvironment env) throws IOException {
        if (env instanceof RegionCoprocessorEnvironment) {
            this.env = (RegionCoprocessorEnvironment)env;
        } else {
            throw new CoprocessorException("no load region");
        }
    }

    @Override
    public void stop(CoprocessorEnvironment env) throws IOException {

    }

    @Override
    public Service getService() {
        return this;
    }

    /**
     * <code>rpc getmax(.maxRequest) returns (.maxResponse);</code>
     *
     * @param controller
     * @param request
     * @param done
     */
    @Override
    public void getmax(RpcController controller, maxProtocol.maxRequest request, RpcCallback<maxProtocol.maxResponse> done) {
        String family = request.getFamily();
        if (null == family || "".equals(family)) {
            throw new NullPointerException("you need specify the family");
        }
        String column = request.getColumn();
        if (null == column || "".equals(column)) {
            throw new NullPointerException("you need specify the column");
        }

        Scan scan = new Scan();
        //设置扫描对象
        scan.addColumn(Bytes.toBytes(family), Bytes.toBytes(column));
        //定义变量
        maxProtocol.maxResponse response = null;
        InternalScanner scanner = null;

        //扫描每个region，取值后求和
        try {
            scanner = env.getRegion().getScanner(scan);
            List<Cell> results = new ArrayList<>();
            boolean hasMore = false;
            Double max = null;
            do {
                hasMore = scanner.next(results);
                if (results.isEmpty()) {
                    continue;
                }
                Cell kv = results.get(0);
                Double temp = Double.parseDouble(new String(CellUtil.cloneValue(kv)));
                System.out.println("temp = " + temp);
                logger.info("temp = " + temp);
                max = max != null && (temp == null || compare(temp, max) <= 0) ? max : temp;
                logger.info("max = " + max);
                results.clear();
                //sum += Long.parseLong(new String(CellUtil.cloneValue(cell)));
                //results.clear();
            } while (hasMore);
            // 设置返回结果
            logger.info("response = " + response);
            response = maxProtocol.maxResponse.newBuilder().setMax((max != null ? max.doubleValue() : Double.MAX_VALUE)).build();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            ResponseConverter.setControllerException(controller, e);
        }finally {
            if (scanner != null) {
                try {
                    scanner.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // 将rpc结果返回给客户端
        done.run(response);
    }

    public static int compare(Double l1, Double l2) {
        if (l1 == null ^ l2 == null) {//因为是异或运算，两者不同的时候才是true，也就是说
            return l1 == null ? -1 : 1; // 两者中有一个为null.
        } else if (l1 == null)//为什么这里只判断一个就断定两者都是null呢？因为上面是异或运算，不同的时候是true，那么这里的else就是相同，也就是说要么两者都是null，
            //要么两者都不是null，这里再加上if(l1==null) 那么也就是说两者都是null了
            return 0; // both are null 两者都是null
        return l1.compareTo(l2); // natural ordering.自然排序
    }
}
