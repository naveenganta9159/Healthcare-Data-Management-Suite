<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.io.File" %>
<!DOCTYPE html>
<html>
<head>
    <title>View Patient Reports</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        h1 { color: #2c3e50; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }
        th { background: #2c3e50; color: #fff; }
        tr:nth-child(even) { background: #f9f9f9; }
        a.download-btn { color: #fff; background: #27ae60; padding: 6px 12px; border-radius: 4px; text-decoration: none; }
        a.download-btn:hover { background: #219150; }
    </style>
</head>
<body>
    <h1>Patient Reports</h1>

    <%
        String reportsDir = "/opt/hospital_reports";
        File folder = new File(reportsDir);
        File[] listOfFiles = folder.listFiles();
    %>

    <table>
        <tr>
            <th>Report Name</th>
            <th>Download</th>
        </tr>
        <%
            if (listOfFiles != null) {
                for (File file : listOfFiles) {
                    if (file.isFile() && file.getName().endsWith(".pdf")) {
        %>
        <tr>
            <td><%= file.getName() %></td>
            <td><a class="download-btn" href="download?file=<%= file.getName() %>">Download</a></td>
        </tr>
        <%
                    }
                }
            }
        %>
    </table>
</body>
</html>

