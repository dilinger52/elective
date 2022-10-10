package org.elective.DBManager.dao.mysql;

import org.elective.DBManager.DBException;
import org.elective.DBManager.dao.StudentsCourseDAO;
import org.elective.DBManager.entity.StudentsCourse;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MysqlStudentsCourseDAO implements StudentsCourseDAO {

    private static final String ADD_COURSE_TO_STUDENT = "INSERT INTO students_courses (student_id, course_id, registration_data) VALUES (?, ?, ?)";
    private static final String FIND_COURSE_BY_COURSE_ID_AND_STUDENT_ID = "SELECT * FROM students_courses sc WHERE sc.course_id=? AND sc.student_id=? ORDER BY sc.student_id";
    private static final String UPDATE_USERS_COURSE_BY_STUDENT_ID_AND_COURSE_ID = "UPDATE students_courses SET registration_data=?, grade=? WHERE student_id=? AND course_id=?";
    private static final String DELETE_USERS_COURSE_BY_STUDENT_ID_AND_COURSE_ID = "DELETE FROM students_courses WHERE student_id=? AND course_id=?";
    private static final String FIND_COURSES_BY_STUDENT = "SELECT * FROM students_courses sc WHERE sc.student_id=? ORDER BY sc.registration_data";
    private static final String FIND_COURSES_BY_COURSE_ID = "SELECT * FROM students_courses sc WHERE sc.course_id=?";


    public void create(Connection con, StudentsCourse course) throws DBException {
        try (PreparedStatement stmt = con.prepareStatement(ADD_COURSE_TO_STUDENT);) {
            int k = 0;
            stmt.setInt(++k, course.getStudentId());
            stmt.setInt(++k, course.getCourseId());
            stmt.setDate(++k, course.getRegistrationDate());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("A big mistake while finding users course", e);
        }
    }


    public StudentsCourse read(Connection con, int courseId, int studentId) throws DBException {
        StudentsCourse course = null;
        try (PreparedStatement stmt = con.prepareStatement(FIND_COURSE_BY_COURSE_ID_AND_STUDENT_ID)) {
            int k = 0;
            stmt.setInt(++k, courseId);
            stmt.setInt(++k, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    course = mapStudentsCourse(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("A big mistake while finding users course", e);
        }
        return course;
    }

    public void update(Connection con, StudentsCourse course) throws DBException {
        try (PreparedStatement stmt = con.prepareStatement(UPDATE_USERS_COURSE_BY_STUDENT_ID_AND_COURSE_ID);) {
            java.util.Date date = course.getRegistrationDate();
            java.sql.Date dateSql = new Date(date.getYear(), date.getMonth(), date.getDay());
            int k = 0;
            stmt.setDate(++k, dateSql);
            stmt.setLong(++k, course.getGrade());
            stmt.setInt(++k, course.getStudentId());
            stmt.setInt(++k, course.getCourseId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("A big mistake while finding users course", e);
        }
    }

    @Override
    public void delete(Connection con, StudentsCourse course) throws DBException {
        try(PreparedStatement stmt = con.prepareStatement(DELETE_USERS_COURSE_BY_STUDENT_ID_AND_COURSE_ID);) {
            int k = 0;
            stmt.setInt(++k, course.getStudentId());
            stmt.setInt(++k, course.getCourseId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("A big mistake while finding users course", e);
        }
    }

    public List<StudentsCourse> findCoursesByStudentId(Connection con, int studentId) throws DBException {
        List<StudentsCourse> courses = new ArrayList<>();
        try(PreparedStatement stmt = con.prepareStatement(FIND_COURSES_BY_STUDENT)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    StudentsCourse c = mapStudentsCourse(rs);
                    courses.add(c);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("A big mistake while finding courses", e);
        }
        return courses;
    }

    public List<StudentsCourse> findCoursesByCourseId(Connection con, int courseId) throws DBException {
        List<StudentsCourse> courses = new ArrayList<>();
        try(PreparedStatement stmt = con.prepareStatement(FIND_COURSES_BY_COURSE_ID)) {
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    StudentsCourse c = mapStudentsCourse(rs);
                    courses.add(c);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("A big mistake while finding courses", e);
        }
        return courses;
    }

    private StudentsCourse mapStudentsCourse(ResultSet rs) throws SQLException {
        StudentsCourse sc = new StudentsCourse();
        sc.setStudentId(rs.getInt("student_id"));
        sc.setCourseId(rs.getInt("course_id"));
        sc.setRegistrationDate(rs.getDate("registration_data"));
        sc.setGrade(rs.getLong("grade"));
        return sc;
    }
}
