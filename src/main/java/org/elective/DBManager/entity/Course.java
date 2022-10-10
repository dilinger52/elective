package org.elective.DBManager.entity;

import java.io.Serializable;

public class Course implements Serializable, Entity {
    int id;
    int teacherId;
    String name;
    String topic;
    String description;
    long duration;

    public Course(int teacherId, String name, String topic, String description, long duration) {
        this.id = 0;
        this.teacherId = teacherId;
        this.name = name;
        this.topic = topic;
        this.description = description;
        this.duration = duration;
    }

    public Course() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", teacherId=" + teacherId +
                ", name='" + name + '\'' +
                ", topic='" + topic + '\'' +
                ", duration=" + duration +
                '}';
    }
}
