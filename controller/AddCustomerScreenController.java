/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import model.Alert;
import model.Customer;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utils.DBQuery;

/**
 * FXML Controller class
 *
 * @author Max
 */
public class AddCustomerScreenController implements Initializable {
    
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
    
    private String exceptionMessage = new String();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {  
        customerCityBox.setItems(DBQuery.getCities());
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
    private void handleAddCustomerAction(ActionEvent event) throws IOException {
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
                // Add the new customer to the database
                DBQuery.addCustomer(customerName, customerAddress1, customerAddress2, customerCity, customerZip, customerPhone);
                // Reload the Add Customer screen
                Parent parent = FXMLLoader.load(getClass().getResource("/view/AddCustomerScreen.fxml"));
                Scene scene = new Scene(parent);
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                window.setScene(scene);
                window.show();
            }
        }
        catch(IOException | SQLException e) {
            System.out.println("addCustomer Error: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleResetAction(ActionEvent event) throws IOException {
        customerNameField.clear();
        customerAddress1Field.clear();
        customerAddress2Field.clear();
        customerCityBox.setItems(DBQuery.getCities());
        customerZipField.clear();
        customerPhoneField.clear();
    }
}
