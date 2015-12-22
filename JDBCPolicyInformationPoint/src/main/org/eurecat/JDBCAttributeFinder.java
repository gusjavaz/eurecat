package org.eurecat;

import org.wso2.carbon.identity.entitlement.pip.AbstractPIPAttributeFinder;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * This is sample implementation of PIPAttributeFinder in WSO2 entitlement
 * engine. Here we are calling to an external user base to find given attribute;
 * Assume that user store is reside on mysql database
 */
public class JDBCAttributeFinder extends AbstractPIPAttributeFinder {

	/**
	 * Connection pool is used to create connection to database
	 */
	private DataSource dataSource;
	private Connection conn;

	/**
	 * List of attribute finders supported by the this PIP attribute finder
	 */
	private Set<String> supportedAttributes = new HashSet<String>();

	private static final String SUBJECT_IS_CARER_OF_RESOURCE = "subjectIsCarerOfResource";
	private static final String RESOURCE_IS_PATIENT_OF_SUBJECT = "resourceIsPatientOfRSubject";

	@Override
	public void init(Properties properties) throws Exception {

		String dataSourceName = (String) properties.get("DataSourceName");

		if (dataSourceName == null || dataSourceName.trim().length() == 0) {
			throw new Exception(
					"Data source name can not be null. Please configure it in the entitlement.properties file.");
		}

		dataSource = (DataSource) InitialContext.doLookup(dataSourceName);
		conn = dataSource.getConnection();

		supportedAttributes.add(SUBJECT_IS_CARER_OF_RESOURCE);
		supportedAttributes.add(RESOURCE_IS_PATIENT_OF_SUBJECT);

	}

	@Override
	public String getModuleName() {
		return "Eurecat PIP Attribute Finder";
	}

	@Override
	public Set<String> getAttributeValues(String subjectId, String resourceId,
			String actionId, String environmentId, String attributeId,
			String issuer) throws Exception {
		System.out.println("subjectId: " + subjectId);
		System.out.println("resourceId: " + resourceId);
		System.out.println("actionId: " + actionId);
		System.out.println("environmentId: " + environmentId);
		System.out.println("attributeId: " + attributeId);
		System.out.println("issuer: " + issuer);
		
		System.out.println("Supported Attributes: "+Arrays.asList(supportedAttributes));
		System.out.println("Index of "+attributeId + " Supported Attributes: "+Arrays.asList(supportedAttributes).indexOf(attributeId));

//		Set<String> values = new HashSet<String>();
//		switch (Arrays.asList(supportedAttributes).indexOf(attributeId)) {
//		case 0:
//			values = subjectIsCarerOfResource(subjectId, resourceId);
//		case 1:
//			values = resourceIsPatientOfRSubject(subjectId, resourceId);
//		}

		return subjectIsCarerOfResource(subjectId, resourceId);
	}

	private Set<String> subjectIsCarerOfResource(String subjectId,
			String resourceId) {
		String sqlStmt = "select * from PATIENT_CARER where CARER='"
				+ subjectId + "' and PATIENT='" + resourceId + "';";
		System.out.println("QUERY: " + sqlStmt);
		return executeQuery(sqlStmt);
	}

	private Set<String> resourceIsPatientOfRSubject(String subjectId,
			String resourceId) {
		String sqlStmt = "select * from PATIENT_CARER where CARER='"
				+ subjectId + "' and PATIENT='" + resourceId + "';";
		System.out.println("QUERY: " + sqlStmt);
		return executeQuery(sqlStmt);
	}

	private HashSet<String> executeQuery(String sqlStmt) {
		Set<String> values = new HashSet<String>();
		PreparedStatement prepStmt = null;
		ResultSet resultSet = null;
		Connection connection = null;
		boolean condition = false;

		try {
			connection = dataSource.getConnection();
				prepStmt = connection.prepareStatement(sqlStmt);
				resultSet = prepStmt.executeQuery();
				while (resultSet.next()) {
					condition = true;
				}
		} catch (SQLException e) {
			System.out.println("Exception!: " + e.getMessage());

		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				System.out.println("Exception!: " + e.getMessage());
			}
		}

		return new HashSet<String>(Arrays.asList(new String[] { String
				.valueOf(condition) }));
	}

	@Override
	public Set<String> getSupportedAttributes() {
		return supportedAttributes;
	}
}
