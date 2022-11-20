package org.elective.logic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elective.database.DBException;
import org.elective.database.DBUtils;
import org.elective.database.dao.DAOFactory;
import org.elective.database.dao.UserDAO;
import org.elective.database.entity.User;

import java.sql.Connection;
import java.util.List;

public class UserManager {

    private static final Logger logger = LogManager.getLogger(UserManager.class);

    private UserManager() {
        throw new IllegalStateException("Utility class");
    }

    public static User findUserByEmail(String email) throws DBException {
        try (Connection con = DBUtils.getInstance().getConnection()) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            UserDAO userDAO = daoFactory.getUserDAO();
            User user = userDAO.readByEmail(con, email);
            logger.debug("find user: {}", user);
            return user;
        } catch (Exception e) {
            logger.error(e);
            throw new DBException(e.getMessage(), e);
        }
    }

    public static User findUserById(int id) throws DBException {
        try (Connection con = DBUtils.getInstance().getConnection()) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            UserDAO userDAO = daoFactory.getUserDAO();
            User user = userDAO.read(con, id);
            logger.debug("find user: {}", user);
            return user;
        } catch (Exception e) {
            logger.error(e);
            throw new DBException(e.getMessage(), e);
        }
    }

    public static void insertUser(User user) throws DBException {
        try (Connection con = DBUtils.getInstance().getConnection()) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            UserDAO userDAO = daoFactory.getUserDAO();
            userDAO.create(con, user);
            logger.debug("User created successfully");
        } catch (Exception e) {
            logger.error(e);
            throw new DBException(e.getMessage(), e);
        }
    }

    public static List<User> findAllTeachers() throws DBException {
        try (Connection con = DBUtils.getInstance().getConnection()) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            UserDAO userDAO = daoFactory.getUserDAO();
            List<User> teachers = userDAO.getAllTeachers(con);
            logger.debug("find teachers: {}", teachers);
            return teachers;
        } catch (Exception e) {
            logger.error(e);
            throw new DBException(e.getMessage(), e);
        }
    }

    public static List<User> findAllStudents() throws DBException {
        try (Connection con = DBUtils.getInstance().getConnection()) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            UserDAO userDAO = daoFactory.getUserDAO();
            List<User> students = userDAO.getAllStudents(con);
            logger.debug("find students: {}", students);
            return students;
        } catch (Exception e) {
            logger.error(e);
            throw new DBException(e.getMessage(), e);
        }
    }

    public static void blockStudent(int studentId) throws DBException {
        try (Connection con = DBUtils.getInstance().getConnection()) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            UserDAO userDAO = daoFactory.getUserDAO();
            User student = userDAO.read(con, studentId);
            student.setBlocked(!student.isBlocked().equals("true"));
            userDAO.update(con, student);
            logger.debug("Student blocked");
        } catch (Exception e) {
            logger.error(e);
            throw new DBException(e.getMessage(), e);
        }
    }


}
