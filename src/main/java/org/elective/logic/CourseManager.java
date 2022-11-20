package org.elective.logic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elective.database.DBException;
import org.elective.database.DBUtils;
import org.elective.database.dao.CourseDAO;
import org.elective.database.dao.DAOFactory;
import org.elective.database.entity.Course;
import org.elective.database.entity.StudentsCourse;
import org.elective.database.entity.User;

import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;

import static org.elective.logic.StudentsCourseManager.findCoursesByCourseId;
import static org.elective.logic.UserManager.findUserById;

public class CourseManager {
    private static final Logger logger = LogManager.getLogger(CourseManager.class);
    private static final String COURSE_MESSAGE = "course: {}";

    private CourseManager() {
        throw new IllegalStateException("Utility class");
    }

    public static Course findCourseById(int courseId) throws DBException {
        try(Connection con = DBUtils.getInstance().getConnection()) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            CourseDAO courseDAO = daoFactory.getCourseDAO();
            Course course = courseDAO.read(con, courseId);
            logger.debug("course properties: {}", course);
            return course;
        } catch (Exception e) {
            logger.error(e);
            throw new DBException(e.getMessage(), e);
        }
    }

    public static List<Course> findCoursesByName(String pattern) throws DBException {
        try(Connection con = DBUtils.getInstance().getConnection()) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            CourseDAO courseDAO = daoFactory.getCourseDAO();
            List<Course> course = courseDAO.findByName(con, pattern);
            logger.debug("courses properties: {}", course);
            return course;
        } catch (Exception e) {
            logger.error(e);
            throw new DBException(e.getMessage(), e);
        }
    }

    public static List<Course> findAllCourses() throws DBException {
        try(Connection con = DBUtils.getInstance().getConnection()) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            CourseDAO courseDAO = daoFactory.getCourseDAO();
            List<Course> courses = courseDAO.getAllCourses(con);
            logger.debug("courses properties: {}", courses);
            return courses;
        } catch (Exception e) {
            logger.error(e);
            throw new DBException(e.getMessage(), e);
        }
    }

    public static List<Course> getAvailableCourses(User user, List<Course> courses) throws DBException {
        List<Course> coursesByStudent = findCoursesByStudent(user.getId());
        List<Integer> sc = new ArrayList<>();
        coursesByStudent.forEach(c -> sc.add(c.getId()));
        return courses.stream().filter(c -> !(sc.contains(c.getId()))).collect(Collectors.toList());
    }

    public static List<Course> findCoursesByStudent(int id) throws DBException {
        List<Course> courses = new ArrayList<>();
        List<StudentsCourse> studentsCourses = StudentsCourseManager.findCoursesByStudent(id);
        for (StudentsCourse c : studentsCourses) {
            Course course = findCourseById(c.getCourseId());
            courses.add(course);
        }
        logger.debug("courses properties: {}", courses);
        return courses;
    }

    public static List<Course> findCoursesByTeacher (int id) throws DBException {
        try(Connection con = DBUtils.getInstance().getConnection()) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            CourseDAO courseDAO = daoFactory.getCourseDAO();
            List<Course> courses = courseDAO.findCoursesByTeacher(con, id);
            logger.debug("courses properties: {}", courses);
            return courses;
        } catch (Exception e) {
            logger.error(e);
            throw new DBException(e.getMessage(), e);
        }
    }

    public static List<String> findAllTopics() throws DBException {
        try(Connection con = DBUtils.getInstance().getConnection()) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            CourseDAO courseDAO = daoFactory.getCourseDAO();
            List<String> topics = courseDAO.getAllTopics(con);
            logger.debug("find topics: {}", topics);
            return topics;
        } catch (Exception e) {
            logger.error(e);
            throw new DBException(e.getMessage(), e);
        }
    }

    public static Map<Integer, String> getCoursesTeacher(List<Course> courses) throws DBException {
        Map<Integer, String> coursesTeacher = new HashMap<>();
        for (Course course : courses) {
            User teacher = findUserById(course.getTeacherId());
            coursesTeacher.put(course.getId(), teacher.getFirstName() + " " +
                    teacher.getLastName());
        }
        return coursesTeacher;
    }

    public static Map<Integer, Integer> getCoursesStudents(List<Course> courses) throws DBException {
        Map<Integer, Integer> coursesStudents = new HashMap<>();
        for (Course course : courses) {
            coursesStudents.put(course.getId(), findCoursesByCourseId(course.getId()).size());
        }
        return coursesStudents;
    }



    public static void updateCourse(int id, String name, String topic, String description, int teacherId, long duration) throws DBException {
        try (Connection con = DBUtils.getInstance().getConnection()) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            CourseDAO courseDAO = daoFactory.getCourseDAO();
            Course course = courseDAO.read(con, id);
            course.setName(name);
            course.setTopic(topic);
            course.setDescription(description);
            course.setTeacherId(teacherId);
            course.setDuration(duration);
            courseDAO.update(con, course);
            logger.debug(COURSE_MESSAGE, course);
        } catch (Exception e) {
            logger.error(e);
            throw new DBException(e.getMessage(), e);
        }
    }
    public static void insertCourse(Course course) throws DBException {
        try (Connection con = DBUtils.getInstance().getConnection()) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            CourseDAO courseDAO = daoFactory.getCourseDAO();
            courseDAO.create(con, course);
            logger.debug(COURSE_MESSAGE, course);
        } catch (Exception e) {
            logger.error(e);
            throw new DBException(e.getMessage(), e);
        }
    }

    public static void deleteCourse(int id) throws DBException {
        try (Connection con = DBUtils.getInstance().getConnection()) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            CourseDAO courseDAO = daoFactory.getCourseDAO();
            courseDAO.delete(con, id);
            logger.debug("Successfully deleting");
        } catch (Exception e) {
            logger.error(e);
            throw new DBException(e.getMessage(), e);
        }
    }
}
