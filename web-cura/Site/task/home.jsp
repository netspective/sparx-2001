<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>
<%@ taglib prefix="xaf" uri="/WEB-INF/tld/sparx.tld"%>

<app:page title="Task Home" heading="Task Home">
<table width='100%' cellpadding='3' cellspacing='0'>
    <tr>
        <td colspan='2' align='left' valign='top'><xaf:query name="task.information" skin="detail-full" /></td>
    </tr>
    <tr>
        <td width='50%' align='left' valign='top'><xaf:query name="task.assigned-members" skin="report" /></td>
        <td width='50%' align='left' valign='top'> <xaf:query name="task.child-task-list-report" skin="report" /></td>
    </tr>
</table>


</app:page>