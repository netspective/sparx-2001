<jsp:directive.taglib prefix="xaf" uri="/WEB-INF/tld/sparx.tld"/>
<jsp:directive.taglib prefix="app" uri="/WEB-INF/tld/page.tld"/>

<app:page title="Baby Registry" heading="Welcome to Baby Registry">
<table>
<tr>
<td>
If already got me something, tell me about it so that somebody else doesn't<br>
get me the same thing.
</td>
</tr>
<tr>
<td>
    <xaf:dialog name="Gift.NewGift"/>
</td>
</tr>
</table>
</app:page>
