package sora.com.saleapi.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sora.com.saleapi.dto.UserDTO.UserDTORequest;
import sora.com.saleapi.dto.UserDTO.UserDTOResponse;
import sora.com.saleapi.entity.User;
import sora.com.saleapi.exception.ResourceNotFoundException;
import sora.com.saleapi.mapper.UserMapper;
import sora.com.saleapi.repo.RoleRepo;
import sora.com.saleapi.repo.UserRepo;
import sora.com.saleapi.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    //ioc
    private final UserMapper userMapper;
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;

    @Override
    public List<UserDTOResponse> findAll() {
        List<User> lista = userRepo.findAll();
        return lista.stream()
                .map(user -> userMapper.toUserDTOResponse(user))
                .toList();
    }

    @Override
    public Page<UserDTOResponse> findAllPage(Pageable pageable) {
        Page<User> lista = userRepo.findAll(pageable);
        return lista.map(user -> userMapper.toUserDTOResponse(user));
    }

    @Override
    public UserDTOResponse findById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("User not found"));
        return userMapper.toUserDTOResponse(user);
    }

    @Override
    public UserDTOResponse save(UserDTORequest userDTORequest) {
        User user = userMapper.toUser(userDTORequest);
        user.setRole(roleRepo.findById(userDTORequest.roleId())
                .orElseThrow( () -> new ResourceNotFoundException("Role not found")));
        userRepo.save(user);
        return userMapper.toUserDTOResponse(user);
    }

    @Override
    public UserDTOResponse update(Long id, UserDTORequest userDTORequest) {
        User user = userRepo.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("User not found"));
        user.setUserName(userDTORequest.userName());
        user.setPassword(userDTORequest.password());
        user.setUserEnabled(userDTORequest.userEnabled());
        // fk
        user.setRole(roleRepo.findById(userDTORequest.roleId())
                .orElseThrow( () -> new ResourceNotFoundException("Role not found")));
        userRepo.save(user);
        return userMapper.toUserDTOResponse(user);
    }

    @Override
    public void deleteById(Long id) {

        User user = userRepo.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("User not found"));
        userRepo.delete(user);
    }

}
