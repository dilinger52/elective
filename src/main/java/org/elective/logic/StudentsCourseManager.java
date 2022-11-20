package org.elective.logic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elective.database.DBException;
import org.elective.database.DBUtils;
import org.elective.database.dao.*;
import org.elective.database.entity.*;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.elective.database.DBUtils.close;
import static org.elective.database.DBUtils.rollback;

public class StudentsCourseManager {

    private static final Logger logger = LogManager.getLogger(StudentsCourseManager.class);

    private StudentsCourseManager() {
        throw new IllegalStateException("Utility class");
    }

    public static StudentsCourse findStudentsCourse(int courseId, int studentId) throws DBException {
        try (Connection con = DBUtils.getInstance().getConnection()) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            StudentsCourseDAO studentsCourseDAO = daoFactory.getStudentsCourseDAO();
            StudentsCourse result = studentsCourseDAO.read(con, courseId, studentId);
            logger.debug("find course: {}", result);
            return result;
        } catch (Exception e) {
            logger.error(e);
            throw new DBException(e.getMessage(), e);
        }
    }

    public static List<StudentsCourse> findCoursesByStudent(int id) throws DBException {
        try (Connection con = DBUtils.getInstance().getConnection()) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            StudentsCourseDAO studentsCourseDAO = daoFactory.getStudentsCourseDAO();
            List<StudentsCourse> result = studentsCourseDAO.findCoursesByStudentId(con, id);
            logger.debug("find courses: {}", result);
            return result;
        } catch (Exception e) {
            logger.error(e);
            throw new DBException(e.getMessage(), e);
        }
    }

    public static List<StudentsCourse> findCoursesByCourseId(int id) throws DBException {
        try (Connection con = DBUtils.getInstance().getConnection()) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            StudentsCourseDAO studentsCourseDAO = daoFactory.getStudentsCourseDAO();
            List<StudentsCourse> result = studentsCourseDAO.findCoursesByCourseId(con, id);
            logger.debug("find courses: {}", result);
            return result;
        } catch (Exception e) {
            logger.error(e);
            throw new DBException(e.getMessage(), e);
        }
    }

    public static void updateGrade(int grade, StudentsCourse studentsCourse) throws DBException {
        studentsCourse.setGrade(grade);
        try (Connection con = DBUtils.getInstance().getConnection()) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            StudentsCourseDAO studentsCourseDAO = daoFactory.getStudentsCourseDAO();
            studentsCourseDAO.update(con, studentsCourse);
            logger.debug("setting new garde: {}", grade);
        } catch (Exception e) {
            logger.error(e);
            throw new DBException(e.getMessage(), e);
        }
    }

    public static void addCourseToStudent(User student, int courseId) throws DBException {
        StudentsCourse course = new StudentsCourse();
        course.setStudentId(student.getId());
        course.setCourseId(courseId);
        java.util.Date date = new Date();
        java.sql.Date dateSql = new java.sql.Date(date.getYear(), date.getMonth(), date.getDate());
        course.setRegistrationDate(dateSql);
        Connection con = null;
        try {
            con = DBUtils.getInstance().getConnection();
            con.setAutoCommit(false);
            DAOFactory daoFactory = DAOFactory.getInstance();
            StudentsCourseDAO studentsCourseDAO = daoFactory.getStudentsCourseDAO();
            SubtopicDAO subtopicDAO = daoFactory.getSubtopicDAO();
            StudentsSubtopicDAO studentsSubtopicDAO = daoFactory.getStudentsSubtopicDAO();
            studentsCourseDAO.create(con, course);
            List<Subtopic> subtopicList = subtopicDAO.findSubtopicsByCourse(con, course.getCourseId());
            for (Subtopic s : subtopicList) {
                StudentsSubtopic ss = new StudentsSubtopic(student.getId(), s.getId());
                studentsSubtopicDAO.create(con, ss);
            }
            con.commit();
            logger.debug("User: {} Joined course: {}", student, course);
        } catch (Exception e) {
            rollback(con);
            logger.error(e);
            throw new DBException(e.getMessage(), e);
        } finally {
            close(con);
        }
    }

    public static Map<Integer, Date> getCoursesRegistrationDate(List<Course> courses, User student) throws DBException {
        Map<Integer, Date> result = new HashMap<>();
        for (Course course : courses) {
            result.put(course.getId(), findStudentsCourse(course.getId(), student.getId()).getRegistrationDate());
        }
        return result;
    }

    public static Map<Integer, Long> getCoursesGrade(List<Course> courses, User student) throws DBException {
        Map<Integer, Long> result = new HashMap<>();
        for (Course course : courses) {
            result.put(course.getId(), findStudentsCourse(course.getId(), student.getId()).getGrade());
        }
        return result;
    }


}
