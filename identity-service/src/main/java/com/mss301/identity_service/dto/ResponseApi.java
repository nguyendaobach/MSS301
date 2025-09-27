package com.mss301.identity_service.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseApi<T>  {
    private int status;
    private String message;
    private T data;
    private boolean success;
}
