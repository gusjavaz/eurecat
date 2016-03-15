package org.eurecat;

import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.xacml3.Attributes;
import org.wso2.carbon.identity.entitlement.pip.AbstractPIPAttributeFinder;
 
/**
 * @author gvazquez
 *
 */
public class JDBCAbstractPIPAttributeFinder extends AbstractPIPAttributeFinder {

	private DataSource dataSource;
	private Connection conn;
    private static Logger LOGGER = Logger.getLogger("JDBCAbstractPIPAttributeFinder");


	private Set<String> supportedAttributes = new TreeSet<String>();

	private static final String SUBJECT_IS_CARER_OF_RESOURCE = "SUBJECT_IS_CARER_OF_RESOURCE";
	private static final String SUBJECT_IS_SAME_AS_RESOURCE = "SUBJECT_IS_SAME_AS_RESOURCE";
	private static final String RESOURCE_IS_ASSIGNED = "RESOURCE_IS_ASSIGNED";
	private static final String SUBJECT_IS_CLINITIAN = "SUBJECT_IS_CLINITIAN";
	private static final String SUBJECT_IS_THERAPIST = "SUBJECT_IS_THERAPIST";
	private static final String SUBJECT_IS_PATIENT = "SUBJECT_IS_PATIENT";
	private static final String SUBJECT_IS_OWNER_OF_RESOURCE = "SUBJECT_IS_OWNER_OF_RESOURCE";

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
		supportedAttributes.add(SUBJECT_IS_SAME_AS_RESOURCE);
		supportedAttributes.add(RESOURCE_IS_ASSIGNED);
		supportedAttributes.add(SUBJECT_IS_CLINITIAN);
		supportedAttributes.add(SUBJECT_IS_THERAPIST);
		supportedAttributes.add(SUBJECT_IS_PATIENT);
		supportedAttributes.add(SUBJECT_IS_OWNER_OF_RESOURCE);
	}

	@Override
	public String getModuleName() {
		return "Eurecat PIP JDBC Abstract Attribute Finder";
	}

	@Override
	public Set<String> getAttributeValues(String subjectId, String resourceId,
			String actionId, String environmentId, String attributeId,
			String issuer) throws Exception {
		LOGGER.info("subjectId: " + subjectId);
		LOGGER.info("resourceId: " + resourceId);
		LOGGER.info("actionId: " + actionId);
		LOGGER.info("environmentId: " + environmentId);
		LOGGER.info("attributeId: " + attributeId);
		LOGGER.info("issuer: " + issuer);

		Set<String> values = new HashSet<String>();
		if (attributeId.equalsIgnoreCase(SUBJECT_IS_CARER_OF_RESOURCE))
			values = subjectIsCarerOfResource(subjectId, resourceId);
		if (attributeId.equalsIgnoreCase(SUBJECT_IS_SAME_AS_RESOURCE))
			values = subjectIsSameAsResource(subjectId, resourceId);
		if (attributeId.equalsIgnoreCase(RESOURCE_IS_ASSIGNED))
			values = resourceIsAssigned(resourceId);
		if (attributeId.equalsIgnoreCase(SUBJECT_IS_CLINITIAN))
			values = subjectIsInRole(subjectId, "clinitian");
		if (attributeId.equalsIgnoreCase(SUBJECT_IS_THERAPIST))
			values = subjectIsInRole(subjectId, "therapist");
		if (attributeId.equalsIgnoreCase(SUBJECT_IS_PATIENT))
			values = subjectIsInRole(subjectId, "patient");
		if (attributeId.equalsIgnoreCase(SUBJECT_IS_OWNER_OF_RESOURCE))
			values = subjectIsOwnerOfResource(subjectId, resourceId, "owner");

		LOGGER.info("Response: " + values.toString());
		return values;
	}

	private Set<String> subjectIsOwnerOfResource(String subjectId,
			String resourceId, String relationId) throws Exception {
		String sqlStmt = "SELECT * FROM RELATION WHERE SOURCE_ID ='"
				+ subjectId + "' AND TARGET_ID ='" + resourceId
				+ "' AND RELATION_ID =" + relationId + ";";
		return executeQuery(sqlStmt); 
	}

	private Set<String> subjectIsCarerOfResource(String subjectId,
			String resourceId) throws Exception {
		try {
		resourceId = resourceId.split("\\/")[8];
		}
		catch (java.lang.ArrayIndexOutOfBoundsException e){
			resourceId = resourceId.split("\\/")[7];
		}
		String sqlStmt = "select * from PATIENT_CARER where CARER='"
				+ subjectId + "' and PATIENT='" + resourceId + "';";
		return executeQuery(sqlStmt);
	}

	private Set<String> subjectIsSameAsResource(String subjectId,
			String resourceId) throws Exception {
		String sqlStmt = "select true where '" + subjectId + "' = '"
				+ resourceId + "';";
		return executeQuery(sqlStmt);
	}

	private Set<String> subjectIsInRole(String subjectId, String role)
			throws Exception {
		String sqlStmt = "select * from USER_ROLE where USER_NAME='"
				+ subjectId + "' and ROLE_NAME='" + role + "';";
		return executeQuery(sqlStmt);
	}

	private Set<String> resourceIsAssigned(String resourceId) throws Exception {
		String sqlStmt = "select * from PATIENT_CARER where PATIENT='"
				+ resourceId + "';";
		return executeQuery(sqlStmt);
	}

	private HashSet<String> executeQuery(String sqlStmt) throws Exception {
		LOGGER.info("QUERY: " + sqlStmt);
		HashSet<String> values = new HashSet<String>();
		Connection connection = dataSource.getConnection();
		PreparedStatement prepStmt = connection.prepareStatement(sqlStmt);
		ResultSet resultSet = prepStmt.executeQuery();
		boolean condition = false;
		while (resultSet.next()) {
			condition = true;
		}
		values.add(String.valueOf(condition));
		connection.close();
		return values;
	}

	@Override
	public Set<String> getSupportedAttributes() {
		return supportedAttributes;
	}
}