package org.elective.servlets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elective.database.DBUtils;
import org.elective.database.dao.DAOFactory;
import org.elective.database.dao.StudentsSubtopicDAO;
import org.elective.database.dao.SubtopicDAO;
import org.elective.database.entity.Course;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.elective.database.entity.StudentsSubtopic.completion.COMPLETED;


/**
 * Pagination servlet changes page key.
 */
@WebServlet("/pagination")
public class PaginationServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(PaginationServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("Change page...");
        HttpSession session = req.getSession();
        int newPageKey = Integer.parseInt(req.getParameter("new_page_key"));
        Map<Integer, Subtopic> pages = (Map<Integer, Subtopic>) session.getAttribute("pages");
        if (newPageKey < 0) {
            newPageKey = 0;
        } else if (newPageKey >= pages.size()) {
            newPageKey = pages.size() - 1;
        }
        String path = (String) session.getAttribute("path");
        Course course = (Course) session.getAttribute("course");
        if (path.equals("courseContent.jsp")) {
            Map<Integer, String> subtopicCompletion = new HashMap<>();
            int key = (int) session.getAttribute("pageKey");
            User student = (User) session.getAttribute("user");
            Subtopic subtopic = pages.get(key);
            try (Connection con = DBUtils.getInstance().getConnection()) {
                DAOFactory daoFactory = DAOFactory.getInstance();
                StudentsSubtopicDAO studentsSubtopicDAO = daoFactory.getStudentsSubtopicDAO();
                StudentsSubtopic studentsSubtopic = studentsSubtopicDAO.read(con, subtopic.getId(), student.getId());
                if (!studentsSubtopic.getCompletion().equals(String.valueOf(COMPLETED))) {
                    studentsSubtopic.setCompletion(String.valueOf(COMPLETED));
                    studentsSubtopicDAO.update(con, studentsSubtopic);
                }
                SubtopicDAO subtopicDAO = daoFactory.getSubtopicDAO();
                List<Subtopic> subtopics = subtopicDAO.findSubtopicsByCourse(con, course.getId());
                int i = 0;
                for (Subtopic s :
                        subtopics) {
                    pages.put(i, s);
                    subtopicCompletion.put(i, studentsSubtopicDAO.read(con, s.getId(), student.getId()).getCompletion());
                    i++;
                }
            } catch (Exception e) {
                logger.error(e);
                req.setAttribute("message", e.getMessage());
                RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
                rd.forward(req, resp);
                return;
            }
            session.setAttribute("subtopicCompletion", subtopicCompletion);
        }
        logger.debug("new page key: {}", newPageKey);
        session.setAttribute("pageKey", newPageKey);
        session.setAttribute("path", path);
        resp.sendRedirect(req.getContextPath() + "/" + path);
    }
}
