package org.elective.DBManager.dao.mysql;

import org.elective.DBManager.DBException;
import org.elective.DBManager.DBUtils;
import org.elective.DBManager.dao.StudentsSubtopicDAO;
import org.elective.DBManager.entity.StudentsSubtopic;

import java.sql.*;
import java.util.List;


public class MysqlStudentsSubtopicDAO implements StudentsSubtopicDAO {
    private static final String CREATE_SUBTOPIC = "INSERT INTO students_subtopic (subtopic_id, student_id, completion) VALUES (?, ?, 'uncompleted')";
    private static final String FIND_SUBTOPIC_BY_ID = "SELECT * FROM students_subtopic s WHERE s.subtopic_id=? AND s.student_id=? ORDER BY s.subtopic_id";
    private static final String UPDATE_SUBTOPIC_BY_ID = "UPDATE students_subtopic s SET s.completion=? WHERE s.subtopic_id=? AND s.student_id=?";
    private static final String DELETE_SUBTOPIC_BY_ID = "DELETE FROM students_subtopic WHERE subtopic_id=?";
    private static final String DELETE_SUBTOPIC_BY_STUDENT_ID = "DELETE FROM students_subtopic WHERE student_id=?";

    public void create(Connection con, StudentsSubtopic subtopic) throws DBException {
        try (PreparedStatement stmt = con.prepareStatement(CREATE_SUBTOPIC)) {
            int k = 0;
            stmt.setInt(++k, subtopic.getSubtopicId());
            stmt.setInt(++k, subtopic.getStudentId());
            stmt.executeUpdate();
            subtopic.setCompletion("uncompleted");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("ABigMistakeAddingSubtopic", e);
        }

    }

    public StudentsSubtopic read(Connection con, int subtopicId, int studentId) throws DBException {
        StudentsSubtopic subtopic = null;
        try (PreparedStatement stmt = con.prepareStatement(FIND_SUBTOPIC_BY_ID)) {
            int k = 0;
            stmt.setInt(++k, subtopicId);
            stmt.setInt(++k, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    subtopic = mapSubtopic(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("ABigMistakeWhileFindingSubtopic", e);
        }
        return subtopic;
    }

    public void update(Connection con, StudentsSubtopic subtopic) throws DBException {
        try (PreparedStatement stmt = con.prepareStatement(UPDATE_SUBTOPIC_BY_ID);) {
            int k = 0;
            stmt.setString(++k, subtopic.getCompletion());
            stmt.setInt(++k, subtopic.getSubtopicId());
            stmt.setInt(++k, subtopic.getStudentId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("ABigMistakeWhileUpdatingSubtopic", e);
        }
    }

    @Override
    public void delete(Connection con, StudentsSubtopic subtopic) throws DBException{
        try (PreparedStatement stmt = con.prepareStatement(DELETE_SUBTOPIC_BY_ID);) {
            int k = 0;
            stmt.setInt(++k, subtopic.getSubtopicId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("ABigMistakeWhileDeletingSubtopic", e);
        }
    }


    private StudentsSubtopic mapSubtopic(ResultSet rs) throws SQLException {
        StudentsSubtopic s = new StudentsSubtopic();
        s.setStudentId(rs.getInt("student_id"));
        s.setSubtopicId(rs.getInt("subtopic_id"));
        s.setCompletion(rs.getString("completion"));
        return s;
    }
}
