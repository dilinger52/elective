<jsp:include page="header.jsp" />
<div class="page">
<div class="heading_container">
    <div class="headings">
        <h1 class="heading1">
            <fmt:message key="error.header" />
        </h1>
        <p class="heading2">
            ${message}
        </p>
    </div>

</div>
    <form action="index.jsp" method="get">
        <input class="primary_button" type="submit" value='<fmt:message key="error.action" />'/>
    </form>
</div>

<jsp:include page="footer.jsp" />