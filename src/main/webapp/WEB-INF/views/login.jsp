<%--
    Document   : login
    Created on : 30-Jun-2012, 12:23:06
    Author     : oliver.elder.esq
--%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <c:set var="pageTitle" value="Login" />
    <%@ include file="/WEB-INF/views/common/header.jsp" %>
    <body>
        <div class="container">
            <form class="form-signin" action="<c:url value='j_spring_security_check' />" method="POST">
                <h2 class="form-signin-heading">ROMS Login</h2>
                <c:if test="${not empty status}"><p class="error">Invalid login details supplied. Please try again</p></c:if>
                <fieldset>
                    <input type='text' class="input-block-level" name='j_username' value=''>
                </fieldset>
                <fieldset>
                    <input type='password' class="input-block-level" name='j_password'/>
                </fieldset>
                <input name="submit" class="btn btn-large btn-primary" type="submit" value="Login"/>
            </form>
        </div>
    </body>
</html>
