<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>
<%@ taglib prefix="xaf" uri="/WEB-INF/tld/sparx.tld"%>

<%
 String heading = "Contact: " + request.getParameter("person_name");
 %>
<app:page title="Contact Home" heading="<%= heading %>">
<table width='100%' cellpadding='3' cellspacing='0'>
    <tr>
        <td align='left' valign='top'><xaf:query name="person.information" skin="detail-compressed"/></td>
        <td align='left' valign='top'><xaf:query name="person.active-org-memberships" skin="report-compressed" debug="no"/></td>
    </tr>

</table>

</app:page>