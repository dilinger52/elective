<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="my" uri="WEB-INF/custom.tld" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="language" value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}" scope="session" />
<fmt:setLocale value="${language}" />
<fmt:setBundle basename="text" />
<jsp:include page="header.jsp" />
<div class="page">
    <div class="heading_container">
        <form action="index.jsp" method="get" >
            <input class="navigation_button" type="submit" value='<fmt:message key="back" />'/>
        </form>
        <div class="subtopic_count">
            <c:if test="${course != null}">
                <h1 class="log_in_header"><fmt:message key="createCourse.head.header" /></h1>
            </c:if>
            <c:if test="${course == null}">
                <h1 class="log_in_header"><fmt:message key="createCourse.head.altHeader" /></h1>
            </c:if>
            <div class="topic_buttons">
                <c:if test="${course != null}">
                    <form method="post" action="delete_course">
                        <input type="hidden" name="courseId" value="${course.id}"/>
                        <input class="primary_button" type="submit" value='<fmt:message key="createCourse.action.delete" />'/>
                    </form>
                    <div class="log_in_form">
                        <input class="log_in_button" type="submit" value='<fmt:message key="createCourse.action.confirm" />' form="create"/>
                        <input type="hidden" name="courseId" value="${course.id}" form="create"/>
                    </div>
                </c:if>
                <div class="log_in_form">
                    <c:if test="${course == null}">
                        <input class="log_in_button" type="submit" value='<fmt:message key="createCourse.action.create" />' form="create"/>
                    </c:if>
                </div>
            </div>
        </div>
        <form class="redactor_content" action="create_course" method="post" id="create">
            <hr class="separator">
            <div class="redactor_row">
                <p class="redactor_row_header">
                    <fmt:message key="createCourse.name.label" />
                </p>
                <input class="redactor_input_title" id="name" type="text" name="name" value="${course.name}" placeholder='<fmt:message key="createCourse.name.placeholder" />' required/>
            </div>
            <hr class="separator">
            <div class="redactor_row">
                <p class="redactor_row_header">
                    <fmt:message key="createCourse.topic.label" />
                </p>
                <div class="redactor_column">
                <select name="topic" class="dropdown" id="topic">
                    <option class="option" disabled><fmt:message key="createCourse.topic.placeholder" /></option>
                    <c:forEach var="topic" items="${topics}">
                        <option class="option" value="${topic}" ${topic == course.getTopic() ? 'selected' : ''}>${topic}</option>
                    </c:forEach>
                </select>
                <input class="redactor_input_title" type="text" name="topic" placeholder='<fmt:message key="createCourse.topic.input" />' />
                </div>
            </div>
            <hr class="separator">
            <div class="redactor_row">
                <p class="redactor_row_header">
                    <fmt:message key="createCourse.duration.label" />
                </p>
                <input class="redactor_input_title" id="duration" type="number" name="duration" value="${course.duration}" placeholder='<fmt:message key="createCourse.duration.placeholder" />' required/>
            </div>
            <hr class="separator">
            <div class="redactor_row">
                <p class="redactor_row_header">
                    <fmt:message key="createCourse.description.label" />
                </p>
                <input class="redactor_input_title" id="description" type="text" name="description" value="${course.description}" placeholder='<fmt:message key="createCourse.description.placeholder" />' required/>
            </div>
            <hr class="separator">
            <div class="redactor_row">
                <p class="redactor_row_header">
                    <fmt:message key="createCourse.teacher.label" />
                </p>
                <div class="redactor_column">
                    <select name="teacher" class="dropdown" id="teacher">
                        <option class="option" disabled><fmt:message key="createCourse.teacher.default" /></option>
                        <c:forEach var="teacher" items="${teachers}">
                            <option class="option" value="${teacher.id}" ${teacher.id == course.teacherId ? 'selected' : ''}>${teacher.firstName} ${teacher.lastName}</option>
                        </c:forEach>
                    </select>
                    <div style="height: 24px"></div>
                    <input form="create_teacher" type="submit" value='<fmt:message key="createCourse.action.newTeacher" />' class="primary_button"/>
                </div>
            </div>
        </form>
        <form id="create_teacher" action="create_teacher" method="get"></form>
    </div>
</div>

<jsp:include page="footer.jsp" />
