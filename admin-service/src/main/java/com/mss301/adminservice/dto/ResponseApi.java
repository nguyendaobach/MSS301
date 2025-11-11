package com.mss301.adminservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseApi<T> {
    private int status;
    private String message;
    private T data;

    public static <T> ResponseApi<T> success(T data, String message) {
        return new ResponseApi<>(200, message, data);
    }

    public static <T> ResponseApi<T> error(int status, String message) {
        return new ResponseApi<>(status, message, null);
    }
}
