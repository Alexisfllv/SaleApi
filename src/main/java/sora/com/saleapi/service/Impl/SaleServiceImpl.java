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

        Sale sale = saleMapper.toSale(saleDTORequest);
        // Detail

        sale.setSaleEnabled(saleDTORequest.saleEnabled());

        // fk client
        Client client = clientRepo.findById(saleDTORequest.clientId())
                .orElseThrow( () -> new RuntimeException("Client not found"));

        sale.setClient(client);

        // fk user
        User user = userRepo.findById(saleDTORequest.userId())
                .orElseThrow( () -> new RuntimeException("User not found"));
        sale.setUser(user);

        // List DetailSale

        List<SaleDetail> listaSaleDetail = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (SaleDetailDTORequest saleDetailDTORequest : saleDTORequest.details()) {

            Product product = productRepo.findById(saleDetailDTORequest.productId())
                    .orElseThrow( () -> new RuntimeException("Product not found"));
            BigDecimal price = product.getProductPrice();
            Integer quantity = saleDetailDTORequest.quantity();
            BigDecimal subTotal = price.multiply(BigDecimal.valueOf(quantity));

            SaleDetail detail =  new SaleDetail();
            detail.setProduct(product);
            detail.setQuantity(quantity);
            detail.setSalePrice(price);
            detail.setDiscount(BigDecimal.ZERO);
            detail.setSale(sale); // relacion inversa

            listaSaleDetail.add(detail);
            total = total.add(subTotal);
        }

        // setear datos
        sale.setDetails(listaSaleDetail);
        sale.setSaleTotal(total);
        sale.setSaleTax(total.multiply(BigDecimal.valueOf(0.18)));

        saleRepo.save(sale);

        return saleMapper.toSaleDTOResponse(sale);

    }

    @Override
    public SaleDTOResponse update(Long id, SaleDTORequest saleDTORequest) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }
}
