package com.uca.pncparcialfinalhotel.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class GeneralResponse<T> {
    private String uri;
    private String message;
    private int status;
    private LocalDateTime time;
    private T data;
}
