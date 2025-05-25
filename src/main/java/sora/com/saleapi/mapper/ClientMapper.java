package sora.com.saleapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import sora.com.saleapi.dto.ClientDTO.ClientDTORequest;
import sora.com.saleapi.dto.ClientDTO.ClientDTOResponse;
import sora.com.saleapi.entity.Client;

@Mapper (componentModel = "spring")
public interface ClientMapper {

    ClientMapper INSTANCE = Mappers.getMapper(ClientMapper.class);

    // Request
    Client toClient(ClientDTORequest clientDTORequest);

    // Response
    ClientDTOResponse toClientDTOResponse(Client client);
}
