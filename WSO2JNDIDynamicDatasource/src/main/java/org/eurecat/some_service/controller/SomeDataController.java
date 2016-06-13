package org.eurecat.some_service.controller;

import java.util.List;

import org.eurecat.dynamic_datasource.aspect.TenantAwareOperation;
import org.eurecat.some_service.model.SomeData;
import org.eurecat.some_service.repository.SomeDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class SomeDataController {

	private static final Logger log = LoggerFactory.getLogger(SomeDataController.class);

	@Autowired
	private SomeDataRepository repo;

	@RequestMapping("/test")
	@TenantAwareOperation
	public SomeData test() {
		SomeData someData = new SomeData();
		someData.setName("Service");
		someData.setValue("OK");
		return someData;
	}

	@RequestMapping("/create")
	@TenantAwareOperation
	public SomeData create(@RequestHeader(value = "enduserTenantId") String tenantId,
			@RequestParam(value = "value") String value) {
		log.debug("Executing create over tenant: {}", tenantId);
		SomeData someData = new SomeData();
		someData.setName("Value");
		someData.setValue(value);
		repo.save(someData);
		return someData;
	}

	@RequestMapping("/list")
	@TenantAwareOperation
	public List<SomeData> list(@RequestHeader(value = "enduserTenantId") String tenantId) {
		log.debug("Executing list over tenant: {}", tenantId);
		List<SomeData> list = repo.findAll();
		return list;
	}

}