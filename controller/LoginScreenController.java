/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import utils.DBQuery;
import model.User;
import utils.DBConnection;

/**
 *
 * @author Max
 */
public class LoginScreenController implements Initializable {
    @FXML
    private Label appTitleLabel;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    
    private String loginFailedTitle;
    private String loginFailedMessage;
        
    @FXML
    public void handleLoginAction(ActionEvent event) throws SQLException, IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();
        // Get consultant data
        Connection conn = DBConnection.startConnection();
        ResultSet resultSet = conn.createStatement().executeQuery(String.format("SELECT userId, active " 
                + "FROM user "
                + "WHERE userName = '%s';",
                username));
        while (resultSet.next()) {
            User.setName(username);
            User.setId(resultSet.getString("userId"));
        }
        // Validate login
        DBQuery dbQuery = new DBQuery();
        boolean flag = dbQuery.validateLogin(username, password);
        if (!flag) {
            System.out.println(loginFailedTitle);
            alertWindow(loginFailedTitle, loginFailedMessage);
        } 
        else {
            // Create log.txt
            String filename = "src/ScheduleApp/log.txt";
            FileWriter filewriter = new FileWriter(filename, true);
            PrintWriter printwriter = new PrintWriter(filewriter);
            printwriter.println(User.getName() + " logged in: " + LocalDateTime.now()); 
            printwriter.close();
            //Check for upcoming appointments
            appointmentAlert();
            // Load Calendar screen
            Parent parent = FXMLLoader.load(getClass().getResource("/view/CalendarScreen.fxml"));
            Scene scene = new Scene(parent);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        }        
    }
    
    public static void alertWindow(String alertTitle, String alertMessage) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(alertTitle);
        alert.setContentText(alertMessage);
        alert.show();        
    }
    
    public static void appointmentAlert() {
        try {
            Connection conn = DBConnection.startConnection();
            ResultSet resultSet = conn.createStatement().executeQuery(String.format("SELECT customerName "
                    + "FROM customer c1 INNER JOIN appointment a1 ON c1.customerId=a1.customerId "
                    + "INNER JOIN user u1 ON a1.userId=u1.userId "
                    + "WHERE a1.userId='%s' AND a1.start "
                    + "BETWEEN '%s' AND '%s';",
                    User.getId(), LocalDateTime.now(ZoneId.of("UTC")), LocalDateTime.now(ZoneId.of("UTC")).plusMinutes(15)));
            while (resultSet.next()) {
                String customerName = resultSet.getString("customerName");
                alertWindow("Upcoming Appointment", "You have an upcoming appointment with " + customerName);
            }
        } 
        catch (SQLException e) {
            System.out.println("No upcoming appointments.");
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        rb = ResourceBundle.getBundle("ScheduleApp/Nat", Locale.getDefault());
        if (Locale.getDefault().getLanguage().equals("de") || Locale.getDefault().getLanguage().equals("en"))
            appTitleLabel.setText(rb.getString("title"));
            usernameField.setPromptText(rb.getString("username"));
            passwordField.setPromptText(rb.getString("password"));
            loginButton.setText(rb.getString("login"));
            loginFailedTitle = rb.getString("loginFailedTitle");
            loginFailedMessage = rb.getString("loginFailedMessage");
    }    
}
