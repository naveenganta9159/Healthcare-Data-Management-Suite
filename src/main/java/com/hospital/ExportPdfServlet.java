package com.hospital;

import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

import javax.imageio.ImageIO;

@MultipartConfig(fileSizeThreshold = 5 * 1024 * 1024, // 5MB in memory
                 maxFileSize = 50 * 1024 * 1024,      // 50MB per file
                 maxRequestSize = 200 * 1024 * 1024)  // 200MB total
public class ExportPdfServlet extends HttpServlet {

    private String baseDir;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext ctx = config.getServletContext();
        baseDir = ctx.getInitParameter("archiveBaseDir");
        if (baseDir == null || baseDir.isBlank()) baseDir = "/opt/hospital_reports";
        // Ensure base dir exists
        try {
            Files.createDirectories(Path.of(baseDir));
        } catch (IOException e) {
            throw new ServletException("Cannot create archive base directory: " + baseDir, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");

        // Patient & billing fields (match index.jsp names)
        String firstName = n(req.getParameter("firstName"));
        String lastName  = n(req.getParameter("lastName"));
        String dob       = n(req.getParameter("dob"));
        String gender    = n(req.getParameter("gender"));
        String contact   = n(req.getParameter("contact"));
        String email     = n(req.getParameter("email"));
        String address   = n(req.getParameter("address"));
        String aadhar    = n(req.getParameter("aadhar"));
        String blood     = n(req.getParameter("bloodGroup"));
        String history   = n(req.getParameter("medicalHistory"));
        String meds      = n(req.getParameter("currentMedications"));
        String allergies = n(req.getParameter("allergies"));
        String insurance = n(req.getParameter("insuranceProvider"));
        String policy    = n(req.getParameter("policyNumber"));

        String visitDate = n(req.getParameter("visitDate"));
        String consultation = n(req.getParameter("consultation"));
        String serviceDesc  = n(req.getParameter("serviceDescription"));
        String testsOrdered = n(req.getParameter("testsOrdered"));
        String testResults  = n(req.getParameter("testResults"));
        String medications  = n(req.getParameter("medications"));
        String consultationFee = n(req.getParameter("consultationFee"));
        String testCharges     = n(req.getParameter("testCharges"));
        String medicineCharges = n(req.getParameter("medicineCharges"));
        String otherCharges    = n(req.getParameter("otherCharges"));
        String totalAmount     = n(req.getParameter("totalAmount"));
        String paymentStatus   = n(req.getParameter("paymentStatus"));
        String notes           = n(req.getParameter("notes"));

        // Build main report PDF
        File tempMain = File.createTempFile("main_report_", ".pdf");
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            PDType1Font fontH = PDType1Font.HELVETICA_BOLD;
            PDType1Font fontB = PDType1Font.HELVETICA;
            float margin = 40;
            float y = page.getMediaBox().getHeight() - margin;

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                // Header
                y = writeHeading(cs, fontH, 18, margin, y, "Hospital Billing & Reports");
                y = writeLine(cs, fontB, 12, margin, y - 6, "Generated: " + LocalDateTime.now());
                y -= 10;

                // Patient section
                y = writeHeading(cs, fontH, 14, margin, y - 10, "Patient Details");
                y = block(cs, fontB, 11, margin, y, 500,
                        "Name: " + firstName + " " + lastName,
                        "DOB: " + dob + "   Gender: " + gender,
                        "Contact: " + contact + "   Email: " + email,
                        "Address: " + address,
                        "Aadhar: " + aadhar + "   Blood Group: " + blood,
                        "Insurance: " + insurance + "   Policy: " + policy
                );

                // Medical section
                y = writeHeading(cs, fontH, 14, margin, y - 12, "Medical Details");
                y = block(cs, fontB, 11, margin, y, 500,
                        "History: " + history,
                        "Current Medications: " + meds,
                        "Allergies: " + allergies
                );

                // Visit section
                y = writeHeading(cs, fontH, 14, margin, y - 12, "Visit & Billing");
                y = block(cs, fontB, 11, margin, y, 500,
                        "Visit Date: " + visitDate + "   Consultation: " + consultation,
                        "Service: " + serviceDesc,
                        "Tests Ordered: " + testsOrdered,
                        "Test Results: " + testResults,
                        "Medications Prescribed: " + medications,
                        "Charges (₹): Consultation=" + consultationFee + ", Tests=" + zeroIfBlank(testCharges)
                                + ", Medicines=" + zeroIfBlank(medicineCharges) + ", Other=" + zeroIfBlank(otherCharges),
                        "Total (₹): " + totalAmount + "   Payment: " + paymentStatus,
                        "Notes: " + notes
                );
            }

            // For each uploaded image: add a new page showing the image
            Collection<Part> parts;
            try {
                parts = req.getParts();
            } catch (ServletException e) {
                throw new IOException("Failed to read multipart data", e);
            }

            for (Part part : parts) {
                if (!"attachments".equals(part.getName())) continue;
                String submitted = part.getSubmittedFileName();
                if (submitted == null || submitted.isBlank()) continue;

                String ext = FilenameUtils.getExtension(submitted).toLowerCase();
                if (ext.matches("jpg|jpeg|png|gif|bmp")) {
                    BufferedImage img = ImageIO.read(part.getInputStream());
                    if (img != null) {
                        PDPage imgPage = new PDPage(PDRectangle.A4);
                        doc.addPage(imgPage);
                        try (PDPageContentStream cs = new PDPageContentStream(doc, imgPage)) {
                            var pdImg = LosslessFactory.createFromImage(doc, img);
                            // scale to fit within margins
                            float maxW = imgPage.getMediaBox().getWidth() - 2 * margin;
                            float maxH = imgPage.getMediaBox().getHeight() - 2 * margin;
                            float scale = Math.min(maxW / pdImg.getWidth(), maxH / pdImg.getHeight());
                            float w = pdImg.getWidth() * scale;
                            float h = pdImg.getHeight() * scale;
                            float x = (imgPage.getMediaBox().getWidth() - w) / 2;
                            float yImg = (imgPage.getMediaBox().getHeight() - h) / 2;
                            cs.drawImage(pdImg, x, yImg, w, h);
                        }
                    }
                }
            }

            doc.save(tempMain);
        }

