package org.eurecat;

import java.util.Map;

import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.dto.APIKeyValidationInfoDTO;
import org.wso2.carbon.apimgt.impl.token.JWTGenerator;

public class JWTCustomGenerator extends JWTGenerator {
	
	
	

	public Map populateStandardClaims(APIKeyValidationInfoDTO keyValidationInfoDTO, String apiContext, String version)
			throws APIManagementException {
		System.out.println(
				"************************************************************************API Key Validation Info :"
						+ keyValidationInfoDTO);
		Map claims = super.populateStandardClaims(keyValidationInfoDTO, apiContext, version);
		boolean isApplicationToken = keyValidationInfoDTO.getUserType()
				.equalsIgnoreCase(APIConstants.ACCESS_TOKEN_USER_TYPE_APPLICATION) ? true : false;
		String dialect = getDialectURI();
		if (claims.get(dialect + "/enduser") != null) {
			if (isApplicationToken) {
				claims.put(dialect + "/enduser", "null");
				claims.put(dialect + "/enduserTenantId", "null");
			} else {
				String enduser = (String) claims.get(dialect + "/enduser");
				if (enduser.endsWith("@carbon.super")) {
					enduser = enduser.replace("@carbon.super", "");
					claims.put(dialect + "/enduser", enduser);
				}
			}
		}
		return claims;
	}

	public Map populateCustomClaims(APIKeyValidationInfoDTO keyValidationInfoDTO, String apiContext, String version,
			String accessToken) throws APIManagementException {
		System.out.println("************************************************************************Access Token is :"
				+ accessToken);
		boolean isApplicationToken = keyValidationInfoDTO.getUserType()
				.equalsIgnoreCase(APIConstants.ACCESS_TOKEN_USER_TYPE_APPLICATION) ? true : false;
		if (getClaimsRetriever() != null) {
			if (isApplicationToken) {
				return null;
			} else {
				return super.populateCustomClaims(keyValidationInfoDTO, apiContext, version, accessToken);
			}
		}
		return null;
	}
}