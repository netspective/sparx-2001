<%@ page import="java.util.Map,
                 com.netspective.sparx.xaf.security.AuthenticatedUser,
                 java.math.BigDecimal"%>
<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>
<%@ taglib prefix="xaf" uri="/WEB-INF/tld/sparx.tld"%>



<app:page title="Cura" heading=" ">


<table width='100%' cellpadding='5' cellspacing='0'>
    <%
        AuthenticatedUser user =
                    (AuthenticatedUser) session.getAttribute(com.netspective.sparx.xaf.security.LoginDialog.DEFAULT_ATTRNAME_USERINFO);
            //String personId = (String) user.getUserId();
            Map personRegistration = (Map) user.getAttribute("registration");
            BigDecimal personId = (BigDecimal) personRegistration.get("person_id");
           BigDecimal personType = (BigDecimal) personRegistration.get("person_type");


        request.setAttribute("person_id",personId);
        if (personType.intValue() == 1) // Physician
        {
    %>

        <xaf:query name="cscs.docs-patient-list" skin="report" debug="no"/>
    <% } else if (personType.intValue() == 0)  {// Admin %>
        <xaf:query name="cscs.physician-list" skin="report" debug="no"/>
    <% } %>
</table>


</app:page>