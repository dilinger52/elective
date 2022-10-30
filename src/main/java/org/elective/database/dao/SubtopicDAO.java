package org.elective.database.dao;

import org.elective.database.DBException;
import org.elective.database.entity.Subtopic;

import java.sql.Connection;
import java.util.List;

public interface SubtopicDAO {
    void create(Connection con, Subtopic subtopic) throws DBException;

    Subtopic read(Connection con, int id) throws DBException;

    void update(Connection con, Subtopic subtopic) throws DBException;

    void delete(Connection con, int id) throws DBException;

    List<Subtopic> findSubtopicsByCourse(Connection con, int courseId) throws DBException;
}
