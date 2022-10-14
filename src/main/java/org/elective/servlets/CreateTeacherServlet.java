package org.elective.servlets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

        session.removeAttribute("email");
        session.removeAttribute("firstName");
        session.removeAttribute("lastName");
        session.removeAttribute("password");
        session.removeAttribute("confpass");

        if (!firstName.matches("[A-Z][a-z]+")) {
            session.setAttribute("firstName", "FirstNameMustStartsWithUppercase");
            logger.debug("firstName must starts with uppercase");
            resp.sendRedirect(req.getContextPath() + "/create_teacher");
            return;
        }
        if (!lastName.matches("[A-Z][a-z]+")) {
            session.setAttribute("lastName", "LastNameMustStartsWithUppercase");
            logger.debug("lastName must starts with uppercase");
            resp.sendRedirect(req.getContextPath() + "/create_teacher");
            return;
        }
        if (password.length() < 8) {
            session.setAttribute("password", "PasswordLengthMustBeAtLeast8Symbols");
            logger.debug("password length must be at least 8 symbols");
            resp.sendRedirect(req.getContextPath() + "/create_teacher");
            return;
        }
        if (!email.matches("[a-z0-9]+@[a-z]+.[a-z]+")) {
            session.setAttribute("email", "EmailMustContain@And.");
            logger.debug("email must contain \"@\" and \".\"");
            resp.sendRedirect(req.getContextPath() + "/create_teacher");
            return;
        }
        if (!confpass.equals(password)) {
            session.setAttribute("confpass", "PasswordsDoesn'tTheSame");
            logger.debug("passwords doesn't the same");
            resp.sendRedirect(req.getContextPath() + "/create_teacher");
            return;
        }
        User user;
        try (Connection con = DBUtils.getInstance().getConnection()) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            UserDAO userDAO = daoFactory.getUserDAO();
            user = userDAO.readByEmail(con, email);
            if (user != null) {
                session.setAttribute("email", "UserWithThiEmailHasAlreadyRegistered");
                resp.sendRedirect(req.getContextPath() + "/create_teacher");
                return;
            }
        } catch (Exception e) {
            logger.error(e);
            req.setAttribute("message", e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
            return;
        }

        user = new User(firstName, lastName, password, 2, email);
        try (Connection con = DBUtils.getInstance().getConnection();) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            UserDAO userDAO = daoFactory.getUserDAO();
            userDAO.create(con, user);
            logger.debug("teacher: {}", user);
        } catch (Exception e) {
            logger.error(e);
            req.setAttribute("message", e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
            return;
        }

            resp.sendRedirect(req.getContextPath() + "/create_course");

    }
}
