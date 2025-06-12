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
import sora.com.saleapi.dto.UserDTO.UserDTORequest;
import sora.com.saleapi.dto.UserDTO.UserDTOResponse;
import sora.com.saleapi.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    //ioc
    private final UserService userService;

    // metodos

    @GetMapping
    public ResponseEntity<List<UserDTOResponse>> findAll(){
        List<UserDTOResponse> lista = userService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(lista);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<UserDTOResponse>> findAllPage(
            @RequestParam (defaultValue = "0") int page,
            @RequestParam (defaultValue = "5") int size,
            @RequestParam (defaultValue = "userId") String sort,
            @RequestParam (defaultValue = "ASC") String direction
            ){
        // sort & direccion
        Sort sortOrder = Sort.by(Sort.Direction.fromString(direction), sort);
        Pageable pageable = PageRequest.of(page, size, sortOrder);
        Page<UserDTOResponse> lista = userService.findAllPage(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTOResponse> findById(@PathVariable("id") Long id){
        UserDTOResponse userDTOResponse = userService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(userDTOResponse);
    }

    @PostMapping
    public ResponseEntity<UserDTOResponse> save( @Valid @RequestBody UserDTORequest userDTORequest){
        UserDTOResponse userDTOResponse = userService.save(userDTORequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTOResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTOResponse> update(@PathVariable ("id") Long id, @Valid @RequestBody UserDTORequest userDTORequest){
        UserDTOResponse userDTOResponse = userService.update(id, userDTORequest);
        return ResponseEntity.status(HttpStatus.OK).body(userDTOResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable ("id") Long id){
        userService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
