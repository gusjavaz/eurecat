package org.eurecat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.description.TransportOutDescription;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.log4j.Logger;
import org.apache.synapse.Mediator;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseException;
import org.wso2.carbon.identity.entitlement.stub.EntitlementServiceStub;

public class EntitlementMediator implements Mediator {

	static Logger l = Logger.getLogger("EntitlementMediator");
	static EntitlementServiceStub stub;

	static String entitlementAdminName;
	static String entitlementAdminPassword;
	static String entitlementServerURL;

	public boolean mediate(MessageContext mc) {

		// logMessageProperties(mc);

		String subjectId = (String) mc.getProperty("api.ut.userName");
		// String resourceId = (String) mc.getProperty("api.ut.resource");
		String resourceId = (String) mc.getProperty("REST_FULL_REQUEST_PATH");
		// String actionId = (String) mc.getProperty("api.ut.HTTP_METHOD");
		String actionId = "read";
		String roles = (String) mc.getProperty("roles");
		String[] environmentId = new String[] { roles };

		l.info("Entitlement XACML:");
		l.info("subjectId:" + subjectId);
		l.info("resourceId:" + resourceId);
		l.info("actionId:" + actionId);
		l.info("environmentId:" + roles);
		boolean continueMediation = false;
		String decisionString = "Deny";
		String decisionResponse;
		try {
			if (stub == null)
				stub = getEntitlementStub();
			// decision = stub.getBooleanDecision(subjectId, resourceId,
			// actionId);
			decisionResponse = stub.getDecisionByAttributes(subjectId,
					resourceId, actionId, environmentId);
			String simpleDecision;
			// OMElement obligations;
			// OMElement advice;
			OMElement decisionElement = AXIOMUtil.stringToOM(decisionResponse);
			decisionString = decisionElement.getFirstElement()
					.getFirstElement().getText();
//			 Iterator it = decisionElement.getChildren();
//			 while(it.hasNext()){
//					l.info("Iterator decisionElement: " + it.next());
//			 }
			 
			 // simpleDecision = decisionElement.getFirstChildWithName(new
			// QName("{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}Decision")).getText();
			// obligations = decisionElement.getFirstChildWithName(new
			// QName("Obligations"));
			// advice = decisionElement.getFirstChildWithName(new
			// QName("AdviceExpressions"));
			// l.info("obligations: " + obligations);
			// l.info("advice: " + advice);
			continueMediation = true;

		} catch (Exception e) {
			mc.setProperty("ERROR_CODE", "XACML Exception");
			mc.setProperty("ERROR_MESSAGE", "Exception Determining XACML Policy Access");
			l.error(e);
			throw new SynapseException(
					"Exception Determining XACML Policy Access");
		}
	
		if (!decisionString.equalsIgnoreCase("Permit")) {
			continueMediation = false;
			mc.setProperty("ERROR_CODE", "XACML Access Denied");
			mc.setProperty("ERROR_MESSAGE", decisionString);
			throw new SynapseException("XACML Policy Access Denied");
		}

		l.info("Decision is " + decisionString);

		return continueMediation;
	}

	static void logMessageProperties(MessageContext mc) {
		l.info("Message is " + mc.toString());
		Iterator props = mc.getPropertyKeySet().iterator();
		while (props.hasNext()) {
			String propName = (String) props.next();
			try {
				String propValue = (String) mc.getProperty(propName);
				l.info("Property name:" + propName + " value:" + propValue);
			} catch (Exception e) {
				l.info(e);
			}
		}

		props = mc.getContextEntries().keySet().iterator();
		while (props.hasNext()) {
			String propName = (String) props.next();
			try {
				String propValue = (String) mc.getProperty(propName);
				l.info("Context name:" + propName + " value:" + propValue);
			} catch (Exception e) {
				l.info(e);
			}
		}

	}

	static EntitlementServiceStub getEntitlementStub() {
		Map<String, EntitlementServiceStub> entitlementStubMap = new ConcurrentHashMap<String, EntitlementServiceStub>();
		HttpTransportProperties.Authenticator authenticator;
		authenticator = new HttpTransportProperties.Authenticator();
		authenticator.setUsername(entitlementAdminName);
		authenticator.setPassword(entitlementAdminPassword);
		authenticator.setPreemptiveAuthentication(true);
		EntitlementServiceStub stub = null;
		ConfigurationContext configurationContext;
		try {
			configurationContext = ConfigurationContextFactory
					.createDefaultConfigurationContext();
			HashMap<String, TransportOutDescription> transportsOut = configurationContext
					.getAxisConfiguration().getTransportsOut();
			for (TransportOutDescription transportOutDescription : transportsOut
					.values()) {
				transportOutDescription.getSender().init(configurationContext,
						transportOutDescription);
			}
			stub = new EntitlementServiceStub(configurationContext,
					entitlementServerURL + "EntitlementService");
			ServiceClient client = stub._getServiceClient();
			Options option = client.getOptions();
			option.setManageSession(true);
			option.setProperty(
					org.apache.axis2.transport.http.HTTPConstants.AUTHENTICATE,
					authenticator);
			entitlementStubMap.put(entitlementServerURL, stub);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return stub;
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setDescription(String arg0) {
		// TODO Auto-generated method stub

	}

	public int getTraceState() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setTraceState(int arg0) {
		// TODO Auto-generated method stub

	}

	public boolean isContentAware() {
		// TODO Auto-generated method stub
		return false;
	}

	public int getMediatorPosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getShortDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setMediatorPosition(int arg0) {
		// TODO Auto-generated method stub

	}

	public void setShortDescription(String arg0) {
		// TODO Auto-generated method stub

	}

	public String getEntitlementAdminName() {
		return entitlementAdminName;
	}

	public void setEntitlementAdminName(String entitlementAdminName) {
		this.entitlementAdminName = entitlementAdminName;
	}

	public String getEntitlementAdminPassword() {
		return entitlementAdminPassword;
	}

	public void setEntitlementAdminPassword(String entitlementAdminPassword) {
		this.entitlementAdminPassword = entitlementAdminPassword;
	}

	public String getEntitlementServerURL() {
		return entitlementServerURL;
	}

	public void setEntitlementServerURL(String entitlementServerURL) {
		this.entitlementServerURL = entitlementServerURL;
	}

}