<%@ taglib prefix="xaf" uri="/WEB-INF/tld/xaf.tld"%>
<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>

<!-- retrieve the organization name for the page heading/title -->
<xaf:query name="org.name-only" storeType="single-column" store="request-attr:org-name" report="none"/>

<%
    String org_name =  (String) request.getAttribute("org-name");
%>
<app:page title="<%= org_name %>" heading="<%= org_name %>">

	<table>
		<tr valign="top">
			<td><xaf:query name="org.registration" skin="detail"/></td>
			<td><xaf:query name="org.classification" skin="component"/></td>
		</tr>
        <tr valign="top">
            <td colspan="2"><xaf:query name="org.contact-list" skin="report"/></td>
        </tr>
	</table>

</app:page>