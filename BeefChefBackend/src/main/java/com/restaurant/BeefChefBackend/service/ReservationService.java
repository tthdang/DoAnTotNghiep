package com.restaurant.BeefChefBackend.service;

import com.restaurant.BeefChefBackend.dto.request.ReservationRequest;
import com.restaurant.BeefChefBackend.dto.request.ReservationUpdateStatusRequest;
import com.restaurant.BeefChefBackend.dto.response.ReservationResponse;
import com.restaurant.BeefChefBackend.dto.response.TableResponse;
import com.restaurant.BeefChefBackend.entity.Reservation;
import com.restaurant.BeefChefBackend.entity.Tables;
import com.restaurant.BeefChefBackend.enums.ReservationStatus;
import com.restaurant.BeefChefBackend.repository.ReservationRepository;
import com.restaurant.BeefChefBackend.repository.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private TableRepository tableRepository;

    public ReservationResponse toResponse(Reservation r) {
        return ReservationResponse.builder()
                .reservationId(r.getReservationId())
                .customerName(r.getCustomerName())
                .customerPhone(r.getCustomerPhone())
                .date(r.getDate())
                .numberOfPeople(r.getNumberOfPeople())
                .note(r.getNote())
                .status(r.getStatus().name())
                .tableId(r.getTables().getTableId())
                .tableName(r.getTables().getTableName())
                .tableCapacity(r.getTables().getTableCapacity())
                .build();
    }

    public ReservationResponse create(ReservationRequest request) {
        LocalDateTime reservationTime = request.getDate();

        if (request.getNumberOfPeople() <= 0) {
            throw new RuntimeException("Số khách không hợp lệ");
        }

        LocalDateTime start = reservationTime.minusHours(2);
        LocalDateTime end = reservationTime.plusHours(2);

        Tables table;

        //Nếu user chọn bàn
        if (request.getTableId() != null) {
            table = tableRepository.findById(request.getTableId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bàn"));

            List<Reservation> conflicts =
                    reservationRepository.findByTablesAndDateBetween(table, start, end);

            if (!conflicts.isEmpty()) {
                throw new RuntimeException("Bàn đã được đặt trong khung giờ này!");
            }

        } else {
            //Auto chọn bàn KHÔNG bị trùng
            List<Tables> tables = tableRepository
                    .findByTableCapacityGreaterThanEqualOrderByTableCapacityAsc(request.getNumberOfPeople());

            table = null;

            for (Tables t : tables) {
                List<Reservation> conflicts =
                        reservationRepository.findByTablesAndDateBetween(t, start, end);

                if (conflicts.isEmpty()) {
                    table = t;
                    break;
                }
            }

            if (table == null) {
                throw new RuntimeException("Không còn bàn trống!");
            }
        }

        Reservation reservation = Reservation.builder()
                .customerName(request.getCustomerName())
                .customerPhone(request.getCustomerPhone())
                .date(reservationTime)
                .numberOfPeople(request.getNumberOfPeople())
                .note(request.getNote())
                .tables(table)
                .status(ReservationStatus.PENDING)
                .build();

        Reservation saved = reservationRepository.save(reservation);

        return toResponse(saved);
    }

    public List<Integer> getUnavailableTableIds(LocalDateTime dateTime) {

        if (dateTime == null) {
            throw new RuntimeException("Thời gian không hợp lệ");
        }

        LocalDateTime start = dateTime.minusHours(2);
        LocalDateTime end = dateTime.plusHours(2);

        List<Reservation> reservations =
                reservationRepository.findByDateBetween(start, end);

        return reservations.stream()
                .map(r -> r.getTables().getTableId())
                .distinct()
                .toList();
    }

    public List<ReservationResponse> getAll(){
        return reservationRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ReservationResponse updateStatus(Integer id, ReservationUpdateStatusRequest request){
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn"));

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new RuntimeException("Đơn đã bị huỷ!");
        }

        if (request.getStatus() == ReservationStatus.CONFIRMED) {

            LocalDateTime time = reservation.getDate();
            LocalDateTime start = time.minusHours(2);
            LocalDateTime end = time.plusHours(2);

            List<Reservation> conflicts = reservationRepository.findByTablesAndDateBetween(
                            reservation.getTables(),
                            start,
                            end
                    );

            boolean hasConflict = conflicts.stream()
                    .anyMatch(r ->
                            !r.getReservationId().equals(id) &&
                                    r.getStatus() == ReservationStatus.CONFIRMED
                    );

            if (hasConflict) {
                throw new RuntimeException("Bàn đã có người khác được xác nhận!");
            }
        }

        reservation.setStatus(request.getStatus());

        return toResponse(reservationRepository.save(reservation));
    }
}
