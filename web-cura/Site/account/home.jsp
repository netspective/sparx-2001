<%@ taglib prefix="xaf" uri="/WEB-INF/tld/xaf.tld"%>
<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>


<%
    String org_name =  (String) request.getParameter("org_name");
%>
<app:page title="<%= org_name %>" heading="<%= org_name %>">

	<table width='100%' cellpadding='1' cellspacing='0'>
		<tr valign="top">
			<td><xaf:query name="org.registration" skin="detail"/></td>
			<td><xaf:query name="org.classification" skin="component"/></td>
		</tr>
        <tr valign="top">
            <td colspan="2"><xaf:query name="org.contact-list" skin="report"/></td>
        </tr>
	</table>

</app:page>