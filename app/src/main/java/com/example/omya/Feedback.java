package com.example.omya;

public class Feedback {
    private String id;
    private String userName;
    private String mobileNumber;
    private String feedbackText;

    // Empty constructor needed for Firebase
    public Feedback() {
    }

    public Feedback(String userName, String mobileNumber, String feedbackText) {
        this.userName = userName;
        this.mobileNumber = mobileNumber;
        this.feedbackText = feedbackText;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getFeedbackText() {
        return feedbackText;
    }

    public void setFeedbackText(String feedbackText) {
        this.feedbackText = feedbackText;
    }
}