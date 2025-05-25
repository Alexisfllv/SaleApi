package sora.com.saleapi.dto.ClientDTO;

public record ClientDTOResponse(
        Long clientId,
        String clientFirstName,
        String clientLastName,
        String clientEmail,
        String clientCardId,
        String clientPhone,
        String clientAddress
) {}

