<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>
<%@ taglib prefix="xaf" uri="/WEB-INF/tld/sparx.tld"%>

<app:page title="Browse Accounts" heading="Browse Accounts">
<table width='100%' cellpadding='3' cellspacing='0'>
    <%
    String url = request.getContextPath() + "/account/index.jsp?cmd=dialog,org.registration,add";
    String searchUrl = "";
    if(request.getParameter("_d_exec") != null)
        searchUrl = request.getContextPath() + "/account/index.jsp";
    %>
<tr>
    <td align='center'>
    <table width='100%' cellpadding='3' cellspacing='0'>
    <tr>
        <td align='right'>
        <%
            if (searchUrl.length() > 0)
            {
        %>
        <a  href='<%= searchUrl %>'>Search Account(s)</a> &nbsp;  | &nbsp;
        <%
            }
        %>
        <a  href='<%= url %>'>Add New Account</a>
        </td>
    </tr>
    <tr><td><xaf:query-select-dialog source="Organization" name="org_search"/></td></tr>
    </table>
    </td>
</tr>
</table>
</app:page>