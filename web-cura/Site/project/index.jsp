<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>
<%@ taglib prefix="xaf" uri="/WEB-INF/tld/xaf.tld"%>

<app:page title="Browse Projects" heading="Browse Projects">

<table width='100%' cellpadding='3' cellspacing='0'>
    <%
    String url = request.getContextPath() + "/project/index.jsp?cmd=dialog,project.registration,add";
    %>
<tr>
    <td align='center'>
    <table>
    <tr><td align='right'><a  href='<%= url %>'>Add New Project</a></td></tr>
    <tr><td><xaf:query-select-dialog source="Project" name="project_search"/></td></tr>
    </table>
    </td>
</tr>
</table>
</app:page>