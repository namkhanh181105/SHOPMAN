<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if (session != null) {
        session.invalidate();
    }
    response.sendRedirect(request.getContextPath() + "/member/LoginView8.jsp?logout=true");
%>

