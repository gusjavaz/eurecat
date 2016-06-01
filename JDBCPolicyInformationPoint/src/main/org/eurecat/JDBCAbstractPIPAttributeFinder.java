package org.eurecat;

import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.balana.XACMLConstants;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.BagAttribute;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.carbon.identity.entitlement.PDPConstants;
import org.wso2.carbon.identity.entitlement.pip.PIPAttributeFinder;
 
/**
 * @author gvazquez
 *
 */
public class JDBCAbstractPIPAttributeFinder implements PIPAttributeFinder {

	private DataSource dataSource;
	private Connection conn;
    private static Logger LOGGER = LoggerFactory.getLogger("JDBCAbstractPIPAttributeFinder");


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

//	@Override
//	public Set<String> getAttributeValues(String subjectId, String resourceId,
//			String actionId, String environmentId, String attributeId,
//			String issuer) throws Exception {
//		System.out.println("subjectId: " + subjectId);
//		System.out.println("resourceId: " + resourceId);
//		System.out.println("actionId: " + actionId);
//		System.out.println("environmentId: " + environmentId);
//		System.out.println("attributeId: " + attributeId);
//		System.out.println("issuer: " + issuer);
//
//		Set<String> values = new HashSet<String>();
//		if (attributeId.equalsIgnoreCase(SUBJECT_IS_CARER_OF_RESOURCE))
//			values = subjectIsCarerOfResource(subjectId, resourceId);
//		if (attributeId.equalsIgnoreCase(SUBJECT_IS_SAME_AS_RESOURCE))
//			values = subjectIsSameAsResource(subjectId, resourceId);
//		if (attributeId.equalsIgnoreCase(RESOURCE_IS_ASSIGNED))
//			values = resourceIsAssigned(resourceId);
//		if (attributeId.equalsIgnoreCase(SUBJECT_IS_CLINITIAN))
//			values = subjectIsInRole(subjectId, "clinitian");
//		if (attributeId.equalsIgnoreCase(SUBJECT_IS_THERAPIST))
//			values = subjectIsInRole(subjectId, "therapist");
//		if (attributeId.equalsIgnoreCase(SUBJECT_IS_PATIENT))
//			values = subjectIsInRole(subjectId, "patient");
//		if (attributeId.equalsIgnoreCase(SUBJECT_IS_OWNER_OF_RESOURCE))
//			values = subjectIsOwnerOfResource(subjectId, resourceId, "owner");
//
//		LOGGER.info("Response: " + values.toString());
//		return values;
//	}

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

	@Override
	public void clearCache() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearCache(String[] arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
    public Set<String> getAttributeValues(URI attributeType, URI attributeId, URI category,
            String issuer, EvaluationCtx evaluationCtx) throws Exception {
	    EvaluationResult subject;
        String subjectId = null;
        EvaluationResult resource;
        String resourceId = null;
        EvaluationResult action;
        String actionId = null;
        EvaluationResult environment;
        String environmentId = null;
        Set<String> attributeValues = null;
        
        subject = evaluationCtx.getAttribute(new URI(StringAttribute.identifier), new URI(
                PDPConstants.SUBJECT_ID_DEFAULT), issuer, new URI(XACMLConstants.SUBJECT_CATEGORY));
        if (subject != null && subject.getAttributeValue() != null &&
            subject.getAttributeValue().isBag()) {
            BagAttribute bagAttribute = (BagAttribute) subject.getAttributeValue();
            if (bagAttribute.size() > 0) {
                subjectId = ((AttributeValue) bagAttribute.iterator().next()).encode();
                if (LOGGER.isDebugEnabled()) {
                	LOGGER.debug(String.format("Finding attributes for the subject %1$s",
                                            subjectId));
                }
            }
        }

        resource = evaluationCtx.getAttribute(new URI(StringAttribute.identifier), new URI(
                PDPConstants.RESOURCE_ID_DEFAULT), issuer, new URI(XACMLConstants.RESOURCE_CATEGORY));
        if (resource != null && resource.getAttributeValue() != null &&
            resource.getAttributeValue().isBag()) {
            BagAttribute bagAttribute = (BagAttribute) resource.getAttributeValue();
            if (bagAttribute.size() > 0) {
                resourceId = ((AttributeValue) bagAttribute.iterator().next()).encode();
                if (LOGGER.isDebugEnabled()) {
                	LOGGER.debug(String.format("Finding attributes for the resource %1$s",
                                            resourceId));
                }
            }
        }

        action = evaluationCtx.getAttribute(new URI(StringAttribute.identifier), new URI(
                PDPConstants.ACTION_ID_DEFAULT), issuer, new URI(XACMLConstants.ACTION_CATEGORY));
        if (action != null && action.getAttributeValue() != null &&
            action.getAttributeValue().isBag()) {
            BagAttribute bagAttribute = (BagAttribute) action.getAttributeValue();
            if (bagAttribute.size() > 0) {
                actionId = ((AttributeValue) bagAttribute.iterator().next()).encode();
                if (LOGGER.isDebugEnabled()) {
                	LOGGER.debug(String.format("Finding attributes for the action %1$s",
                                            actionId));
                }
            }
        }

        environment = evaluationCtx.getAttribute(new URI(StringAttribute.identifier), new URI(
                PDPConstants.ENVIRONMENT_ID_DEFAULT), issuer, new URI(XACMLConstants.ENT_CATEGORY));
        if (environment != null && environment.getAttributeValue() != null &&
            environment.getAttributeValue().isBag()) {
            BagAttribute bagAttribute = (BagAttribute) environment.getAttributeValue();
            if (bagAttribute.size() > 0) {
                environmentId = ((AttributeValue) bagAttribute.iterator().next()).encode();
                if (LOGGER.isDebugEnabled()) {
                	LOGGER.debug(String.format("Finding attributes for the environment %1$s",
                                            environmentId));
                }
            }
        }

		System.out.println("EvaluationCtx: " + evaluationCtx);
//		System.out.println("resourceId: " + resourceId);
//		System.out.println("actionId: " + actionId);
//		System.out.println("environmentId: " + environmentId);
//		System.out.println("attributeId: " + attributeId);
//		System.out.println("issuer: " + issuer);
//
		return null;
	}

	@Override
	public boolean overrideDefaultCache() {
		// TODO Auto-generated method stub
		return false;
	}
}