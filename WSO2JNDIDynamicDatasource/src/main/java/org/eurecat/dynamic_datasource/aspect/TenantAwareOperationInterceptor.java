package org.eurecat.dynamic_datasource.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.eurecat.dynamic_datasource.routing.TenantContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TenantAwareOperationInterceptor {

	private static final Logger log = LoggerFactory.getLogger(TenantAwareOperationInterceptor.class);

	@Pointcut(value = "execution(public * *(..))")
	public void anyPublicMethod() {
	}

	@Around("@annotation(tenantAwareOperation)")
	public Object proceed(ProceedingJoinPoint pjp, TenantAwareOperation tenantAwareOperation) throws Throwable {
		try {
			log.debug("Entering TenantAwareOperationInterceptor");
			if (pjp.getArgs().length > 0)
				TenantContextHolder.setTenantDb(String.valueOf(pjp.getArgs()[0]));
			Object result = pjp.proceed();
			TenantContextHolder.clearTenantDb();
			return result;
		} finally {
			TenantContextHolder.clearTenantDb();
		}
	}

}
