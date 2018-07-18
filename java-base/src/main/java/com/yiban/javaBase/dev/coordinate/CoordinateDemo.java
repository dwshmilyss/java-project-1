package com.yiban.javaBase.dev.coordinate;

import ch.hsr.geohash.GeoHash;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.spatial4j.core.context.SpatialContext;
import com.spatial4j.core.distance.DistanceUtils;
import com.spatial4j.core.io.GeohashUtils;
import com.spatial4j.core.shape.Rectangle;

import java.net.URL;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 根据 纬度lat 经度log计算排序
 *  a）在纬度相等的情况下：

 经度每隔0.00001度，距离相差约1米；

 每隔0.0001度，距离相差约10米；

 每隔0.001度，距离相差约100米；

 每隔0.01度，距离相差约1000米；

 每隔0.1度，距离相差约10000米。

 b）在经度相等的情况下：

 纬度每隔0.00001度，距离相差约1.1米；

 每隔0.0001度，距离相差约11米；

 每隔0.001度，距离相差约111米；

 每隔0.01度，距离相差约1113米；

 每隔0.1度，距离相差约11132米。



 geohash length
 lat bits
 lng bits
 lat error
 lng error
 km error
 1	2	3	±23	±23	±2500
 2	5	5	± 2.8	± 5.6	±630
 3	7	8	± 0.70	± 0.7	±78
 4	10	10	± 0.087	± 0.18	±20
 5	12	13	± 0.022	± 0.022	±2.4
 6	15	15	± 0.0027	± 0.0055	±0.61
 7	17	18	±0.00068	±0.00068	±0.076
 8	20	20	±0.000085	±0.00017	±0.019
 *
 *
 * @auther WEI.DUAN
 * @date 2018/7/16
 * @website http://blog.csdn.net/dwshmilyss
 */
