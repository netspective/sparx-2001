<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>
<%@ taglib prefix="xaf" uri="/WEB-INF/tld/xaf.tld"%>

<app:page title="Browse Accounts" heading="Browse Accounts">
<table>
<tr>
    <%
    String url = request.getContextPath() + "/account/index.jsp?cmd=dialog,org.registration,add";
    %>
    <td align='right'><a class='Menu' href='<%= url %>'>Add New Account</a></td>
</tr>
<tr>
    <td>
	<xaf:query-select-dialog source="Organization" name="org_search"/>
	</td>
</tr>
</table>
</app:page>