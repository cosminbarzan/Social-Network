package socialnetwork;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.Date;


import com.itextpdf.text.Anchor;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import socialnetwork.controller.ReportsController;
import socialnetwork.domain.Message;
import socialnetwork.domain.User;
import socialnetwork.utils.FriendD;
import socialnetwork.utils.Report;


public class FirstPdf {
    private static String FILE = "C:/Users/barza/IdeaProjects/proiect-lab-schelet/proiect-lab-schelet/FirstPdf.pdf";
    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
            Font.BOLD);
    private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.NORMAL, BaseColor.RED);
    private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
            Font.BOLD);
    private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.BOLD);

    private static java.util.List<Report> reports = ReportsController.getReports();

    // Userul logat
    private static User loggedUser = ReportsController.getServiceUser().getOne(ReportsController.getServiceUser().getLoggedIn());

    public static void main(String[] args) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(FILE));
            document.open();
            addMetaData(document);
            addTitlePage(document);
            addContent(document);
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // iText allows to add metadata to the PDF which can be viewed in your Adobe
    // Reader
    // under File -> Properties
    private static void addMetaData(Document document) {
        document.addTitle("My first PDF");
        document.addSubject("Using iText");
        document.addKeywords("Java, PDF, iText");
        document.addAuthor("Lars Vogel");
        document.addCreator("Lars Vogel");
    }

    private static String parseDate(LocalDate date) {
        String result = "";

        String day = date.getDayOfWeek().toString().substring(0,3);
        String dayLow = day.substring(0,1).toUpperCase() + day.substring(1).toLowerCase();

        String month = date.getMonth().toString().substring(0,3);
        String monthLow = month.substring(0,1).toUpperCase() + month.substring(1).toLowerCase();

        result = dayLow + " " + date.getDayOfMonth() + " " + monthLow + " " + date.getYear();
        return result;
    }

    private static void addTitlePage(Document document)
            throws DocumentException {
        Paragraph preface = new Paragraph();
        // We add one empty line
        addEmptyLine(preface, 1);
        // Lets write a big header
        preface.add(new Paragraph("User Activity", catFont));

        addEmptyLine(preface, 1);
        // Will create: Report generated by: _name, _date
        preface.add(new Paragraph(
                "Report generated by: " + loggedUser.getFirstName() + " " + loggedUser.getLastName() +
                        ", " + new Date(), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                smallBold));
        addEmptyLine(preface, 3);
        LocalDate dateStart = reports.get(0).getDate();
        LocalDate dateEnd = reports.get(reports.size()-1).getDate();

        preface.add(new Paragraph(
                "This document describes the activity on MSN between " + parseDate(dateStart) + " and "
                        + parseDate(dateEnd),
                smallBold));

        addEmptyLine(preface, 8);

//        preface.add(new Paragraph(
//                "This document is a preliminary version and not subject to your license agreement or any other agreement with vogella.com ;-).",
//                redFont));

        document.add(preface);
        // Start a new page
        document.newPage();
    }

    private static void addContent(Document document) throws DocumentException {
        Anchor anchor = new Anchor("New friends and received messages", catFont);
        anchor.setName("First Chapter");

        // Second parameter is the number of the chapter
        Paragraph paragraph1 = new Paragraph(anchor);
        Chapter catPart = new Chapter(paragraph1, 1);
        addEmptyLine(paragraph1, 1);

        Paragraph subPara;
        Section subCatPart;
        Paragraph textPara = new Paragraph();

        java.util.List<FriendD> users;
        java.util.List<Message> messages;

        for(Report report : reports) {
            subPara = new Paragraph(parseDate(report.getDate()), subFont);
            subCatPart = catPart.addSection(subPara);

            users = report.getUsers();
            messages = report.getMessages();

            if(users == null && messages == null) {
                textPara = new Paragraph("No activity");
                subCatPart.add(textPara);
            }
            else {
                if (users != null) {
                    for (FriendD friendD : users) {
                        textPara = new Paragraph(loggedUser.getFirstName() + " " + loggedUser.getLastName() +
                                " and " + friendD.getUser().getFirstName() + " " + friendD.getUser().getLastName() +
                                " became friends");
                        subCatPart.add(textPara);
                    }
                }
                if (messages != null) {
                    for(Message message : messages) {
                        User from = ReportsController.getServiceUser().getOne(message.getFrom().getId());
                        textPara = new Paragraph("Message from " + from.getFirstName() + " " +
                                from.getLastName() + ": " + message.getMessage());
                        subCatPart.add(textPara);
                    }
                }
            }

            addEmptyLine(textPara, 1);
        }

//        Paragraph subPara = new Paragraph("Subcategory 1", subFont);
//        Section subCatPart = catPart.addSection(subPara);
//        subCatPart.add(new Paragraph("Hello"));

//        subPara = new Paragraph("Subcategory 2", subFont);
//        subCatPart = catPart.addSection(subPara);
//        subCatPart.add(new Paragraph("Paragraph 1"));
//        subCatPart.add(new Paragraph("Paragraph 2"));
//        subCatPart.add(new Paragraph("Paragraph 3"));
//
//        // add a list
//        createList(subCatPart);
//        Paragraph paragraph = new Paragraph();
//        addEmptyLine(paragraph, 5);
//        subCatPart.add(paragraph);

        // add a table
        //createTable(subCatPart);

        // now add all this to the document
        document.add(catPart);

        // Next section
        anchor = new Anchor("Second Chapter", catFont);
        anchor.setName("Second Chapter");

        // Second parameter is the number of the chapter
        catPart = new Chapter(new Paragraph(anchor), 1);

        subPara = new Paragraph("Subcategory", subFont);
        subCatPart = catPart.addSection(subPara);
        subCatPart.add(new Paragraph("This is a very important message"));

        // now add all this to the document
        document.add(catPart);

    }

    private static void createTable(Section subCatPart)
            throws BadElementException {
        PdfPTable table = new PdfPTable(3);

        // t.setBorderColor(BaseColor.GRAY);
        // t.setPadding(4);
        // t.setSpacing(4);
        // t.setBorderWidth(1);

        PdfPCell c1 = new PdfPCell(new Phrase("Table Header 1"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Table Header 2"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Table Header 3"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);
        table.setHeaderRows(1);

        table.addCell("1.0");
        table.addCell("1.1");
        table.addCell("1.2");
        table.addCell("2.1");
        table.addCell("2.2");
        table.addCell("2.3");

        subCatPart.add(table);

    }

    private static void createList(Section subCatPart) {
        List list = new List(true, false, 10);
        list.add(new ListItem("First point"));
        list.add(new ListItem("Second point"));
        list.add(new ListItem("Third point"));
        subCatPart.add(list);
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }
}