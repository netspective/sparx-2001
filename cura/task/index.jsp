<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>
<%@ taglib prefix="xaf" uri="/WEB-INF/tld/sparx.tld"%>

<%
    String createUrl = request.getContextPath() + "/task/create.jsp?task_type=2";
    String searchUrl = "";
    if(request.getParameter("_d_exec") != null)
        searchUrl = request.getContextPath() + "/task/index.jsp";
%>
<app:page title="Browse Tasks" heading="Browse Tasks">
<table width='100%' cellpadding='3' cellspacing='0'>
<tr>
    <td align='center'>
    <table  width='100%' cellpadding='3' cellspacing='0'>
    <tr>
        <td align='right'>
        <%
            if (searchUrl.length() > 0)
            {
        %>
        <a  href='<%= searchUrl %>'>Search Task(s)</a> &nbsp;  | &nbsp;
        <%
            }
        %>
        <a  href='<%= createUrl %>'>Add New Task</a>
        </td>
    </tr>
    <tr><td><xaf:query-select-dialog source="Task" name="task_search"/></td></tr>
    </table>
    </td>
</tr>
</table>
</app:page>