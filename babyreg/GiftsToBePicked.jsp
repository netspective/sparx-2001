<jsp:directive.taglib prefix="xaf" uri="/WEB-INF/tld/sparx.tld"/>
<jsp:directive.taglib prefix="app" uri="/WEB-INF/tld/page.tld"/>

<app:page title="Baby Registry" heading="Welcome to Baby Registry">
<table>
<tr>
<td>
Pick a gift from this list by clicking on the 'Pick' link beside it.<br><br>
</td>
</tr>
<tr valign="top">
<td>
    <xaf:query name="Gift.GiftsToPick"/>
</td>
</tr>
</table>
</app:page>
