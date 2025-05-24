package com.demoproject.lakesidehotel.controller;

import com.demoproject.lakesidehotel.exception.InvalidBookingRequestException;
import com.demoproject.lakesidehotel.exception.ResourceNotFoundException;
import com.demoproject.lakesidehotel.model.BookedRoom;
import com.demoproject.lakesidehotel.model.Room;
import com.demoproject.lakesidehotel.response.BookingResponse;
import com.demoproject.lakesidehotel.response.RoomResponse;
import com.demoproject.lakesidehotel.service.BookedRoomService;
import com.demoproject.lakesidehotel.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@CrossOrigin("http://localhost:5173")
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookedRoomController {

    private final BookedRoomService bookingService;
    private final RoomService roomService;

    @GetMapping("all-bookings")
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        List <BookedRoom> booking = bookingService.getAllBookings();
        List<BookingResponse> bookingResponses = new ArrayList<>();
        for (BookedRoom bookingRoom : booking) {
            BookingResponse bookingResponse = getBookingResponse(bookingRoom);
            bookingResponses.add(bookingResponse);
        }
        return ResponseEntity.ok(bookingResponses);
    }

    @GetMapping("/confirmation/{code}")
    public ResponseEntity<?> getBookingByConfirmationCode(@PathVariable String code) {
        try {
            BookedRoom booking = bookingService.findByBookingConfirmationCode(code);
            BookingResponse bookingResponse = getBookingResponse(booking);
            return ResponseEntity.ok(bookingResponse);
        }catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/room/{roomId}/booking")
    public ResponseEntity<?> saveBooking(@PathVariable Long roomId,
                                         @RequestBody BookedRoom bookingRequest){
        try{
            String confirmationCode = bookingService.saveBooking(roomId, bookingRequest);
            return ResponseEntity.ok("Room booked successfully ! Your confirmation code is: " + confirmationCode);
        }catch ( InvalidBookingRequestException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/booking/{bookingId}/delete")
    public void cancelBooking(@PathVariable Long bookingId){
        bookingService.cancelBooking(bookingId);
    }

    private BookingResponse getBookingResponse(BookedRoom booking) {
        Room room = roomService.getRoomById(booking.getRoom().getId()).get()
        RoomResponse roomResponse = new RoomResponse(
                room.getId(),
                room.getRoomType(),
                room.getRoomPrice());
        return new BookingResponse(
                booking.getBookingId(), booking.getCheckinDate(),
                booking.getCheckoutDate(), booking.getGuestFullName(),
                booking.getGuestEmail(), booking.getNumberOfAdults(),
                booking.getNumberOfChildren(), booking.getTotalNumOfGuest(),
                booking.getBookingConfirmationCode(), roomResponse);

    }
}
