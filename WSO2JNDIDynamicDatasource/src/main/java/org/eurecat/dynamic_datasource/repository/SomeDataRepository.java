package org.eurecat.dynamic_datasource.repository;

import java.util.List;

import org.eurecat.dynamic_datasource.model.SomeData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SomeDataRepository extends JpaRepository<SomeData, Integer>{
    List<SomeData> findByName(String name);
}
