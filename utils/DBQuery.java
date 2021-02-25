/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Alert;
import model.Customer;
import model.Appointment;
import model.User;
import static utils.DBConnection.conn;

/**
 *
 * @author Max
 */
public class DBQuery {  

    static ObservableList<String> cities = FXCollections.observableArrayList();
    static ObservableList<Customer> customerNames = FXCollections.observableArrayList();
    static ObservableList<Appointment> appointments = FXCollections.observableArrayList();
    static Customer customer;
    static Appointment appointment;
    static String cityId;
    static String addressId;
    static String userId;
    static String customerId;
    static String appointmentId;

    // loginQuery   
    private static final String loginQuery = "SELECT * FROM user WHERE userName = ? and password = ?";
    
    public boolean validateLogin(String username, String password) throws SQLException 
    {
        try
        {
            Connection conn = DBConnection.startConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(loginQuery);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            
            System.out.println(preparedStatement);
            
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                return true;
            }
        }
        catch(SQLException e) {
            System.out.println("Login Error: " + e.getMessage());
        }
        return false;
    }
    
    // Insert customer into customer table
    public static void addCustomer(String name, String address1, String address2, String city, String zip, String phone) throws SQLException {
        try {        
            // Insert new customer address
            conn.createStatement().executeUpdate(String.format("INSERT INTO address " 
                    + "(address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy) " 
                    + "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')", 
                    address1, address2, getCityId(city), zip, phone, LocalDateTime.now(), User.getName(), LocalDateTime.now(), User.getName()));
            // Insert new customer
            conn.createStatement().executeUpdate(String.format("INSERT INTO customer " 
                    + "(customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy) " 
                    + "VALUES ('%s', '%s', 1, '%s', '%s', '%s', '%s')", 
                    name, getAddressId(address1, address2, getCityId(city), zip), LocalDateTime.now(), User.getName(), LocalDateTime.now(), User.getName()));
            Alert.alertWindow("Confirmation", "Customer added.");
        }
        catch(SQLException e) {
            System.out.println("addCustomer Error: " + e.getMessage());
            Alert.alertWindow("Error adding customer", "Could not add new customer, please try again.");
        }
    }
    
    // Update existing customer in customer table
    public static void updateCustomer(String customerId, String name, String address1, String address2, String city, String zip, String phone) {
         try {                    
            // Update customer
            conn.createStatement().executeUpdate(String.format("UPDATE customer " 
                    + "SET customerName = '%s', lastUpdate = '%s', lastUpdateBy = '%s' " 
                    + "WHERE customerId = '%s'", 
                    name, LocalDateTime.now(), User.getName(), customerId));
            // Get the addressId       
            ResultSet resultAddressId = conn.createStatement().executeQuery(String.format("SELECT addressId FROM customer WHERE customerId = '%s'", customerId));
            while(resultAddressId.next()) {
                addressId = resultAddressId.getString("addressId");
            };
            // Update customer address
            conn.createStatement().executeUpdate(String.format("UPDATE address " 
                    + "SET address = '%s', address2 = '%s', cityId = '%s', postalCode = '%s', phone = '%s', lastUpdate = '%s', lastUpdateBy = '%s' " 
                    + "WHERE addressId = '%s'", 
                    address1, address2, getCityId(city), zip, phone, LocalDateTime.now(), User.getName(), addressId));
            
            Alert.alertWindow("Confirmation", "Customer updated.");
        }
        catch(Exception e) {
            System.out.println("updateCustomer Error: " + e.getMessage());
            Alert.alertWindow("Update failed", "Could not update customer, please try again.");
        }
    }
    
    // Delete existing customer in customer table
    public static void deleteCustomer(String customerId) {
        try {       
            // Get the addressId       
            ResultSet resultAddressId = conn.createStatement().executeQuery(String.format("SELECT addressId FROM customer WHERE customerId = '%s'", customerId));
            while(resultAddressId.next()) {
                addressId = resultAddressId.getString("addressId");
            };
            // Delete the customerId
            conn.createStatement().executeUpdate(String.format("DELETE FROM customer " 
                    + "WHERE customerId = '%s'", 
                    customerId));
            // Delete the addressId
            conn.createStatement().executeUpdate(String.format("DELETE FROM address " 
                    + "WHERE addressId = '%s'", 
                    addressId));        
            Alert.alertWindow("Confirmation", "Customer deleted.");
        }
        catch(Exception e) {
            System.out.println("deleteCustomer Error: " + e.getMessage());
        }
    }
    
    // Select city names from city table
    public static ObservableList<String> getCities() {
        try {
            cities.removeAll(cities);
            ResultSet resultSet = conn.createStatement().executeQuery("SELECT city FROM city;");
            while(resultSet.next()) {
                cities.add(resultSet.getString("city"));
            }
        } 
        catch(SQLException e) {
            System.out.println("getCities Error: " + e.getMessage());
        }
        return cities;
    }
    
    // Select customer names from customer table
    public static ObservableList<Customer> getCustomerNames() {
        try {
            customerNames.removeAll(customerNames);
            ResultSet resultSet = conn.createStatement().executeQuery("SELECT customerName FROM customer;");
            while (resultSet.next()) {
                customerNames.add(new Customer(resultSet.getString("customerName")));
            }
        } 
        catch (SQLException e) {
            System.out.println("getCustomerNames Error: " + e.getMessage());
        }
        return customerNames;
    }
        
    // Select cityId from city table
    public static String getCityId(String city) throws SQLException {
        try {
            ResultSet resultSet = conn.createStatement().executeQuery(String.format("SELECT cityId FROM city WHERE city = '%s'", city));
            while(resultSet.next()) {
                cityId = resultSet.getString("cityId");
            }
        }
        catch(SQLException e) {
            System.out.println("getCityId Error: " + e.getMessage());
        }
        return cityId;
    }
    
    // Select addressId from address table
    public static String getAddressId(String address1, String address2, String cityId, String zip) throws SQLException {
        try {
            ResultSet resultSet = conn.createStatement().executeQuery(String.format("SELECT addressId FROM address WHERE address='%s' AND address2='%s' AND cityId='%s' AND postalCode='%s'", 
                    address1, address2, cityId, zip));
            while(resultSet.next()) {
                addressId = resultSet.getString("addressId");
            }
        }
        catch(SQLException e) {
            System.out.println("getAddressId Error: " + e.getMessage());
        }
        return addressId;
     }
    
    // Select customerId from customer table
    public static String getCustomerId(String customerName) throws SQLException {
        try {
            ResultSet resultSet = conn.createStatement().executeQuery(String.format("SELECT customerId FROM customer WHERE customerName='%s'", customerName));
            while(resultSet.next()) {
                customerId = resultSet.getString("customerId");
            }
        }
        catch(SQLException e) {
            System.out.println("getCustomerId Error: " + e.getMessage());
        }
        return customerId;
     }
    
    // Insert appointment into appointment table
    public static void addAppointment(String customerName, String username, String title, String description, String location, String contact, String type, String url, String start, String end, String date) throws SQLException {
        try {   
            // Get customer id
            String customerId = getCustomerId(customerName);
            // Insert new appointment
            conn.createStatement().executeUpdate(String.format("INSERT INTO appointment " 
                    + "(customerId, userId, title, description, location, contact, type, url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy) " 
                    + "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')", 
                    customerId, User.getId(), title, description, location, contact, type, url, TimeUtil.convertToUTC(date, start), TimeUtil.convertToUTC(date, end), LocalDateTime.now(), User.getId(), LocalDateTime.now(), User.getId()));
            Alert.alertWindow("Confirmation", "Appointment added.");
        }
        catch(SQLException e) {
            System.out.println("addAppointment Error: " + e.getMessage());
            Alert.alertWindow("Error adding appointment", "Could not add new appointment, please try again.");
        }
    }
    
    // Update appointment in appointment table
    public static void updateAppointment(String appointmentId, String title, String description, String location, String contact, String type, String url, String start, String end, String date) throws SQLException {
        try {   
            ResultSet resultSet = conn.createStatement().executeQuery(String.format("SELECT * FROM appointment WHERE appointmentId = '%s';", 
                    appointmentId));
            while(resultSet.next()) {
                appointmentId = resultSet.getString("appointmentId");
                customerId = resultSet.getString("customerId");
            }
            // Update appointment
            conn.createStatement().executeUpdate(String.format("UPDATE appointment " 
                    + "SET customerId = '%s', userId = '%s', title = '%s', description = '%s', location = '%s', contact = '%s', type = '%s', url = '%s', start = '%s', end = '%s', lastUpdate = '%s', lastUpdateBy = '%s' " 
                    + "WHERE appointmentId = '%s'", 
                    customerId, User.getId(), title, description, location, contact, type, url, TimeUtil.convertToUTC(date, start), TimeUtil.convertToUTC(date, end), LocalDateTime.now(), User.getId(), appointmentId));
            Alert.alertWindow("Confirmation", "Appointment updated.");
        }
        catch(SQLException e) {
            System.out.println("updateAppointment Error: " + e.getMessage());
            Alert.alertWindow("Error updating appointment", "Could not update appointment, please try again.");
        }
    }
    
    // Delete existing appointment in appointment table
    public static void deleteAppointment(String appointmentId) {
        try {      
            conn.createStatement().executeUpdate(String.format("DELETE FROM appointment " 
                    + "WHERE appointmentId = '%s'", 
                    appointmentId));      
            Alert.alertWindow("Confirmation", "Appointment deleted.");
        }
        catch(Exception e) {
            System.out.println("deleteAppointment Error: " + e.getMessage());
        }
    }
    
    // Select appointment data from appointment table
    public static ObservableList<Appointment> getAppointments() {
        try {
            appointments.removeAll(appointments);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            ResultSet resultSet = conn.createStatement().executeQuery("SELECT appointmentId, customerName, start, end " 
                    + "FROM customer c1 INNER JOIN appointment a1 ON c1.customerId = a1.customerId " 
                    + "ORDER BY start;");
            while (resultSet.next()) {                
                appointments.add(new Appointment(resultSet.getString("appointmentId"), 
                        resultSet.getString("customerName"),
                        TimeUtil.convertToLDT(resultSet.getString("start")).format(formatter),
                        TimeUtil.convertToLDT(resultSet.getString("end")).format(formatter)));
            }
        } 
        catch (SQLException e) {
            System.out.println("getAppointments Error: " + e.getMessage());
        }
        return appointments;
    }
}
