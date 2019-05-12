/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SchedulingApplicationASJ.view;

import SchedulingApplicationASJ.model.Appointment;
import SchedulingApplicationASJ.model.Customer;
import SchedulingApplicationASJ.util.DBConnection;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author ashley.johnson
 */
public class AppointmentMainController implements Initializable {

     @FXML
    private AnchorPane appointmentMain;
    
    @FXML
    private TableView<Customer> customerTable;
    
    @FXML 
    private TableColumn<Customer, Integer> customerId;
    
    @FXML
    private TableColumn<Customer, String> customerName;
    
    @FXML
    private Label monthCustomerLabel;
    
    @FXML
    private TableView<Appointment> monthAptTable;
    
    @FXML
    private TableColumn<Appointment, String> monthDescription;
    
    @FXML
    private TableColumn<Appointment, String> monthContact;
    
    @FXML
    private TableColumn<Appointment, String> monthLocation;
    
    @FXML
    private TableColumn<Appointment, String> monthStart;
    
    @FXML
    private TableColumn<Appointment, String> monthEnd;
    
    @FXML
    private Label weekCustomerLabel;
    
    @FXML
    private TableView<Appointment> weekAptTable;
    
    @FXML
    private TableColumn<Appointment, String> weekDescription;
    
    @FXML
    private TableColumn<Appointment, String> weekContact;
    
    @FXML
    private TableColumn<Appointment, String> weekLocation;
    
    @FXML
    private TableColumn<Appointment, String> weekStart;
    
    @FXML
    private TableColumn<Appointment, String> weekEnd;
    
    @FXML 
    private Tab monthly;
    
    private Customer selectedCustomer;
    
    private Appointment selectedAppointment;
    
    private boolean isMonthly;
    
    @FXML
    public void handleCustomerClick(MouseEvent event) {
        selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        int id = selectedCustomer.getCustomerId();
        monthCustomerLabel.setText(selectedCustomer.getCustomerName());
        weekCustomerLabel.setText(selectedCustomer.getCustomerName());
        monthAptTable.setItems(getMonthlyAppointments(id));
        weekAptTable.setItems(getWeeklyAppoinments(id));
    }
    
