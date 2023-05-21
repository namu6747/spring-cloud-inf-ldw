package com.cloud.catalogservice.controller;

import com.cloud.catalogservice.dto.ServerInfoDto;
import com.cloud.catalogservice.jpa.CatalogEntity;
import com.cloud.catalogservice.service.CatalogService;
import com.cloud.catalogservice.vo.ResponseCatalog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/catalog-service")
public class CatalogController {
    private final CatalogService catalogService;
    private final Environment environment;

    @GetMapping("/health_check")
    public ServerInfoDto status() throws Exception {
        String[] activeProfiles = environment.getActiveProfiles();
        for (String activeProfile : activeProfiles) {
            log.info("active {}:[{}]", activeProfile, environment.getProperty(activeProfile));
        }
        String[] defaultProfiles = environment.getDefaultProfiles();
        for (String defaultProfile : defaultProfiles) {
            log.info("default {}:[{}]", defaultProfile, environment.getProperty(defaultProfile));
        }
        ServerInfoDto serverInfoDto = new ServerInfoDto();
        serverInfoDto.setHost(Inet4Address.getLocalHost().getHostAddress());
        serverInfoDto.setPort(environment.getProperty("local.server.port"));
        return serverInfoDto;
    }

    @GetMapping("/catalogs")
    public ResponseEntity<List<ResponseCatalog>> getUsers() {
        List<CatalogEntity> catalogEntities = catalogService.getAllCatalogs();
        ModelMapper modelMapper = new ModelMapper();
        List<ResponseCatalog> responseCatalogs = catalogEntities.stream().map(e -> modelMapper.map(e, ResponseCatalog.class)).collect(Collectors.toList());
        return ResponseEntity.ok(responseCatalogs);
    }
}
