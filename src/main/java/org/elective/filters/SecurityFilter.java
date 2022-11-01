package org.elective.filters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elective.database.entity.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Security filter checks if user in session have access to current page.
 */
@WebFilter({"/content_redactor", "/create_teacher", "/course_content", "/create_course", "/delete_course", "/delete_subtopic",
        "/filter", "/index.jsp", "/join_course", "/new_subtopic", "/pagination", "/personal_courses", "/rate", "/search_by_name",
        "/students", "/view_course"})
public class SecurityFilter implements Filter {

    private static final Logger logger = LogManager.getLogger(SecurityFilter.class);
    private static final String ERROR_PAGE = "error.jsp";
    private static final String MESSAGE = "message";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        HttpSession session = req.getSession();
        /*if (req.getServletPath().equals("/authorisation") ||
                req.getServletPath().equals("/registration.jsp") ||
                req.getServletPath().equals("/registration") ||
                req.getServletPath().equals("/mailconfirmed") ||
                req.getServletPath().equals("/confirmEmail.jsp") ||
                req.getServletPath().equals("/log_out")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }*/
        if (session.getAttribute("user") == null) {
            logger.debug("No one is authorised");
            resp.sendRedirect(req.getContextPath() + "/authorisation");
            return;
        }
        User user = (User) session.getAttribute("user");
        if (user.isBlocked().equals("true")) {
            logger.debug("User {} is blocked", user);
            req.setAttribute(MESSAGE, "AccountWasBlocked");
            RequestDispatcher rd = req.getRequestDispatcher(ERROR_PAGE);
            rd.forward(req, resp);
            return;
        }

        if (user.getRoleId() != 1 &&
                (req.getServletPath().equals("/students") ||
                        req.getServletPath().equals("/create_course") ||
                        req.getServletPath().equals("/create_teacher") ||
                        req.getServletPath().equals("/delete_course"))) {
            logger.debug("User {} is not admin", user);
            req.setAttribute(MESSAGE, "OnlyForAdministrator");
            RequestDispatcher rd = req.getRequestDispatcher(ERROR_PAGE);
            rd.forward(req, resp);
            return;
        }
        if (user.getRoleId() != 2 &&
                (req.getServletPath().equals("/content_redactor") ||
                        req.getServletPath().equals("/delete_subtopic") ||
                        req.getServletPath().equals("/new_subtopic") ||
                        req.getServletPath().equals("/rate"))) {
            logger.debug("User {} is not teacher", user);
            req.setAttribute(MESSAGE, "OnlyForTeachers");
            RequestDispatcher rd = req.getRequestDispatcher(ERROR_PAGE);
            rd.forward(req, resp);
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
