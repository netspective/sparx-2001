<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>
<%@ taglib prefix="xaf" uri="/WEB-INF/tld/xaf.tld"%>

<%
    String heading = request.getParameter("org_name");
    request.setAttribute("data_cmd", "edit");
%>
<app:page title="Add Organization Member" heading="<%= heading %>">

    <xaf:dialog name="org.person_registeration" />

</app:page>