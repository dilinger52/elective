package org.elective.DBManager.dao;

import org.elective.DBManager.DBException;
import org.elective.DBManager.entity.User;

import java.sql.Connection;
import java.util.List;

public interface UserDAO {

    void create(Connection con, User user) throws DBException;

    User read(Connection con, String name) throws DBException;

    User read(Connection con, int id) throws DBException;

    User readByEmail(Connection con, String email) throws DBException;

    void update(Connection con, User user) throws DBException;

    void delete (Connection con, User user) throws DBException;

    List<User> getAllStudents(Connection con) throws DBException;

    List<User> getAllTeachers(Connection con) throws DBException;
}
