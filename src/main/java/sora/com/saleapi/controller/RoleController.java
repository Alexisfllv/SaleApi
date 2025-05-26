package sora.com.saleapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sora.com.saleapi.dto.RoleDTO.RoleDTORequest;
import sora.com.saleapi.dto.RoleDTO.RoleDTOResponse;
import sora.com.saleapi.service.RoleService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    //ioc
    private final RoleService roleService;

    // metodos

    @GetMapping
    public ResponseEntity<List<RoleDTOResponse>> findAll(){
        List<RoleDTOResponse> lista = roleService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleDTOResponse> findById(@PathVariable("id") Long id){
        RoleDTOResponse roleDTOResponse = roleService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(roleDTOResponse);
    }

    @PostMapping
    public ResponseEntity<RoleDTOResponse> save( @Valid @RequestBody RoleDTORequest roleDTORequest){
        RoleDTOResponse roleDTOResponse = roleService.save(roleDTORequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(roleDTOResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleDTOResponse> update(@PathVariable ("id") Long id, @Valid @RequestBody RoleDTORequest roleDTORequest){
        RoleDTOResponse roleDTOResponse = roleService.update(id, roleDTORequest);
        return ResponseEntity.status(HttpStatus.OK).body(roleDTOResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable ("id") Long id){
        roleService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
