<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>
<%@ taglib prefix="xaf" uri="/WEB-INF/tld/sparx.tld"%>

<%
    request.setAttribute("data_cmd", "edit");
%>
<app:page title="Edit Task" heading="Edit Task">

	<xaf:dialog  name="task.registration"/>

</app:page>