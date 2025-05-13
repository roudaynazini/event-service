module org.example.event_project {
    requires javafx.fxml;
    requires java.sql;
    requires com.github.librepdf.openpdf;
    requires jakarta.mail;
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires twilio;
    requires kernel;
    requires layout;
    requires io;
    requires sendgrid.java;
    requires itextpdf;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires javafx.web;
    requires org.json;
    requires org.kordamp.ikonli.materialdesign2;
    requires java.desktop;

    opens org.example.event_project to javafx.fxml;
    opens org.example.event_project.Controllers to javafx.fxml;
    opens org.example.event_project.entities to javafx.base;

    exports org.example.event_project;
    exports org.example.event_project.Controllers;
}
