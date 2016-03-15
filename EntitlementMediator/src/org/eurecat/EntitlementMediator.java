package org.eurecat;

import java.rmi.RemoteException;
import java.util.Iterator;

import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;
import org.apache.synapse.Mediator;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.identity.entitlement.stub.EntitlementServiceException;
import org.wso2.carbon.identity.entitlement.stub.EntitlementServiceStub;
import org.wso2.carbon.identity.entitlement.stub.dto.EntitledAttributesDTO;

public class EntitlementMediator implements Mediator {

	private static Logger l = Logger.getLogger("EntitlementMediator");

	@Override
	public boolean mediate(MessageContext mc) {
		Iterator props = mc.getPropertyKeySet().iterator();
		while (props.hasNext()) {
			String propName = (String) props.next();
			try {
				String propValue = (String) mc.getProperty(propName);
				l.debug("Property name:" + propName + " value:" + propValue);
			} catch (Exception e) {
				l.debug(e);
			}
		}

		String subjectId = (String) mc.getProperty("api.ut.userName");
		String resourceId = (String) mc.getProperty("api.ut.resource");
		String actionId = (String) mc.getProperty("api.ut.HTTP_METHOD");
		EntitlementServiceStub stub = null;
		boolean decision = false;
		try {
			stub = new EntitlementServiceStub();
			decision = stub.getBooleanDecision(subjectId, resourceId, actionId);
			decision = stub.getBooleanDecision(subjectId, resourceId, actionId);
		} catch (AxisFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EntitlementServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return decision;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDescription(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getTraceState() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTraceState(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isContentAware() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getMediatorPosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getShortDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMediatorPosition(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setShortDescription(String arg0) {
		// TODO Auto-generated method stub

	}

}