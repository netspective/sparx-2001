<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>
<%@ taglib prefix="xaf" uri="/WEB-INF/tld/xaf.tld"%>

<app:page title="Browse Tasks" heading="Browse Tasks">
<table>
<tr>
    <%
        String url = request.getContextPath() + "/task/create.jsp";
    %>
    <td align='right'><a class='Menu' href='<%= url %>'>Add New Task</a></td>
</tr>
<tr>
    <td>
    <xaf:query-select-dialog source="Task" name="task_search"/>
    </td>
</tr>
</table>
</app:page>