public class CoordinateDemo {
    /**
     * 调用阿里云的API 根据经纬度获取用户所在的城市
     *
     * @param log
     * @param lat
     * @return
     */
    public static String getAdd(String log, String lat) {
        // lat 小 log 大
        // 参数解释: 纬度,经度 type 001 (100代表道路，010代表POI，001代表门址，111可以同时显示前三项)
        String urlString = "http://gc.ditu.aliyun.com/regeocoding?l=" + lat
                + "," + log + "&type=010";
        String res = "";
        try {
            URL url = new URL(urlString);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url
                    .openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            java.io.BufferedReader in = new java.io.BufferedReader(
                    new java.io.InputStreamReader(conn.getInputStream(),
                            "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                res += line + "\n";
            }
            in.close();
        } catch (Exception e) {
            System.out.println("error in wapaction,and e is " + e.getMessage());
        }
        System.out.println(res);
        return res;
    }


    public static void testGetAdd() {
        // lat 31.2990170 纬度
        // log 121.3466440 经度
        String add = getAdd("121.3466440", "31.2990170");
        JSONObject jsonObject = JSONObject.parseObject(add);
        JSONArray jsonArray = JSONArray.parseArray(jsonObject
                .getString("addrList"));
        JSONObject j_2 = JSONObject.parseObject(jsonArray.get(0).toString());
        String allAdd = j_2.getString("admName");
        String arr[] = allAdd.split(",");
        System.out.println("省:" + arr[0] + "\n市:" + arr[1] + "\n区:" + arr[2]);
    }


    /**
     * ============ 根据坐标计算附近的商户
     * 需要在mysql中维护一张表，表结构大概是：
     * CREATE TABLE `customer` (
     `id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
     `name` VARCHAR(5) NOT NULL COMMENT '名称' COLLATE 'latin1_swedish_ci',
     `lon` DOUBLE(9,6) NOT NULL COMMENT '经度',
     `lat` DOUBLE(8,6) NOT NULL COMMENT '纬度',
     `geo_code` CHAR(12) NOT NULL COMMENT 'geohash编码',
     PRIMARY KEY (`id`),
     INDEX `idx_geo_code` (`geo_code`)
     )
     COMMENT='商户表'
     CHARSET=utf8mb4
     ENGINE=InnoDB;
     */

    /**
     * 给定经纬度 及范围（1公里）  计算范围坐标
     * 其实就是：给定圆心坐标和半径，求该圆外切正方形四个顶点的坐标。而我们面对的是一个球体，可以使用spatial4j来计算。
     */
    public static void testSpatial4j() {
        // 移动设备经纬度
        double lon = 116.312528, lat = 39.983733;
        // 千米
        int radius = 1;

        SpatialContext geo = SpatialContext.GEO;
        Rectangle rectangle = geo.getDistCalc().calcBoxByDistFromPt(
                geo.makePoint(lon, lat), radius * DistanceUtils.KM_TO_DEG, geo, null);
        System.out.println(rectangle.getMinX() + "-" + rectangle.getMaxX());// 经度范围
        System.out.println(rectangle.getMinY() + "-" + rectangle.getMaxY());// 纬度范围

        //获取当前移动设备的10位精度的geo code
        GeoHash geoHash = GeoHash.withCharacterPrecision(lat, lon, 10);
        // 当前
        System.out.println(geoHash.toBase32());

        /**
         * 获取当前网格周边的8个网格
         *  N, NE, E, SE, S, SW, W, NW
         */
        System.out.println("===================");
        GeoHash[] adjacent = geoHash.getAdjacent();
        for (GeoHash hash : adjacent) {
            System.out.println(hash.toBase32());
        }
    }

    /**
     * geohash算法能把二维的经纬度编码成一维的字符串，它的特点是越相近的经纬度编码后越相似，所以可以通过前缀like的方式去匹配周围的商户。
     * 维护商户表中的geo_code
     */
    public static void testGeoHash(){
        //默认精度是12位
        String geoCode_12 = GeohashUtils.encodeLatLon(39.983733,116.312528);
        String geoCode_6 = GeohashUtils.encodeLatLon(39.983733,116.312528,6);
        System.out.println("geoCode_12 = " + geoCode_12);
        System.out.println("geoCode_6 = " + geoCode_6);
    }

    /**
     * 上面的1公里搜索只是尽量的缩小搜索范围 如果要精确
     * 则必须按照距离计算，过滤掉大于1公里的商户。
     * 然后按照距离排序 这个很简单  利用Collections.sort(list, comparator)。
     * 最后是分页  可以在内存中分页
     */
    public static void testDistance(){
        // 移动设备经纬度
        double lon1 = 116.3125333347639, lat1 = 39.98355521792821;
        // 商户经纬度
        double lon2 = 116.312528, lat2 = 39.983733;

        SpatialContext geo = SpatialContext.GEO;
        double distance = geo.calcDistance(geo.makePoint(lon1, lat1), geo.makePoint(lon2, lat2))
                * DistanceUtils.DEG_TO_KM;
        System.out.println(distance);// KM
    }

    public static void main(String[] args) {
//        testGetAdd();
//        testSpatial4j();
//        testGeoHash();
//        testDistance();
        Integer[] array = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        List<Integer> list = Arrays.asList(array);

        Pager<Integer> pager = Pager.create(list, 10);

        List<Integer> page1 = pager.getPagedList(1);
        System.out.println(page1);

        List<Integer> page2 = pager.getPagedList(2);
        System.out.println(page2);

        List<Integer> page3 = pager.getPagedList(3);
        System.out.println(page3);
    }
}

/**
 * 内存分页器 适用于小的集合
 * @param <T>
 */
class Pager<T> {

    /**
     * 每页显示条数
     */
    private int pageSize;
    /**
     * 原集合
     */
    private List<T> data;

    private Pager(List<T> data, int pageSize) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("data must be not empty!");
        }

        this.data = data;
        this.pageSize = pageSize;
    }

    /**
     * 创建分页器
     *
     * @param data     需要分页的数据
     * @param pageSize 每页显示条数
     * @param <T>      业务对象
     * @return 分页器
     */
    public static <T> Pager<T> create(List<T> data, int pageSize) {
        return new Pager<>(data, pageSize);
    }

    /**
     * 得到分页后的数据
     *
     * @param pageNum 页码
     * @return 分页后结果
     */
    public List<T> getPagedList(int pageNum) {
        int fromIndex = (pageNum - 1) * pageSize;
        if (fromIndex >= data.size()) {
            return Collections.emptyList();
        }

        int toIndex = pageNum * pageSize;
        if (toIndex >= data.size()) {
            toIndex = data.size();
        }
        return data.subList(fromIndex, toIndex);
    }

    public int getPageSize() {
        return pageSize;
    }

    public List<T> getData() {
        return data;
    }
}