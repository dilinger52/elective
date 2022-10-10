package org.elective.DBManager.dao;

import org.elective.DBManager.DBException;
import org.elective.DBManager.entity.StudentsSubtopic;

import java.sql.Connection;
import java.sql.SQLException;

public interface StudentsSubtopicDAO {

    void create(Connection con, StudentsSubtopic subtopic) throws DBException, SQLException;

    StudentsSubtopic read(Connection con, int subtopicId, int studentId) throws DBException;

    void update(Connection con, StudentsSubtopic subtopic) throws DBException, SQLException;

    void delete (Connection con, StudentsSubtopic subtopic) throws DBException, SQLException;
}
