package com.cloud.catalogservice.service;

import com.cloud.catalogservice.jpa.CatalogEntity;

import java.util.List;

public interface CatalogService {
    List<CatalogEntity> getAllCatalogs();
}
