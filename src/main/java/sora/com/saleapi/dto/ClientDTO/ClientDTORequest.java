package sora.com.saleapi.dto.ClientDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ClientDTORequest(

        @NotBlank(message = "{field.required}")
        @Size(max = 50, min = 2, message = "{field.size.range}")
        @Pattern(regexp = "^[\\p{L} ]+$", message = "{field.invalid.format}")
        String clientFirstName,

        @NotBlank(message = "{field.required}")
        @Size(max = 50, min = 2, message = "{field.size.range}")
        @Pattern(regexp = "^[\\p{L} ]+$", message = "{field.invalid.format}")
        String clientLastName,

        @NotBlank(message = "{field.required}")
        @Size(max = 100, min = 2, message = "{field.size.range}")
        @Pattern(regexp = "^[\\p{L} ]+$", message = "{field.invalid.format}")
        @Email(message = "{field.email.invalid}")
        String clientEmail,

        @NotBlank(message = "{field.required}")
        @Size(max = 30, min = 2, message = "{field.size.range}")
        @Pattern(regexp = "^[\\p{L} ]+$", message = "{field.invalid.format}")
        String clientCardId,

        @NotBlank(message = "{field.required}")
        @Size(max = 9, min = 9, message = "{field.size.range}")
        @Pattern(regexp = "^[\\p{L} ]+$", message = "{field.invalid.format}")
        String clientPhone,

        @NotBlank(message = "{field.required}")
        @Size(max = 100, min = 2, message = "{field.size.range}")
        @Pattern(regexp = "^[\\p{L} ]+$", message = "{field.invalid.format}")
        String clientAddress

) {}
