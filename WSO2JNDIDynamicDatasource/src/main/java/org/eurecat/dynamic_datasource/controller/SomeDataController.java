package org.eurecat.dynamic_datasource.controller;

import java.util.List;

import org.eurecat.dynamic_datasource.aspect.TenantAwareOperation;
import org.eurecat.dynamic_datasource.model.SomeData;
import org.eurecat.dynamic_datasource.repository.SomeDataRepository;
import org.eurecat.dynamic_datasource.routing.TenantContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest")
public class SomeDataController {

	private static final Logger log = LoggerFactory
			.getLogger(SomeDataController.class);

	@Autowired
	private SomeDataRepository repo;

	@RequestMapping("/create")
	@TenantAwareOperation
	public SomeData create(
			@RequestHeader(value = "enduserTenantId", defaultValue = "-1234") String tenantId,
			@RequestParam(value = "value", defaultValue = "XXXX") String value) {
		SomeData someData = new SomeData();
		someData.setName("Value");
		someData.setValue(value);
		repo.save(someData);
		return someData;
	}

	@RequestMapping("/list")
	@TenantAwareOperation
	public List<SomeData> list(
			@RequestHeader(value = "enduserTenantId", defaultValue = "-1234") String tenantId) {
		List<SomeData> list = repo.findAll();
		return list;
	}

}