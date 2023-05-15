package com.cloud.orderservice.vo;

import lombok.Data;

@Data
public class ServerInfo {
    private String host;
    private String port;
    private String message;
}
