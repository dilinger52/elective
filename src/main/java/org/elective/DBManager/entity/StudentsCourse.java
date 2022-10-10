package org.elective.DBManager.entity;

import java.io.Serializable;
import java.sql.Date;

public class StudentsCourse implements Serializable, Entity {
    int studentId;
    int courseId;
    Date registrationDate;

    long grade;

    public StudentsCourse() {
    }

    public StudentsCourse(int studentId, int courseId, Date registrationDate) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.registrationDate = registrationDate;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getCourseId() {
        return courseId;
    }

    public int getId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public long getGrade() {
        return grade;
    }

    public void setGrade(long grade) {
        this.grade = grade;
    }

    @Override
    public String toString() {
        return "StudentsCourse{" +
                "studentId=" + studentId +
                ", courseId=" + courseId +
                ", registrationDate=" + registrationDate +
                ", grade=" + grade +
                '}';
    }
}