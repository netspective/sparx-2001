<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>
<%@ taglib prefix="xaf" uri="/WEB-INF/tld/sparx.tld"%>

<%
 String heading = request.getParameter("person_name");
 %>
<app:page title="Contact Home" heading="<%= heading %>">
<table width='100%' cellpadding='3' cellspacing='0'>
    <tr>
        <td colspan="2" align='left' valign='top'><xaf:query name="person.information" skin="detail-full"/></td>
    </tr>
    <tr>
        <td colspan='2'  align='left' valign='top'><xaf:query name="person.address-by-id" skin="detail-full"/></td>
    </tr>
    <tr>
        <td width='50%' align='left' valign='top'><xaf:query name="person.active-org-memberships" skin="report" debug="no"/></td>
        <td width='50%' align='left' valign='top'><xaf:query name="person.contact-info" skin="report" debug="no"/></td>
    </tr>

</table>

</app:page>