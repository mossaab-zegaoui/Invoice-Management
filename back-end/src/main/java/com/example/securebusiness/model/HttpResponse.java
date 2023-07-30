package com.example.securebusiness.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)

public class HttpResponse {
    protected String timeStamp;
    protected HttpStatus status;
    protected String reason;
    private String message;
    private String developerMessage;
    private Map<?, ?> data;
}
