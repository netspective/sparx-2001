<%@ taglib prefix="xaf" uri="/WEB-INF/tld/sparx.tld"%>
<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>


<%
    String org_name =  (String) request.getParameter("org_name");
%>
<app:page title="<%= org_name %>" heading="<%= org_name %>">
<table width='100%' cellpadding='3' cellspacing='0'>
    <tr>
        <td colspan='2' align='left' valign='top'><xaf:query name="org.registration" skin="detail-full" /></td>
    </tr>
    <tr>
        <td width='50%' align='left' valign='top'><xaf:query name="org.contact-list" skin="report" /></td>
        <td width='50%' align='left' valign='top'>&nbsp;</td>
    </tr>
</table>
</app:page>