<jsp:directive.taglib prefix="xaf" uri="/WEB-INF/tld/xaf.tld"/>
<jsp:directive.taglib prefix="app" uri="/WEB-INF/tld/page.tld"/>

<!-- retrieve the organization name for the page heading/title -->
<xaf:query name="org.name-only" storeType="single-column" store="request-attr:org-name" report="none"/>

<app:page title="<%= (String) request.getAttribute("org-name") %>" heading="<%= (String) request.getAttribute("org-name") %>">

	<table>
		<tr valign="top">
			<td><xaf:query name="org.registration" skin="detail"/></td>
			<td><xaf:query name="org.classification" skin="component"/></td>
		</tr>
	</table>
	
</app:page>