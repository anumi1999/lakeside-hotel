package com.demoproject.lakesidehotel.service;

import com.demoproject.lakesidehotel.model.Room;
import com.demoproject.lakesidehotel.repository.RoomRepository;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.math.BigDecimal;
import java.sql.Blob;

public class RoomServiceImpl implements RoomService {
    private RoomRepository roomRepository;

    @Override
    public Room addRoom(MultipartFile photo, String roomType, BigDecimal roomPrice){
        Room room = new Room();
        room.setRoomType(roomType);
        room.setRoomPrice(roomPrice);
        if(!photo.isEmpty()) {
            byte[] photoBytes = photo.getBytes();
            Blob photoBlob = new SerialBlob(photoBytes);
            room.setPhoto(photoBlob);
        }
        return room;
    }
}
