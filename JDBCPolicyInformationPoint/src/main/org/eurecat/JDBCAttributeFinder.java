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

import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.xacml3.Attributes;
import org.wso2.carbon.identity.entitlement.pip.AbstractPIPAttributeFinder;

/**
 * @author gvazquez
 *
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
	private Set<String> supportedAttributes = new TreeSet<String>();

	private static final String SUBJECT_IS_CARER_OF_RESOURCE = "SUBJECT_IS_CARER_OF_RESOURCE";
	private static final String SUBJECT_IS_SAME_AS_RESOURCE = "SUBJECT_IS_SAME_AS_RESOURCE";
	private static final String RESOURCE_IS_ASSIGNED = "RESOURCE_IS_ASSIGNED";
	private static final String SUBJECT_IS_CLINITIAN = "SUBJECT_IS_CLINITIAN";
	private static final String SUBJECT_IS_THERAPIST = "SUBJECT_IS_THERAPIST";
	private static final String SUBJECT_IS_PATIENT = "SUBJECT_IS_PATIENT";

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
	}

	@Override
	public String getModuleName() {
		return "Eurecat PIP Attribute Finder";
	}
//
//	@Override
//	public Set<String> getAttributeValues(URI arg0, URI arg1, URI arg2,
//			String arg3, EvaluationCtx arg4) throws Exception {
//		System.out.println("arg0: " + arg0);
//		System.out.println("arg1: " + arg1);
//		System.out.println("arg2: " + arg2);
//		System.out.println("arg3: " + arg3);
//		Set<Attributes> attributes = arg4.getRequestCtx().getAttributesSet();
//		String[] stringAttr = new String[attributes.size()];
//		attributes.toArray(stringAttr);
//		for (int i=0;i<stringAttr.length;i++){
//			System.out.println("Attribute: " +stringAttr[i]);
//		}
//		System.out.println("EvaluationCtx: " + arg4);
//		return super.getAttributeValues(arg0, arg1, arg2, arg3, arg4);
//	}

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

		Set<String> values = new HashSet<String>();
		resourceId = resourceId.split("\\/")[2];
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

		System.out.println("Response: " + values.toString());
		return values;
	}

	private Set<String> subjectIsCarerOfResource(String subjectId,
			String resourceId) throws Exception {
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
		System.out.println("QUERY: " + sqlStmt);
		HashSet<String> values = new HashSet<String>();
		Connection connection = dataSource.getConnection();
		PreparedStatement prepStmt = connection.prepareStatement(sqlStmt);
		ResultSet resultSet = prepStmt.executeQuery();
		boolean condition = false;
		while (resultSet.next()) {
			condition = true;
		}
		values.add(String.valueOf(condition));
		return values;
	}

	@Override
	public Set<String> getSupportedAttributes() {
		return supportedAttributes;
	}
}