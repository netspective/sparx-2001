<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>
<%@ taglib prefix="xaf" uri="/WEB-INF/tld/xaf.tld"%>

<app:page title="Project Home" heading="Project Home">
<table>
    <tr>
        <td align='left' valign='top'><xaf:query name="project.information" skin="detail"/></td>
        <td align='left' valign='top'><xaf:query name="project.member-persons" skin="report" debug="no"/></td>
    </tr>
</table>

</app:page>