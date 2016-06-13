package org.eurecat.dynamic_datasource.routing;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.jndi.JndiTemplate;

public class RoutingDataSource extends AbstractRoutingDataSource {
	private static final Logger log = LoggerFactory
			.getLogger(RoutingDataSource.class);

	@Override
	protected Object determineCurrentLookupKey() {
		log.info(">>> Tenant Routing: {}", TenantContextHolder.getTenantDb());
		return TenantContextHolder.getTenantDb();
	}

	@Override
	protected DataSource determineTargetDataSource() {
		log.info(">>> Tenant Routing: {}", TenantContextHolder.getTenantDb());
		String tenantId = TenantContextHolder.getTenantDb();
		String jndiDatasource = null;
		if (tenantId == null || tenantId.equals("-1234"))
			jndiDatasource = "jdbc/Tenant_Super";
		else
			jndiDatasource = "jdbc/Tenant_" + tenantId;
		log.debug(">>> Looking for JNDI datasource {}",jndiDatasource);
		JndiTemplate jndi = new JndiTemplate();
		DataSource dataSource = null;
		try {
			dataSource = (DataSource) jndi.lookup(jndiDatasource);
		} catch (NamingException e) {
			log.error("NamingException for " + jndiDatasource, e);
		}
		log.debug(">>> Datasource found: {}", dataSource);
		return dataSource;
	}
}
