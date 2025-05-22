package com.demoproject.lakesidehotel.service;

import com.demoproject.lakesidehotel.model.BookedRoom;

import java.util.List;

public interface BookedRoomService {
    List<BookedRoom> getAllBookingsByRoomId(Long roomId);
}
