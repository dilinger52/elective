<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="language" value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}" scope="session" />
<fmt:setLocale value="${language}" />
<fmt:setBundle basename="text" />
<html>
<body>
<div align="center">
    <h2><fmt:message key="confirmEmail.header" /></h2>
    <p>
        <fmt:message key="confirmEmail.description" />: ${email}.
    </p>
    <p>
        <fmt:message key="confirmEmail.description2" />
    </p>
</div>
</body>
</html>