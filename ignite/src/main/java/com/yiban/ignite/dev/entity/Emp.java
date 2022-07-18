package com.yiban.ignite.dev.entity;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.io.Serializable;

public class Emp implements Serializable {
    private static final long serialVersionUID = 5158183685676617514L;

    /** Indexed field. Will be visible to the SQL engine. */
    @QuerySqlField(index = true)
    private long id;

    /** Queryable field. Will be visible to the SQL engine. */
    @QuerySqlField
    private long empNo;

    /** Queryable field. Will be visible to the SQL engine. */
    @QuerySqlField
    private String empName;

    public Emp() {
    }

    public Emp(long id, long empNo, String empName) {
        this.id = id;
        this.empNo = empNo;
        this.empName = empName;
    }

    @Override
    public String toString() {
        return "Emp{" +
                "id=" + id +
                ", empno=" + empNo +
                ", ename='" + empName + '\'' +
                '}';
    }

    public long getEmpNo() {
        return empNo;
    }

    public void setEmpNo(long empNo) {
        this.empNo = empNo;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }
}
