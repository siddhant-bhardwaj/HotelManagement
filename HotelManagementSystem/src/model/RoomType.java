package model;

public enum RoomType {

    SINGLE(2000),
    DOUBLE(3000),
    DELUXE(5000);

    private int price;

    RoomType(int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }
}