<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>
<%@ taglib prefix="xaf" uri="/WEB-INF/tld/sparx.tld"%>

<%
  String heading = "Project: " + request.getParameter("project_name");
%>

<app:page title="Project Home" heading="<%= heading %>">
<table width='100%' cellpadding='1' cellspacing='0'>
    <tr>
        <td colspan='2' align='left' valign='top'><xaf:query name="project.information" skin="detail-full"/></td>
    </tr>
    <tr>
        <td align='left' valign='top'><xaf:query name="project.member-persons-report" skin="report" debug="no"/></td>
        <td align='left' valign='top'> <xaf:query name="project.member-tasks-report" skin="report" debug="no"/></td>
    </tr>
</table>

</app:page>