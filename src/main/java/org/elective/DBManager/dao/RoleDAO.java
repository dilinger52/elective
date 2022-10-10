package org.elective.DBManager.dao;

import org.elective.DBManager.DBException;
import org.elective.DBManager.entity.Role;

import java.sql.Connection;


public interface RoleDAO {

    void create(Connection con, Role role) throws DBException;

    Role read (Connection con, int id) throws DBException;

    void update (Connection con, Role role) throws DBException;

    void delete (Connection con, Role role) throws DBException;
}
