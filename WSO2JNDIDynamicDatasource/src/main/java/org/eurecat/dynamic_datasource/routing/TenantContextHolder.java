package org.eurecat.dynamic_datasource.routing;

public class TenantContextHolder {

   private static final ThreadLocal<String> contextHolder =
            new ThreadLocal<String>();

   public static void setTenantDb(String tenantId) {
       if(tenantId == null){
           throw new NullPointerException();
       }
      contextHolder.set(tenantId);
   }

   public static String getTenantDb() {
      return (String) contextHolder.get();
   }

   public static void clearTenantDb() {
      contextHolder.remove();
   }
}