        // Merge main + any uploaded PDFs
        LocalDate today = LocalDate.now();
        Path archiveDir = Path.of(baseDir, String.valueOf(today.getYear()),
                String.format("%02d", today.getMonthValue()));
        Files.createDirectories(archiveDir);

        String safeName = (firstName + "_" + lastName).trim().replaceAll("\\s+", "_");
        String fileName = String.format("Patient_%s_Report_%s.pdf",
                safeName.isEmpty() ? "Unknown" : safeName,
                DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss").format(LocalDateTime.now()));
        Path finalPdf = archiveDir.resolve(fileName);

        PDFMergerUtility merger = new PDFMergerUtility();
        merger.setDestinationFileName(finalPdf.toString());
        merger.addSource(tempMain);

        // Add uploaded PDFs to merger
        try {
            for (Part part : req.getParts()) {
                if (!"attachments".equals(part.getName())) continue;
                String submitted = part.getSubmittedFileName();
                if (submitted == null || submitted.isBlank()) continue;

                String ext = FilenameUtils.getExtension(submitted).toLowerCase();
                if ("pdf".equals(ext)) {
                    // Save part to temp file first
                    File tmp = File.createTempFile("upload_", ".pdf");
                    try (InputStream in = part.getInputStream();
                         OutputStream out = new FileOutputStream(tmp)) {
                        in.transferTo(out);
                    }
                    merger.addSource(tmp);
                }
            }
            merger.mergeDocuments(null);
        } catch (Exception e) {
            throw new IOException("Failed to merge documents", e);
        } finally {
            Files.deleteIfExists(tempMain.toPath());
        }

        // Stream file for download
        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        try (OutputStream out = resp.getOutputStream()) {
            Files.copy(finalPdf, out);
            out.flush();
        }
    }

    private static String n(String s) { return s == null ? "" : s.trim(); }
    private static String zeroIfBlank(String s) { return (s == null || s.isBlank()) ? "0" : s; }

    // text utilities
    private float writeHeading(PDPageContentStream cs, PDType1Font font, int size, float x, float y, String text) throws IOException {
        return writeLine(cs, font, size, x, y, text);
    }

    private float writeLine(PDPageContentStream cs, PDType1Font font, int size, float x, float y, String text) throws IOException {
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
        return y - (size + 4);
    }

    private float block(PDPageContentStream cs, PDType1Font font, int size, float x, float y, float width, String... lines) throws IOException {
        float yy = y;
        for (String line : lines) {
            yy = writeWrapped(cs, font, size, x, yy, width, line);
            yy -= 4;
        }
        return yy;
    }

    private float writeWrapped(PDPageContentStream cs, PDType1Font font, int size, float x, float y, float width, String text) throws IOException {
        float leading = size + 2;
        String[] words = text.split("\\s+");
        StringBuilder line = new StringBuilder();
        for (String w : words) {
            String test = line.length() == 0 ? w : line + " " + w;
            float testWidth = font.getStringWidth(test) / 1000 * size;
            if (testWidth > width) {
                // draw current line
                y = writeLine(cs, font, size, x, y, line.toString());
                line = new StringBuilder(w);
                y -= leading - (size + 4);
            } else {
                line = new StringBuilder(test);
            }
        }
        if (line.length() > 0) y = writeLine(cs, font, size, x, y, line.toString());
        return y;
    }
}
