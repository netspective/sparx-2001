<%@ page import="java.util.Map,
                 com.netspective.sparx.xaf.security.AuthenticatedUser,
                 java.math.BigDecimal"%>
<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>
<%@ taglib prefix="xaf" uri="/WEB-INF/tld/sparx.tld"%>


<app:page title="Reports" heading="">

     <xaf:query-select-dialog source="Drugs" name="drug_search"/>

</app:page>