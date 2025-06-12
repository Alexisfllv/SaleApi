package sora.com.saleapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sora.com.saleapi.dto.ClientDTO.ClientDTORequest;
import sora.com.saleapi.dto.ClientDTO.ClientDTOResponse;
import sora.com.saleapi.service.ClientService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
public class ClientController {

    //ioc
    private final ClientService clientService;

    // metodos
    @GetMapping
    public ResponseEntity<List<ClientDTOResponse>> findAll(){
        List<ClientDTOResponse> lista = clientService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(lista);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<ClientDTOResponse>> findAllPage(
            @RequestParam (defaultValue = "0") int page,
            @RequestParam (defaultValue = "5") int size,
            @RequestParam (defaultValue = "clientId") String sort,
            @RequestParam (defaultValue = "ASC") String direction ){
        // sort
        Sort sortOrder = Sort.by(Sort.Direction.fromString(direction),sort);
        Pageable pageable = PageRequest.of(page,size,sortOrder);
        Page<ClientDTOResponse> lista = clientService.findAllPage(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDTOResponse> findById(@PathVariable("id") Long id){
        ClientDTOResponse clientDTOResponse = clientService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(clientDTOResponse);
    }

    @PostMapping
    public ResponseEntity<ClientDTOResponse> save( @Valid @RequestBody ClientDTORequest clientDTORequest){
        ClientDTOResponse clientDTOResponse = clientService.save(clientDTORequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(clientDTOResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientDTOResponse> update(@PathVariable ("id") Long id, @Valid @RequestBody ClientDTORequest clientDTORequest){
        ClientDTOResponse clientDTOResponse = clientService.update(id, clientDTORequest);
        return ResponseEntity.status(HttpStatus.OK).body(clientDTOResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable ("id") Long id){
        clientService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
