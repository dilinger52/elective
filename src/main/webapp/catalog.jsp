<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="my" uri="WEB-INF/custom.tld" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="language" value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}" scope="session" />
<fmt:setLocale value="${language}" />
<fmt:setBundle basename="text" />
<jsp:include page="header.jsp" />
<div class="page">
    <form id="clear" aria-disabled="true"></form>
    <div class="heading_container">
        <div class="headings">
            <h1 class="heading1">
                <fmt:message key="catalog.head.header" />
            </h1>
            <p class="heading2">
                <fmt:message key="catalog.head.description" />
            </p>
        </div>
            <input form="filter" class="search_input" type="search" placeholder='<fmt:message key="catalog.search.placeholder" />' name="pattern"/>
    </div>
    <div class="content">
        <jsp:include page="filter.jsp" />
        <div class="main_information">
            <div class="sorting_wrapper">
                <select name="sorting_pattern" class="dropdown" form="filter" onchange="submit()">
                    <option class="option" value="0"><fmt:message key="sorting.default" /></option>
                    <option class="option" value="1" ${param.sorting_pattern == 1 ? 'selected' : ''}><fmt:message key="sorting.studentsL-H" /></option>
                    <option class="option" value="2" ${param.sorting_pattern == 2 ? 'selected' : ''}><fmt:message key="sorting.studentsH-L" /></option>
                    <option class="option" value="3" ${param.sorting_pattern == 3 ? 'selected' : ''}><fmt:message key="sorting.A-Z" /></option>
                    <option class="option" value="4" ${param.sorting_pattern == 4 ? 'selected' : ''}><fmt:message key="sorting.Z-A" /></option>
                    <option class="option" value="5" ${param.sorting_pattern == 5 ? 'selected' : ''}><fmt:message key="sorting.durationL-H" /></option>
                    <option class="option" value="6" ${param.sorting_pattern == 6 ? 'selected' : ''}><fmt:message key="sorting.durationH-L" /></option>
                </select>
                <div class="info_count">
                    <my:MapSize map="${sessionScope.pages}" /> <fmt:message key="courseCount" />
                </div>
                <c:if test="${user.roleId==1}">
                    <form action="create_course" method="get">
                        <input type="submit" value='<fmt:message key="catalog.action.newCourse" />' class="primary_button"/>
                    </form>
                </c:if>
            </div>
            <div class="info_wrapper">
                <c:forEach var="course" items="${sessionScope.pages[pageKey]}">
                    <div class="course_card">
                        <div class="text_container">
                            <div class="course_title" title="<fmt:message key="catalog.title.title" />">
                                ${course.name}
                            </div>
                            <div class="course_description" title="<fmt:message key="catalog.description.title" />">
                                ${course.description}
                            </div>
                            <div class="course_detail" title="<fmt:message key="catalog.topic.title" />">
                                <div class="course_tag">
                                    ${course.topic}
                                </div>
                                <div class="teacher" title="<fmt:message key="catalog.teacher.title" />">
                                    <img src="images/Teacher.svg" class="course_pic">
                                    <c:if test="${sessionScope.user.roleId == 2}" >
                                        <fmt:message key="catalog.you" />
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId != 2}" >
                                        ${coursesTeacher[course.id]}
                                    </c:if>
                                </div>
                                <div class="teacher" title="<fmt:message key="catalog.students.title" />">
                                    <img src="images/Users.svg" class="course_pic">
                                    ${coursesStudents[course.id]}
                                </div>
                                <div class="teacher" title="<fmt:message key="catalog.duration.title" />">
                                    <img src="images/Clock.svg" class="course_pic">
                                    ${course.duration}
                                </div>
                            </div>
                        </div>
                        <c:if test="${sessionScope.user.roleId == 1}" >
                            <form action="create_course" method="get">
                                <input type="hidden" name="courseId" value="${course.id}">
                                <input type="submit" value='<fmt:message key="catalog.action.edit" />' class="primary_button"/>
                            </form>
                        </c:if>
                        <c:if test="${sessionScope.user.roleId == 2}" >
                            <form action="view_course" method="get">
                                <button class="primary_button" name="courseId" value="${course.id}">
                                    <fmt:message key="catalog.action.viewCourse" />
                                </button>
                            </form>
                        </c:if>
                        <c:if test="${sessionScope.user.roleId == 3}" >
                            <form action="join_course" method="post">
                                <button class="primary_button" name="course" value="${course.id}">
                                    <fmt:message key="catalog.action.join" />
                                </button>
                            </form>
                        </c:if>
                    </div>
                </c:forEach>
                <form method="post" action="pagination" class="navigation_panel">

                        <button class="navigation_button" name="new_page_key" value="${sessionScope.pageKey - 1}">
                            <fmt:message key="pagination.previous" />
                        </button>

                    <div class="page_number_wrapper">
                        <c:forEach var="page" items="${sessionScope.pages}">
                            <button class="navigation_button" name="new_page_key" value="${page.key}">
                                ${page.key + 1}
                            </button>
                        </c:forEach>
                    </div>

                        <button class="navigation_button" name="new_page_key" value="${sessionScope.pageKey + 1}">
                            <fmt:message key="pagination.next" />
                        </button>

                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp" />

