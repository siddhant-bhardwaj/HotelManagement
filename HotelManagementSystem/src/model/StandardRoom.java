package model;

public class StandardRoom extends Room {

    public StandardRoom(int roomNumber, RoomType type) {
        super(roomNumber, type);
    }

    @Override
    public double calculateTariff() {
        return getType().getPrice();
    }
}
