package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import model.*;
import service.*;

/*Improvments: Lets try making access to room booking and creating new rooms

    

    Add Rooms:
    Lets try to create number of people (maybe family plan etc)
    Lets also add benifits for each room type (complimentory)
    Limit the room Numbers (maybe like till 1030 (30 rooms each floor))


    Book Room:
    For company vs for vacation/stay => for company then take in company name and number of employees
    Nationality/Country
    Language preferences
    Age of everyone
    Pets then dog/cat/other 
    Id: license etc
    For Contact lets do the dropdown box for each country code
    Assuming more than 1 person we need names of everyone else
    Any allegies etc (additional)
    Meal plan (veg/non-veg/jain and breakfast, dinner, lunch)

    Number of days and nights staying (use the calender ui)

    Also ensure that everything has a boundary condition

    View Rooms:
    Price per day + Number of days residing etc
    Show rooms button down

    Add a delete room feature incase the room is under repairs etc

    In checkout add a rating system (maybe the 5 stars one) and like feedback




*/

public class HotelApp extends Application {

    HotelService service = new HotelService();
    TextArea display = new TextArea();

    @Override
    public void start(Stage stage) {

        TabPane tabs = new TabPane();

        tabs.getTabs().add(addRoomTab());
        tabs.getTabs().add(viewRoomTab());
        tabs.getTabs().add(bookRoomTab());
        tabs.getTabs().add(checkoutTab());

        Scene scene = new Scene(tabs, 600, 400);

        stage.setTitle("Hotel Management System");
        stage.setScene(scene);
        stage.show();
    }

    private Tab addRoomTab() {

        TextField roomField = new TextField();
        ComboBox<RoomType> typeBox = new ComboBox<>();

        typeBox.getItems().addAll(RoomType.values());

        Button addBtn = new Button("Add Room");

        addBtn.setOnAction(e -> {

            int roomNo = Integer.parseInt(roomField.getText());
            RoomType type = typeBox.getValue();

            service.addRoom(new StandardRoom(roomNo, type));

            display.appendText("Room Added\n");
        });

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);

        grid.add(new Label("Room Number"), 0, 0);
        grid.add(roomField, 1, 0);

        grid.add(new Label("Room Type"), 0, 1);
        grid.add(typeBox, 1, 1);

        grid.add(addBtn, 1, 2);

        return new Tab("Add Room", grid);
    }

    private Tab viewRoomTab() {

        Button show = new Button("Show Rooms");

        show.setOnAction(e -> {

            display.clear();

            for (Room r : service.getRooms()) {

                display.appendText(
                        "Room: " + r.getRoomNumber() +
                                " | Type:" + r.getType() +
                                " | Price:" + r.calculateTariff() +
                                " | Booked:" + r.isBooked() + "\n");
            }

        });

        VBox box = new VBox(10, show, display);

        return new Tab("View Rooms", box);
    }

    private Tab bookRoomTab() {

        TextField roomField = new TextField();
        TextField name = new TextField();
        TextField contact = new TextField();

        Button book = new Button("Book Room");

        book.setOnAction(e -> {

            boolean status = service.bookRoom(
                    Integer.parseInt(roomField.getText()),
                    name.getText(),
                    contact.getText());

            if (status)
                display.appendText("Room booked\n");
            else
                display.appendText("Room unavailable\n");
        });

        GridPane grid = new GridPane();

        grid.setVgap(10);
        grid.setHgap(10);

        grid.add(new Label("Room Number"), 0, 0);
        grid.add(roomField, 1, 0);

        grid.add(new Label("Customer Name"), 0, 1);
        grid.add(name, 1, 1);

        grid.add(new Label("Contact"), 0, 2);
        grid.add(contact, 1, 2);

        grid.add(book, 1, 3);

        return new Tab("Book Room", grid);
    }

    private Tab checkoutTab() {

        TextField room = new TextField();

        Button checkout = new Button("Checkout");

        checkout.setOnAction(e -> {

            boolean status = service.checkout(
                    Integer.parseInt(room.getText()));

            if (status)
                display.appendText("Checkout successful\n");
            else
                display.appendText("Room not booked\n");
        });

        VBox box = new VBox(10,
                new Label("Room Number"),
                room,
                checkout);

        return new Tab("Checkout", box);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
