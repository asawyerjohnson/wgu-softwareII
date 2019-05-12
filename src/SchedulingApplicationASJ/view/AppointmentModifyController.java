/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SchedulingApplicationASJ.view;

import SchedulingApplicationASJ.model.Appointment;
import SchedulingApplicationASJ.model.Customer;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author ashley.johnson
 */
public class AppointmentModifyController implements Initializable {

//    @FXML
//    private TextField cstId;

    @FXML
    private TextField customerName;
    
    @FXML
    private TextField contact;
    
    @FXML
    private TextField location;
    
    @FXML 
    private DatePicker date;
    
    @FXML
    private ComboBox time;
    
    @FXML
    private ComboBox type;
    
    @FXML
    private Label currentTime;

    @FXML
    private Label PhoenixTime;

    @FXML
    private Label NewYorkTime;

    @FXML
    private Label LondonTime;
    private Customer customer;
    private Appointment appt;
    
    
//    private final ObservableList<String> contacts = FXCollections.observableArrayList("jdoe", "rsmith", "ajohnson");
    
    private final ObservableList<String> times = FXCollections.observableArrayList("09:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00","19:00","20:00");
    
    private final ObservableList<String> types = FXCollections.observableArrayList(
    "Initial:Social Analysis","Consultation:Media Tips","Consultation:Online Presence","Consultation:Maximizing Followers");
    
    private ObservableList<String> errors = FXCollections.observableArrayList();
    
    public boolean handleModifyAppointment(int id) {
        errors.clear();
        
        String aptContact = contact.getText();
        int aptType = type.getSelectionModel().getSelectedIndex();
        int aptTime = time.getSelectionModel().getSelectedIndex();
        LocalDate ld = date.getValue();
        if(!validateType(aptType)||!validateTime(aptTime)||!validateDate(ld)) {
            return false;
        }
        if(AppointmentMainController.overlappingAppointment(id,  location.getText(), ld.toString(), times.get(aptTime))) {
            errors.add("Overlapping Appointments");
            return false;
        }
        if(AppointmentMainController.updateAppointment(id, types.get(aptType), contact.getText(), location.getText(), ld.toString(), times.get(aptTime))) {
            return true;
        } else {
            errors.add("Database Error");
            return false;
        }
    }
    
    public void populateFields(Customer c, Appointment apt) {
        if (c == null) {
            System.out.println("Your customer object is null.");
        } else {
            System.out.println("Your customer object is good! Modifying Appointment...");
        this.customer = c;
        this.appt = apt;
        String t = apt.getAptTitle() + ":" + apt.getAptDescription();
//        cstId.setValue(Integer.toString(c.getCustomerId()));
        customerName.setText(c.getCustomerName());
        contact.setText(apt.getAptContact());
        location.setText(apt.getAptLocation());
        type.setValue(t);
        time.setValue(apt.getTimeOnly());
        date.setValue(apt.getDateOnly());
        }
    }
        
    
        public boolean validateContact(int aptContact) {
        if(aptContact == -1) {
            errors.add("A Contact must be selected");
            return false;
        } else {
            return true;
        }
    }
    
    public boolean validateType(int aptType) {
        if(aptType == -1) {
            errors.add("An Appointment Type must be selected");
            return false;
        } else {
            return true;
        }
    }
    
    public boolean validateTime(int aptTime) {
        if(aptTime == -1) {
            errors.add("An Appointment Time must be selected");
            return false;
        } else {
            return true;
        }
    }
    
    public boolean validateDate(LocalDate aptDate) {
        if(aptDate == null) {
            errors.add("An Appointment Date must be selected");
            return false;
        } else {
            return true;
        }
    }
    
    public String displayErrors(){
        String s = "";
        if(errors.size() > 0) {
            for(String err : errors) {
                s = s.concat(err);
            }
            return s;
        } else {
            s = "Database Error";
            return s;
        }
    }
    
    @FXML
    public void handleLocation() {
        String c = contact.getText();
       if (c.equalsIgnoreCase("Joe Doe")) {
            location.setText("New York");
       } else if(c.equalsIgnoreCase("Ruby Smith")) {
            location.setText("London");
       } else {
            location.setText("Phoenix");
        }
    }
    
    public static String getCurrentTime(){
        LocalTime time = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime=time.format(formatter);
        return formattedTime;
    }
    
    public static String getCurrentTimePhoenix(){
        ZoneId zoneId = ZoneId.of("America/Phoenix");
        LocalTime localTime=LocalTime.now(zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime=localTime.format(formatter);
        return formattedTime;
    }
    
    public static String getCurrentTimeNewYork(){
        ZoneId zoneId = ZoneId.of("America/New_York");
        LocalTime localTime=LocalTime.now(zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime=localTime.format(formatter);
        return formattedTime;
    }
    
    public static String getCurrentTimeLondon(){
        ZoneId zoneId = ZoneId.of("GB");
        LocalTime localTime=LocalTime.now(zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime=localTime.format(formatter);
        return formattedTime;
    }
    
    public void determineContact(Customer customer) {
        if(customer.getCustomerCity().equalsIgnoreCase("New York")) {
            contact.setText("Joe Doe");
            location.setText("New York");
        } else if (customer.getCustomerCity().equalsIgnoreCase("Phoenix")) {
            contact.setText("Ashley Johnson");
            location.setText("Phoenix");
        } else {
            contact.setText("Ruby Smith");
            location.setText("London");
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Set Time Zone Clocks
        currentTime.setText(getCurrentTime());
        PhoenixTime.setText(getCurrentTimePhoenix());
        NewYorkTime.setText(getCurrentTimeNewYork());
        LondonTime.setText(getCurrentTimeLondon());   
        
        
        time.setItems(times);
        type.setItems(types);
        date.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(
                    empty || 
                    date.isBefore(LocalDate.now()));
                if(date.isBefore(LocalDate.now())) {
                    setStyle("-fx-background-color: #ffc4c4;");
                }
            }
        });
    }    
    
}
