package com.restaurant.BeefChefBackend.service;

import com.restaurant.BeefChefBackend.dto.request.UserCreateRequest;
import com.restaurant.BeefChefBackend.dto.request.UserUpdateRequest;
import com.restaurant.BeefChefBackend.dto.response.TableResponse;
import com.restaurant.BeefChefBackend.dto.response.UserResponse;
import com.restaurant.BeefChefBackend.entity.Ranks;
import com.restaurant.BeefChefBackend.entity.Tables;
import com.restaurant.BeefChefBackend.entity.User;
import com.restaurant.BeefChefBackend.enums.Roles;
import com.restaurant.BeefChefBackend.repository.RankRepository;
import com.restaurant.BeefChefBackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class UserService {

    @Autowired
    public UserRepository userRepository;

    @Autowired
    private RankRepository rankRepository;

    private UserResponse toResponse(User user){
        return UserResponse.builder()
                .userId(user.getUserId())
                .userPhone(user.getUserPhone())
                .userPassword(user.getUserPassword())
                .userFirstname(user.getUserFirstname())
                .userLastname(user.getUserLastname())
                .userGender(user.getUserGender())
                .userRole(user.getUserRole())
                .userDoB(user.getUserDoB())
                .userPoint(user.getUserPoint())
                .rank(user.getRank())
                .build();
    }

    //tạo user mới
    public UserResponse createUser (UserCreateRequest request){

        List<User> list = userRepository.findAll();

        for (User u : list){
            if (u.getUserPhone().equalsIgnoreCase(request.getUserPhone())){
                throw new IllegalArgumentException("Số điện thoại đã được sử dụng!");
            }
        }

        User user = new User();

        user.setUserPhone(request.getUserPhone());
        //mã hoá mk
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setUserPassword(passwordEncoder.encode(request.getUserPassword()));

        user.setUserFirstname(request.getUserFirstname());
        user.setUserLastname(request.getUserLastname());
        user.setUserDoB(request.getUserDoB());
        user.setUserPoint(0);
        //set role user
        HashSet<String> role = new HashSet<>();
        role.add(Roles.USER.name());
        user.setUserRole(role);
        user.setUserGender(request.getUserGender());
        //gan rank
        Ranks rank = rankRepository.findByRankMinPoint(0L)
                .orElseThrow(() -> new RuntimeException("Rank not found!"));

        user.setRank(rank);
        User save = userRepository.save(user);
        return toResponse(save);
    }

    //Get toàn bộ user
    public List<User> getUsers(){
        return userRepository.findAll().stream().toList();
    }

    //Get user theo ID
    public User getUser(Integer id){
        return userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("User not found!")
        );
    }

    //Update user
    public User updateUser(Integer id, UserUpdateRequest request){
        User userUpdate = getUser(id);

        //mã hoá mk
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        userUpdate.setUserPassword(passwordEncoder.encode(request.getUserPassword()));

        userUpdate.setUserFirstname(request.getUserFirstName());
        userUpdate.setUserLastname(request.getUserLastname());
        userUpdate.setUserDoB(request.getUserDoB());

        userUpdate.setUserGender(request.getUserGender());

        return userRepository.save(userUpdate);
    }

    //Del user
    public void deleteUser(Integer id){
        userRepository.deleteById(id);
    }

    //capt nhat lai rank cho user
    public void updateRankForUser(User user){
        List<Ranks> listRanks = rankRepository.findAll();
        for(Ranks r : listRanks){
            if(user.getUserPoint() >= r.getRankMinPoint() ){
                user.setRank(r);
            }
        }
    }
}
