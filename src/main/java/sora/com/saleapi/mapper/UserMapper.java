package sora.com.saleapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import sora.com.saleapi.dto.UserDTO.UserDTORequest;
import sora.com.saleapi.dto.UserDTO.UserDTOResponse;
import sora.com.saleapi.entity.User;

@Mapper (componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    // Request

    @Mapping(target = "role.roleId", source = "roleId")
    User toUser(UserDTORequest userDTORequest);

    // Response
    UserDTOResponse toUserDTOResponse(User user);
}
