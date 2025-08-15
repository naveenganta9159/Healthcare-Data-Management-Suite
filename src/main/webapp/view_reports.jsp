<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, java.text.SimpleDateFormat" %>
<%
  List<Map<String,String>> items = (List<Map<String,String>>) request.getAttribute("items");
  if (items == null) items = new ArrayList<>();
  String q = request.getParameter("q") == null ? "" : request.getParameter("q");
%>
<!DOCTYPE html>
<html>
<head>
  <title>Reports Archive</title>
  <link rel="stylesheet" href="assets/css/style.css">
</head>
<body>
<div class="container">
  <h1>📄 Hospital Reports Archive</h1>
  <form method="get" action="reports" class="search">
    <input type="text" name="q" placeholder="Search by patient name or ID..." value="<%= q %>"/>
    <button type="submit">Search</button>
    <a class="secondary" href="reports">Clear</a>
    <a class="secondary" href="index.jsp" style="float:right;">← Back to Form</a>
  </form>

  <%
    if (items.isEmpty()) {
  %>
    <p>No reports found<%= q.isEmpty() ? "" : " for '"+ q +"'" %>.</p>
  <%
    } else {
  %>
  <table class="tbl">
    <thead>
      <tr><th>File Name</th><th>Saved At</th><th>Download</th></tr>
    </thead>
    <tbody>
    <%
      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      for (Map<String,String> row : items) {
        String name = row.get("name");
        String rel  = row.get("rel");
        String date = df.format(new Date(Long.parseLong(row.get("date"))));
    %>
      <tr>
        <td><%= name %></td>
        <td><%= date %></td>
        <td><a href="download?file=<%= java.net.URLEncoder.encode(rel, "UTF-8") %>">Download</a></td>
      </tr>
    <%
      }
    %>
    </tbody>
  </table>
  <%
    }
  %>
</div>
</body>
</html>

