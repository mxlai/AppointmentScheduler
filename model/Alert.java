/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author Max
 */
public class Alert {
    public static void alertWindow(String alertTitle, String alertMessage) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(alertTitle);
        alert.setContentText(alertMessage);
        alert.show();        
    }
}
