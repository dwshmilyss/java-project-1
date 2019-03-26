package com.yiban.mybatis.dev.entity;

import java.util.Date;

/**
 * table name:  student
 * author name: xc
 * create time: 2019-03-26 18:13:58
 */
public class Student {

    private int studentId;
    private String name;
    private String phone;
    private String email;
    private byte sex;
    private byte locked;
    private Date gmtCreated;
    private Date gmtModified;
    private int delete;

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setSex(byte sex) {
        this.sex = sex;
    }

    public byte getSex() {
        return sex;
    }

    public void setLocked(byte locked) {
        this.locked = locked;
    }

    public byte getLocked() {
        return locked;
    }

    public void setGmtCreated(Date gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public Date getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setDelete(int delete) {
        this.delete = delete;
    }

    public int getDelete() {
        return delete;
    }
}

