package com.example.omya;

public class RoomData {
    private String roomImage, roomPrice, roomLocation, roomType, roomFacility, roomContact,
            roomOwnerName, roomOpenTime, roomCloseTime, roomName;
    private boolean checkbox1, checkbox2, checkbox3, checkbox4, checkbox5, checkbox6;

    public RoomData() {
        // Default constructor required for Firebase
    }

    public RoomData(String roomImage, String roomPrice, String roomLocation, String roomType,
                    String roomFacility, String roomContact, String roomOwnerName,
                    String roomOpenTime, String roomCloseTime, String roomName,
                    boolean checkbox1, boolean checkbox2, boolean checkbox3,
                    boolean checkbox4, boolean checkbox5, boolean checkbox6) {
        this.roomImage = roomImage;
        this.roomPrice = roomPrice;
        this.roomLocation = roomLocation;
        this.roomType = roomType;
        this.roomFacility = roomFacility;
        this.roomContact = roomContact;
        this.roomOwnerName = roomOwnerName;
        this.roomOpenTime = roomOpenTime;
        this.roomCloseTime = roomCloseTime;
        this.roomName = roomName;
        this.checkbox1 = checkbox1;
        this.checkbox2 = checkbox2;
        this.checkbox3 = checkbox3;
        this.checkbox4 = checkbox4;
        this.checkbox5 = checkbox5;
        this.checkbox6 = checkbox6;
    }

    // Getters and Setters
    public String getRoomImage() { return roomImage; }
    public String getRoomPrice() { return roomPrice; }
    public String getRoomLocation() { return roomLocation; }
    public String getRoomType() { return roomType; }
    public String getRoomFacility() { return roomFacility; }
    public String getRoomContact() { return roomContact; }
    public String getRoomOwnerName() { return roomOwnerName; }
    public String getRoomOpenTime() { return roomOpenTime; }
    public String getRoomCloseTime() { return roomCloseTime; }
    public String getRoomName() { return roomName; }
    public boolean isCheckbox1() { return checkbox1; }
    public boolean isCheckbox2() { return checkbox2; }
    public boolean isCheckbox3() { return checkbox3; }
    public boolean isCheckbox4() { return checkbox4; }
    public boolean isCheckbox5() { return checkbox5; }
    public boolean isCheckbox6() { return checkbox6; }
}
