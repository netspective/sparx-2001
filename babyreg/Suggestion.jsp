<jsp:directive.taglib prefix="xaf" uri="/WEB-INF/tld/sparx.tld"/>
<jsp:directive.taglib prefix="app" uri="/WEB-INF/tld/page.tld"/>

<app:page title="Baby Registry" heading="Welcome to Baby Registry">
<table>
<tr>
<td>
If you know of something I might need but is not in the list, let me know<br>
You do not have to get for me, it will simply show on the list so that my mommy<br>
and daddy know about it, and people can pick it.
</td>
</tr>
<tr>
<td>
    <xaf:dialog name="Gift.Suggestion"/>
</td>
</tr>
</table>
</app:page>
