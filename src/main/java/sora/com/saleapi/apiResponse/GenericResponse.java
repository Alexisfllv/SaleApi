package sora.com.saleapi.apiResponse;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GenericResponse<T>(
        int status,
        String message,
        List<T> data
) {
    public GenericResponse(StatusApi status, List<T> data) {
        this(status.code(), status.message(), data);
    }

    public GenericResponse(StatusApi status) {
        this(status.code(), status.message(), null);
    }
}