package sora.com.saleapi.serviceimplTest;


// static
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sora.com.saleapi.dto.UserDTO.UserDTORequest;
import sora.com.saleapi.dto.UserDTO.UserDTOResponse;
import sora.com.saleapi.entity.User;
import sora.com.saleapi.mapper.UserMapper;
import sora.com.saleapi.repo.UserRepo;
import sora.com.saleapi.service.Impl.UserServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    // mock de user repo
    @Mock
    private UserRepo userRepo;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;


    // variables reutilizables
    private User user1;
    private User user2;
    private User user3;

    private UserDTOResponse userDTOResponse;
    private UserDTOResponse userDTOResponse2;
    private UserDTOResponse userDTOResponse3;

    private UserDTORequest userDTORequest1;
    private User userRequest1;




}
