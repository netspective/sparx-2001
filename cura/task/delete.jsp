<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>
<%@ taglib prefix="xaf" uri="/WEB-INF/tld/sparx.tld"%>

<%
    request.setAttribute("data_cmd", "delete");
%>
<app:page title="Delete Task" heading="Delete Task">

	<xaf:dialog  name="task.registration"/>

</app:page>