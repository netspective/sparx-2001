<jsp:directive.taglib prefix="xaf" uri="/WEB-INF/tld/sparx.tld"/>
<jsp:directive.taglib prefix="app" uri="/WEB-INF/tld/page.tld"/>

<app:page title="Baby Registry" heading="Welcome to Baby Registry">
<table>
<tr>
<td>
These are the gift(s) you picked for me.
</td>
</tr>
<tr>
<td>
    <xaf:query name="Gift.YourGiftList" skin="report"/>
</td>
</tr>
</table>
</app:page>
