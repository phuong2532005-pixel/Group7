<%-- Forward root requests to HomeServlet so HomeServlet populates data (keeps URL as root) --%>
<%
    request.getRequestDispatcher("/home").forward(request, response);
%>
