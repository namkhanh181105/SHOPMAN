<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Customer8, model.Manager8" %>
<%
    Object memberObj = session.getAttribute("member");
    if (memberObj instanceof Customer8) {
        response.sendRedirect(request.getContextPath() + "/customer/CustomerMainMenu8.jsp");
    } else if (memberObj instanceof Manager8) {
        response.sendRedirect(request.getContextPath() + "/manager/ManagerMainMenu8.jsp");
    } else {
        response.sendRedirect(request.getContextPath() + "/member/LoginView8.jsp");
    }
%>

