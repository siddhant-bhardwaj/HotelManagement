package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.*;
import service.HotelService;

public class AddRoomController {

    @FXML private TextField roomField;
    @FXML private ComboBox<RoomType> typeBox;

    private HotelService service;

    public void setService(HotelService service) {
        this.service = service;
        typeBox.getItems().addAll(RoomType.values());
    }

    @FXML
    public void handleAddRoom() {
        int roomNo = Integer.parseInt(roomField.getText());
        service.addRoom(new StandardRoom(roomNo, typeBox.getValue()));
    }

   

}
