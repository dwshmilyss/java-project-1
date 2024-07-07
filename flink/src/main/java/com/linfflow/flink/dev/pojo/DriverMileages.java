package com.linfflow.flink.dev.pojo;

/**
 * @Description: TODO
 * @Author: David.duan
 * @Date: 2023/8/1
 **/
public class DriverMileages {
    public String driverId;
    /**
     * 上报时的时间戳，单位毫秒
     */
    public long timestamp;
    /**
     * 相比上一个时间戳内行驶的里程数,单位米
     */
    public double currentMileage;

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getCurrentMileage() {
        return currentMileage;
    }

    public void setCurrentMileage(double currentMileage) {
        this.currentMileage = currentMileage;
    }

    @Override
    public String toString() {
        return "DriverMileages{" +
                "driverId='" + driverId + '\'' +
                ", timestamp=" + timestamp +
                ", currentMileage=" + currentMileage +
                '}';
    }
}

