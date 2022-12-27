package org.elective.logic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elective.database.DBException;
import org.elective.database.DBUtils;
import org.elective.database.dao.DAOFactory;
import org.elective.database.dao.StudentsSubtopicDAO;
import org.elective.database.dao.SubtopicDAO;
import org.elective.database.entity.Course;
import org.elective.database.entity.StudentsSubtopic;
import org.elective.database.entity.Subtopic;
import org.elective.database.entity.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.elective.database.entity.StudentsSubtopic.completion.COMPLETED;
import static org.elective.logic.SubtopicManager.findSubtopicsByCourse;

public class StudentsSubtopicManager {
    private static final Logger logger = LogManager.getLogger(StudentsSubtopicManager.class);

    private StudentsSubtopicManager() {
        throw new IllegalStateException("Utility class");
    }

    public static Map<Integer, String> getSubtopicCompletion(User student, int courseId) throws DBException {
        List<Subtopic> subtopics = SubtopicManager.findSubtopicsByCourse(courseId);
        Map<Integer, String> result = new HashMap<>();
        int i = 0;
        for (Subtopic subtopic : subtopics) {
            result.put(i, getCompletion(subtopic.getId(), student.getId()));
            i++;
        }
        return result;
    }

    private static String getCompletion(int subtopicId, int studentId) throws DBException {
        try {
            return findStudentsSubtopic(subtopicId, studentId).getCompletion();
        } catch (Exception e) {
            logger.error(e);
            throw new DBException(e.getMessage(), e);
        }
    }

    public static Map<Integer, String> changeCompletion(int key, User student, Map<Integer, Subtopic> pages, Course course) throws DBException {
        Map<Integer, String> subtopicCompletion = new HashMap<>();
        Subtopic subtopic = pages.get(key);
        try (Connection con = DBUtils.getInstance().getConnection()) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            StudentsSubtopicDAO studentsSubtopicDAO = daoFactory.getStudentsSubtopicDAO();
            StudentsSubtopic studentsSubtopic = studentsSubtopicDAO.read(con, subtopic.getId(), student.getId());

            changeCompletion(con, studentsSubtopicDAO, studentsSubtopic);

            SubtopicDAO subtopicDAO = daoFactory.getSubtopicDAO();
            List<Subtopic> subtopics = subtopicDAO.findSubtopicsByCourse(con, course.getId());

            updateCompletion(student, pages, subtopicCompletion, con, studentsSubtopicDAO, subtopics);
            return subtopicCompletion;
        } catch (Exception e) {
            logger.error(e);
            throw new DBException(e.getMessage(), e);
        }
    }

    private static void changeCompletion(Connection con, StudentsSubtopicDAO studentsSubtopicDAO, StudentsSubtopic studentsSubtopic) throws SQLException {
        if (!studentsSubtopic.getCompletion().equals(String.valueOf(COMPLETED))) {
            studentsSubtopic.setCompletion(String.valueOf(COMPLETED));
            studentsSubtopicDAO.update(con, studentsSubtopic);
        }
    }

    private static void updateCompletion(User student, Map<Integer, Subtopic> pages, Map<Integer, String> subtopicCompletion, Connection con, StudentsSubtopicDAO studentsSubtopicDAO, List<Subtopic> subtopics) throws DBException {
        int i = 0;
        for (Subtopic s : subtopics) {
            pages.put(i, s);
            subtopicCompletion.put(i, studentsSubtopicDAO.read(con, s.getId(), student.getId()).getCompletion());
            i++;
        }
    }

    public static List<StudentsSubtopic> getFinishedSubtopics(int studentId, int courseId) throws DBException {
        return findAllStudentsSubtopic(studentId, courseId).stream()
                .filter(s -> s.getCompletion().equals(COMPLETED.toString()))
                .collect(Collectors.toList());
    }

    public static Map<Integer, Integer> getFinishedSubtopicsNum(List<Course> courses, User student) throws DBException {
        Map<Integer, Integer> result = new HashMap<>();
        for (Course course : courses) {
            result.put(course.getId(), getFinishedSubtopics(student.getId(), course.getId()).size());
        }
        return result;
    }

    public static StudentsSubtopic findStudentsSubtopic(int subtopicId, int studentId) throws DBException {
        try (Connection con = DBUtils.getInstance().getConnection()) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            StudentsSubtopicDAO studentsSubtopicDAO = daoFactory.getStudentsSubtopicDAO();
            StudentsSubtopic subtopic = studentsSubtopicDAO.read(con, subtopicId, studentId);
            logger.debug("subtopic properties: {}", subtopic);
            return subtopic;
        } catch (Exception e) {
            logger.error(e);
            throw new DBException(e.getMessage(), e);
        }
    }

    public static List<StudentsSubtopic> findAllStudentsSubtopic(int studentId, int courseId) throws DBException {
        List<StudentsSubtopic> studentsSubtopics = new ArrayList<>();
        try {
            List<Subtopic> subtopics = findSubtopicsByCourse(courseId);
            for (Subtopic subtopic : subtopics) {
                studentsSubtopics.add(findStudentsSubtopic(subtopic.getId(), studentId));
            }
        } catch (DBException e) {
            logger.error(e);
            throw new DBException(e.getMessage(), e);
        }
        return studentsSubtopics;
    }
}
