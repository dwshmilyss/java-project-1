package com.yiban.javaBase.test;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @Description: TODO
 * @Author: David.duan
 * @Date: 2022/8/2
 **/
@Data
public class Contact {
    private long id;
    private long ts;
    private String date = "2020/01/01";
    private long version;
    private String anonymous_id;
    private String avatar;
    private String city;
    private String comments;
    private String company;
    private String country;
    private Timestamp date_created;
    private Date date_of_birthday;
    private String email;
    private String gender;
    private String home_phone;
    private String industry;
    private Timestamp last_updated;
    private String mobile_phone;
    private String name;
    private String postal_code;
    private String state;
    private String street;
    private long tenant_id;
    private String title;
    private String user_name;
    private String website;
    private String nickname;
    private String department;
    private long merged_id;
    private byte is_anonymous;
    private String id_card;
    private String company_short_name;
    private int age;
    private long group1;
    private long group2;
    private long group3;
    private long group4;
    private long group5;
    private long group6;
    private long group7;
    private long group8;
    private long group9;
    private long group10;
    private long group11;
    private long group12;

    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", ts=" + ts +
                ", date='" + date + '\'' +
                ", version=" + version +
                ", anonymous_id='" + anonymous_id + '\'' +
                ", avatar='" + avatar + '\'' +
                ", city='" + city + '\'' +
                ", comments='" + comments + '\'' +
                ", company='" + company + '\'' +
                ", country='" + country + '\'' +
                ", date_created=" + date_created +
                ", date_of_birthday=" + date_of_birthday +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", home_phone='" + home_phone + '\'' +
                ", industry='" + industry + '\'' +
                ", last_updated=" + last_updated +
                ", mobile_phone='" + mobile_phone + '\'' +
                ", name='" + name + '\'' +
                ", postal_code='" + postal_code + '\'' +
                ", state='" + state + '\'' +
                ", street='" + street + '\'' +
                ", tenant_id=" + tenant_id +
                ", title='" + title + '\'' +
                ", user_name='" + user_name + '\'' +
                ", website='" + website + '\'' +
                ", nickname='" + nickname + '\'' +
                ", department='" + department + '\'' +
                ", merged_id=" + merged_id +
                ", is_anonymous=" + is_anonymous +
                ", id_card='" + id_card + '\'' +
                ", company_short_name='" + company_short_name + '\'' +
                ", age=" + age +
                ", group1=" + group1 +
                ", group2=" + group2 +
                ", group3=" + group3 +
                ", group4=" + group4 +
                ", group5=" + group5 +
                ", group6=" + group6 +
                ", group7=" + group7 +
                ", group8=" + group8 +
                ", group9=" + group9 +
                ", group10=" + group10 +
                ", group11=" + group11 +
                ", group12=" + group12 +
                '}';
    }
}
