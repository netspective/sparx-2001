<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>
<%@ taglib prefix="xaf" uri="/WEB-INF/tld/xaf.tld"%>

<app:page title="Browse Projects" heading="Browse Projects">

<table>
<tr>
    <%
    String url = request.getContextPath() + "/project/index.jsp?cmd=dialog,project.registration,add";
    %>
    <td align='right'><a class='Menu' href='<%= url %>'>Add New Project</a></td>
</tr>
<tr>
    <td>
    <xaf:query-select-dialog source="Project" name="project_search"/>
    </td>
</tr>
</table>
</app:page>