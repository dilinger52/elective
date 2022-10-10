package org.elective.DBManager.entity;

import java.io.Serializable;

import static org.elective.DBManager.entity.StudentsSubtopic.completion.UNCOMPLETED;

public class StudentsSubtopic implements Serializable {

    int studentId;
    int subtopicId;
    String completion;
    public enum completion {

        UNCOMPLETED ("uncompleted"),
        COMPLETED ("completed");

        private String title;

        completion(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    public StudentsSubtopic(int studentId, int subtopicId) {
        this.studentId = studentId;
        this.subtopicId = subtopicId;
        this.completion = UNCOMPLETED.toString();
    }

    public StudentsSubtopic() {
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getSubtopicId() {
        return subtopicId;
    }

    public void setSubtopicId(int subtopicId) {
        this.subtopicId = subtopicId;
    }

    public String getCompletion() {
        return completion;
    }

    public void setCompletion(String completion) {
        this.completion = completion;
    }
}
