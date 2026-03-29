package model;

import java.io.Serializable;

public abstract class Room implements Serializable {

    private static final long serialVersionUID = 1L;
    private int roomNumber;
    private RoomType type;
    private String status;

    public Room(int roomNumber, RoomType type) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.status = "AVAILABLE";
    }

    public int getRoomNumber() { return roomNumber; }
    public RoomType getType() { return type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    protected double pricePerDay;

    public double getPricePerDay() {
        return pricePerDay;
    }

    public abstract double calculateTariff(int days);
}