package com.restaurant.BeefChefBackend.controller;

import com.restaurant.BeefChefBackend.dto.request.RankCreateRequest;
import com.restaurant.BeefChefBackend.dto.request.RankUpdateRequest;
import com.restaurant.BeefChefBackend.dto.response.ApiResponse;
import com.restaurant.BeefChefBackend.dto.response.RankResponse;
import com.restaurant.BeefChefBackend.service.RankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rank")
public class RankController {
    @Autowired
    private RankService rankService;

    //create
    @PostMapping
    public ApiResponse<RankResponse> createRank(@RequestBody RankCreateRequest request){
        try {
            return ApiResponse.<RankResponse>builder()
                    .message("Create Rank successfully!")
                    .result(rankService.createRank(request))
                    .build();
        } catch (Exception e) {
            return ApiResponse.<RankResponse>builder()
                    .message("Lỗi khi tạo rank!" + e.getMessage())
                    .build();
        }
    }

    //getRank by Id
    @GetMapping("/{id}")
    public ApiResponse<RankResponse> getRank(@PathVariable Integer id){
        try {
            return ApiResponse.<RankResponse>builder()
                    .message("Get Rank successfully!")
                    .result(rankService.toResponse(rankService.getRankById(id)))
                    .build();
        } catch (Exception e) {
            return ApiResponse.<RankResponse>builder()
                    .message("Lỗi khi Lấy rank!" + e.getMessage())
                    .build();
        }
    }

    //get all
    @GetMapping
    public ApiResponse<List<RankResponse>> getRanks(){
        try {
            return ApiResponse.<List<RankResponse>>builder()
                    .message("Get Ranks successfully!")
                    .result(rankService.getRanks())
                    .build();
        } catch (Exception e) {
            return ApiResponse.<List<RankResponse>>builder()
                    .message("Lỗi khi Lấy tất cả rank!" + e.getMessage())
                    .build();
        }
    }

    //update rank
    @PutMapping("/{id}")
    public ApiResponse<RankResponse> updateRank(@PathVariable Integer id, @RequestBody RankUpdateRequest request){
        try {
            return ApiResponse.<RankResponse>builder()
                    .message("Update Rank successfully!")
                    .result(rankService.updateRank(id, request))
                    .build();
        } catch (Exception e) {
            return ApiResponse.<RankResponse>builder()
                    .message("Lỗi khi update rank!" + e.getMessage())
                    .build();
        }
    }

    //delete rank
    @DeleteMapping("/{id}")
    public String deleteRank(@PathVariable Integer id){
        rankService.deleteRank(id);
        return "Rank has been deleted!";
    }
}
