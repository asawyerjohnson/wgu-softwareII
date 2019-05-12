/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SchedulingApplicationASJ.view;

import SchedulingApplicationASJ.model.User;
import SchedulingApplicationASJ.util.DBConnection;
import SchedulingApplicationASJ.util.Logger;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author ashley.johnson
 */
public class LoginController implements Initializable {

     @FXML
    private TextField usernameTxt;
    
    @FXML
    private PasswordField passwordTxt;
   
    @FXML
    private Label usernameLabel;
    
    @FXML
    private Label passwordLabel;
    
    @FXML
    private Label mainMessage;
    
    @FXML 
    private Label languageMessage;
    
    @FXML
    private Button loginButton;
    
    private static User currentUser;
    
    private String errorHeader;
    private String errorTitle;
    private String errorText;    
    @FXML
    public void handleLogin(ActionEvent event) throws IOException {
        String username = usernameTxt.getText();
        String password = passwordTxt.getText();
        boolean validUser = login(username, password);
        if(validUser) {
            ((Node) (event.getSource())).getScene().getWindow().hide();
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(errorTitle);
            alert.setHeaderText(errorHeader);
            alert.setContentText(errorText);
            alert.showAndWait();
        }
    }
    
    public static Locale getCurrentLocale() {
         return Locale.getDefault();
    }
    
    Locale[] suppportedLocales = {
        Locale.ENGLISH,
        Locale.GERMAN
    };
    
    public void setLoginLabels(ResourceBundle language) {
    
        Locale here = getCurrentLocale();
//        Locale here = Locale.GERMAN;
        language = ResourceBundle.getBundle("SchedulingApplicationASJ.languageBundle/languageBundle", here);
        
        usernameLabel.setText(language.getString("username"));
        passwordLabel.setText(language.getString("password"));
        loginButton.setText(language.getString("login"));
        mainMessage.setText(language.getString("message"));
        languageMessage.setText(language.getString("language"));
        errorHeader = language.getString("errorheader");
        errorTitle = language.getString("errortitle");
        errorText = language.getString("errortext");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle language) {
    
        try {
            setLoginLabels(language);
        } catch (Exception e) {
            System.out.println("Something isn't right: " + e.getMessage());
        }
        
    }    
    
    public static User getCurrentUser() {
        return currentUser;
    }
    
    // Attempt Login
    public static Boolean login(String username, String password) {
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            String query = "SELECT * FROM user WHERE userName='" + username + "' AND password='" + password + "'";
            ResultSet results = statement.executeQuery(query);
            if(results.next()) {
                currentUser = new User();
                currentUser.setUsername(results.getString("userName"));
                statement.close();
                Logger.log(username, true);
                return true;
            } else {
                Logger.log(username, false);
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Something isn't right with the database: " + e.getMessage());
            return false;
        }
    }

  
}
