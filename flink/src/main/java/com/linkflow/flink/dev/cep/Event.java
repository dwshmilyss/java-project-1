package com.linkflow.flink.dev.cep;

import java.util.Objects;

/**
 * @author david.duan
 * @packageName com.linkflow.flink.dev.cep
 * @className Event
 * @date 2024/11/24
 * @description
 */
public class Event {
    private String name;
    private int id;

    public Event(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return ("Event(" + id + ", " + name + ")");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Event) {
            Event other = (Event) obj;

            return name.equals(other.name) && id == other.id;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id);
    }
}
