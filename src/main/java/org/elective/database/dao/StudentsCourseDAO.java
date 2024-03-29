package org.elective.database.dao;

import org.elective.database.DBException;
import org.elective.database.entity.StudentsCourse;

import java.sql.Connection;
import java.util.List;

public interface StudentsCourseDAO {
    void create(Connection con, StudentsCourse course) throws DBException;

    StudentsCourse read(Connection con, int courseId, int studentId) throws DBException;

    void update(Connection con, StudentsCourse course) throws DBException;

    void delete(Connection con, StudentsCourse course) throws DBException;

    List<StudentsCourse> findCoursesByStudentId(Connection con, int studentId) throws DBException;

    List<StudentsCourse> findCoursesByCourseId(Connection con, int courseId) throws DBException;
}
