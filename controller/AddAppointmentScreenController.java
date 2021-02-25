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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Customer;
import model.Appointment;
import model.User;
import model.Alert;
import utils.DBConnection;
import utils.DBQuery;
import utils.TimeUtil;
import interfaces.LocalDateToInteger;
import java.time.DayOfWeek;

/**
 * FXML Controller class
 *
 * @author Max
 */
public class AddAppointmentScreenController implements Initializable {

    @FXML 
    private Button selectCustomerButton;
    @FXML 
    private TextField customerNameField;
    @FXML 
    private TextField customerContactField;
    @FXML 
    private TextField appointmentTitleField;
    @FXML 
    private ChoiceBox appointmentTypeBox;
    @FXML 
    private ChoiceBox appointmentLocationBox;
    @FXML 
    private TextField appointmentUrlField;
    @FXML 
    private DatePicker appointmentDatePicker;
    @FXML 
    private ComboBox appointmentStartBox;
    @FXML 
    private ComboBox appointmentEndBox;
    @FXML 
    private TextArea appointmentDescriptionArea;
    @FXML 
    private TableView<Customer> customerTable;
    @FXML 
    private TableColumn<Customer, String> customerNameColumn;
    
    private String exceptionMessage = new String();
    static ObservableList<String> locationChoices = FXCollections.observableArrayList();
    static ObservableList<String> typeChoices = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        customerTable.setItems(DBQuery.getCustomerNames());
        appointmentTypeBox.setItems(FXCollections.observableArrayList("Onsite", "Remote"));
        appointmentLocationBox.setItems(DBQuery.getCities());
        appointmentDescriptionArea.setWrapText(true);
        appointmentStartBox.setItems(Appointment.getAppointmentTimes());
        appointmentEndBox.setItems(Appointment.getAppointmentTimes());
        appointmentDatePicker.setValue(LocalDate.now());
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
    
    @FXML private void selectCustomerAction (ActionEvent event) throws IOException {
        try {
            Customer selectedName = customerTable.getSelectionModel().getSelectedItem();
            String customerName = selectedName.getName();
            Connection conn = DBConnection.startConnection();
            ResultSet resultSet = conn.createStatement().executeQuery(String.format("SELECT customerId, customerName, address, address2, city, postalCode, country, phone " 
                    + "FROM customer c1 INNER JOIN address a1 ON c1.addressId = a1.addressId " 
                    + "INNER JOIN city c2 ON a1.cityId = c2.cityId " 
                    + "INNER JOIN country c3 ON c2.countryId = c3.countryId WHERE customerName = '%s';", 
                    customerName));
            while (resultSet.next()) {
                Customer selectedCustomer = new Customer();
                selectedCustomer.setId(resultSet.getString("customerId"));
                selectedCustomer.setName(resultSet.getString("customerName"));
                // Populate customer data
                customerNameField.setText(selectedCustomer.getName());
                customerNameField.setDisable(true);
                customerTable.setDisable(true);
                selectCustomerButton.setDisable(true);
            }
        }
        catch(SQLException e) {
            System.out.println("selectCustomerAction Error: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleCreateAppointmentAction(ActionEvent event) throws IOException, SQLException {
        try {
            // Get the customerName
            Customer selectedName = customerTable.getSelectionModel().getSelectedItem();
            String customerName = selectedName.getName();
            // Get appointment data
            String customerContact = customerContactField.getText();
            String appointmentTitle = appointmentTitleField.getText();
            String appointmentType = appointmentTypeBox.getValue().toString();
            String appointmentLocation = appointmentLocationBox.getValue().toString();
            String appointmentUrl = appointmentUrlField.getText();
            String appointmentDate = appointmentDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));        
            String appointmentStart = appointmentStartBox.getValue().toString();
            String appointmentEnd = appointmentEndBox.getValue().toString();
            String appointmentDescription = appointmentDescriptionArea.getText();
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate ld = LocalDate.parse(appointmentDate, df);
            
            LocalDateToInteger getDayValue = (date) -> { // lambda returns the day of the week to validate business days
                DayOfWeek day = date.getDayOfWeek();
                return day.getValue();
            };
            Integer dayInt = getDayValue.returnInteger(ld);
        
            if (dayInt != 6 && dayInt != 7 && TimeUtil.checkBusinessHours(appointmentDate, appointmentStart, appointmentEnd) && TimeUtil.checkOverlap(appointmentDate, appointmentStart, appointmentEnd)) {
                // Insert appointment into database
                DBQuery.addAppointment(customerName, User.getName(), appointmentTitle, appointmentDescription, appointmentLocation, customerContact, appointmentType, appointmentUrl, appointmentStart, appointmentEnd, appointmentDate);
            }
            else {
                if (dayInt != 6 || dayInt != 7){
                    Alert.alertWindow("Business Days", "Unable to schedule appointment: Please schedule within normal business days (Monday - Friday).");
                }
                if (TimeUtil.checkBusinessHours(appointmentDate, appointmentStart, appointmentEnd) == false) {
                    Alert.alertWindow("Business Hours", "Unable to schedule appointment: Please schedule within normal business hours (09:00 - 17:00).");
                }
                if (TimeUtil.checkOverlap(appointmentDate, appointmentStart, appointmentEnd) == false) {
                    Alert.alertWindow("Overlapping Appointment", "Unable to schedule appointment: Appointment time conflicts with another.");
                }
            }
        }
        catch(Exception e) {
            System.out.println("CreateAppointmentAction Error: " + e.getMessage());
            Alert.alertWindow("Incomplete Data", "Required Fields: Customer, Title, Type, Location, Date, Start, End, Description");
        }
            
        // Reset data fields
        customerNameField.clear();
        customerContactField.clear();
        appointmentTitleField.clear();
        appointmentTypeBox.getSelectionModel().clearSelection();
        appointmentLocationBox.getSelectionModel().clearSelection();
        appointmentUrlField.clear();
        appointmentDatePicker.setValue(null);
        appointmentStartBox.getSelectionModel().clearSelection();
        appointmentEndBox.getSelectionModel().clearSelection();
        appointmentDescriptionArea.clear();
        customerNameField.setDisable(false);
        customerTable.setDisable(false);
        selectCustomerButton.setDisable(false);
    }
    
    
    @FXML
    private void handleResetAction(ActionEvent event) throws IOException {
        customerNameField.clear();
        customerContactField.clear();
        appointmentTitleField.clear();
        appointmentTypeBox.getSelectionModel().clearSelection();
        appointmentLocationBox.getSelectionModel().clearSelection();
        appointmentUrlField.clear();
        appointmentDatePicker.setValue(null);
        appointmentStartBox.getSelectionModel().clearSelection();
        appointmentEndBox.getSelectionModel().clearSelection();
        appointmentDescriptionArea.clear();
    }
}
