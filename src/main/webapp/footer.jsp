<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="language" value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}" scope="session" />
<fmt:setLocale value="${language}" />
<fmt:setBundle basename="text" />
<div class="footer">
    <hr class="separator"/>
    <div class="footer_content">
        <p class="text">
            <fmt:message key="footer.copyright" />
        </p>
        <button class="navigation_button"><fmt:message key="footer.action" /><img src="images/ArrowUp.svg" class="navigation_ico"/></button>
    </div>
</div>
</body>
</html>