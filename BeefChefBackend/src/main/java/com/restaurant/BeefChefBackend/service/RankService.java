package com.restaurant.BeefChefBackend.service;

import com.restaurant.BeefChefBackend.dto.request.RankCreateRequest;
import com.restaurant.BeefChefBackend.dto.request.RankUpdateRequest;
import com.restaurant.BeefChefBackend.dto.response.RankResponse;
import com.restaurant.BeefChefBackend.entity.Ranks;
import com.restaurant.BeefChefBackend.repository.RankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RankService {
    @Autowired
    private RankRepository rankRepository;

    public RankResponse toResponse(Ranks rank){
        return RankResponse.builder()
                .rankId(rank.getRankId())
                .rankName(rank.getRankName())
                .rankMinPoint(rank.getRankMinPoint())
                .build();
    }

    //create rank
    public RankResponse createRank(RankCreateRequest request){
        Ranks rank = new Ranks();
        rank.setRankName(request.getRankName());
        rank.setRankMinPoint(request.getRankMinPoint());
        Ranks save = rankRepository.save(rank);
        return toResponse(save);
    }

    //get rank by id
    public Ranks getRankById(Integer id){
        return rankRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Rank not found!")
        );
    }

    //get all rank
    public List<RankResponse> getRanks(){
        return rankRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    //update rank
    public RankResponse updateRank(Integer id, RankUpdateRequest request){
        Ranks rank = getRankById(id);
        rank.setRankName(request.getRankName());
        rank.setRankMinPoint(request.getRankMinPoint());

        Ranks save = rankRepository.save(rank);
        return toResponse(save);
    }
    //delete Rank
    public void deleteRank(Integer id){
        rankRepository.deleteById(id);
    }
}
