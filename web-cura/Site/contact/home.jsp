<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>
<%@ taglib prefix="xaf" uri="/WEB-INF/tld/sparx.tld"%>

<%
 String heading = request.getParameter("person_name");
 %>
<app:page title="Contact Home" heading="<%= heading %>">
<table>
    <tr>
        <td align='left' valign='top'><xaf:query name="person.information" skin="detail"/></td>
        <td align='left' valign='top'><xaf:query name="person.active-org-memberships" skin="report" debug="no"/></td>
    </tr>
    <tr>
        <td align='left' valign='top'><xaf:query name="person.address-by-id" skin="detail"/></td>
        <td align='left' valign='top'><xaf:query name="person.contact-info" skin="report" debug="no"/></td>
    </tr>

</table>

</app:page>