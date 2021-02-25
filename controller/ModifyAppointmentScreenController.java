/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import interfaces.LocalDateToInteger;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
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
import model.Alert;
import model.Appointment;
import utils.DBConnection;
import utils.DBQuery;
import utils.TimeUtil;

/**
 * FXML Controller class
 *
 * @author Max
 */
public class ModifyAppointmentScreenController implements Initializable {

    @FXML 
    private TextField appointmentIdField;
    @FXML 
    private Button selectAppointmentButton;
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
    private TableView<Appointment> appointmentTable;
    @FXML 
    private TableColumn<Appointment, String> appointmentIdColumn;
    @FXML 
    private TableColumn<Appointment, String> customerNameColumn;
    @FXML 
    private TableColumn<Appointment, String> appointmentStartColumn;
    @FXML 
    private TableColumn<Appointment, String> appointmentEndColumn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        appointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        appointmentStartColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
        appointmentEndColumn.setCellValueFactory(new PropertyValueFactory<>("end"));
        appointmentTable.setItems(DBQuery.getAppointments());
        appointmentTypeBox.setItems(FXCollections.observableArrayList("Onsite", "Remote"));
        appointmentLocationBox.setItems(DBQuery.getCities());
        appointmentDescriptionArea.setWrapText(true);
        appointmentStartBox.setItems(Appointment.getAppointmentTimes());
        appointmentEndBox.setItems(Appointment.getAppointmentTimes());
        appointmentDatePicker.setValue(LocalDate.now());
    }    

    @FXML private void selectAppointmentAction (ActionEvent event) throws IOException, SQLException {
        try {
            Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
            String appointmentId = selectedAppointment.getId();
            Connection conn = DBConnection.startConnection();
            ResultSet resultSet = conn.createStatement().executeQuery(String.format("SELECT customerName, contact, title, type, location, url, start, end, description " 
                    + "FROM customer c1 INNER JOIN appointment a1 ON c1.customerId = a1.customerId "
                    + "WHERE appointmentId = '%s';",
                    appointmentId));
            while (resultSet.next()) {
                Appointment selectedApp = new Appointment();
                selectedApp.setId(appointmentId);
                selectedApp.setName(resultSet.getString("customerName"));
                selectedApp.setContact(resultSet.getString("contact"));
                selectedApp.setTitle(resultSet.getString("title"));
                selectedApp.setType(resultSet.getString("type"));
                selectedApp.setLocation(resultSet.getString("location"));
                selectedApp.setUrl(resultSet.getString("url"));
                selectedApp.setStart(resultSet.getString("start"));
                selectedApp.setEnd(resultSet.getString("end"));
                selectedApp.setDescription(resultSet.getString("description"));
                // Populate appointment data
                appointmentTable.setDisable(true);
                selectAppointmentButton.setDisable(true);
                customerNameField.setText(selectedApp.getName()); 
                customerNameField.setDisable(true);
                customerContactField.setText(selectedApp.getContact()); 
                appointmentIdField.setText(selectedApp.getId());
                appointmentIdField.setDisable(true);
                appointmentTitleField.setText(selectedApp.getTitle()); 
                appointmentTypeBox.setValue(selectedApp.getType()); 
                appointmentLocationBox.setValue(selectedApp.getLocation()); 
                appointmentDescriptionArea.setText(selectedApp.getDescription()); 
                // Convert UTC times to LocalTime
                String start = resultSet.getString("start");
                String end = resultSet.getString("end");
                LocalDateTime ldtStart = TimeUtil.convertToLDT(start);
                LocalDateTime ldtEnd = TimeUtil.convertToLDT(end);
                System.out.println(ldtStart);
                System.out.println(ldtEnd);
            }
        }
        catch(Exception e) {
            System.out.println("selectAppointmentAction Error: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleBackAction(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/view/CalendarScreen.fxml"));
        Scene scene = new Scene(parent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }
    
    @FXML
    private void handleUpdateAppointmentAction(ActionEvent event) throws IOException, SQLException {
        try {
            // Get the appointment id
            Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
            String appointmentId = selectedAppointment.getId();
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
                // Update appointment in database
                DBQuery.updateAppointment(
                        appointmentId,
                        appointmentTitle, 
                        appointmentDescription, 
                        appointmentLocation, 
                        customerContact, 
                        appointmentType, 
                        appointmentUrl, 
                        appointmentStart, 
                        appointmentEnd,
                        appointmentDate);
            }
            else {
                if (dayInt != 6 || dayInt != 7){
                    Alert.alertWindow("Business Days", "Unable to schedule appointment: Please schedule within normal business days (Monday - Friday).");
                }
                if (TimeUtil.checkBusinessHours(appointmentDate, appointmentStart, appointmentEnd) == false) {
                    Alert.alertWindow("Business Hours", "Unable to schedule appointment: Please schedule within normal business hours (Mon - Fri\t 09:00 - 17:00).");
                }
                if (TimeUtil.checkOverlap(appointmentDate, appointmentStart, appointmentEnd) == false) {
                    Alert.alertWindow("Overlapping Appointment", "Unable to schedule appointment: Appointment time conflicts with another.");
                }
            }
            appointmentTable.setDisable(false);
            selectAppointmentButton.setDisable(false);
            customerNameField.setDisable(false);
            appointmentIdField.setDisable(false);
        }
        catch(Exception e) {
            System.out.println("UpdateAppointmentAction Error: " + e.getMessage());
            Alert.alertWindow("Incomplete Data", "Required Fields: Customer, Title, Type, Location, Date, Start, End, Description");
        }
    }
    
    @FXML
    private void handleDeleteAppointmentAction(ActionEvent event) throws IOException {
        // Get the appointment id
        Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
        String appointmentId = selectedAppointment.getId();
        try {
            // Delete the customer
            DBQuery.deleteAppointment(appointmentId);
            // Reload the Update Customer screen
            Parent parent = FXMLLoader.load(getClass().getResource("/view/ModifyAppointmentScreen.fxml"));
            Scene scene = new Scene(parent);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        }
        catch(IOException e) {
            System.out.println("deleteAppointment Error: " + e.getMessage());
            Alert.alertWindow("Delete failed", "Could not delete appointment, please try again.");
        }
    }
    
    public static ZonedDateTime parseZoneDateTime(String timestamp) {
        // Convert timestamp string to LocalDateTime object
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss.S");
        LocalDateTime dateTime = LocalDateTime.parse(timestamp, formatter); 
        ZoneId zid = ZoneId.systemDefault();
        ZonedDateTime zdt = dateTime.atZone(zid);
        ZonedDateTime newzdt = zdt.toLocalDateTime().atZone(ZoneId.of("UTC"));
        ZonedDateTime newldt = newzdt.withZoneSameInstant(zid);
        return newldt;
    }
}
