package com.demoproject.lakesidehotel.service;

import com.demoproject.lakesidehotel.exception.InvalidBookingRequestException;
import com.demoproject.lakesidehotel.model.BookedRoom;
import com.demoproject.lakesidehotel.model.Room;
import com.demoproject.lakesidehotel.repository.BookedRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookedRoomServiceImpl implements BookedRoomService {

    private final BookedRoomRepository bookedRoomRepository;
    private final RoomService roomService;

    @Override
    public List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
        return bookedRoomRepository.findByRoomId(roomId);
    }

    @Override
    public void cancelBooking(Long bookingId) {
        bookedRoomRepository.deleteById(bookingId);
    }

    @Override
    public List<BookedRoom> getAllBookings() {
        return bookedRoomRepository.findAll();
    }

    @Override
    public BookedRoom findByBookingConfirmationCode(String code) {
        return bookedRoomRepository.findByBookingConfirmationCode(code);
    }

    @Override
    public String saveBooking(Long roomId, BookedRoom bookingRequest) {
        if (bookingRequest.getCheckoutDate().isBefore(bookingRequest.getCheckinDate())) {
            throw new InvalidBookingRequestException("Checkout date must be after checkin date");
        }
        Room room = roomService.getRoomById(roomId).get();
        List<BookedRoom> existingBookings = room.getBookings();
        Boolean roomIsAvailable = roomIsAvailable(bookingRequest, existingBookings);
        if (roomIsAvailable) {
            room.addBooking(bookingRequest);
            bookedRoomRepository.save(bookingRequest);
        }else{
            throw new InvalidBookingRequestException("Sorry, This room has already been booked");
        }
        return bookingRequest.getBookingConfirmationCode();
    }

    private Boolean roomIsAvailable(BookedRoom bookingRequest, List<BookedRoom> existingBookings) {
        return existingBookings.stream()
                .noneMatch(existingBooking ->
                        bookingRequest.getCheckinDate().equals(existingBooking.getCheckinDate())
                    || bookingRequest.getCheckoutDate().isBefore(existingBooking.getCheckoutDate())
                        || (bookingRequest.getCheckinDate().isAfter(existingBooking.getCheckinDate())
                        && bookingRequest.getCheckoutDate().isBefore(existingBooking.getCheckoutDate()))

                        || (bookingRequest.getCheckinDate().isBefore(existingBooking.getCheckinDate())
                        && (bookingRequest.getCheckoutDate().equals(existingBooking.getCheckoutDate())))

                        || (bookingRequest.getCheckinDate().isBefore(existingBooking.getCheckinDate())
                        &&  (bookingRequest.getCheckoutDate().isAfter(existingBooking.getCheckoutDate())))

                        || (bookingRequest.getCheckinDate().equals(existingBooking.getCheckoutDate())
                                && bookingRequest.getCheckoutDate().equals(existingBooking.getCheckinDate()))

                        || (bookingRequest.getCheckinDate().equals(existingBooking.getCheckoutDate())
                                && bookingRequest.getCheckoutDate().equals(bookingRequest.getCheckinDate()))


                );
    }
}
