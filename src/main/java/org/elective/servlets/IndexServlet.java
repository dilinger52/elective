package org.elective.servlets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elective.DBManager.DBException;
import org.elective.DBManager.DBUtils;
import org.elective.DBManager.dao.CourseDAO;
import org.elective.DBManager.dao.DAOFactory;
import org.elective.DBManager.dao.StudentsCourseDAO;
import org.elective.DBManager.dao.UserDAO;
import org.elective.DBManager.entity.Course;
import org.elective.DBManager.entity.StudentsCourse;
import org.elective.DBManager.entity.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/index.jsp")
public class IndexServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(IndexServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("Loading catalog...");
        List<String> topics;
        List<User> teachers;
        List<Course> courses;
        Map<Integer, Integer> coursesStudents = new HashMap<>();
        Map<Integer, String> coursesTeacher = new HashMap<>();
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        try(Connection con = DBUtils.getInstance().getConnection();) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            CourseDAO courseDAO = daoFactory.getCourseDAO();
            UserDAO userDAO = daoFactory.getUserDAO();
            StudentsCourseDAO studentsCourseDAO = daoFactory.getStudentsCourseDAO();
            topics = courseDAO.getAllTopics(con);
            teachers = userDAO.getAllTeachers(con);
            if (user.getRoleId() == 2) {
                courses = courseDAO.findCoursesByTeacher(con, user.getId());
            } else {
                courses = courseDAO.getAllCourses(con);
            }
            if (user.getRoleId() == 3) {
                List<StudentsCourse> studentsCourses = studentsCourseDAO.findCoursesByStudentId(con, user.getId());
                List<Integer> sc = new ArrayList<>();
                studentsCourses.forEach(c -> sc.add(c.getCourseId()));
                courses = courses.stream().filter(c -> !(sc.contains(c.getId()))).collect(Collectors.toList());
            }
            for (Course course : courses) {
                coursesStudents.put(course.getId(), studentsCourseDAO.findCoursesByCourseId(con, course.getId()).size());
                User teacher = userDAO.read(con, course.getTeacherId());
                coursesTeacher.put(course.getId(), teacher.getFirstName() + " " +
                        teacher.getLastName());
            }
            Map<Integer, List<Course>> pages = Pagination.pageConstructor(courses);

            session.setAttribute("topics", topics);
            session.setAttribute("teachers", teachers);
            session.setAttribute("pages", pages);
            session.setAttribute("coursesStudents", coursesStudents);
            session.setAttribute("coursesTeacher", coursesTeacher);
            session.setAttribute("pageKey", 0);
            logger.debug("Successfully loading");
        } catch (Exception e) {
            logger.error(e);
            req.setAttribute("message", e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
            return;
        }
        session.setAttribute("path", "catalog.jsp");
        RequestDispatcher rd = req.getRequestDispatcher("catalog.jsp");
            rd.forward(req, resp);

    }





}
