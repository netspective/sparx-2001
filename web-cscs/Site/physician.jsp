<%@ page import="java.util.Map,
                 com.netspective.sparx.xaf.security.AuthenticatedUser"%>
<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>
<%@ taglib prefix="xaf" uri="/WEB-INF/tld/sparx.tld"%>
<%
    request.setAttribute("data_cmd", "add");
%>


<app:page title="Cura" heading="">

     <xaf:dialog name="main.physician_entry"/>


</app:page>