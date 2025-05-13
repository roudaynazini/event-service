module org.example.post_events {
    requires javafx.controls;

    requires java.sql;

    requires javafx.fxml;
    requires mysql.connector.j;
    requires com.google.protobuf;
    requires java.desktop;


    opens org.example.post_events.controllers to javafx.fxml;
    opens org.example.post_events.entities to javafx.base;

    exports org.example.post_events;
}
