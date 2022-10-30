package org.elective.database.dao;

import org.elective.database.DBException;
import org.elective.database.entity.StudentsSubtopic;

import java.sql.Connection;
import java.sql.SQLException;

public interface StudentsSubtopicDAO {

    void create(Connection con, StudentsSubtopic subtopic) throws SQLException;

    StudentsSubtopic read(Connection con, int subtopicId, int studentId) throws DBException;

    void update(Connection con, StudentsSubtopic subtopic) throws SQLException;

    void delete(Connection con, StudentsSubtopic subtopic) throws SQLException;
}
