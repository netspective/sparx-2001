<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>
<%@ taglib prefix="xaf" uri="/WEB-INF/tld/xaf.tld"%>

<app:page title="Cura" heading="Welcome to Cura">


<table width='100%' cellpadding='5' cellspacing='0'>
    <tr>
        <td align='left' valign='top'><xaf:query name="person.active-tasks" skin="report" debug="no"/></td>
    </tr>
    <tr>
        <td align='left' valign='top'><xaf:query name="person.active-projects" skin="report" debug="no"/></td>
    </tr>

</table>


</app:page>