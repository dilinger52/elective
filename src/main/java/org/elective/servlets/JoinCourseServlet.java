package org.elective.servlets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elective.database.DBUtils;
import org.elective.database.dao.DAOFactory;
import org.elective.database.dao.StudentsCourseDAO;
import org.elective.database.dao.StudentsSubtopicDAO;
import org.elective.database.dao.SubtopicDAO;
import org.elective.database.entity.StudentsCourse;
import org.elective.database.entity.StudentsSubtopic;
import org.elective.database.entity.Subtopic;
import org.elective.database.entity.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.Date;
import java.util.List;

import static org.elective.database.DBUtils.close;
import static org.elective.database.DBUtils.rollback;

/**
 * Join course servlet assigned student to course and all subtopics of this course.
 */
@WebServlet("/join_course")
public class JoinCourseServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(JoinCourseServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        logger.debug("Joining course...");
        HttpSession session = req.getSession();
        User student = (User) session.getAttribute("user");
        int courseId = Integer.parseInt(req.getParameter("course"));
        StudentsCourse course = new StudentsCourse();
        course.setStudentId(student.getId());
        course.setCourseId(courseId);
        java.util.Date date = new Date();
        java.sql.Date dateSql = new java.sql.Date(date.getYear(), date.getMonth(), date.getDate());
        course.setRegistrationDate(dateSql);
        Connection con = null;
        try {
            con = DBUtils.getInstance().getConnection();
            con.setAutoCommit(false);
            DAOFactory daoFactory = DAOFactory.getInstance();
            StudentsCourseDAO studentsCourseDAO = daoFactory.getStudentsCourseDAO();
            SubtopicDAO subtopicDAO = daoFactory.getSubtopicDAO();
            StudentsSubtopicDAO studentsSubtopicDAO = daoFactory.getStudentsSubtopicDAO();
            studentsCourseDAO.create(con, course);
            List<Subtopic> subtopicList = subtopicDAO.findSubtopicsByCourse(con, course.getCourseId());
            for (Subtopic s : subtopicList) {
                StudentsSubtopic ss = new StudentsSubtopic(student.getId(), s.getId());
                studentsSubtopicDAO.create(con, ss);
            }
            con.commit();
            logger.debug("User: {} Joined course: {}", student, course);
        } catch (Exception e) {
            rollback(con);
            logger.error(e);
            req.setAttribute("message", e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
        } finally {
            close(con);
        }

        resp.sendRedirect(req.getContextPath());

    }
}
