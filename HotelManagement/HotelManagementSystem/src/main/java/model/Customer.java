package model;

import java.io.Serializable;

public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String contact;
    private int roomNumber;
    private int days;

    private String nationality;
    private boolean hasPets;

    // 🔥 NEW
    private int rating;
    private String feedback;

    public Customer(String name, String contact, int roomNumber, int days,
                    String nationality, boolean hasPets) {

        this.name = name;
        this.contact = contact;
        this.roomNumber = roomNumber;
        this.days = days;
        this.nationality = nationality;
        this.hasPets = hasPets;
    }

    // GETTERS
    public String getName() { return name; }
    public String getContact() { return contact; }
    public int getRoomNumber() { return roomNumber; }
    public int getDays() { return days; }
    public String getNationality() { return nationality; }
    public boolean hasPets() { return hasPets; }

    // 🔥 NEW METHODS
    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public int getRating() { return rating; }
    public String getFeedback() { return feedback; }

    private double bill;

    /** Extra charges at checkout (spa, food). */
    private double spaCharges;
    private double foodCharges;

    public double getSpaCharges() {
        return spaCharges;
    }

    public void setSpaCharges(double spaCharges) {
        this.spaCharges = Math.max(0, spaCharges);
    }

    public double getFoodCharges() {
        return foodCharges;
    }

    public void setFoodCharges(double foodCharges) {
        this.foodCharges = Math.max(0, foodCharges);
    }

public double getBill() {
    return bill;
}

public void setBill(double bill) {
    this.bill = bill;
}

}