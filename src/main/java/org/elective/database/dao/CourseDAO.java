package org.elective.database.dao;

import org.elective.database.DBException;
import org.elective.database.entity.Course;

import java.sql.Connection;
import java.util.List;

public interface CourseDAO {

    void create(Connection con, Course course) throws DBException;

    Course read(Connection con, int id) throws DBException;

    void update(Connection con, Course course) throws DBException;

    void delete(Connection con, int courseId) throws DBException;

    List<Course> findByName(Connection con, String pattern) throws DBException;

    List<Course> getAllCourses(Connection con) throws DBException;

    List<String> getAllTopics(Connection con) throws DBException;

    List<Course> findCoursesByTeacher(Connection con, int pattern) throws DBException;
}
