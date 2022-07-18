package com.yiban.ignite.dev.entity;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.io.Serializable;

public class Person implements Serializable {
    private static final long serialVersionUID = 5158183685676617514L;

    /** Indexed field. Will be visible to the SQL engine. */
    @QuerySqlField(index = true)
    private long id;

    /** Queryable field. Will be visible to the SQL engine. */
    @QuerySqlField
    private String name;

    public Person() {
    }

    public Person(long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
