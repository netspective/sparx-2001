<%@ taglib prefix="xaf" uri="/WEB-INF/tld/xaf.tld"%>
<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>


<app:page title="Browse Contacts" heading="Browse Contacts">
    <%
    String url = request.getContextPath() + "/contact/create.jsp";
    %>
<table width='100%' cellpadding='3' cellspacing='0'>
<tr>
    <td align='center'>
    <table>
    <tr><td align='right'><a  href='<%= url %>'>Add New Contact</a></td></tr>
    <tr><td><xaf:query-select-dialog source="Person" name="person_search"/></td></tr>
    </table>
    </td>
</tr>
</table>
</app:page>