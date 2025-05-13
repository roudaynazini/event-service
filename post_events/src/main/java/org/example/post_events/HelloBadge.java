package org.example.post_events;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;



    public class HelloBadge extends Application {
        @Override
        public void start(Stage stage) throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("org/example/post_events/Badges.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 917, 600); // Match your FXML width/height
            stage.setTitle("Badge Manager");
            stage.setScene(scene);
            stage.show();
        }
        public static void main(String[] args) {
            launch();
        }
    }

