package org.elective.DBManager.dao.mysql;

import org.elective.DBManager.dao.*;

public class MysqlDAOFactory extends DAOFactory {

    private RoleDAO roleDAO;
    private UserDAO userDAO;
    private CourseDAO courseDAO;
    private SubtopicDAO subtopicDAO;
    private StudentsSubtopicDAO studentsSubtopicDAO;
    private StudentsCourseDAO studentsCourseDAO;

    @Override
    public RoleDAO getRoleDAO() {
        if (roleDAO == null) {
            roleDAO = new MysqlRoleDAO();
        }
        return roleDAO;
    }

    @Override
    public UserDAO getUserDAO() {
        if (userDAO == null) {
            userDAO = new MysqlUserDAO();
        }
        return userDAO;
    }

    @Override
    public CourseDAO getCourseDAO() {
        if (courseDAO == null) {
            courseDAO = new MysqlCourseDAO();
        }
        return courseDAO;
    }

    @Override
    public SubtopicDAO getSubtopicDAO() {
        if (subtopicDAO == null) {
            subtopicDAO = new MysqlSubtopicDAO();
        }
        return subtopicDAO;
    }

    public StudentsSubtopicDAO getStudentsSubtopicDAO() {
        if (studentsSubtopicDAO == null) {
            studentsSubtopicDAO = new MysqlStudentsSubtopicDAO();
        }
        return studentsSubtopicDAO;
    }

    public StudentsCourseDAO getStudentsCourseDAO() {
        if (studentsCourseDAO == null) {
            studentsCourseDAO = new MysqlStudentsCourseDAO();
        }
        return studentsCourseDAO;
    }
}
