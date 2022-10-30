package org.elective.database.dao.mysql;

import org.elective.database.DBException;
import org.elective.database.dao.CourseDAO;
import org.elective.database.entity.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.elective.database.DBUtils.close;

public class MysqlCourseDAO implements CourseDAO {

    static final String CREATE_COURSE = "INSERT INTO course (id, teacher_id, name, topic, description, duration) VALUES (DEFAULT, ?, ?, ?, ?, ?)";
    static final String FIND_COURSE_BY_ID = "SELECT * FROM course c WHERE c.id=? ORDER BY c.name";
    static final String UPDATE_COURSE_BY_ID = "UPDATE course SET teacher_id=?, name=?, topic=?, description=?, duration=? WHERE id=?";
    static final String DELETE_COURSE_BY_ID = "DELETE FROM course WHERE id=?";
    static final String GET_ALL_COURSES = "SELECT * FROM course c ORDER BY c.name";
    static final String GET_ALL_TOPICS = "SELECT topic FROM course c ORDER BY c.topic";
    static final String FIND_COURSES_BY_TEACHER = "SELECT * FROM course c WHERE c.teacher_id=? ORDER BY c.name";
    static final String FIND_COURSES_BY_NAME = "SELECT * FROM course c WHERE c.name LIKE ? ORDER BY c.name";

    public void create(Connection con, Course course) throws DBException {
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(CREATE_COURSE, Statement.RETURN_GENERATED_KEYS);
            int k = 0;
            stmt.setInt(++k, course.getTeacherId());
            stmt.setString(++k, course.getName());
            stmt.setString(++k, course.getTopic());
            stmt.setString(++k, course.getDescription());
            stmt.setLong(++k, course.getDuration());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                course.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("ABigMistakeWhileAddingCourse", e);
        } finally {
            close(stmt);
        }
    }

    public Course read(Connection con, int id) throws DBException {
        Course course = null;
        try (PreparedStatement stmt = con.prepareStatement(FIND_COURSE_BY_ID)) {
            int k = 0;
            stmt.setInt(++k, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    course = mapCourse(rs);
                }
            }
        } catch (SQLException e) {
            throw new DBException("ABigMistakeWhileFindingCourse", e);
        }
        return course;
    }

    public void update(Connection con, Course course) throws DBException {
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(UPDATE_COURSE_BY_ID);
            int k = 0;
            stmt.setInt(++k, course.getTeacherId());
            stmt.setString(++k, course.getName());
            stmt.setString(++k, course.getTopic());
            stmt.setString(++k, course.getDescription());
            stmt.setLong(++k, course.getDuration());
            stmt.setInt(++k, course.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("ABigMistakeWhileUpdatingCourse", e);
        } finally {
            close(stmt);
        }
    }

    public void delete(Connection con, int courseId) throws DBException {
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(DELETE_COURSE_BY_ID);
            int k = 0;
            stmt.setInt(++k, courseId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("ABigMistakeWhileDeletingCourse", e);
        } finally {
            close(stmt);
        }
    }

    public List<Course> findByName(Connection con, String pattern) throws DBException {
        List<Course> courses = new ArrayList<>();
        try (PreparedStatement stmt = con.prepareStatement(FIND_COURSES_BY_NAME)) {
            stmt.setString(1, "%" + escapeForLike(pattern) + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Course c = mapCourse(rs);
                    courses.add(c);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("ABigMistakeWhileFindingCourses", e);
        }
        return courses;
    }

    private String escapeForLike(String param) {
        return param.replace("\\", "\\\\").replace("'", "''");
    }

    public List<Course> getAllCourses(Connection con) throws DBException {
        List<Course> courses = new ArrayList<>();
        try (PreparedStatement stmt = con.prepareStatement(GET_ALL_COURSES)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Course c = mapCourse(rs);
                    courses.add(c);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("ABigMistakeWhileGettingCourses", e);
        }
        return courses;
    }

    public List<String> getAllTopics(Connection con) throws DBException {
        List<String> topics = new ArrayList<>();
        try (PreparedStatement stmt = con.prepareStatement(GET_ALL_TOPICS)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String topic = rs.getString("topic");
                    if (!topics.contains(topic)) {
                        topics.add(topic);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("ABigMistakeWhileGettingTopics", e);
        }
        return topics;
    }

    public List<Course> findCoursesByTeacher(Connection con, int pattern) throws DBException {
        List<Course> courses = new ArrayList<>();
        try (PreparedStatement stmt = con.prepareStatement(FIND_COURSES_BY_TEACHER)) {
            stmt.setInt(1, pattern);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Course c = mapCourse(rs);
                    courses.add(c);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("ABigMistakeWhileFindingCourses", e);
        }
        return courses;
    }

    private Course mapCourse(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setId(rs.getInt("id"));
        c.setTeacherId(rs.getInt("teacher_id"));
        c.setName(rs.getString("name"));
        c.setTopic(rs.getString("topic"));
        c.setDescription(rs.getString("description"));
        c.setDuration(rs.getLong("duration"));
        return c;
    }
}
