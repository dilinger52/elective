package org.elective.servlets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elective.DBManager.DBException;
import org.elective.DBManager.DBUtils;
import org.elective.DBManager.dao.DAOFactory;
import org.elective.DBManager.dao.UserDAO;
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

@WebServlet("/create_teacher")
public class CreateTeacherServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(CreateTeacherServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher rd = req.getRequestDispatcher("createTeacher.jsp");

            rd.forward(req, resp);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        logger.debug("Creating teacher...");
        HttpSession session = req.getSession();
        String firstName = req.getParameter("first_name");
        String lastName = req.getParameter("last_name");
        String password = req.getParameter("password");
        String confpass = req.getParameter("confpass");
        String email = req.getParameter("email");
        if (!confpass.equals(password)) {
            session.setAttribute("confpass", "passwords doesn't the same");
            resp.sendRedirect(req.getContextPath() + "/registration");
            return;
        }

        User user = new User(firstName, lastName, password, 2, email);
        try (Connection con = DBUtils.getInstance().getConnection();) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            UserDAO userDAO = daoFactory.getUserDAO();
            userDAO.create(con, user);
            logger.debug("teacher: {}", user);
        } catch (DBException | Exception e) {
            logger.error(e);
            e.printStackTrace();
            req.setAttribute("message", e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
            return;
        }

            resp.sendRedirect(req.getContextPath() + "/create_course");

    }
}
