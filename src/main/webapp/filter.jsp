<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="my" uri="WEB-INF/custom.tld" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="language" value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}" scope="session" />
<fmt:setLocale value="${language}" />
<fmt:setBundle basename="text" />

<form action="filter" method="get" class="sidebar" id="filter">
    <div class="header_filter_wrapper">
        <div class="filter_header">
            <fmt:message key="filter.header" />
        </div>
        <input class="clear_button" type="submit" value="<fmt:message key="filter.clear" />" form="clear">
    </div>
    <div class="filter_wrapper">
        <div class="filter_name">
            <fmt:message key="filter.topics" />
        </div>
        <c:forEach var="topic" items="${sessionScope.topics}">
            <div class="filter">
                <input type="checkbox" name="topic" value="${topic}" class="checkbox" onchange="submit()" ${my:isCheck(paramValues.topic, topic)}>
                <div class="filter_value">
                    ${topic}
                </div>
            </div>
        </c:forEach>
        <c:if test="${sessionScope.user.roleId != 2}">
        <div class="filter_name">
            <fmt:message key="filter.teachers" />
        </div>
        <c:forEach var="teacher" items="${sessionScope.teachers}">
            <div class="filter">
                <input type="checkbox" class="checkbox" name="teacher" value="${teacher.id}" onchange="submit()" ${my:isCheck(paramValues.teacher, teacher.id)}>
                <div class="filter_value">
                    ${teacher.firstName} ${teacher.lastName}
                </div>
            </div>
        </c:forEach>
        </c:if>
        <c:if test="${sessionScope.path == 'personalCourses.jsp'}">
            <div class="filter_name">
                <fmt:message key="filter.completion" />
            </div>
                <div class="filter">
                    <input type="checkbox" class="checkbox" name="completion" value="0" onchange="submit()" ${my:isCheck(paramValues.completion, 0)}>
                    <div class="filter_value">
                        <fmt:message key="filter.todo" />
                    </div>
                </div>
            <div class="filter">
                <input type="checkbox" class="checkbox" name="completion" value="1" onchange="submit()" ${my:isCheck(paramValues.completion, 1)}>
                <div class="filter_value">
                    <fmt:message key="filter.inProgress" />
                </div>
            </div>
            <div class="filter">
                <input type="checkbox" class="checkbox" name="completion" value="2" onchange="submit()" ${my:isCheck(paramValues.completion, 2)}>
                <div class="filter_value">
                    <fmt:message key="filter.completed" />
                </div>
            </div>
        </c:if>
    </div>
</form>

