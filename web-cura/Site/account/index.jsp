<jsp:directive.taglib prefix="app" uri="/WEB-INF/tld/page.tld"/>
<jsp:directive.taglib prefix="xaf" uri="/WEB-INF/tld/xaf.tld"/>

<app:page title="Browse Accounts" heading="Browse Accounts">
	
	<xaf:query-select-dialog source="Organization" name="org_search"/>
	
</app:page>