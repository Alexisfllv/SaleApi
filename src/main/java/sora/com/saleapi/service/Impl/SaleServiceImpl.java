package sora.com.saleapi.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sora.com.saleapi.dto.SaleDTO.SaleDTORequest;
import sora.com.saleapi.dto.SaleDTO.SaleDTOResponse;
import sora.com.saleapi.entity.*;
import sora.com.saleapi.mapper.SaleMapper;
import sora.com.saleapi.repo.*;
import sora.com.saleapi.service.Impl.SaveSale.SaleHelperService;
import sora.com.saleapi.service.SaleService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

    // IOC
    private final SaleMapper saleMapper;
    private final SaleRepo saleRepo;
    private final SaleDetailRepo saleDetailRepo;

    // client
    private final ClientRepo clientRepo;
    // user
    private final UserRepo userRepo;
    // product
    private final ProductRepo productRepo;

    // helper
    private final SaleHelperService saleHelperService;


    @Override
    public List<SaleDTOResponse> findAll() {
        List<Sale> lista = saleRepo.findAll();
        return lista.stream()
                .map(sale -> saleMapper.toSaleDTOResponse(sale))
                .toList();
    }

    @Override
    public Page<SaleDTOResponse> findAllPage(Pageable pageable) {
        Page<Sale> lista = saleRepo.findAll(pageable);
        return lista.map(s -> saleMapper.toSaleDTOResponse(s));
    }

    @Override
    public SaleDTOResponse findById(Long id) {
        Sale sale = saleRepo.findById(id)
                .orElseThrow( () -> new RuntimeException("Sale not found"));
        return saleMapper.toSaleDTOResponse(sale);
    }

    @Transactional
    @Override
    public SaleDTOResponse save(SaleDTORequest saleDTORequest) {


        // validate request
        saleHelperService.validateRequest(saleDTORequest);

        Sale sale = saleMapper.toSale(saleDTORequest);
        sale.setSaleEnabled(saleDTORequest.saleEnabled());

        // validate client and user
        sale.setClient(saleHelperService.findClient(saleDTORequest.clientId()));
        sale.setUser(saleHelperService.findUser(saleDTORequest.userId()));

        // validate details
        // insert details -  product
        List<SaleDetail> details = saleHelperService.buildDetails(sale, saleDTORequest.details());
        sale.setDetails(details);

        BigDecimal total = saleHelperService.calculateTotal(details);
        sale.setSaleTotal(total);
        sale.setSaleTax(total.multiply(BigDecimal.valueOf(0.18)).setScale(2, RoundingMode.HALF_UP));

        saleRepo.save(sale);

        return saleMapper.toSaleDTOResponse(sale);
    }



    @Transactional
    @Override
    public SaleDTOResponse update(Long id, SaleDTORequest saleDTORequest) {

        return null;
    }

    @Override
    public void deleteById(Long id) {

        Sale sale = saleRepo.findById(id)
                .orElseThrow( () -> new RuntimeException("Sale not found"));
        saleRepo.delete(sale);

    }
}
