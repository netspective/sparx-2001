<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>
<%@ taglib prefix="xaf" uri="/WEB-INF/tld/sparx.tld"%>

<%
    String heading = "Account: " + request.getParameter("org_name");
    request.setAttribute("data_cmd", "add");
%>
<app:page title="Add Organization Member" heading="<%= heading %>">

    <xaf:dialog name="org.person_registration" />

</app:page>