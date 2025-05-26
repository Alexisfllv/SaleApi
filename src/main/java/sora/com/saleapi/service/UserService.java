package sora.com.saleapi.service;


import sora.com.saleapi.dto.UserDTO.UserDTORequest;
import sora.com.saleapi.dto.UserDTO.UserDTOResponse;
import sora.com.saleapi.entity.User;

import java.util.List;

public interface UserService {
    // metodos
    List<UserDTOResponse> findAll();
    UserDTOResponse findById(Long id);
    UserDTOResponse save(UserDTORequest userDTORequest);
    UserDTOResponse update(Long id, UserDTORequest userDTORequest);
    void deleteById(Long id);
}
