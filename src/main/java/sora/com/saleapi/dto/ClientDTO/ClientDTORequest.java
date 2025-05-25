package sora.com.saleapi.dto.ClientDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClientDTORequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 50)
        String clientFirstName,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 50)
        String clientLastName,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo debe ser válido")
        @Size(max = 100)
        String clientEmail,

        @NotBlank(message = "El número de documento es obligatorio")
        @Size(max = 30)
        String clientCardId,

        @NotBlank(message = "El teléfono es obligatorio")
        @Size(min = 9, max = 9, message = "El teléfono debe tener 9 dígitos")
        String clientPhone,

        @NotBlank(message = "La dirección es obligatoria")
        @Size(max = 100)
        String clientAddress

) {}
