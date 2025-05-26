package sora.com.saleapi.service;

import sora.com.saleapi.dto.ClientDTO.ClientDTORequest;
import sora.com.saleapi.dto.ClientDTO.ClientDTOResponse;

import java.util.List;

public interface ClientService {

    // Metodos
    List<ClientDTOResponse> findAll();
    ClientDTOResponse findById(Long id);
    ClientDTOResponse save(ClientDTORequest clientDTORequest);
    ClientDTOResponse update(Long id, ClientDTORequest clientDTORequest);
    void deleteById(Long id);
}
