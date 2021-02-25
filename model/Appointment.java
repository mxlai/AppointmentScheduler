/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Max
 */
public class Appointment {
    StringProperty id;
    StringProperty name;
    StringProperty title;
    StringProperty description;
    StringProperty location;
    StringProperty contact;
    StringProperty type;
    StringProperty url;
    StringProperty start;
    StringProperty end;
    
    public Appointment() {
        id = new SimpleStringProperty();
        name = new SimpleStringProperty();
        title = new SimpleStringProperty();
        description = new SimpleStringProperty();
        location = new SimpleStringProperty();
        contact = new SimpleStringProperty();
        type = new SimpleStringProperty();
        url = new SimpleStringProperty();
        start = new SimpleStringProperty();
        end = new SimpleStringProperty();
    }
    
    static ObservableList<String> appointmentTimes = FXCollections.observableArrayList();

    public Appointment(String id, String name, String start, String end) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.start = new SimpleStringProperty(start);
        this.end = new SimpleStringProperty(end);
    }
    
    // Getters
    public String getId() {
        return id.get();
    }
    
    public String getName() {
        return name.get();
    }
    
    public String getTitle() {
        return title.get();
    }
    
    public String getDescription() {
        return description.get();
    }
    
    public String getLocation() {
        return location.get();
    }
    
    public String getContact() {
        return contact.get();
    }
    
    public String getType() {
        return type.get();
    }
    
    public String getUrl() {
        return url.get();
    }
    
    public String getStart() {
        return start.get();
    }
    
    public String getEnd() {
        return end.get();
    }
    
    // Setters
    public void setId(String id) {
        this.id.set(id);
    }
    
    public void setName(String name) {
        this.name.set(name);
    }
    
    
    public void setTitle(String title) {
        this.title.set(title);
    }
    
    public void setDescription(String description) {
        this.description.set(description);
    }
    
    public void setLocation(String location) {
        this.location.set(location);
    }
    
    public void setContact(String contact) {
        this.contact.set(contact);
    }
    
    public void setType(String type) {
        this.type.set(type);
    }
    
    public void setUrl(String url) {
        this.url.set(url);
    }
    
    public void setStart(String start) {
        this.start.set(start);
    }
    
    public void setEnd(String end) {
        this.end.set(end);
    }
    
    public static ObservableList<String> getAppointmentTimes() {
        appointmentTimes.removeAll(appointmentTimes);
        try {
            for (int i = 0; i < 24; i++ ) {
                String hour;
                hour = Integer.toString(i);
                if (i < 10) {
                    appointmentTimes.add("0" + hour + ":00:00");
                    appointmentTimes.add("0" + hour + ":30:00");
                }
                else {
                    appointmentTimes.add(hour + ":00:00");
                    appointmentTimes.add(hour + ":30:00");
                }
            }
        }
        catch(Exception e) {
            System.out.println("getAppointmentTimes Error: " + e.getMessage());
        }
        return appointmentTimes;
    }
}
