<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>
<%@ taglib prefix="xaf" uri="/WEB-INF/tld/sparx.tld"%>

<%
    request.setAttribute("data_cmd", "edit");
%>
<app:page title="Edit Account" heading="Edit Account">

	<xaf:dialog name="org.registration"/>

</app:page>