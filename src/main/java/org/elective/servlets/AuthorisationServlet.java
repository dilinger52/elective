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


@WebServlet("/authorisation")
public class AuthorisationServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(AuthorisationServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        RequestDispatcher rd = req.getRequestDispatcher("authorisation.jsp");
        rd.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        logger.debug("Log in...");
        HttpSession session = req.getSession();
        String email = req.getParameter("email");
        logger.debug("email: {}", email);
        String password = req.getParameter("password");
        User user;
        try(Connection con = DBUtils.getInstance().getConnection();) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            UserDAO userDAO = daoFactory.getUserDAO();
            user = userDAO.readByEmail(con, email);
            logger.debug("find user: {}", user);
        } catch (Exception e) {
            logger.error(e);
            req.setAttribute("message", e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
            return;
        }
        session.removeAttribute("email");
        session.removeAttribute("password");
            if (user == null) {
                session.setAttribute("email", "invalid email");
                logger.debug("invalid email");
                resp.sendRedirect(req.getContextPath() + "/authorisation");
            } else if (password.equals(user.getPassword())){
                session.setAttribute("user", user);
                logger.debug("log in completed");
                resp.sendRedirect(req.getContextPath());
            } else {
                session.setAttribute("password", "invalid password");
                logger.debug("invalid password");
                resp.sendRedirect(req.getContextPath() + "/authorisation");
            }


    }
}
