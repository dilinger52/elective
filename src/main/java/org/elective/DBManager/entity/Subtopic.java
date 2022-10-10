package org.elective.DBManager.entity;

import java.io.Serializable;

public class Subtopic implements Serializable, Entity {
    int courseId;
    int id;
    String subtopicName;
    String subtopicContent;

    public Subtopic(int courseId, String subtopicName) {
        this.courseId = courseId;
        this.id = 0;
        this.subtopicName = subtopicName;
    }

    public Subtopic() {

    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubtopicName() {
        return subtopicName;
    }

    public void setSubtopicName(String subtopicName) {
        this.subtopicName = subtopicName;
    }

    public String getSubtopicContent() {
        return subtopicContent;
    }

    public void setSubtopicContent(String subtopicContent) {
        this.subtopicContent = subtopicContent;
    }

    @Override
    public String toString() {
        return "Subtopic{" +
                "courseId=" + courseId +
                ", subtopicId=" + id +
                ", subtopicName='" + subtopicName + '\'' +
                '}';
    }
}
