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
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import utils.DBConnection;

/**
 * FXML Controller class
 *
 * @author Max
 */
public class GenerateReportsScreenController implements Initializable {

    @FXML 
    private ChoiceBox reportTypeBox;
    @FXML 
    private TextArea reportTextArea;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        reportTypeBox.setItems(FXCollections.observableArrayList("Appointment Type", "Consultant", "Location"));
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
    private void handleGenerateReportAction(ActionEvent event) throws IOException, SQLException {
        String reportType = reportTypeBox.getValue().toString();
        reportTextArea.clear();
        generateReport(reportType);
    }
    
    public void generateReport(String reportType) throws SQLException {
        Connection conn = DBConnection.startConnection();
        StringBuilder reportText = new StringBuilder();
        // number of appointment types by month
        if (reportType == "Appointment Type") {
            reportText.append("Report: Appointment Types by Month\n\n");
            reportText.append("Month\t" + "Number\t" + "Type\n" + "--------------------------------------------------------------\n\n");
            ResultSet resultSet = conn.createStatement().executeQuery(String.format("SELECT MONTHNAME(start) as month, type, COUNT(*) as number " 
                    + "FROM appointment "
                    + "GROUP BY MONTHNAME(start), type;"));
            while (resultSet.next()) {
                String month = resultSet.getString("month");
                String type = resultSet.getString("type");
                String number = resultSet.getString("number");               
                reportText.append(month + ":\t" + number + "\t" + type + "\n");
            };
        }
        // the schedule for each consultant
        else if (reportType == "Consultant") {
            reportText.append("Report: Consultant Schedule\n\n");
            reportText.append("Consultant\t" + "Start\t\t" + "End\t\t" + "Details\n" + "--------------------------------------------------------------\n\n");
            ResultSet resultSet = conn.createStatement().executeQuery(String.format("SELECT userName, start, end, title, type, location "
                    + "FROM appointment a1 "
                    + "INNER JOIN user u1 "
                    + "ON a1.userId = u1.userId "
                    + "ORDER BY userName;"));
            while (resultSet.next()) {
                String userName = resultSet.getString("userName");
                String start = resultSet.getString("start");
                String end = resultSet.getString("end");
                String title = resultSet.getString("title");
                String type = resultSet.getString("type");
                String location = resultSet.getString("location");
                reportText.append(userName + "\t\t" + start + "\t\t" + end + "\t\t" + title + "\t\t" + type + "\t\t" + location + "\n");
            };
        }
        // appointments by location
        else if (reportType == "Location") {
            reportText.append("Report: Appointments by Location\n\n");
            reportText.append("Location\t\t" + "Number\n" + "--------------------------------------------------------------\n\n");
             ResultSet resultSet = conn.createStatement().executeQuery(String.format("SELECT location, COUNT(*) as number "
                    + "FROM appointment "
                    + "GROUP BY location;"));
            while (resultSet.next()) {
                String location = resultSet.getString("location");
                String number = resultSet.getString("number");
                reportText.append(location + "\t\t" + number + "\n");
            };
        }
        reportTextArea.setText(reportText.toString());
    }
}
