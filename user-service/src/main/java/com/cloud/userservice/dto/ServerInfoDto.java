package com.cloud.userservice.dto;

import lombok.Data;

@Data
public class ServerInfoDto {
    private String host;
    private String port;
    private String message;
}
