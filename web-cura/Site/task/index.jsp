<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>
<%@ taglib prefix="xaf" uri="/WEB-INF/tld/xaf.tld"%>

<app:page title="Browse Tasks" heading="Browse Tasks">
<table width='100%' cellpadding='3' cellspacing='0'>
    <%
        String url = request.getContextPath() + "/task/create.jsp?task_type=2";
    %>
<tr>
    <td align='center'>
    <table>
    <tr><td align='right'><a  href='<%= url %>'>Add New Task</a></td></tr>
    <tr><td><xaf:query-select-dialog source="Task" name="task_search"/></td></tr>
    </table>
    </td>
</tr>
</table>
</app:page>