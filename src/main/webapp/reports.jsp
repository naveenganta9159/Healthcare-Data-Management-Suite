<%@ page import="java.io.File" %>
<!DOCTYPE html>
<html>
<head>
    <title>Saved Reports</title>
</head>
<body>
<h1>Patient Reports</h1>
<ul>
<%
    File reportsDir = new File("/opt/hospital_reports/");
    String[] files = reportsDir.list();
    if (files != null) {
        for (String f : files) {
%>
        <li><a href="export?file=<%= f %>"><%= f %></a></li>
<%
        }
    } else {
%>
    <li>No reports found</li>
<%
    }
%>
</ul>
<a href="generate.jsp">Back to Registration</a>
</body>
</html>

