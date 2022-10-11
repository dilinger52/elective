package org.elective.servlets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elective.DBManager.DBException;
import org.elective.DBManager.DBUtils;
import org.elective.DBManager.dao.CourseDAO;
import org.elective.DBManager.dao.DAOFactory;
import org.elective.DBManager.dao.UserDAO;
import org.elective.DBManager.entity.Course;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/create_course")
public class CreateCourseServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(CreateCourseServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("Loading course redactor page...");
        HttpSession session = req.getSession();
        session.removeAttribute("course");
        session.removeAttribute("selected");
        List<User> teachers;
        try(Connection con = DBUtils.getInstance().getConnection()) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            UserDAO userDAO = daoFactory.getUserDAO();
            teachers = userDAO.getAllTeachers(con);
            logger.debug("teachers: {}", teachers);
        } catch (DBException | Exception e) {
            logger.error(e);
            e.printStackTrace();
            req.setAttribute("message", e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
            return;
        }
        if (req.getParameter("courseId") != null) {
            int courseId = Integer.parseInt(req.getParameter("courseId"));
            Course course;
            try (Connection con = DBUtils.getInstance().getConnection();){
                DAOFactory daoFactory = DAOFactory.getInstance();
                CourseDAO courseDAO = daoFactory.getCourseDAO();
                course = courseDAO.read(con, courseId);
                logger.debug("course: {}", course);
            } catch (DBException | Exception e) {
                logger.error(e);
                e.printStackTrace();
                req.setAttribute("message", e.getMessage());
                RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
                rd.forward(req, resp);
                return;
            }
            session.setAttribute("course", course);
            Map<Integer, String> selected = new HashMap<>();
            for (User teacher : teachers) {
                if (course.getTeacherId() == teacher.getId()) {
                    selected.put(teacher.getId(), "selected");
                } else {
                    selected.put(teacher.getId(), "");
                }
            }
            session.setAttribute("selected", selected);
            session.setAttribute("teachers", teachers);
        }
        RequestDispatcher rd = req.getRequestDispatcher("createCourse.jsp");

            rd.forward(req, resp);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        logger.debug("Saving course...");
        String name = req.getParameter("name");
        String topic = req.getParameter("topic");
        String description = req.getParameter("description");
        long duration = Long.parseLong(req.getParameter("duration"));
        int teacherId = Integer.parseInt(req.getParameter("teacher"));
        if (req.getParameter("courseId") != null) {
            int courseId = Integer.parseInt(req.getParameter("courseId"));
            try(Connection con = DBUtils.getInstance().getConnection();) {
                DAOFactory daoFactory = DAOFactory.getInstance();
                CourseDAO courseDAO = daoFactory.getCourseDAO();
                Course course = courseDAO.read(con, courseId);
                course.setName(name);
                course.setTopic(topic);
                course.setDescription(description);
                course.setTeacherId(teacherId);
                course.setDuration(duration);
                courseDAO.update(con, course);
                logger.debug("course: {}", course);
            } catch (DBException | Exception e) {
                logger.error(e);
                e.printStackTrace();
                return;
            }
        } else {
            Course course = new Course(teacherId, name, topic, description, duration);
            try(Connection con = DBUtils.getInstance().getConnection();) {
                DAOFactory daoFactory = DAOFactory.getInstance();
                CourseDAO courseDAO = daoFactory.getCourseDAO();
                courseDAO.create(con, course);
                logger.debug("course: {}", course);
            } catch (DBException | Exception e) {
                logger.error(e);
                e.printStackTrace();
                req.setAttribute("message", e.getMessage());
                RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
                rd.forward(req, resp);
                return;
            }
        }
            resp.sendRedirect(req.getContextPath() + "/index.jsp");

    }
}
