<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>
<%@ taglib prefix="xaf" uri="/WEB-INF/tld/sparx.tld"%>

<%
    request.setAttribute("data_cmd", "add");
%>
<app:page title="Add Project" heading="Add Project">

	<xaf:dialog name="project.registration"/>

</app:page>