package com.restaurant.BeefChefBackend.service;

import com.restaurant.BeefChefBackend.dto.request.ShiftCreateRequest;
import com.restaurant.BeefChefBackend.dto.request.ShiftUpdateRequest;
import com.restaurant.BeefChefBackend.dto.response.ShiftResponse;
import com.restaurant.BeefChefBackend.entity.Products;
import com.restaurant.BeefChefBackend.entity.Shift;
import com.restaurant.BeefChefBackend.repository.ShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
public class ShiftService {
    @Autowired
    private ShiftRepository shiftRepository;

    public ShiftResponse toResponse(Shift shift){
        return ShiftResponse.builder()
                .shiftId(shift.getShiftId())
                .shiftName(shift.getShiftName())
                .startTime(shift.getStartTime())
                .endTime(shift.getEndTime())
                .build();
    }

    //create shift
    public ShiftResponse createShift(ShiftCreateRequest request){
        Shift shift = new Shift();
        shift.setShiftName(request.getShiftName());
        shift.setStartTime(request.getStartTime());
        shift.setEndTime(request.getEndTime());
        Shift save = shiftRepository.save(shift);
        return toResponse(save);
    }

    //getShift
    public Shift getShiftById(Integer id){
        return shiftRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Shift not found!")
        );
    }

    //update Shift
    public ShiftResponse updateShift(Integer id, ShiftUpdateRequest request){
        Shift shift = getShiftById(id);
        shift.setShiftName(request.getShiftName());
        shift.setStartTime(request.getStartTime());
        shift.setEndTime(request.getEndTime());

        Shift save = shiftRepository.save(shift);
        return toResponse(save);
    }

    //Get all shift
    public List<ShiftResponse> getShifts(){
        return shiftRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    //Delete Shift
    public void deleteShift(Integer id){
        shiftRepository.deleteById(id);
    }

    //Tim ca cho order
    public Shift getCurrentShift(){
        LocalTime now = LocalTime.now();
        return shiftRepository.findCurrentShift(now).orElseThrow(
                () -> new RuntimeException("Không tìm thấy ca phù hợp"));
    }

}
