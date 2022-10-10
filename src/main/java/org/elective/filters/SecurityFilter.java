package org.elective.filters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elective.DBManager.entity.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*")
public class SecurityFilter implements Filter {

    private static final Logger logger = LogManager.getLogger(SecurityFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req =  (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        HttpSession session = req.getSession();
        if (req.getServletPath().equals("/authorisation") ||
                req.getServletPath().equals("/registration") ||
                req.getServletPath().equals("/mailconfirmed") ||
                req.getServletPath().equals("/confirmEmail.jsp") ||
                req.getServletPath().equals("/log_out")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        if (session.getAttribute("user") == null) {
            logger.debug("No one is authorised");
            resp.sendRedirect(req.getContextPath() + "/authorisation");
            return;
        }
        User user = (User) session.getAttribute("user");
        if (user.isBlocked().equals("true")) {
            logger.debug("User " + user + " is blocked");
            req.setAttribute("message", "Sorry, but your account was blocked. For detail information head toward administrator.");
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
            return;
        }

        if (user.getRoleId() != 1 &&
                (req.getServletPath().equals("/students") ||
                        req.getServletPath().equals("/create_course") ||
                        req.getServletPath().equals("/create_teacher") ||
                        req.getServletPath().equals("/delete_course"))) {
            logger.debug("User " + user + " is not admin");
            req.setAttribute("message", "This page only for administrator");
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
            return;
        }
        if (user.getRoleId() != 2 &&
                (req.getServletPath().equals("/content_redactor") ||
                        req.getServletPath().equals("/delete_subtopic") ||
                        req.getServletPath().equals("/new_subtopic") ||
                        req.getServletPath().equals("/rate"))) {
            logger.debug("User " + user + " is not teacher");
            req.setAttribute("message", "This page only for teachers");
            RequestDispatcher rd = req.getRequestDispatcher("error.jsp");
            rd.forward(req, resp);
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);

    }
}
