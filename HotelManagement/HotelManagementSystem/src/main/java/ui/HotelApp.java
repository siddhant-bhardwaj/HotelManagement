package ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.*;
import service.HotelService;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.time.temporal.ChronoUnit;

public class HotelApp extends Application {

    HotelService service = new HotelService();
    GridPane roomGrid = new GridPane();

    @Override
    public void start(Stage stage) {
        service.loadData();
        updateRoomGrid();
        TabPane tabs = new TabPane();

  

        try{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/addRoom.fxml"));
        Parent root = loader.load();

        AddRoomController controller = loader.getController();
        controller.setService(service);

        tabs.getTabs().add(new Tab("Add Room", root));

        tabs.getTabs().add(roomGridTab());
        tabs.getTabs().add(bookRoomTab());
        
    }   

    catch(Exception e){
        System.out.println("FXML Load Failed, loading default UI");
        tabs.getTabs().add(addRoomTab());
    }

        Scene scene = new Scene(tabs, 750, 500);
        scene.getStylesheets().add(
    getClass().getResource("/style.css").toExternalForm()
);

        stage.setTitle("Hotel Management System");
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> {
            service.saveData();
            Platform.exit();
        });
        stage.show();

        startAutoRefresh();
    }

    // 🔧 ADD ROOM
    private Tab addRoomTab() {

        TextField roomField = new TextField();
        ComboBox<RoomType> typeBox = new ComboBox<>();
        Label message = new Label();

        typeBox.getItems().addAll(RoomType.values());

        Button addBtn = new Button("Add Room");

        addBtn.setOnAction(e -> {
            try {
                int roomNo = Integer.parseInt(roomField.getText());
                service.addRoom(new StandardRoom(roomNo, typeBox.getValue()));
                message.setText("Room Added");
                updateRoomGrid();
            } catch (Exception ex) {
                message.setText("Invalid input");
            }
        });

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);

        grid.add(new Label("Room Number"), 0, 0);
        grid.add(roomField, 1, 0);

        grid.add(new Label("Room Type"), 0, 1);
        grid.add(typeBox, 1, 1);

        grid.add(addBtn, 1, 2);
        grid.add(message, 1, 3);

        return new Tab("Add Room", grid);
    }

    // 🔥 ROOM DASHBOARD
    private Tab roomGridTab() {

        roomGrid.setVgap(10);
        roomGrid.setHgap(10);

        ScrollPane scroll = new ScrollPane(roomGrid);
        return new Tab("Rooms Dashboard", scroll);
    }

    // 🔥 UPDATE GRID
    private void updateRoomGrid() {

        roomGrid.getChildren().clear();

        int col = 0;
        int row = 0;

        for (Room r : service.getRooms()) {

           VBox box = new VBox(5);
box.setPrefSize(120, 80);

// ✅ CLEAR old styles (THIS FIXES YOUR BUG)
box.getStyleClass().clear();

// ✅ Add base style
box.getStyleClass().add("room-box");

// ✅ Add correct status style
switch (r.getStatus()) {
    case "AVAILABLE":
        box.getStyleClass().add("available");
        break;
    case "BOOKED":
        box.getStyleClass().add("booked");
        break;
    case "MAINTENANCE":
        box.getStyleClass().add("maintenance");
        break;
}

            Label roomLabel = new Label("Room " + r.getRoomNumber());
            Label statusLabel = new Label(r.getStatus());

            box.getChildren().addAll(roomLabel, statusLabel);

            // 🔥 CLICK POPUP
            box.setOnMouseClicked(e -> showRoomDetails(r));

            // 🔥 TOOLTIP
            Customer c = service.getBookings().get(r.getRoomNumber());

            String tooltipText;

            if (c != null) {
                tooltipText =
                        "Customer: " + c.getName() + "\n" +
                        "Days: " + c.getDays() + "\n" +
                        "Nationality: " + c.getNationality() + "\n" +
                        "Pets: " + (c.hasPets() ? "Yes" : "No");
            } else {
                tooltipText = "Room Available";
            }

            Tooltip.install(box, new Tooltip(tooltipText));

            roomGrid.add(box, col, row);

            col++;
            if (col == 5) {
                col = 0;
                row++;
            }
        }
    }

    // 🔥 POPUP WITH RATING
    private void showRoomDetails(Room r) {

        Customer c = service.getBookings().get(r.getRoomNumber());

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Room Details");

        VBox content = new VBox(10);

        Label info = new Label(
                "Room: " + r.getRoomNumber() +
                "\nStatus: " + r.getStatus()
        );

        content.getChildren().add(info);

        HBox stars = new HBox(5);

Label s1 = new Label("☆");
Label s2 = new Label("☆");
Label s3 = new Label("☆");
Label s4 = new Label("☆");
Label s5 = new Label("☆");

Label[] starArr = {s1, s2, s3, s4, s5};

final int[] rating = {0};

for (int i = 0; i < 5; i++) {
    int index = i;

    starArr[i].setStyle("-fx-font-size: 24; -fx-cursor: hand;");

    starArr[i].setOnMouseClicked(e -> {
        rating[0] = index + 1;
        for (int j = 0; j < 5; j++) {
            if (j <= index) {
                starArr[j].setText("★");
                starArr[j].setStyle("-fx-text-fill: gold; -fx-font-size: 24;");
                
            } else {
                starArr[j].setText("☆");
                starArr[j].setStyle("-fx-text-fill: grey; -fx-font-size: 24;");
                
            }
        }
    });
}

stars.getChildren().addAll(starArr);

        TextArea feedbackArea = new TextArea();
        feedbackArea.setPromptText("Enter feedback...");

        TextField spaField = new TextField();
        spaField.setPromptText("0");
        TextField foodField = new TextField();
        foodField.setPromptText("0");

        if (c != null) {
            spaField.setText(c.getSpaCharges() > 0 ? String.valueOf(c.getSpaCharges()) : "");
            foodField.setText(c.getFoodCharges() > 0 ? String.valueOf(c.getFoodCharges()) : "");

            HBox spaRow = new HBox(8, new Label("Spa (₹):"), spaField);
            HBox foodRow = new HBox(8, new Label("Food (₹):"), foodField);
            spaRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            foodRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            content.getChildren().addAll(
                    new Label("Customer: " + c.getName()),
                    new Label("Contact: " + c.getContact()),
                    new Label("Days: " + c.getDays()),
                    new Label("Extra charges (added at checkout):"),
                    spaRow,
                    foodRow,
                    new Label("Give Rating & Feedback:"),
                    stars,
                    feedbackArea
            );
        }

        dialog.getDialogPane().setContent(content);

        ButtonType checkoutBtn = new ButtonType("Checkout", ButtonBar.ButtonData.OK_DONE);
        ButtonType maintenanceBtn = new ButtonType("Maintenance", ButtonBar.ButtonData.OTHER);
        ButtonType closeBtn = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().setAll(checkoutBtn, maintenanceBtn, closeBtn);

        

       

        dialog.showAndWait().ifPresent(button -> {

    if (button.equals(checkoutBtn) && c != null) {

        /*int rating = ratingBox.getValue() == null ? 0 : ratingBox.getValue();
        String feedback = feedbackArea.getText();

        service.checkout(r.getRoomNumber(), rating, feedback);*/

       int finalRating = rating[0];
       if (finalRating == 0) {
        Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a rating");
        alert.showAndWait();
        return;
        }
        String feedback = feedbackArea.getText();

        c.setSpaCharges(parseAmount(spaField.getText()));
        c.setFoodCharges(parseAmount(foodField.getText()));

        double bill = service.checkout(r.getRoomNumber(), finalRating, feedback);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Billing");
        alert.setHeaderText("Checkout Successful");
        alert.setContentText("Total Bill: ₹" + bill);

        alert.showAndWait();

    }

   else if (button.getButtonData() == ButtonBar.ButtonData.OTHER) {
        boolean ok = service.setMaintenance(r.getRoomNumber());
        Alert maintAlert = new Alert(
                ok ? Alert.AlertType.INFORMATION : Alert.AlertType.WARNING,
                ok
                        ? "Room " + r.getRoomNumber() + " is now in maintenance and will return to available shortly."
                        : "Maintenance was not applied. The room may already be in maintenance.");
        maintAlert.setHeaderText(ok ? "Maintenance set" : "Maintenance unavailable");
        maintAlert.showAndWait();
    }

    updateRoomGrid();
});
    }

    private static double parseAmount(String raw) {
        if (raw == null || raw.isBlank()) {
            return 0;
        }
        try {
            return Double.parseDouble(raw.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // 🎨 COLOR
  private String getColor(String status) {

    switch (status) {
        case "AVAILABLE":
            return "-fx-background-color: lightgreen;";
        case "BOOKED":
            return "-fx-background-color: lightcoral;";
        case "MAINTENANCE":
            return "-fx-background-color: orange;";
        case "CLEANING":
            return "-fx-background-color: yellow;";
        case "REPAIRING":
            return "-fx-background-color: gold;";
        default:
            return "-fx-background-color: gray;";
    }
}

    // 🔄 AUTO REFRESH
    private void startAutoRefresh() {

        Thread t = new Thread(() -> {
        while (true) {
            try {
                Thread.sleep(2000);
                Platform.runLater(this::updateRoomGrid);
            }
            catch (Exception e) {
            e.printStackTrace();
            }
        }
        });
    t.setDaemon(true);   // 🔥 IMPORTANT
    t.start();
    }

    // 🔧 BOOK ROOM
    private Tab bookRoomTab() {

        TextField roomField = new TextField();
        TextField name = new TextField();
        TextField contact = new TextField();
        TextField nationality = new TextField();

        CheckBox pets = new CheckBox("Has Pets");

        DatePicker checkIn = new DatePicker();
        DatePicker checkOut = new DatePicker();

        Label message = new Label();

        Button book = new Button("Book Room");

        book.setOnAction(e -> {

            try {
                long days = ChronoUnit.DAYS.between(checkIn.getValue(), checkOut.getValue());

                if (days <= 0) {
                    days = 1; // minimum stay = 1 day
                }

                Customer customer = new Customer(
                        name.getText(),
                        contact.getText(),
                        Integer.parseInt(roomField.getText()),
                        (int) days,
                        nationality.getText(),
                        pets.isSelected()
                );

                boolean status = service.bookRoom(
                        Integer.parseInt(roomField.getText()),
                        customer
                );

                message.setText(status ? "Booked" : "Unavailable");

                updateRoomGrid();

            } catch (Exception ex) {
                message.setText("Error");
            }
        });

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);

        grid.add(new Label("Room Number"), 0, 0);
        grid.add(roomField, 1, 0);

        grid.add(new Label("Name"), 0, 1);
        grid.add(name, 1, 1);

        grid.add(new Label("Contact"), 0, 2);
        grid.add(contact, 1, 2);

        grid.add(new Label("Nationality"), 0, 3);
        grid.add(nationality, 1, 3);

        grid.add(pets, 1, 4);

        grid.add(new Label("Check-In"), 0, 5);
        grid.add(checkIn, 1, 5);

        grid.add(new Label("Check-Out"), 0, 6);
        grid.add(checkOut, 1, 6);

        grid.add(book, 1, 7);
        grid.add(message, 1, 8);

        return new Tab("Book Room", grid);
    }

    // 🔧 MAINTENANCE TAB
    private Tab maintenanceTab() {

        TextField roomField = new TextField();
        Label message = new Label();

        Button btn = new Button("Set Maintenance");

        btn.setOnAction(e -> {
            boolean status = service.setMaintenance(Integer.parseInt(roomField.getText()));
            message.setText(status ? "Maintenance Set" : "Failed");
            updateRoomGrid();
        });

        return new Tab("Maintenance",
                new VBox(10, new Label("Room Number"), roomField, btn, message));
    }

    public static void main(String[] args) {
        launch(args);
    }
}