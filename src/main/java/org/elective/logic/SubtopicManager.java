package org.elective.logic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elective.database.DBException;
import org.elective.database.DBUtils;
import org.elective.database.dao.DAOFactory;
import org.elective.database.dao.StudentsCourseDAO;
import org.elective.database.dao.StudentsSubtopicDAO;
import org.elective.database.dao.SubtopicDAO;
import org.elective.database.entity.Course;
import org.elective.database.entity.StudentsCourse;
import org.elective.database.entity.StudentsSubtopic;
import org.elective.database.entity.Subtopic;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.elective.database.DBUtils.close;
import static org.elective.database.DBUtils.rollback;

public class SubtopicManager {
    private static final Logger logger = LogManager.getLogger(SubtopicManager.class);

    private SubtopicManager() {
        throw new IllegalStateException("Utility class");
    }

    public static void addSubtopicToCourse(int courseId) throws DBException {
        Subtopic subtopic = new Subtopic(courseId, "Default name");
        Connection con = null;
        try {
            con = DBUtils.getInstance().getConnection();
            con.setAutoCommit(false);
            DAOFactory daoFactory = DAOFactory.getInstance();
            SubtopicDAO subtopicDAO = daoFactory.getSubtopicDAO();
            StudentsCourseDAO studentsCourseDAO = daoFactory.getStudentsCourseDAO();
            StudentsSubtopicDAO studentsSubtopicDAO = daoFactory.getStudentsSubtopicDAO();
            subtopicDAO.create(con, subtopic);
            List<StudentsCourse> courses = studentsCourseDAO.findCoursesByCourseId(con, courseId);
            for (StudentsCourse studentsCourse : courses) {
                StudentsSubtopic studentsSubtopic = new StudentsSubtopic(studentsCourse.getStudentId(), subtopic.getId());
                studentsSubtopicDAO.create(con, studentsSubtopic);
            }
            con.commit();
            logger.debug("Created subtopic: {}; for course id: {}", subtopic, courseId);
        } catch (Exception e) {
            rollback(con);
            logger.error(e);
            throw new DBException(e.getMessage(), e);
        } finally {
            close(con);
        }
    }

    public static List<Subtopic> findSubtopicsByCourse(int courseId) throws DBException {
        try (Connection con = DBUtils.getInstance().getConnection()) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            SubtopicDAO subtopicDAO = daoFactory.getSubtopicDAO();
            List<Subtopic> subtopics = subtopicDAO.findSubtopicsByCourse(con, courseId);
            logger.debug("subtopics properties: {}", subtopics);
            return subtopics;
        } catch (Exception e) {
            logger.error(e);
            throw new DBException(e.getMessage(), e);
        }
    }

    public static Map<Integer, Integer> getStudentsSubtopicsNum(List<Course> courses) throws DBException {
        Map<Integer, Integer> result = new HashMap<>();
        int i = 0;
        for (Course course : courses) {
            result.put(i++, findSubtopicsByCourse(course.getId()).size());
        }
        return result;
    }


    public static void updateSubtopic(int id, String name, String content) throws DBException {
        try (Connection con = DBUtils.getInstance().getConnection()) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            SubtopicDAO subtopicDAO = daoFactory.getSubtopicDAO();
            Subtopic subtopic = subtopicDAO.read(con, id);
            subtopic.setSubtopicName(name);
            subtopic.setSubtopicContent(content);
            subtopicDAO.update(con, subtopic);
            logger.debug("subtopic properties: {}", subtopic);
        } catch (Exception e) {
            logger.error(e);
            throw new DBException(e.getMessage(), e);
        }
    }

    public static void deleteSubtopic(int id) throws DBException {
        try (Connection con = DBUtils.getInstance().getConnection()) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            SubtopicDAO subtopicDAO = daoFactory.getSubtopicDAO();
            subtopicDAO.delete(con, id);
            logger.debug("Successfully deleting");
        } catch (Exception e) {
            logger.error(e);
            throw new DBException(e.getMessage(), e);
        }
    }


}
