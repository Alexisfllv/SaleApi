package sora.com.saleapi.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sora.com.saleapi.dto.ClientDTO.ClientDTORequest;
import sora.com.saleapi.dto.ClientDTO.ClientDTOResponse;
import sora.com.saleapi.entity.Client;
import sora.com.saleapi.exception.ResourceNotFoundException;
import sora.com.saleapi.mapper.ClientMapper;
import sora.com.saleapi.repo.ClientRepo;
import sora.com.saleapi.service.ClientService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    // ioc
    private final ClientMapper clientMapper;
    private final ClientRepo clientRepo;


    @Override
    public List<ClientDTOResponse> findAll() {
        List<Client> lista = clientRepo.findAll();
        return lista.stream()
                .map(client -> clientMapper.toClientDTOResponse(client))
                .toList();
    }

    @Override
    public Page<ClientDTOResponse> findAllPage(Pageable pageable) {
        Page<Client> lista = clientRepo.findAll(pageable);
        return lista.map(client -> clientMapper.toClientDTOResponse(client));
    }

    @Override
    public ClientDTOResponse findById(Long id) {
        Client client = clientRepo.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("Client not found"));

        return clientMapper.toClientDTOResponse(client);
    }

    @Override
    public ClientDTOResponse save(ClientDTORequest clientDTORequest) {
        Client client = clientMapper.toClient(clientDTORequest);
        clientRepo.save(client);
        return clientMapper.toClientDTOResponse(client);
    }

    @Override
    public ClientDTOResponse update(Long id, ClientDTORequest clientDTORequest) {
        Client client = clientRepo.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("Client not found"));
        client.setClientFirstName(clientDTORequest.clientFirstName());
        client.setClientLastName(clientDTORequest.clientLastName());
        client.setClientEmail(clientDTORequest.clientEmail());
        client.setClientCardId(clientDTORequest.clientCardId());
        client.setClientAddress(clientDTORequest.clientAddress());
        client.setClientPhone(clientDTORequest.clientPhone());

        clientRepo.save(client);
        return clientMapper.toClientDTOResponse(client);
    }

    @Override
    public void deleteById(Long id) {
        Client client = clientRepo.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("Client not found"));
        clientRepo.delete(client);
    }
}
