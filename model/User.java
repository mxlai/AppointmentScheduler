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
public class User {
    private static String name;
    private static String id;
    private static boolean active;

    public User(String name, String id, boolean active) {
        this.name = name;
        this.id = id;
        this.active = active;
    }

    public static void setName(String name) {
        User.name = name;
    }

    public static void setId(String id) {
        User.id = id;
    }

    public static void setActive(boolean active) {
        User.active = active;
    }

    public static String getName() {
        return name;
    }

    public static String getId() {
        return id;
    }   
}
