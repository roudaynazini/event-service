module com.example.bouthaina {
    requires javafx.fxml;
    requires java.sql;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires org.kordamp.ikonli.materialdesign2;
    requires javafx.web;
    requires org.json;
    requires java.desktop;

    opens com.example.bouthaina to javafx.fxml;
    opens com.example.bouthaina.BackOffice.controllers to javafx.fxml;
    opens com.example.bouthaina.ClientView.controllers to javafx.fxml;
    opens com.example.bouthaina.BackOffice.models to javafx.base;

    exports com.example.bouthaina;
    exports com.example.bouthaina.BackOffice.controllers;
    exports com.example.bouthaina.ClientView.controllers;
    exports com.example.bouthaina.BackOffice.models;
    exports com.example.bouthaina.BackOffice.services;
}