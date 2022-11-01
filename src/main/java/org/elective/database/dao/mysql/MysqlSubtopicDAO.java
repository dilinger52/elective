package org.elective.database.dao.mysql;

import org.elective.database.DBException;
import org.elective.database.dao.SubtopicDAO;
import org.elective.database.entity.Subtopic;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.elective.database.DBUtils.close;

public class MysqlSubtopicDAO implements SubtopicDAO {


    static final String CREATE_SUBTOPIC = "INSERT INTO course_subtopics (subtopic_id, course_id, subtopic_name, subtopic_content) VALUES (DEFAULT, ?, ?, ?)";
    static final String FIND_SUBTOPIC_BY_ID = "SELECT * FROM course_subtopics s WHERE s.subtopic_id=? ORDER BY s.subtopic_name";
    static final String UPDATE_SUBTOPIC_BY_ID = "UPDATE course_subtopics SET course_id=?, subtopic_name=?, subtopic_content=? WHERE subtopic_id=?";
    static final String DELETE_SUBTOPIC_BY_ID = "DELETE FROM course_subtopics WHERE subtopic_id=?";
    static final String FIND_SUBTOPICS_BY_COURSE_ID = "SELECT * FROM course_subtopics s WHERE s.course_id=? ORDER BY s.subtopic_id";

    private static Subtopic mapSubtopic(ResultSet rs) throws SQLException {
        Subtopic s = new Subtopic();
        s.setId(rs.getInt("subtopic_id"));
        s.setCourseId(rs.getInt("course_id"));
        s.setSubtopicName(rs.getString("subtopic_name"));
        s.setSubtopicContent(rs.getString("subtopic_content"));
        return s;
    }

    public void create(Connection con, Subtopic subtopic) throws DBException {
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(CREATE_SUBTOPIC, Statement.RETURN_GENERATED_KEYS);
            int k = 0;
            stmt.setInt(++k, subtopic.getCourseId());
            stmt.setString(++k, subtopic.getSubtopicName());
            stmt.setString(++k, subtopic.getSubtopicContent());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                subtopic.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new DBException("ABigMistakeAddingSubtopic", e);
        } finally {
            close(stmt);
        }
    }

    public Subtopic read(Connection con, int id) throws DBException {
        Subtopic subtopic = null;
        try (PreparedStatement stmt = con.prepareStatement(FIND_SUBTOPIC_BY_ID)) {
            int k = 0;
            stmt.setInt(++k, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    subtopic = mapSubtopic(rs);
                }
            }
        } catch (SQLException e) {
            throw new DBException("ABigMistakeWhileFindingSubtopic", e);
        }
        return subtopic;
    }

    public void update(Connection con, Subtopic subtopic) throws DBException {
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(UPDATE_SUBTOPIC_BY_ID);
            int k = 0;
            stmt.setInt(++k, subtopic.getCourseId());
            stmt.setString(++k, subtopic.getSubtopicName());
            stmt.setString(++k, subtopic.getSubtopicContent());
            stmt.setInt(++k, subtopic.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("ABigMistakeWhileUpdatingSubtopic", e);
        } finally {
            close(stmt);
        }
    }

    public void delete(Connection con, int id) throws DBException {
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(DELETE_SUBTOPIC_BY_ID);
            int k = 0;
            stmt.setInt(++k, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("ABigMistakeWhileDeletingSubtopic", e);
        } finally {
            close(stmt);
        }
    }

    public List<Subtopic> findSubtopicsByCourse(Connection con, int courseId) throws DBException {
        List<Subtopic> subtopics = new ArrayList<>();
        try (PreparedStatement stmt = con.prepareStatement(FIND_SUBTOPICS_BY_COURSE_ID)) {
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Subtopic s = mapSubtopic(rs);
                    subtopics.add(s);
                }
            }
        } catch (SQLException e) {
            throw new DBException("ABigMistakeWhileFindingCourses", e);
        }
        return subtopics;
    }


}
