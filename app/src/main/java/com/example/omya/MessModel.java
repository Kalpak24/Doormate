package com.example.omya;

public class MessModel {
    private String upl_mess_name;
    private String mess_type;
    private String upl_mess_location;
    private String upl_mess_price;
    private String upl_mess_image;
    private String upl_facility;
    private String upl_owner_name;
    private String upl_owner_contact;
    private String mess_close_time;

    private boolean checkBox1, checkBox2, checkBox3, checkBox4, checkBox5, checkBox6;

    // Getters and setters for checkbox states
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

//    public boolean isCheckBox4() {
//        return checkBox4;
//    }

//    public void setCheckBox4(boolean checkBox4) {
//        this.checkBox4 = checkBox4;
//    }
//
//    public boolean isCheckBox5() {
//        return checkBox5;
//    }
//
//    public void setCheckBox5(boolean checkBox5) {
//        this.checkBox5 = checkBox5;
//    }
//
//    public boolean isCheckBox6() {
//        return checkBox6;
//    }
//
//    public void setCheckBox6(boolean checkBox6) {
//        this.checkBox6 = checkBox6;
//    }

    public String getMess_key() {
        return mess_key;
    }

    public void setMess_key(String mess_key) {
        this.mess_key = mess_key;
    }

    private String mess_key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private String key;


    public String getMess_open_time() {
        return mess_open_time;
    }

    public void setMess_open_time(String mess_open_time) {
        this.mess_open_time = mess_open_time;
    }

    public String getMess_close_time() {
        return mess_close_time;
    }

    public void setMess_close_time(String mess_close_time) {
        this.mess_close_time = mess_close_time;
    }

    public String getUpl_owner_contact() {
        return upl_owner_contact;
    }

    public void setUpl_owner_contact(String upl_owner_contact) {
        this.upl_owner_contact = upl_owner_contact;
    }

    public String getUpl_owner_name() {
        return upl_owner_name;
    }

    public void setUpl_owner_name(String upl_owner_name) {
        this.upl_owner_name = upl_owner_name;
    }

    public String getUpl_facility() {
        return upl_facility;
    }

    public void setUpl_facility(String upl_facility) {
        this.upl_facility = upl_facility;
    }

    private String mess_open_time;

// No-argument constructor (required for Firebase)
public MessModel() {
}

// Parameterized constructor
public MessModel(String upl_mess_name, String mess_type, String upl_mess_location, String upl_mess_price, String upl_mess_image) {
    this.upl_mess_name = upl_mess_name;
    this.mess_type = mess_type;
    this.upl_mess_location = upl_mess_location;
    this.upl_mess_price = upl_mess_price;
    this.upl_mess_image = upl_mess_image;
}

// Getters and Setters
public String getUpl_mess_name() {
    return upl_mess_name;
}

public void setUpl_mess_name(String upl_mess_name) {
    this.upl_mess_name = upl_mess_name;
}

public String getMess_type() {
    return mess_type;
}

public void setMess_type(String mess_type) {
    this.mess_type = mess_type;
}

public String getUpl_mess_location() {
    return upl_mess_location;
}

public void setUpl_mess_location(String upl_mess_location) {
    this.upl_mess_location = upl_mess_location;
}

public String getUpl_mess_price() {
    return upl_mess_price;
}

public void setUpl_mess_price(String upl_mess_price) {
    this.upl_mess_price = upl_mess_price;
}

public String getUpl_mess_image() {
    return upl_mess_image;
}

public void setUpl_mess_image(String upl_mess_image) {
    this.upl_mess_image = upl_mess_image;
}
}
