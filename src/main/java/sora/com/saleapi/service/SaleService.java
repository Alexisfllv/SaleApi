package sora.com.saleapi.service;

import sora.com.saleapi.dto.SaleDTO.SaleDTORequest;
import sora.com.saleapi.dto.SaleDTO.SaleDTOResponse;

import java.util.List;

public interface SaleService {
    // metodos

    List<SaleDTOResponse> findAll();
    SaleDTOResponse findById(Long id);
    SaleDTOResponse save(SaleDTORequest saleDTORequest);
    SaleDTOResponse update(Long id, SaleDTORequest saleDTORequest);
    void deleteById(Long id);
}
