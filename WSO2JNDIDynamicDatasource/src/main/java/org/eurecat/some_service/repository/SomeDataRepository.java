package org.eurecat.some_service.repository;

import java.util.List;

import org.eurecat.some_service.model.SomeData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SomeDataRepository extends JpaRepository<SomeData, Integer>{
    List<SomeData> findByName(String name);
}
