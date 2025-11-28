package com.example.omya;

public class RoomModel {
    private String upl_room_name;
    private String room_type;
    private String upl_room_location;
    private String upl_room_price;
    private String upl_room_image;
    private String key;
    private String RoomKey;
    private String room_key;

    public String getRoom_key() {
        return room_key;
    }

    public void setRoom_key(String room_key) {
        this.room_key = room_key;
    }

        private boolean checkBox1, checkBox2, checkBox3, checkBox4, checkBox5, checkBox6;

    public boolean isCheckBox1() {
        return checkBox1;
    }

    public void setCheckBox1(boolean checkBox1) {
        this.checkBox1 = checkBox1;
    }

    public boolean isCheckBox2() {
        return checkBox2;
    }

    public void setCheckBox2(boolean checkBox2) {
        this.checkBox2 = checkBox2;
    }

    public boolean isCheckBox3() {
        return checkBox3;
    }

    public void setCheckBox3(boolean checkBox3) {
        this.checkBox3 = checkBox3;
    }

    public boolean isCheckBox4() {
        return checkBox4;
    }

    public void setCheckBox4(boolean checkBox4) {
        this.checkBox4 = checkBox4;
    }

    public boolean isCheckBox5() {
        return checkBox5;
    }

    public void setCheckBox5(boolean checkBox5) {
        this.checkBox5 = checkBox5;
    }

    public boolean isCheckBox6() {
        return checkBox6;
    }

    public void setCheckBox6(boolean checkBox6) {
        this.checkBox6 = checkBox6;
    }

    // Getters and setters for checkbox states
    public String getRoomKey() {
        return RoomKey;
    }

    public void setRoomKey(String roomKey) {
        RoomKey = roomKey;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUpl_facility() {
        return upl_facility;
    }

    public void setUpl_facility(String upl_facility) {
        this.upl_facility = upl_facility;
    }

    public String getUpl_owner_name() {
        return upl_owner_name;
    }

    public void setUpl_owner_name(String upl_owner_name) {
        this.upl_owner_name = upl_owner_name;
    }

    public String getUpl_owner_contact() {
        return upl_owner_contact;
    }

    public void setUpl_owner_contact(String upl_owner_contact) {
        this.upl_owner_contact = upl_owner_contact;
    }

    public String getRoom_open_time() {
        return room_open_time;
    }

    public void setRoom_open_time(String room_open_time) {
        this.room_open_time = room_open_time;
    }

    public String getRoom_close_time() {
        return room_close_time;
    }

    public void setRoom_close_time(String room_close_time) {
        this.room_close_time = room_close_time;
    }

    private String upl_facility;
    private String upl_owner_name;
    private String upl_owner_contact;
    private String room_open_time;
    private String room_close_time;

    // No-argument constructor (required for Firebase)
    public RoomModel() {
    }

    // Parameterized constructor
    public RoomModel(String upl_room_name, String room_type, String upl_room_location, String upl_room_price, String upl_room_image) {
        this.upl_room_name = upl_room_name;
        this.room_type = room_type;
        this.upl_room_location = upl_room_location;
        this.upl_room_price = upl_room_price;
        this.upl_room_image = upl_room_image;
    }

    // Getters and Setters
    public String getUpl_room_name() {
        return upl_room_name;
    }

    public void setUpl_room_name(String upl_room_name) {
        this.upl_room_name = upl_room_name;
    }

    public String getRoom_type() {
        return room_type;
    }

    public void setRoom_type(String room_type) {
        this.room_type = room_type;
    }

    public String getUpl_room_location() {
        return upl_room_location;
    }

    public void setUpl_room_location(String upl_room_location) {
        this.upl_room_location = upl_room_location;
    }

    public String getUpl_room_price() {
        return upl_room_price;
    }

    public void setUpl_room_price(String upl_room_price) {
        this.upl_room_price = upl_room_price;
    }

    public String getUpl_room_image() {
        return upl_room_image;
    }

    public void setUpl_room_image(String upl_room_image) {
        this.upl_room_image = upl_room_image;
    }
}
