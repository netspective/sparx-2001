<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>
<%@ taglib prefix="xaf" uri="/WEB-INF/tld/sparx.tld"%>

<%
    request.setAttribute("data_cmd", "add");
%>
<app:page title="Register Contact" heading="Register Contact">

	<xaf:dialog  name="person.registration"/>

</app:page>