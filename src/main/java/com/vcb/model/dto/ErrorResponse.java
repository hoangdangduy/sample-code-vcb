package com.vcb.model.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {

    private String code;
    private String message;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}