    /**
     * handleAddButton utilizes two lambda expressions to both maximize efficiency
     * and improve readability
     */
    @FXML 
    public void handleAddButton() {
        if(customerTable.getSelectionModel().getSelectedItem() != null) {
            selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        } else {
            return;
        }
        Dialog<ButtonType> dialog = new Dialog();
        dialog.initOwner(appointmentMain.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("AppointmentAdd.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch(IOException e) {
            System.out.println("Error opening 'Add Appointment' window: " + e.getMessage());
            e.printStackTrace();
        }
        ButtonType save = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(save);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        AppointmentAddController controller = fxmlLoader.getController();
//        controller.populateCstId(selectedCustomer.getCustomerId());
        controller.populateCustomerName(selectedCustomer.getCustomerName());
        controller.determineContact(selectedCustomer);
        dialog.showAndWait().ifPresent((response -> {
            if(response == save) {
                if(controller.handleAddAppointment(selectedCustomer.getCustomerId())) {
                    monthAptTable.setItems(getMonthlyAppointments(selectedCustomer.getCustomerId()));
                    weekAptTable.setItems(getWeeklyAppoinments(selectedCustomer.getCustomerId()));
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Add Appointment Error");
                    alert.setContentText(controller.displayErrors());
                    alert.showAndWait().ifPresent((response2 -> {
                        if(response2 == ButtonType.OK) {
                            handleAddButton();
                        }
                    }));
                }
            }
        }));
    }
    
    /**
     * handleModifyButton utilizes two lambda expressions to both maximize efficiency
     * and improve readability
     */
    @FXML
    public void handleModifyButton() {
//        selectedCustomer = customerTable.g;
//        selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        if(monthly.isSelected()) {
            if(monthAptTable.getSelectionModel().getSelectedItem() != null) {
                selectedAppointment = monthAptTable.getSelectionModel().getSelectedItem();
            } else {
                return;
            }
        } else {
            if(weekAptTable.getSelectionModel().getSelectedItem() != null) {
                selectedAppointment = weekAptTable.getSelectionModel().getSelectedItem();
            } else {
                return;
            }
        }
        Dialog<ButtonType> dialog = new Dialog();
        dialog.initOwner(appointmentMain.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("AppointmentModify.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch(IOException e) {
            System.out.println("Error opening 'Modify Appointment' window: " + e.getMessage());
            e.printStackTrace();
        }
        ButtonType save = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(save);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        AppointmentModifyController controller = fxmlLoader.getController();
        controller.populateFields(selectedCustomer, selectedAppointment);
        dialog.showAndWait().ifPresent((response -> {
            if(response == save) {
                if(controller.handleModifyAppointment(selectedAppointment.getAptId())) {
                    monthAptTable.setItems(getMonthlyAppointments(selectedCustomer.getCustomerId()));
                    weekAptTable.setItems(getWeeklyAppoinments(selectedCustomer.getCustomerId()));
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Modify Appointment Error");
                    alert.setContentText(controller.displayErrors());
                    alert.showAndWait().ifPresent((response2 -> {
                        if(response2 == ButtonType.OK) {
                            handleModifyButton();
                        }
                    }));
                }
            }
        }));
    }
    
    @FXML
    public void handleBackButton(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }
    
    /**
     * handleDeleteButton utilizes a lambda expression to both maximize efficiency
     * and improve readability
     */
    @FXML
    public void handleDeleteButton() {
        if(monthly.isSelected()) {
            isMonthly = true;
            if(monthAptTable.getSelectionModel().getSelectedItem() != null) {
                selectedAppointment = monthAptTable.getSelectionModel().getSelectedItem();
            } else {
                return;
            }
        } else {
            isMonthly = false;
            if(weekAptTable.getSelectionModel().getSelectedItem() != null) {
                selectedAppointment = weekAptTable.getSelectionModel().getSelectedItem();
            } else {
                return;
            }
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete");
        alert.setHeaderText("Delete Appointment Record");
        alert.setContentText("Delete Appointment?");
        alert.showAndWait().ifPresent((response -> {
            if(response == ButtonType.OK) {
                deleteAppointment(selectedAppointment.getAptId());
                if(isMonthly) {
                   monthAptTable.setItems(getMonthlyAppointments(selectedCustomer.getCustomerId())); 
                } else {
                    weekAptTable.setItems(getWeeklyAppoinments(selectedCustomer.getCustomerId()));
                }
            }
        }));
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        customerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        customerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerTable.setItems(CustomerMainController.getAllCustomers());
        // Lambda Expression utilized to both improve readability and maximize efficiency
        monthDescription.setCellValueFactory(cellData -> {
            return cellData.getValue().getAptDescriptionProperty();
        });
        // Lambda Expression utilized to both improve readability and maximize efficiency
        monthContact.setCellValueFactory(cellData -> {
            return cellData.getValue().getAptContactProperty();
        });
        // Lambda Expression utilized to both improve readability and maximize efficiency
        monthLocation.setCellValueFactory(cellData -> {
            return cellData.getValue().getAptLocationProperty();
        });
        // Lambda Expression utilized to both improve readability and maximize efficiency
        monthStart.setCellValueFactory(cellData -> {
            return cellData.getValue().getAptStartProperty();
        });
        // Lambda Expression utilized to both improve readability and maximize efficiency
        monthEnd.setCellValueFactory(cellData -> {
            return cellData.getValue().getAptEndProperty();
        });
        // Lambda Expression utilized to both improve readability and maximize efficiency
        weekDescription.setCellValueFactory(cellData -> {
            return cellData.getValue().getAptDescriptionProperty();
        });
        // Lambda Expression utilized to both improve readability and maximize efficiency
        weekContact.setCellValueFactory(cellData -> {
            return cellData.getValue().getAptContactProperty();
        });
        // Lambda Expression utilized to both improve readability and maximize efficiency
        weekLocation.setCellValueFactory(cellData -> {
            return cellData.getValue().getAptLocationProperty();
        });
        // Lambda Expression utilized to both improve readability and maximize efficiency
        weekStart.setCellValueFactory(cellData -> {
            return cellData.getValue().getAptStartProperty();
        });
        // Lambda Expression utilized to both improve readability and maximize efficiency
        weekEnd.setCellValueFactory(cellData -> {
            return cellData.getValue().getAptEndProperty();
        });
    }
    
    public static ObservableList<Appointment> getMonthlyAppointments (int id) {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        Appointment appointment;
        LocalDate begin = LocalDate.now();
        LocalDate end = LocalDate.now().plusMonths(1);
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            String query = "SELECT * FROM appointment WHERE customerId = '" + id + "' AND " + 
                "start >= '" + begin + "' AND start <= '" + end + "'"; 
            ResultSet results = statement.executeQuery(query);
            while(results.next()) {
                appointment = new Appointment(results.getInt("appointmentId"), results.getInt("customerId"), results.getString("start"),
                    results.getString("end"), results.getString("title"), results.getString("description"),
                    results.getString("location"), results.getString("contact"));
                appointments.add(appointment);
            }
            statement.close();
            return appointments;
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            return null;
        }
    }
    
    public static ObservableList<Appointment> getWeeklyAppoinments(int id) {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        Appointment appointment;
        LocalDate begin = LocalDate.now();
        LocalDate end = LocalDate.now().plusWeeks(1);
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            String query = "SELECT * FROM appointment WHERE customerId = '" + id + "' AND " + 
                "start >= '" + begin + "' AND start <= '" + end + "'";
            ResultSet results = statement.executeQuery(query);
            while(results.next()) {
                appointment = new Appointment(results.getInt("appointmentId"), results.getInt("customerId"), results.getString("start"),
                    results.getString("end"), results.getString("title"), results.getString("description"),
                    results.getString("location"), results.getString("contact"));
                appointments.add(appointment);
            }
            statement.close();
            return appointments;
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            return null;
        }
    }
    
