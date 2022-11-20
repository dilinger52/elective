<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="my" uri="WEB-INF/custom.tld" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="language" value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}" scope="session" />
<fmt:setLocale value="${language}" />
<fmt:setBundle basename="text" />
<jsp:include page="header.jsp" />
<div class="page">
    <form id="clear"></form>
    <div class="heading_container">
        <div class="headings">
            <h1 class="heading1">
                <fmt:message key="personalCourses.head.header" />
            </h1>
            <p class="heading2">
                <fmt:message key="personalCourses.head.description" />
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
                    <option class="option" value="3" ${param.sorting_pattern == 3 ? 'selected' : ''}><fmt:message key="sorting.A-Z" /></option>
                    <option class="option" value="4" ${param.sorting_pattern == 4 ? 'selected' : ''}><fmt:message key="sorting.Z-A" /></option>
                    <option class="option" value="5" ${param.sorting_pattern == 5 ? 'selected' : ''}><fmt:message key="sorting.durationL-H" /></option>
                    <option class="option" value="6" ${param.sorting_pattern == 6 ? 'selected' : ''}><fmt:message key="sorting.durationH-L" /></option>
                </select>
                <div class="info_count">
                    <my:MapSize map="${sessionScope.pages}" /> <fmt:message key="courseCount" />
                </div>
            </div>
            <div class="info_wrapper">
                <c:forEach var="course" items="${sessionScope.pages[pageKey]}">
                    <div class="course_card">
                        <div class="text_container">
                            <div class="course_title" title="<fmt:message key="catalog.title.title" />">
                                ${course.name}
                            </div>
                            <div class="course_detail" title="<fmt:message key="catalog.topic.title" />">
                                <div class="course_tag">
                                    ${course.topic}
                                </div>
                                <div class="teacher" title="<fmt:message key="catalog.teacher.title" />">
                                    <img src="images/Teacher.svg" class="course_pic">
                                    ${sessionScope.coursesTeacher[course.id]}
                                </div>
                                <div class="teacher" title="<fmt:message key="personalCourses.registrationDate.title" />">
                                    ${sessionScope.coursesRegistrationDate[course.id]}
                                </div>
                                <div class="teacher" title="<fmt:message key="personalCourses.progress.title" />">
                                    <progress value="${sessionScope.finishedSubtopicsNum[course.id]}" max="${sessionScope.studentsSubtopicsNum[course.id]}">  </progress>
                                    ${sessionScope.finishedSubtopicsNum[course.id]} / ${sessionScope.studentsSubtopicsNum[course.id]}
                                </div>
                                <c:if test="${sessionScope.coursesGrade[course.id]} != 0}">
                                    <div class="teacher" title="<fmt:message key="personalCourses.garde.title" />">
                                        ${sessionScope.coursesGrade[course.id]}
                                    </div>
                                </c:if>
                            </div>
                        </div>
                        <form action="course_content" method="get">
                            <button class="primary_button" name="courseId" value="${course.id}">
                                <fmt:message key="personalCourses.action" />
                            </button>
                        </form>
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

