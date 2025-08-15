package com.hospital;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class ViewReportsServlet extends HttpServlet {

    private String baseDir;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext ctx = config.getServletContext();
        baseDir = ctx.getInitParameter("archiveBaseDir");
        if (baseDir == null || baseDir.isBlank()) baseDir = "/opt/hospital_reports";
        try { Files.createDirectories(Path.of(baseDir)); } catch (IOException ignored) {}
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String q = Optional.ofNullable(req.getParameter("q")).orElse("").trim().toLowerCase();

        List<Map<String, String>> items = new ArrayList<>();
        Path root = Path.of(baseDir);
        if (Files.exists(root)) {
            try {
                // walk recursively and collect PDFs
                List<Path> files = Files.walk(root)
                        .filter(p -> !Files.isDirectory(p) && p.toString().toLowerCase().endsWith(".pdf"))
                        .collect(Collectors.toList());
                for (Path p : files) {
                    String rel = root.relativize(p).toString().replace('\\','/');
                    String name = p.getFileName().toString();
                    if (!q.isEmpty() && !(name.toLowerCase().contains(q) || rel.toLowerCase().contains(q))) continue;

                    Map<String, String> row = new HashMap<>();
                    row.put("name", name);
                    row.put("rel", rel);
                    row.put("date", String.valueOf(Files.getLastModifiedTime(p).toMillis()));
                    items.add(row);
                }
            } catch (Exception ignored) {}
        }

        // sort by last modified desc
        items.sort((a,b) -> Long.compare(Long.parseLong(b.get("date")), Long.parseLong(a.get("date"))));

        req.setAttribute("items", items);
        req.getRequestDispatcher("/view_reports.jsp").forward(req, resp);
    }
package com.hospital;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class ViewReportsServlet extends HttpServlet {

    private String baseDir;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext ctx = config.getServletContext();
        baseDir = ctx.getInitParameter("archiveBaseDir");
        if (baseDir == null || baseDir.isBlank()) baseDir = "/opt/hospital_reports";
        try { Files.createDirectories(Path.of(baseDir)); } catch (IOException ignored) {}
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String q = Optional.ofNullable(req.getParameter("q")).orElse("").trim().toLowerCase();

        List<Map<String, String>> items = new ArrayList<>();
        Path root = Path.of(baseDir);
        if (Files.exists(root)) {
            try {
                // walk recursively and collect PDFs
                List<Path> files = Files.walk(root)
                        .filter(p -> !Files.isDirectory(p) && p.toString().toLowerCase().endsWith(".pdf"))
                        .collect(Collectors.toList());
                for (Path p : files) {
                    String rel = root.relativize(p).toString().replace('\\','/');
                    String name = p.getFileName().toString();
                    if (!q.isEmpty() && !(name.toLowerCase().contains(q) || rel.toLowerCase().contains(q))) continue;

                    Map<String, String> row = new HashMap<>();
                    row.put("name", name);
                    row.put("rel", rel);
                    row.put("date", String.valueOf(Files.getLastModifiedTime(p).toMillis()));
                    items.add(row);
                }
            } catch (Exception ignored) {}
        }

        // sort by last modified desc
        items.sort((a,b) -> Long.compare(Long.parseLong(b.get("date")), Long.parseLong(a.get("date"))));

        req.setAttribute("items", items);
        req.getRequestDispatcher("/view_reports.jsp").forward(req, resp);
    }
}}