    public static Appointment appointmentIn15Min() {
        Appointment appointment;
        LocalDateTime now = LocalDateTime.now();
        ZoneId zid = ZoneId.systemDefault();
        ZonedDateTime zdt = now.atZone(zid);
        LocalDateTime ldt = zdt.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        LocalDateTime ldt2 = ldt.plusMinutes(15);
        String user = LoginController.getCurrentUser().getUsername();
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            String query = "SELECT * FROM appointment WHERE start BETWEEN '" + ldt + "' AND '" + ldt2 + "' AND " + 
                "contact='" + user + "'";
            ResultSet results = statement.executeQuery(query);
            if(results.next()) {
                appointment = new Appointment(results.getInt("appointmentId"), results.getInt("customerId"), results.getString("start"),
                    results.getString("end"), results.getString("title"), results.getString("description"),
                    results.getString("location"), results.getString("contact"));
                return appointment;
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
        return null;
    }
    
    public static boolean saveAppointment(int id, String type, String contact, String location, String date, String time) {
        String title = type.split(":")[0];
        String description = type.split(":")[1];
        String tsStart = createTimeStamp(date, time, location, true);
        String tsEnd = createTimeStamp(date, time, location, false);
        int appointmentId = 0;
        
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            
            ResultSet rs1 = statement.executeQuery("SELECT MAX(appointmentId) FROM appointment");
            if(rs1.next()){appointmentId = rs1.getInt(1); appointmentId++;}
            
            String query = "INSERT INTO appointment SET appointmentId="+appointmentId+", customerId=" + id + ", title='" + title + "', description='" +
                description + "', contact='" + contact + "', location='" + location + "', start='" + tsStart + "', end='" + 
                tsEnd + "', url='', createDate=NOW(), createdBy='', lastUpdate=NOW(), lastUpdateBy=''";
            int update = statement.executeUpdate(query);
            if(update == 1) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Cannot save Appointment: " + e.getMessage());
        }
        return false;
    }
    
    public static boolean updateAppointment(int id, String type, String contact, String location, String date, String time) {
        String title = type.split(":")[0];
        String description = type.split(":")[1];
        String tsStart = createTimeStamp(date, time, location, true);
        String tsEnd = createTimeStamp(date, time, location, false);
        
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            String query = "UPDATE appointment SET title='" + title + "', description='" + description + "', contact='" +
                contact + "', location='" + location + "', start='" + tsStart + "', end='" 
                    + tsEnd +"', lastUpdate=NOW()"
                    + " WHERE appointmentId=" + id;
            int update = statement.executeUpdate(query);
            if(update == 1) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Cannot update Appointment: " + e.getMessage());
        }
        return false;
    }
    
    public static boolean overlappingAppointment(int id, String location, String date, String time) {
        String start = createTimeStamp(date, time, location, true);
//        int cst = Integer.parseInt(customer);
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            String query1 = "SELECT * FROM appointment "
                    + "WHERE start = '"+start+"' AND location = '"+location+"'";
            
//            String query2 = "SELECT * FROM appointment "
//                    + "WHERE start='"+start+"' AND customerId="+cst;
            
            ResultSet results = statement.executeQuery(query1);
            if(results.next()) {
                if(results.getInt("appointmentId") == id) {
                    statement.close();
                    return false;
                }
                statement.close();
                return true;
            } else {
                statement.close();
                return false;
            }
            
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            return true;
        }
    }
    
    public static boolean deleteAppointment(int id) {
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            String query = "DELETE FROM appointment WHERE appointmentId = " + id;
            int update = statement.executeUpdate(query);
            if(update == 1) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Cannot delete Appointment: " + e.getMessage());
        }
        return false;
    }
    
    public static String createTimeStamp(String date, String time, String location, boolean startMode) {
        String h = time.split(":")[0];
        int rawH = Integer.parseInt(h);
        if(rawH < 9) {
            rawH += 12;
        }
        if(!startMode) {
            rawH += 1;
        }
        String rawD = String.format("%s %02d:%s", date, rawH, "00");
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm");
        LocalDateTime ldt = LocalDateTime.parse(rawD, df);
        ZoneId zid;
         switch (location) {
             case "New York":
                 zid = ZoneId.of("America/New_York");
                 break;
             case "Phoenix":
                 zid = ZoneId.of("America/Phoenix");
                 break;
             default:
                 zid = ZoneId.of("Europe/London");
                 break;
         }
        ZonedDateTime zdt = ldt.atZone(zid);
        ZonedDateTime utcDate = zdt.withZoneSameInstant(ZoneId.of("UTC"));
        ldt = utcDate.toLocalDateTime();
        Timestamp ts = Timestamp.valueOf(ldt); 
        return ts.toString();
    }
    
}
