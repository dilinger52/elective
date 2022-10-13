package org.elective.servlets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elective.DBManager.DBException;
import org.elective.DBManager.DBUtils;
import org.elective.DBManager.dao.*;
import org.elective.DBManager.entity.*;

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

import static org.elective.DBManager.entity.StudentsSubtopic.completion.COMPLETED;

@WebServlet("/filter")
public class FilterServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(FilterServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("Starting filtering...");
        String[] topics = req.getParameterValues("topic");
        String[] teachers = req.getParameterValues("teacher");
        String[] completions = req.getParameterValues("completion");
        String sortingPattern = req.getParameter("sorting_pattern");
        HttpSession session = req.getSession();
        String path = (String) session.getAttribute("path");
        User user = (User) session.getAttribute("user");
        List<Course> courses = new ArrayList<>();
        try(Connection con = DBUtils.getInstance().getConnection();) {
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


    private static List<Course> filterTopic(String[] topics, List<Course> courses) {
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

    private static List<Course> filterTeachers(String[] teachers, List<Course> courses) {
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

    private static List<Course> filterCompletions(String[] completions, List<Course> courses, User user) throws DBException {
        if (completions != null) {
            try (Connection con = DBUtils.getInstance().getConnection();) {
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
                    if (finishedSubtopics.size() == 0 && completion.equals("0")) {
                        result.add(course);
                    } else if (finishedSubtopics.size() == studentsSubtopics.size() && studentsSubtopics.size() > 0 && completion.equals("2")) {
                        result.add(course);
                    } else if (finishedSubtopics.size() < studentsSubtopics.size() && completion.equals("1")) {
                        result.add(course);
                    }
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
}
