package com.gymapp.gym_backend_service.error_handler;

import com.gymapp.gym_backend_service.model.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GymManagementErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> requestBodyValidator(MethodArgumentNotValidException exception) {
        String errorMsg = exception.getBindingResult().getFieldErrors().stream().map(error -> error.getDefaultMessage()).findFirst().get().toString();

        return ResponseEntity.badRequest().body(new ApiResponse("error", errorMsg));
    }

}
