package org.elective.controller;

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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import static org.elective.database.DBUtils.close;
import static org.elective.database.DBUtils.rollback;

/**
 * New subtopic servlet uses on content redactor page and create new subtopic with default name.
 */
@WebServlet("/new_subtopic")
public class NewSubtopicServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(NewSubtopicServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        logger.debug("Creating subtopic...");
        int courseId = Integer.parseInt(req.getParameter("courseId"));
        HttpSession session = req.getSession();
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
            req.setAttribute("message", e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
        } finally {
            close(con);
        }
        session.setAttribute("path", "new_subtopic");
        resp.sendRedirect(req.getContextPath() + "/content_redactor");

    }
}
