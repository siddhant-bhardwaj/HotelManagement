package model;

import java.util.*;
import java.io.Serializable;

import model.RoomType;

public class StandardRoom extends Room implements Serializable{
    private static final long serialVersionUID = 1L;
 
    public StandardRoom(int roomNumber, RoomType type) {
        super(roomNumber, type);

        switch (type) {
        case SINGLE:
            pricePerDay = 1000;
            break;
        case DOUBLE:
            pricePerDay = 2000;
            break;
        case DELUXE:
            pricePerDay = 3000;
            break;
    }
    }

    


    @Override
    public  double calculateTariff(int days) {
        return pricePerDay * days;
    }
}
