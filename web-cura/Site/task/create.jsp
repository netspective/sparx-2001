<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>
<%@ taglib prefix="xaf" uri="/WEB-INF/tld/xaf.tld"%>

<%
    request.setAttribute("data_cmd", "add");
%>
<app:page title="Create Task" heading="Create Task">

	<xaf:dialog  name="task.registration"/>

</app:page>