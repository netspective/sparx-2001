<%@ page import="java.util.Map,
                 com.netspective.sparx.xaf.security.AuthenticatedUser,
                 java.math.BigDecimal"%>
<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>
<%@ taglib prefix="xaf" uri="/WEB-INF/tld/sparx.tld"%>
<%

AuthenticatedUser user =
                    (AuthenticatedUser) session.getAttribute(com.netspective.sparx.xaf.security.LoginDialog.DEFAULT_ATTRNAME_USERINFO);
            //String personId = (String) user.getUserId();
            Map personRegistration = (Map) user.getAttribute("registration");
            BigDecimal personId = (BigDecimal) personRegistration.get("person_id");


    request.setAttribute("person_id",personId);
%>


<app:page title="Cura" heading="">

     <xaf:query name="cscs.docs-patient-list" skin="report" debug="no"/>

</app:page>