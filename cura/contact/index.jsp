<%@ taglib prefix="xaf" uri="/WEB-INF/tld/sparx.tld"%>
<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>


<app:page title="Browse Contacts" heading="Browse Contacts">
    <%
    String createUrl = request.getContextPath() + "/contact/create.jsp";
    String searchUrl = "";
    if(request.getParameter("_d_exec") != null)
        searchUrl = request.getContextPath() + "/contact/index.jsp";

    %>
<table width='100%' cellpadding='3' cellspacing='0'>
<tr>
    <td align='center'>
    <table width='100%' cellpadding='0' cellspacing='0'>
    <tr>
        <td align='right'>
        <%
            if (searchUrl.length() > 0)
            {
        %>
        <a  href='<%= searchUrl %>'>Search Contact(s)</a> &nbsp;  | &nbsp;
        <%
            }
        %>
        <a  href='<%= createUrl %>'>Add New Contact</a>
        </td>
    </tr>
    <tr><td><xaf:query-select-dialog source="Person" name="person_search"/></td></tr>
    </table>
    </td>
</tr>
</table>
</app:page>