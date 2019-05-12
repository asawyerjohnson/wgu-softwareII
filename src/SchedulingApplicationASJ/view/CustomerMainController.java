/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SchedulingApplicationASJ.view;

import SchedulingApplicationASJ.model.City;
import SchedulingApplicationASJ.model.Customer;
import SchedulingApplicationASJ.util.DBConnection;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author ashley.johnson
 */
public class CustomerMainController implements Initializable {

   @FXML
    private AnchorPane customerMain;
    
    @FXML
    private TableView<Customer> customerTable;
    
    @FXML
    private TableColumn<Customer, Integer> customerId;
    
    @FXML
    private TableColumn<Customer, String> customerName;
    
    private Customer selectedCustomer;
    
    @FXML
    public void handleBackButton(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }
    
    /**
     * handleAddButton utilizes two lambda expressions to both maximize efficiency
     * and improve readability
     */
    @FXML
    public void handleAddButton() {
        Dialog<ButtonType> dialog = new Dialog();
        dialog.initOwner(customerMain.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("CustomerAdd.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch(IOException e) {
            System.out.println("CustomerAdd Error: " + e.getMessage());
        }
        ButtonType save = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(save);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        CustomerAddController controller = fxmlLoader.getController();
        dialog.showAndWait().ifPresent((response -> {
            if(response == save) {
                try {
                    if(controller.handleAddCustomer()) {
                        customerTable.setItems(getAllCustomers());
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Add Customer Error");
                        alert.setContentText(controller.displayErrors());
                        alert.showAndWait().ifPresent((response2 -> {
                            if(response2 == ButtonType.OK) {
                                handleAddButton();
                            }
                        }));
                    }
                } catch (SQLException ex) {
                    System.out.println("Something isn't right: "+ ex.getMessage());
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
        if(customerTable.getSelectionModel().getSelectedItem() != null) {
            selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        } else {
            return;
        }
        Dialog<ButtonType> dialog = new Dialog();
        dialog.initOwner(customerMain.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("CustomerModify.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch(IOException e) {
            System.out.println("CustomerModify Error: " + e.getMessage());
        }
        ButtonType save = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(save);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        CustomerModifyController controller = fxmlLoader.getController();
        controller.populateFields(selectedCustomer);
        dialog.showAndWait().ifPresent((response -> {
            if(response == save) {
                if(controller.handleModifyCustomer()) {
                    customerTable.setItems(getAllCustomers());
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Modify Customer Error");
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
    
    /**
     * handleDeleteButton utilizes a lambda expression to both maximize efficiency
     * and improve readability
     */
    @FXML
    public void handleDeleteButton() {
        if(customerTable.getSelectionModel().getSelectedItem() != null) {
            selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        } else {
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete");
        alert.setHeaderText("Delete Customer Record");
        alert.setContentText("Delete Customer: " + selectedCustomer.getCustomerName() + " ?");
        alert.showAndWait().ifPresent((response -> {
            if(response == ButtonType.OK) {
                deleteCustomer(selectedCustomer.getCustomerId());
                customerTable.setItems(getAllCustomers());
            }
        }));
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        customerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        customerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerTable.setItems(getAllCustomers());
    }

     private static ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    
    public static Customer getCustomer(int id) {
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            String query = "SELECT * FROM customer WHERE customerId="+id;
            ResultSet results = statement.executeQuery(query);
            if(results.next()) {
                Customer customer = new Customer();
                customer.setCustomerName(results.getString("customerName"));
                statement.close();
                return customer;
            }
        } catch (SQLException e) {
            System.out.println("Something isn't right: " + e.getMessage());
        }
        return null;
    }
    
    public static City getCity(int cityId) {
        try { 
            Statement statement = DBConnection.getConnection().createStatement();
            String query = "SELECT city FROM city WHERE cityId="+cityId;
            ResultSet results = statement.executeQuery(query);
            if (results.next()) {
                City city = new City();
                city.setCity(results.getString("city"));
                return city;                
            }                
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
       return null;
    }
    
    // Returns all Customers in Database
    public static ObservableList<Customer> getAllCustomers() {
        System.out.println("Retrieving Customer Records");
        allCustomers.clear();
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            String query = "SELECT customer.customerId, customer.customerName, address.address, address.phone, address.postalCode, city.city"
                + " FROM customer INNER JOIN address ON customer.addressId = address.addressId "
                + "INNER JOIN city ON address.cityId = city.cityId";
            ResultSet results = statement.executeQuery(query);
            while(results.next()) {
               
                Customer customer = new Customer(
                    results.getInt("customerId"), 
                    results.getString("customerName"), 
                    results.getString("address"),
                    results.getString("city"),
                    results.getString("phone"),
                    results.getString("postalCode"));
                allCustomers.add(customer);
                System.out.println("Customer ID: " + results.getInt("customerId"));
            }
            statement.close();
            return allCustomers;
        } catch (SQLException e) {
            System.out.println("Cannot retrieve Customers: " + e.getMessage());
            return null;
        }
    }
            
    // Saves new Customer to Database
    public static boolean saveCustomer(String name, String address, int cityId, String zip, String phone) {
        int addressId = 0;
        int customerId = 0;
        
        try {
            
            Statement statement = DBConnection.getConnection().createStatement();
            
            ResultSet rs1 = statement.executeQuery("SELECT MAX(addressId) FROM address");
            if(rs1.next()){addressId = rs1.getInt(1); addressId++;}
                
            ResultSet rs2 = statement.executeQuery("SELECT MAX(customerId) FROM customer");
            if(rs2.next()){customerId = rs2.getInt(1); customerId++;}
            String queryOne = "INSERT INTO address SET addressId=" + addressId + ", address='" 
                    + address + "', address2='none', phone='" + phone + "', postalCode='" + zip + "', cityId=" 
                    + cityId + ", createDate=NOW(), createdBy='', lastUpdate=NOW(), lastUpdateBy=''";
            int updateOne = statement.executeUpdate(queryOne);
            if(updateOne == 1) {
            String queryTwo = "INSERT INTO customer SET customerId="+customerId+", customerName='" 
                    + name + "', addressId=" + addressId +", active=1, createDate=NOW(), createdBy='', lastUpdate=NOW(), lastUpdateBy=''";
            int updateTwo = statement.executeUpdate(queryTwo);
            if(updateTwo == 1) {
                return true;
            }
            }
        } catch (SQLException e) {
            System.out.println("Cannot save Customer Information: " + e.getMessage());
        }
        return false;
    }
    
    // Update existing Customer in Database
    public static boolean updateCustomer(int id, String name, String address, int cityId, String zip, String phone) {
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            
            String queryOne = 
                    "UPDATE address SET address='" +address+ "', cityId=" 
                    +cityId+ ", postalCode='" +zip+ "', phone='" +phone
                    + "', lastUpdate=CURRENT_TIMESTAMP "
                    + "WHERE addressId=" +id;
            
            int updateOne = statement.executeUpdate(queryOne);
            
            if(updateOne == 1) {
                String queryTwo = 
                        "UPDATE customer SET customerName='" +name+ "', addressId=" 
                        +id+ ", lastUpdate=CURRENT_TIMESTAMP "
                        + "WHERE customerId="+id;
                int updateTwo = statement.executeUpdate(queryTwo);
                if(updateTwo == 1) {
                    return true;
                }
            }
        } catch(SQLException e) {
            System.out.println("Cannot update Customer Information: " + e.getMessage());
        }
        return false;
    }
    
    // Delete Customer from Database
    public static boolean deleteCustomer(int id) {
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            String queryOne = "DELETE FROM address WHERE addressId=" + id;
            int updateOne = statement.executeUpdate(queryOne);
            if(updateOne == 1) {
                String queryTwo = "DELETE FROM customer WHERE customerId=" + id;
                int updateTwo = statement.executeUpdate(queryTwo);
                if(updateTwo == 1) {
                    return true;
                }
            }
        } catch(SQLException e) {
            System.out.println("Cannot delete Customer: " + e.getMessage());
        }
        return false;
    }
}
