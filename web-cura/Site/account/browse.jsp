<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>
<%@ taglib prefix="xaf" uri="/WEB-INF/tld/xaf.tld"%>

<app:page title="Browse Accounts" heading="Browse Accounts">
	
	<xaf:query-select-dialog source="Organization" name="org_search"/>
	
</app:page>