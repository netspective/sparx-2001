<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>
<%@ taglib prefix="xaf" uri="/WEB-INF/tld/sparx.tld"%>

<app:page title="Browse Projects" heading="Browse Projects">

<table width='100%' cellpadding='3' cellspacing='0'>
    <%
    String createUrl = request.getContextPath() + "/project/create.jsp";
    String searchUrl = "";
    if(request.getParameter("_d_exec") != null)
        searchUrl = request.getContextPath() + "/project/index.jsp";

    %>
<tr>
    <td align='center'>
    <table  width='100%' cellpadding='3' cellspacing='0'>
    <tr>
        <td align='right'>
        <%
            if (searchUrl.length() > 0)
            {
        %>
        <a  href='<%= searchUrl %>'>Search Project(s)</a> &nbsp;  | &nbsp;
        <%
            }
        %>
        <a  href='<%= createUrl %>'>Add New Project</a>
        </td>
    </tr>
    <tr><td><xaf:query-select-dialog source="Project" name="project_search"/></td></tr>
    </table>
    </td>
</tr>
</table>
</app:page>