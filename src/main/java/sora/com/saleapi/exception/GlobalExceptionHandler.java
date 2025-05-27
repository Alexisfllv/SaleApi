package sora.com.saleapi.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.nio.file.AccessDeniedException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 🔍 2.1 - Recurso no encontrado (404)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI(),
                "ResourceNotFound"
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // 🧪 2.2 - Validaciones fallidas (400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errorMsg = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(" | "));

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                errorMsg,
                request.getRequestURI(),
                "ValidationError"
        );
        return ResponseEntity.badRequest().body(response);
    }

    // 🧾 2.3 - JSON mal formado o errores de parsing (400)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJson(HttpMessageNotReadableException ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "JSON mal formado o inválido",
                request.getRequestURI(),
                "MalformedJsonError"
        );
        return ResponseEntity.badRequest().body(response);
    }

    // 🔢 2.4 - Error de tipo en argumentos (400)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String errorMsg = String.format("El parámetro '%s' debe ser del tipo '%s'",
                ex.getName(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "desconocido");

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                errorMsg,
                request.getRequestURI(),
                "TypeMismatchError"
        );
        return ResponseEntity.badRequest().body(response);
    }

    // 💽 2.5 - Errores de base de datos (SQL, integridad, etc)
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDatabase(DataAccessException ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error en la base de datos",
                request.getRequestURI(),
                "DatabaseError"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // 🔒 2.6 - Acceso denegado (403)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.FORBIDDEN,
                "Acceso denegado",
                request.getRequestURI(),
                "AccessDenied"
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // ❗ 2.7 - Genérico para errores no controlados (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor",
                request.getRequestURI(),
                "InternalServerError"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
