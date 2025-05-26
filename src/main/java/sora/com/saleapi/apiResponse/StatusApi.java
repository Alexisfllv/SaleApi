package sora.com.saleapi.apiResponse;

public enum StatusApi {
    SUCCESS(200, "Operación exitosa"),
    CREATED(201, "Creado correctamente"),
    UPDATED(200, "Actualizado correctamente"),
    DELETED(200, "Eliminado correctamente"),
    NOT_FOUND(404, "Recurso no encontrado");

    private final int code;
    private final String message;

    StatusApi(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int code() { return code; }
    public String message() { return message; }
}
