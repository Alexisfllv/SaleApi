package sora.com.saleapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sora.com.saleapi.dto.SaleDTO.SaleDTORequest;
import sora.com.saleapi.dto.SaleDTO.SaleDTOResponse;

import java.util.List;

public interface SaleService {
    // metodos

    List<SaleDTOResponse> findAll();
    Page<SaleDTOResponse> findAllPage(Pageable pageable);
    SaleDTOResponse findById(Long id);
    SaleDTOResponse save(SaleDTORequest saleDTORequest);
    // SaleDTOResponse update(Long id, SaleDTORequest saleDTORequest);
    void deleteById(Long id);
}
