/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Alert;
import model.Customer;
import utils.DBConnection;
import utils.DBQuery;
import interfaces.GetString;

/**
 * FXML Controller class
 *
 * @author Max
 */
public class ModifyCustomerScreenController implements Initializable {
    
    @FXML 
    private Button selectCustomerButton;
    @FXML 
    private TextField customerNameField;
    @FXML 
    private TextField customerAddress1Field;
    @FXML 
    private TextField customerAddress2Field;
    @FXML 
    private ChoiceBox customerCityBox;
    @FXML 
    private TextField customerZipField;
    @FXML 
    private TextField customerPhoneField;   
    @FXML 
    private TableView<Customer> customerTable;
    @FXML 
    private TableColumn<Customer, String> customerNameColumn;
    
    private String exceptionMessage = new String();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        customerTable.setItems(DBQuery.getCustomerNames());
        customerCityBox.setItems(DBQuery.getCities());
    }    
    
    @FXML private void selectCustomerAction (ActionEvent event) throws IOException {
        try {
            Customer selectedName = customerTable.getSelectionModel().getSelectedItem();
            String customerName = selectedName.getName();
            Connection conn = DBConnection.startConnection();
            ResultSet resultSet = conn.createStatement().executeQuery(String.format("SELECT customerId, customerName, address, address2, city, postalCode, country, phone " 
                    + "FROM customer c1 INNER JOIN address a1 ON c1.addressId = a1.addressId " 
                    + "INNER JOIN city c2 ON a1.cityId = c2.cityId " 
                    + "INNER JOIN country c3 ON c2.countryId = c3.countryId WHERE customerName = '%s'", customerName));
            while (resultSet.next()) {
                Customer selectedCustomer = new Customer();
                selectedCustomer.setId(resultSet.getString("customerId"));
                selectedCustomer.setName(resultSet.getString("customerName"));
                selectedCustomer.setAddress1(resultSet.getString("address"));
                selectedCustomer.setAddress2(resultSet.getString("address2"));
                selectedCustomer.setCity(resultSet.getString("city"));
                selectedCustomer.setZip(resultSet.getString("postalCode"));
                selectedCustomer.setCountry(resultSet.getString("country"));
                selectedCustomer.setPhone(resultSet.getString("phone"));
                
                // Populate the customer fields
                customerTable.setDisable(true);
                selectCustomerButton.setDisable(true);
                customerNameField.setText(selectedCustomer.getName());
                customerAddress1Field.setText(selectedCustomer.getAddress1());
                customerAddress2Field.setText(selectedCustomer.getAddress2());
                customerCityBox.setValue(selectedCustomer.getCity());
                customerZipField.setText(selectedCustomer.getZip());
                customerPhoneField.setText(selectedCustomer.getPhone()); 
            }
        }
        catch(SQLException e) {
            System.out.println("selectCustomerAction Error: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleBackAction(ActionEvent event) throws IOException {
        System.out.println("Login successful.");
        Parent parent = FXMLLoader.load(getClass().getResource("/view/CalendarScreen.fxml"));
        Scene scene = new Scene(parent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }
    
    @FXML
    private void handleUpdateCustomerAction(ActionEvent event) throws IOException, SQLException {
        // Get the customerId
        Customer selectedName = customerTable.getSelectionModel().getSelectedItem();
        String originalName = selectedName.getName();
        
        GetString getCustomerId = (name) -> { // lambda returns the customerId needed to update the DB
            try {
                Connection conn = DBConnection.startConnection();
                ResultSet resultSet = conn.createStatement().executeQuery(String.format("SELECT customerId FROM customer WHERE customerName='%s'", name));
                resultSet.next();
                return resultSet.getString("customerId");
            }
            catch(SQLException e) {
                System.out.println("getCustomerId Error: " + e.getMessage());
            }
            return null;
        };
        String customerId = getCustomerId.returnString(originalName);
        
        // Get updated customer data
        String customerName = customerNameField.getText();
        String customerAddress1 = customerAddress1Field.getText();
        String customerAddress2 = customerAddress2Field.getText();
        String customerCity = customerCityBox.getValue().toString();
        String customerZip = customerZipField.getText();
        String customerPhone = customerPhoneField.getText();          
        try {
            exceptionMessage = "";
            exceptionMessage = Customer.isValid(customerName, customerAddress1, customerAddress2, customerCity, customerZip, customerPhone, exceptionMessage);
            if(exceptionMessage.length() > 0) {
                Alert.alertWindow("Customer fields cannot be null", exceptionMessage);
            }
            else {
                // Update the customer in the database
                DBQuery.updateCustomer(customerId, customerName, customerAddress1, customerAddress2, customerCity, customerZip, customerPhone);           
                // Reload the Update Customer screen
                Parent parent = FXMLLoader.load(getClass().getResource("/view/ModifyCustomerScreen.fxml"));
                Scene scene = new Scene(parent);
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                window.setScene(scene);
                window.show();
            }
        }
        catch(IOException e) {
            System.out.println("updateCustomer Error: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleDeleteAction(ActionEvent event) throws IOException, SQLException {
        // Get the customerId
        Customer selectedName = customerTable.getSelectionModel().getSelectedItem();
        String originalName = selectedName.getName();
        String customerId = DBQuery.getCustomerId(originalName);
        try {
            // Delete the customer
            DBQuery.deleteCustomer(customerId);
            // Reload the Update Customer screen
            Parent parent = FXMLLoader.load(getClass().getResource("/view/ModifyCustomerScreen.fxml"));
            Scene scene = new Scene(parent);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        }
        catch(IOException e) {
            System.out.println("deleteCustomer Error: " + e.getMessage());
            Alert.alertWindow("Delete failed", "Could not delete customer, please try again.");
        }
    }
}
