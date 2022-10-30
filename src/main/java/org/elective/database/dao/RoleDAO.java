package org.elective.database.dao;

import org.elective.database.DBException;
import org.elective.database.entity.Role;

import java.sql.Connection;


public interface RoleDAO {

    void create(Connection con, Role role) throws DBException;

    Role read(Connection con, int id) throws DBException;

    void update(Connection con, Role role) throws DBException;

    void delete(Connection con, int roleId) throws DBException;
}
