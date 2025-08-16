<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Export Report</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; text-align: center; }
        h1 { color: #2c3e50; }
        .btn { display: inline-block; margin-top: 20px; padding: 12px 20px; background: #2980b9; color: #fff; text-decoration: none; border-radius: 5px; }
        .btn:hover { background: #1f6690; }
    </style>
</head>
<body>
    <h1>Export Patient Report</h1>
    <p>Click the button below to generate a sample report.</p>
    <form action="export" method="post">
        <button type="submit" class="btn">Generate Report</button>
    </form>
</body>
</html>

