module org.example.event_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;

    opens org.example.event_project to javafx.fxml;
    opens org.example.event_project.Controllers to javafx.fxml;
    opens org.example.event_project.entities to javafx.base;


    exports org.example.event_project;
    exports org.example.event_project.Controllers;
}
