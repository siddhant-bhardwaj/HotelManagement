package service;

import model.*;

import java.util.ArrayList;
import java.util.HashMap;

public class HotelService {

    private ArrayList<Room> rooms = new ArrayList<>();
    private HashMap<Integer, Customer> bookings = new HashMap<>();

    public void addRoom(Room room) {
        rooms.add(room);
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public boolean bookRoom(int roomNo, String name, String contact) {

        for (Room r : rooms) {

            if (r.getRoomNumber() == roomNo && !r.isBooked()) {

                r.setBooked(true);

                Customer c = new Customer(name, contact, roomNo);
                bookings.put(roomNo, c);

                return true;
            }
        }

        return false;
    }

    public boolean checkout(int roomNo) {

        for (Room r : rooms) {

            if (r.getRoomNumber() == roomNo && r.isBooked()) {

                r.setBooked(false);
                bookings.remove(roomNo);

                return true;
            }
        }

        return false;
    }

    public HashMap<Integer, Customer> getBookings() {
        return bookings;
    }
}
