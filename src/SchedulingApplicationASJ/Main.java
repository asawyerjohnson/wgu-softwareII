/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SchedulingApplicationASJ;

import SchedulingApplicationASJ.util.DBConnection;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author ashley.johnson
 */
public class Main extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("Launching Login Page");

//            Parent root = FXMLLoader.load(getClass().getResource(".fxml"));
            // C:\Users\Ashley\Documents\NetBeansProjects\Main\src\c195_ashleyjohnson\view\Login.fxml
            Parent root = FXMLLoader.load(getClass().getResource("view/Login.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            DBConnection.makeConnection();
            launch(args);
            DBConnection.closeConnection();
        } catch (SQLException ex) {
            System.out.println("SQL Error: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Error 2: " + ex.getMessage());
            
        }
    }
    
}
