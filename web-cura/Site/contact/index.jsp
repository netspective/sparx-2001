<%@ taglib prefix="xaf" uri="/WEB-INF/tld/xaf.tld"%>
<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>


<app:page title="Browse Contacts" heading="Browse Contacts">
<table>
<tr>
    <%
    String url = request.getContextPath() + "/contact/create.jsp";
    %>
    <td align='right'><a class='Menu' href='<%= url %>'>Add New Contact</a></td>
</tr>
<tr>
    <td>
    <xaf:query-select-dialog source="Person" name="person_search"/>
    </td>
</tr>
</table>
</app:page>