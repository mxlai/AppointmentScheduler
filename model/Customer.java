/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Max
 */
public class Customer {
    StringProperty id;
    StringProperty name;
    StringProperty address1;
    StringProperty address2;
    StringProperty city;
    StringProperty zip;
    StringProperty country;
    StringProperty phone;
    
    public Customer() {
        id = new SimpleStringProperty();
        name = new SimpleStringProperty();
        address1 = new SimpleStringProperty();
        address2 = new SimpleStringProperty();
        city = new SimpleStringProperty();
        zip = new SimpleStringProperty();
        country = new SimpleStringProperty();
        phone = new SimpleStringProperty();
    }
    
    public Customer(String name) {
        this.name = new SimpleStringProperty(name);
    }
    
    // Getters
    public String getId() {
        return id.get();
    }

    public String getName() {
        return name.get();
    }
    
     public String getAddress1() {
        return address1.get();
    }
     
     public String getAddress2() {
        return address2.get();
    }
     
     public String getCity() {
        return city.get();
    }
     
     public String getZip() {
        return zip.get();
    }
     
     public String getCountry() {
        return country.get();
    }
     
     public String getPhone() {
        return phone.get();
    }
    
    // Setters
    public void setId(String id) {
        this.id.set(id);
    }

    public void setName(String name) {
        this.name.set(name);
    }
    
    public void setAddress1(String address1) {
        this.address1.set(address1);
    }
    
    public void setAddress2(String address2) {
        this.address2.set(address2);
    }
    
    public void setCity(String city) {
        this.city.set(city);
    }
    
    public void setZip(String zip) {
        this.zip.set(zip);
    }
    
    public void setCountry(String country) {
        this.country.set(country);
    }
    
    public void setPhone(String phone) {
        this.phone.set(phone);
    }
    
    // Validate the Customer
    public static String isValid(String name, String address1, String address2, String city, String zip, String phone, String exceptionMessage){        
        if(name.isEmpty()) {
            exceptionMessage = "Please enter a customer name.";
        }
        if(address1.isEmpty()) {
            exceptionMessage = "Please enter a customer address.";
        }
        if(city.isEmpty()) {
            exceptionMessage = "Please enter a customer city.";
        }
        if(zip.isEmpty()) {
            exceptionMessage = "Please enter a customer zip code.";
        }
        if(phone.isEmpty()) {
            exceptionMessage = "Please enter a customer phone number.";
        }
        return exceptionMessage;
    }
}
