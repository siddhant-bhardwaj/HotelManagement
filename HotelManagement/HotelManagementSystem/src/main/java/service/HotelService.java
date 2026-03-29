package service;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.*;

public class HotelService {

    /** Fixed location so loads work regardless of IDE working directory. */
    private static final Path DATA_DIR = Path.of(System.getProperty("user.home"), ".hotel_management_system");
    private static final Path DATA_FILE = DATA_DIR.resolve("hotel.dat");

    private ArrayList<Room> rooms = new ArrayList<>();
    private HashMap<Integer, Customer> bookings = new HashMap<>();
    private ArrayList<Customer> completedBookings = new ArrayList<>();


    public void addRoom(Room room) {
        rooms.add(room);
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public HashMap<Integer, Customer> getBookings() {
        return bookings;
    }

    // 🔥 BOOK WITH THREAD
    public boolean bookRoom(int roomNo, Customer customer) {

        for (Room r : rooms) {

            if (r.getRoomNumber() == roomNo && !r.getStatus().equals("MAINTENANCE")) {

                r.setStatus("CLEANING");
                bookings.put(roomNo, customer);
              new Thread(() -> {
    try {
        Thread.sleep(3000);

        synchronized (r) {
            if (r.getStatus().equals("CLEANING")) {
                r.setStatus("BOOKED");
                System.out.println("Room " + r.getRoomNumber() + " set to BOOKED");
            } else {
                System.out.println("Booking thread ignored due to status: " + r.getStatus());
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}).start();

                return true;
            }
        }

        return false;
    }

    public double checkout(int roomNo, int rating, String feedback) {

       Customer c = bookings.get(roomNo);
    Room r = getRoom(roomNo);

    if (c == null || r == null) return 0;

    double roomTotal = c.getDays() * r.getPricePerDay();
    double bill = roomTotal + c.getSpaCharges() + c.getFoodCharges();

c.setRating(rating);
c.setFeedback(feedback);
c.setBill(bill);

completedBookings.add(c);   // ✅ SAVE HISTORY

bookings.remove(roomNo);
r.setStatus("AVAILABLE");

    System.out.println("Bill: " + bill);

        return bill;
         
    }

    // 🔥 MAINTENANCE THREAD
    public boolean setMaintenance(int roomNo) {

        for (Room r : rooms) {
            if (r.getRoomNumber() != roomNo) {
                continue;
            }

            String status = r.getStatus();
            if (status.equals("MAINTENANCE")) {
                return false;
            }

            synchronized (r) {
                if (status.equals("BOOKED") || status.equals("CLEANING")) {
                    bookings.remove(roomNo);
                }
                r.setStatus("MAINTENANCE");
            }

            new Thread(() -> {
                try {
                    Thread.sleep(4000);
                    r.setStatus("AVAILABLE");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            return true;
        }

        return false;
    }

    


   public void saveData() {
    try {
        Files.createDirectories(DATA_DIR);
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(DATA_FILE.toFile()))) {

            oos.writeObject(rooms);
            oos.writeObject(bookings);
            oos.writeObject(completedBookings);
            System.out.println("Data saved to " + DATA_FILE.toAbsolutePath());
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}

@SuppressWarnings("unchecked")
public void loadData() {
    migrateLegacyDatIfPresent();
    if (!Files.isRegularFile(DATA_FILE)) {
        System.out.println("No previous data found at " + DATA_FILE.toAbsolutePath());
        return;
    }
    try (ObjectInputStream ois = new ObjectInputStream(
            new FileInputStream(DATA_FILE.toFile()))) {

        rooms = (ArrayList<Room>) ois.readObject();
        bookings = (HashMap<Integer, Customer>) ois.readObject();
        try {
            completedBookings = (ArrayList<Customer>) ois.readObject();
        } catch (EOFException e) {
            completedBookings = new ArrayList<>();
        }
        System.out.println("Data loaded from " + DATA_FILE.toAbsolutePath());

    } catch (FileNotFoundException e) {
        System.out.println("No previous data found");
    } catch (Exception e) {
        e.printStackTrace();
        backupCorruptDataFile();
        rooms = new ArrayList<>();
        bookings = new HashMap<>();
        completedBookings = new ArrayList<>();
        System.out.println("Started with empty data; see backup next to hotel.dat if you need to recover.");
    }
}

    /**
     * If an old {@code hotel.dat} exists in the process working directory only, copy it once
     * to the canonical path so existing projects keep their data.
     */
    private void migrateLegacyDatIfPresent() {
        Path legacy = Path.of("hotel.dat").toAbsolutePath().normalize();
        try {
            if (!Files.isRegularFile(legacy) || Files.isSameFile(legacy, DATA_FILE.normalize())) {
                return;
            }
            if (Files.isRegularFile(DATA_FILE)) {
                return;
            }
            Files.createDirectories(DATA_DIR);
            Files.copy(legacy, DATA_FILE, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Migrated legacy hotel.dat to " + DATA_FILE.toAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void backupCorruptDataFile() {
        try {
            if (!Files.isRegularFile(DATA_FILE)) {
                return;
            }
            String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            Path bak = DATA_DIR.resolve("hotel.dat.corrupt-" + stamp + ".bak");
            Files.copy(DATA_FILE, bak, StandardCopyOption.REPLACE_EXISTING);
            Files.deleteIfExists(DATA_FILE);
            System.out.println("Corrupt file backed up to " + bak.toAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

public void closeRoom(int roomNumber) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'closeRoom'");
}

public Room getRoom(int roomNumber) {
    for (Room r : rooms) {
        if (r.getRoomNumber() == roomNumber) {
            return r;
        }
    }
    return null;
}

public ArrayList<Customer> getCompletedBookings() {
    return completedBookings;
}

}