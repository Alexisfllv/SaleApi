package sora.com.saleapi.service.Impl.SaveSale;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sora.com.saleapi.dto.SaleDTO.SaleDTORequest;
import sora.com.saleapi.dto.SaleDetailDTO.SaleDetailDTORequest;
import sora.com.saleapi.entity.*;
import sora.com.saleapi.repo.ClientRepo;
import sora.com.saleapi.repo.ProductRepo;
import sora.com.saleapi.repo.UserRepo;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleHelperService {

    // client
    private final ClientRepo clientRepo;
    // user
    private final UserRepo userRepo;
    // product
    private final ProductRepo productRepo;

    public void validateRequest(SaleDTORequest request) {
        if (request == null || request.clientId() == null || request.userId() == null || request.details() == null || request.details().isEmpty())
            throw new IllegalArgumentException("Invalid sale request");
    }

    public Client findClient(Long clientId) {
        return clientRepo.findById(clientId).orElseThrow(() -> new RuntimeException("Client not found"));
    }

    public User findUser(Long userId) {
        return userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<SaleDetail> buildDetails(Sale sale, List<SaleDetailDTORequest> detailRequests) {
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

    public BigDecimal calculateTotal(List<SaleDetail> details) {
        return details.stream()
                .map(d -> d.getSalePrice().multiply(BigDecimal.valueOf(d.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


}
