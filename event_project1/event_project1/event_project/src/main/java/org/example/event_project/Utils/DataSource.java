package org.example.event_project.Utils;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {
    private  Connection con;

    private  String url="jdbc:mysql://localhost:3306/project_event";
    private String user="root";
    private  String pass="";
    private static DataSource data;
    private DataSource()
    {
        try {
            con= DriverManager.getConnection(url,user,pass);
            System.out.println("connexion établie");
        } catch (SQLException e) {
            System.out.println(e);
        }

    }

    public Connection getCon() {
        return con;
    }

    public static DataSource getInstance()
    {
        if (data == null) data = new DataSource();
        return data;
    }


}
