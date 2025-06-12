package sora.com.saleapi.controller;


import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sora.com.saleapi.dto.SaleDTO.SaleDTORequest;
import sora.com.saleapi.dto.SaleDTO.SaleDTOResponse;
import sora.com.saleapi.entity.Sale;
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

    @GetMapping("/page")
    public ResponseEntity<Page<SaleDTOResponse>> findAllPage(
            @RequestParam (defaultValue = "0") int page,
            @RequestParam (defaultValue = "5") int size,
            @RequestParam (defaultValue = "saleId") String sort,
            @RequestParam (defaultValue = "ASC") String direction
    ) {
        Sort sortOrder = Sort.by(Sort.Direction.fromString(direction), sort);
        Pageable pageable = PageRequest.of(page, size, sortOrder);
        Page<SaleDTOResponse> lista = saleService.findAllPage(pageable);
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable ("id") Long id){
        saleService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
