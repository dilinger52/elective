package org.elective.servlets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elective.database.DBException;
import org.elective.database.DBUtils;
import org.elective.database.dao.*;
import org.elective.database.entity.*;
import org.elective.utils.Pagination;
import org.elective.utils.Sorting;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.elective.database.entity.StudentsSubtopic.completion.COMPLETED;

/**
 * Filter servlet realized filtering courses in different pages.
 */
@WebServlet("/filter")
public class FilterServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(FilterServlet.class);

    /**
     * Filter topic filters list of courses that will be displayed by specified topics.
     *
     * @param topics  array of topics for which filtering courses
     * @param courses list of courses to filter
     * @return the list of courses after filtering
     */
    static List<Course> filterTopic(String[] topics, List<Course> courses) {
        if (topics != null) {
            List<Course> result = new ArrayList<>();
            for (String topic : topics) {
                courses.stream()
                        .filter(c -> c.getTopic().equals(topic))
                        .forEach(result::add);
            }
            return result;
        }
        return courses;
    }

    /**
     * Filter teacher filters list of courses that will be displayed by specified teachers.
     *
     * @param teachers  array of teachers for which filtering courses
     * @param courses list of courses to filter
     * @return the list of courses after filtering
     */
    static List<Course> filterTeachers(String[] teachers, List<Course> courses) {
        if (teachers != null) {
            List<Course> result = new ArrayList<>();
            for (String teacher : teachers) {
                courses.stream()
                        .filter(c -> c.getTeacherId() == Integer.parseInt(teacher))
                        .forEach(result::add);
            }
            return result;
        }
        return courses;
    }

    /**
     * Filter completions filters list of courses that will be displayed by specified completion.
     *
     * @param completions the completions of course by current student
     * @param courses     the courses of current student
     * @param user        current student
     * @return the list of courses after filtering
     * @throws DBException custom exception that signals database errors
     */
    static List<Course> filterCompletions(String[] completions, List<Course> courses, User user) throws DBException {
        if (completions != null) {
            try (Connection con = DBUtils.getInstance().getConnection()) {
                DAOFactory daoFactory = DAOFactory.getInstance();
                SubtopicDAO subtopicDAO = daoFactory.getSubtopicDAO();
                StudentsSubtopicDAO studentsSubtopicDAO = daoFactory.getStudentsSubtopicDAO();
                List<Course> result = new ArrayList<>();
                for (Course course : courses) {
                    List<Subtopic> subtopics = subtopicDAO.findSubtopicsByCourse(con, course.getId());
                    List<StudentsSubtopic> studentsSubtopics = new ArrayList<>();

                    for (Subtopic subtopic : subtopics) {
                        studentsSubtopics.add(studentsSubtopicDAO.read(con, subtopic.getId(), user.getId()));
                    }
                    List<StudentsSubtopic> finishedSubtopics = studentsSubtopics.stream()
                            .filter(s -> s.getCompletion().equals(COMPLETED.toString()))
                            .collect(Collectors.toList());
                    for (String completion : completions) {
                        checkCompletion(result, course, studentsSubtopics, finishedSubtopics, completion);
                    }

                }
                return result;
            } catch (Exception e) {
                logger.error(e);
                throw new DBException("CannotFilterByCompletions", e);
            }
        }
        return courses;

    }

    private static void checkCompletion(List<Course> result, Course course, List<StudentsSubtopic> studentsSubtopics, List<StudentsSubtopic> finishedSubtopics, String completion) {
        if (completion.equals("0") && finishedSubtopics.isEmpty()) {
            result.add(course);
        }
        if (completion.equals("2") && finishedSubtopics.size() == studentsSubtopics.size() && !studentsSubtopics.isEmpty()) {
            result.add(course);
        }
        if (completion.equals("1") && finishedSubtopics.size() < studentsSubtopics.size()) {
            result.add(course);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("Starting filtering...");
        String[] topics = req.getParameterValues("topic");
        String[] teachers = req.getParameterValues("teacher");
        String[] completions = req.getParameterValues("completion");
        String sortingPattern = req.getParameter("sorting_pattern");

        if (sortingPattern == null) {
            sortingPattern = "0";
        }
        HttpSession session = req.getSession();
        String path = (String) session.getAttribute("path");
        User user = (User) session.getAttribute("user");
        List<Course> courses = new ArrayList<>();
        try (Connection con = DBUtils.getInstance().getConnection()) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            CourseDAO courseDAO = daoFactory.getCourseDAO();
            StudentsCourseDAO studentsCourseDAO = daoFactory.getStudentsCourseDAO();
            if ("personalCourses.jsp".equals(path)) {
                List<StudentsCourse> studentsCourses = studentsCourseDAO.findCoursesByStudentId(con, user.getId());
                for (StudentsCourse c :
                        studentsCourses) {
                    Course course = courseDAO.read(con, c.getCourseId());
                    courses.add(course);
                }

                session.setAttribute("studentsCourses", studentsCourses);

            } else if (user.getRoleId() == 2) {
                courses = courseDAO.findCoursesByTeacher(con, user.getId());
            } else {
                courses = courseDAO.getAllCourses(con);
                if (user.getRoleId() == 3) {
                    List<StudentsCourse> studentsCourses = studentsCourseDAO.findCoursesByStudentId(con, user.getId());
                    List<Integer> sc = new ArrayList<>();
                    studentsCourses.forEach(c -> sc.add(c.getCourseId()));
                    courses = courses.stream().filter(c -> !(sc.contains(c.getId()))).collect(Collectors.toList());
                }
            }
            logger.debug("courses before filter: {}", courses);
        } catch (Exception e) {
            logger.error(e);
            req.setAttribute("message", e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
            return;
        }
        courses = filterTopic(topics, courses);
        courses = filterTeachers(teachers, courses);
        try {
            courses = filterCompletions(completions, courses, user);
        } catch (DBException e) {
            logger.error(e);
            req.setAttribute("message", e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
            return;
        }
        courses = Sorting.sort(session, courses, Integer.parseInt(sortingPattern));
        logger.debug("courses after filter: {}", courses);
        Map<Integer, List<Course>> pages = Pagination.pageConstructor(courses);
        session.setAttribute("pages", pages);
        session.setAttribute("pageKey", 0);
        RequestDispatcher rd = req.getRequestDispatcher(path);
        session.setAttribute("path", path);
        rd.forward(req, resp);

    }
}
