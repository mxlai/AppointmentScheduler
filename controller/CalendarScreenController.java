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
import java.time.temporal.WeekFields;
import java.util.Locale;
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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Appointment;
import utils.DBQuery;
import utils.DBConnection;
import utils.TimeUtil;

/**
 * FXML Controller class
 *
 * @author Max
 */
public class CalendarScreenController implements Initializable {
    
    @FXML 
    private DatePicker appointmentDatePicker;
    @FXML 
    private ChoiceBox viewByBox;
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
    
    static ObservableList<Appointment> appointments = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        appointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        appointmentStartColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
        appointmentEndColumn.setCellValueFactory(new PropertyValueFactory<>("end"));
        appointmentDatePicker.setValue(LocalDate.now());
        viewByBox.setItems(FXCollections.observableArrayList("All", "Monthly", "Weekly"));
    }    
    
    @FXML
    public void handleAddCustomerAction(ActionEvent event) throws SQLException, IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/view/AddCustomerScreen.fxml"));
        Scene scene = new Scene(parent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }
    
    @FXML
    public void handleUpdateCustomerAction(ActionEvent event) throws SQLException, IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/view/ModifyCustomerScreen.fxml"));
        Scene scene = new Scene(parent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }
    
    @FXML
    public void handleAddAppointmentAction(ActionEvent event) throws SQLException, IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/view/AddAppointmentScreen.fxml"));
        Scene scene = new Scene(parent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }
    
    @FXML
    public void handleUpdateAppointmentAction(ActionEvent event) throws SQLException, IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/view/ModifyAppointmentScreen.fxml"));
        Scene scene = new Scene(parent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }
    
    @FXML
    public void handleViewReportsAction(ActionEvent event) throws SQLException, IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/view/GenerateReportsScreen.fxml"));
        Scene scene = new Scene(parent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }
    
    @FXML
    public void handleRefreshViewAction(ActionEvent event) throws SQLException, IOException {
        String viewType = viewByBox.getValue().toString();
        appointments.removeAll(appointments);
        refreshView(viewType);
    }
    
    public void refreshView(String viewType) throws SQLException {
        LocalDate appointmentDate = appointmentDatePicker.getValue();
        String appointmentYear = appointmentDate.toString().substring(0,4);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Connection conn = DBConnection.startConnection();
        if (viewType == "All") {
            appointmentTable.setItems(DBQuery.getAppointments());
        }
        else if (viewType == "Monthly") {
            String appointmentMonth = appointmentDate.toString().substring(5,7);
            ResultSet resultSet = conn.createStatement().executeQuery(String.format("SELECT appointmentId, customerName, start, end " 
                    + "FROM customer c1 INNER JOIN appointment a1 ON c1.customerId = a1.customerId " 
                    + "WHERE MONTH(start) = '%s' AND YEAR(start) = '%s' "
                    + "ORDER BY start;",
                    appointmentMonth, appointmentYear));
            while (resultSet.next()) {
                appointments.add(new Appointment(resultSet.getString("appointmentId"), 
                resultSet.getString("customerName"), 
                TimeUtil.convertToLDT(resultSet.getString("start")).format(formatter),
                TimeUtil.convertToLDT(resultSet.getString("end")).format(formatter)));
            }
            appointmentTable.setItems(appointments);
        }
        else if (viewType == "Weekly") {
            // Get local definition of week
            Locale locale = Locale.US;
            int weekOfYear = appointmentDate.get(WeekFields.of(locale).weekOfWeekBasedYear());
            String appointmentWeek = Integer.toString(weekOfYear);
            ResultSet resultSet = conn.createStatement().executeQuery(String.format("SELECT appointmentId, customerName, start, end " 
                    + "FROM customer c1 INNER JOIN appointment a1 ON c1.customerId = a1.customerId " 
                    + "WHERE WEEK(DATE(start))+1 = '%s' AND YEAR(start) = '%s' "
                    + "ORDER BY start;",
                    appointmentWeek, appointmentYear));
            while (resultSet.next()) {
                appointments.add(new Appointment(resultSet.getString("appointmentId"), 
                resultSet.getString("customerName"), 
                TimeUtil.convertToLDT(resultSet.getString("start")).format(formatter),
                TimeUtil.convertToLDT(resultSet.getString("end")).format(formatter)));
            }
            appointmentTable.setItems(appointments);
        }
    }
}
