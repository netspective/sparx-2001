<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>
<%@ taglib prefix="xaf" uri="/WEB-INF/tld/xaf.tld"%>

<%
  String heading = "Project: " + request.getParameter("project_name");
%>

<app:page title="Project Home" heading="<%= heading %>">
<table width='100%' cellpadding='3' cellspacing='0'>
    <tr>
        <td align='left' valign='top'><xaf:query name="project.information" skin="detail"/></td>
        <td align='left' valign='top'><xaf:query name="project.member-persons" skin="report" debug="no"/></td>
    </tr>
</table>

</app:page>