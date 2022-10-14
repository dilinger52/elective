<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="language" value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}" scope="session" />
<fmt:setLocale value="${language}" />
<fmt:setBundle basename="text" />

    <div class="log_in_input_wrapper">
        <label class="log_in_label" for="email"><fmt:message key="login.email.label" /></label>
        <input class="log_in_input" id="email" type="email" name="email" placeholder='<fmt:message key="login.email.placeholder" />' required/>
        <c:if test="${not empty email}">
            <div class="log_in_input_alert"><fmt:message key="${email}" /></div>
        </c:if>
    </div>
    <div class="log_in_input_wrapper">
        <label class="log_in_label" for="firstName"><fmt:message key="registerForm.firstName.label" /></label>
        <input class="log_in_input" id="firstName" type="text" name="first_name" placeholder='<fmt:message key="registerForm.firstName.placeholder" />' required/>
        <c:if test="${not empty firstName}">
            <div class="log_in_input_alert"><fmt:message key="${firstName}" /></div>
        </c:if>
    </div>
    <div class="log_in_input_wrapper">
        <label class="log_in_label" for="lastName"><fmt:message key="registerForm.lastName.label" /></label>
        <input class="log_in_input" id="lastName" type="text" name="last_name" placeholder='<fmt:message key="registerForm.lastName.placeholder" />' required/>
        <c:if test="${not empty lastName}">
            <div class="log_in_input_alert"><fmt:message key="${lastName}" /></div>
        </c:if>
    </div>
    <div class="log_in_input_wrapper">
        <label class="log_in_label" for="password"><fmt:message key="login.password.label" /></label>
        <input class="log_in_input" type="password" name="password" id="password" placeholder='<fmt:message key="login.password.placeholder" />' required/>
        <c:if test="${not empty password}">
            <div class="log_in_input_alert"><fmt:message key="${password}" /></div>
        </c:if>
    </div>
    <div class="log_in_input_wrapper">
        <label class="log_in_label" for="confpass"><fmt:message key="registerForm.confpass.label" /></label>
        <input class="log_in_input" type="password" name="confpass" id="confpass" placeholder='<fmt:message key="registerForm.confpass.placeholder" />' required/>
        <c:if test="${not empty confpass}">
            <div class="log_in_input_alert"><fmt:message key="${confpass}" /></div>
        </c:if>
    </div>
    <input class="log_in_button" type="submit" value='<fmt:message key="registerForm.action" />'/>
    <div class="filter">
        <input class="checkbox" type="checkbox" onclick="showPassword()"/>
        <div class="log_in_text"><fmt:message key="login.checkbox" /></div>
    </div>

</form>
<c:if test="${user.roleId != 1}" >
    <p class="log_in_text">
        <fmt:message key="registerForm.login.label" /> <a class="log_in_ref" href="/elective/authorisation"><fmt:message key="registerForm.login.submit" /></a>
    </p>
</c:if>


<script>
    function showPassword() {
    var x = document.getElementById("password");
    var y = document.getElementById("confpass");
    if (x.type === "password") {
        x.type = "text";
        y.type = "text";
    } else {
        x.type = "password";
        y.type = "password";
    }
}
</script>