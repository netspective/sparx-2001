<jsp:directive.taglib prefix="xaf" uri="/WEB-INF/tld/sparx.tld"/>
<jsp:directive.taglib prefix="app" uri="/WEB-INF/tld/page.tld"/>

<app:page title="Baby Registry" heading="Welcome to Baby Registry">
<table>
<tr>
These are all of the items that have already been picked or bought.
</tr>
<tr>
<td>
    <xaf:query name="Gift.GiftsPicked" skin="report"/>
</td>
</tr>
</table>
</app:page>
