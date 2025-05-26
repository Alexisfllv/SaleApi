package sora.com.saleapi.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sora.com.saleapi.dto.SaleDTO.SaleDTORequest;
import sora.com.saleapi.dto.SaleDTO.SaleDTOResponse;
import sora.com.saleapi.service.SaleService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;


    @GetMapping
    public ResponseEntity<List<SaleDTOResponse>> findAll(){
        List<SaleDTOResponse> lista = saleService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body( lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleDTOResponse> findById(@PathVariable("id") Long id){
        SaleDTOResponse saleDTOResponse = saleService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body( saleDTOResponse );
    }

    @PostMapping
    public ResponseEntity<SaleDTOResponse> save( @RequestBody SaleDTORequest saleDTORequest){
        SaleDTOResponse obj = saleService.save(saleDTORequest);
        return ResponseEntity.status(HttpStatus.CREATED).body( obj );
    }

    @PutMapping("/{id}")
    public ResponseEntity<SaleDTOResponse> update(@PathVariable ("id") Long id, @RequestBody SaleDTORequest saleDTORequest){
        SaleDTOResponse obj = saleService.update(id, saleDTORequest);
        return ResponseEntity.status(HttpStatus.OK).body( obj );
    }
}
