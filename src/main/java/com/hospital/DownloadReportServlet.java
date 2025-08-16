package com.hospital;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Files;

@WebServlet("/export")

public class DownloadReportServlet extends HttpServlet {

    private String baseDir;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext ctx = config.getServletContext();
        baseDir = ctx.getInitParameter("archiveBaseDir");
        if (baseDir == null || baseDir.isBlank()) baseDir = "/opt/hospital_reports";
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // relative path like "2025/08/Patient_John_Doe_Report_2025-08-14_17-10-00.pdf"
        String rel = req.getParameter("file");
        if (rel == null || rel.contains("..")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid file.");
            return;
        }

        Path file = Path.of(baseDir, rel).normalize();
        if (!file.startsWith(Path.of(baseDir)) || !Files.exists(file)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + file.getFileName() + "\"");
        try (OutputStream out = resp.getOutputStream()) {
            Files.copy(file, out);
            out.flush();
        }
    }
}
