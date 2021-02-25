/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Max
 */
public class TimeUtil {
    
    public static LocalDateTime convertToLDT(String dateTime) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss.S");
        ZoneId zid = ZoneId.of("UTC");
        LocalDateTime ldt =  LocalDateTime.parse(dateTime, df);
        LocalDateTime newLdt = ldt.atZone(zid).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        return newLdt;
    }
    
    public static LocalDateTime convertToUTC(String date, String time) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss");
        ZoneId zid = ZoneId.systemDefault();
        String unparsedDateTime = date + " " + time;
        LocalDateTime ldt =  LocalDateTime.parse(unparsedDateTime, df);
        LocalDateTime utc = ldt.atZone(zid).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        return utc;
    }
    
    public static boolean checkBusinessHours(String date, String start, String end) {
        // Business hours 09:00-17:00
        LocalTime openTime = LocalTime.parse("08:59");
        LocalTime closedTime = LocalTime.parse("17:01");
        
        // Convert and parse times
        LocalDateTime ldtStart = convertToUTC(date, start);
        LocalDateTime ldtEnd = convertToUTC(date, end);
        String ldtStartString = ldtStart.toString().substring(11,16);
        String ldtEndString = ldtEnd.toString().substring(11,16);
        LocalTime ltStart = LocalTime.parse(ldtStartString);
        LocalTime ltEnd = LocalTime.parse(ldtEndString);
        
        // Check times and date
        if (ltStart.isAfter(openTime) && ltEnd.isBefore(closedTime)) {
            return true;
        }
        else {
            return false;
        }  
    }
    
    public static boolean checkOverlap(String date, String start, String end) {
        // Convert and parse times
        LocalDateTime ldtStart = convertToUTC(date, start);
        LocalDateTime ldtEnd = convertToUTC(date, end);
        String utcStart = ldtStart.toString();
        String utcEnd = ldtEnd.toString();
        
        try {
            Connection conn = DBConnection.startConnection();
            ResultSet resultSet = conn.createStatement().executeQuery(String.format("SELECT start, end, customerName "
                    + "FROM appointment a1 "
                    + "INNER JOIN customer c1 ON a1.customerId=c1.customerId " 
                    + "WHERE ('%s' >= start AND '%s' <= end) " 
                    + "OR ('%s' <= start AND '%s' >= start) " 
                    + "OR ('%s' <= start AND '%s' >= end) " 
                    + "OR ('%s' <= end AND '%s' >= end);",
                    utcStart, utcStart, utcStart, utcEnd, utcEnd, utcEnd, utcStart, utcEnd));
            while (resultSet.next()) {
                return false;
            }
        } 
        catch (SQLException e) {
            System.out.println("checkOverlap Exception: " + e.getMessage());
            return true;
        }
        return true;
    }
}
