package sora.com.saleapi.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sora.com.saleapi.dto.SaleDTO.SaleDTORequest;
import sora.com.saleapi.dto.SaleDTO.SaleDTOResponse;
import sora.com.saleapi.dto.SaleDetailDTO.SaleDetailDTORequest;
import sora.com.saleapi.entity.*;
import sora.com.saleapi.mapper.SaleMapper;
import sora.com.saleapi.repo.*;
import sora.com.saleapi.service.SaleService;

import java.math.BigDecimal;
import java.util.ArrayList;
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


    @Override
    public List<SaleDTOResponse> findAll() {
        List<Sale> lista = saleRepo.findAll();
        return lista.stream()
                .map(sale -> saleMapper.toSaleDTOResponse(sale))
                .toList();
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
        validateRequest(saleDTORequest);

        Sale sale = saleMapper.toSale(saleDTORequest);
        sale.setSaleEnabled(saleDTORequest.saleEnabled());
        sale.setClient(findClient(saleDTORequest.clientId()));
        sale.setUser(findUser(saleDTORequest.userId()));

        List<SaleDetail> details = buildDetails(sale, saleDTORequest.details());
        sale.setDetails(details);

        BigDecimal total = calculateTotal(details);
        sale.setSaleTotal(total);
        sale.setSaleTax(total.multiply(BigDecimal.valueOf(0.18)));

        saleRepo.save(sale);

        return saleMapper.toSaleDTOResponse(sale);
    }

    private void validateRequest(SaleDTORequest request) {
        if (request == null || request.clientId() == null || request.userId() == null || request.details() == null || request.details().isEmpty())
            throw new IllegalArgumentException("Invalid sale request");
    }

    private Client findClient(Long clientId) {
        return clientRepo.findById(clientId).orElseThrow(() -> new RuntimeException("Client not found"));
    }

    private User findUser(Long userId) {
        return userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    }

    private List<SaleDetail> buildDetails(Sale sale, List<SaleDetailDTORequest> detailRequests) {
        return detailRequests.stream().map(dto -> {
            Product product = productRepo.findById(dto.productId()).orElseThrow(() -> new RuntimeException("Product not found"));
            SaleDetail detail = new SaleDetail();
            detail.setProduct(product);
            detail.setQuantity(dto.quantity());
            detail.setSalePrice(product.getProductPrice());
            detail.setDiscount(BigDecimal.ZERO);
            detail.setSale(sale);
            return detail;
        }).toList();
    }

    private BigDecimal calculateTotal(List<SaleDetail> details) {
        return details.stream()
                .map(d -> d.getSalePrice().multiply(BigDecimal.valueOf(d.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public SaleDTOResponse update(Long id, SaleDTORequest saleDTORequest) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }
}
