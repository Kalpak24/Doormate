package com.example.omya;

public class RoomModel1 {
    public String room_name, room_price, room_location, room_type, room_facility, room_contact, room_ow_name, room_open_time, room_close_time, room_image;
    public boolean checkbox1, checkbox2, checkbox3, checkbox4, checkbox5, checkbox6;

    public RoomModel1() {} // Required for Firebase

    public RoomModel1(String room_name, String room_price, String room_location, String room_type,
                     String room_facility, String room_contact, String room_ow_name,
                     String room_open_time, String room_close_time, String room_image,
                     boolean checkbox1, boolean checkbox2, boolean checkbox3,
                     boolean checkbox4, boolean checkbox5, boolean checkbox6) {
        this.room_name = room_name;
        this.room_price = room_price;
        this.room_location = room_location;
        this.room_type = room_type;
        this.room_facility = room_facility;
        this.room_contact = room_contact;
        this.room_ow_name = room_ow_name;
        this.room_open_time = room_open_time;
        this.room_close_time = room_close_time;
        this.room_image = room_image;
        this.checkbox1 = checkbox1;
        this.checkbox2 = checkbox2;
        this.checkbox3 = checkbox3;
        this.checkbox4 = checkbox4;
        this.checkbox5 = checkbox5;
        this.checkbox6 = checkbox6;
    }
}
