<jsp:directive.taglib prefix="xaf" uri="/WEB-INF/tld/sparx.tld"/>
<jsp:directive.taglib prefix="app" uri="/WEB-INF/tld/page.tld"/>

<app:page title="Baby Registry" heading="Welcome to Baby Registry">
<table>
<tr valign="top">
<td>

    <xaf:query-select-dialog source="itemList" name="List"/>

</td>
</tr>
</table>
</app:page>
