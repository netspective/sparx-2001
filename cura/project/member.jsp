<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>
<%@ taglib prefix="xaf" uri="/WEB-INF/tld/sparx.tld"%>

<%
    String heading = "Project: " + request.getParameter("project_name");
    request.setAttribute("data_cmd", "edit");
%>
<app:page title="Add Project Member" heading="<%= heading %>">

    <xaf:dialog name="project.person_registeration" />

</app:page>