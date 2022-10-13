<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="language" value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}" scope="session" />
<fmt:setLocale value="${language}" />
<fmt:setBundle basename="text" />
<jsp:include page="header.jsp" />
<div class="page">
<div class="heading_container">
    <div class="headings">
        <h1 class="heading1">
            <fmt:message key="error.header" />
        </h1>
        <p class="heading2">
            <fmt:message key="${message}" />
        </p>
    </div>

</div>
    <form action="index.jsp" method="get">
        <input class="primary_button" type="submit" value='<fmt:message key="error.action" />'/>
    </form>
</div>

<jsp:include page="footer.jsp" />