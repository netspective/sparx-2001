<%@ taglib prefix="sparx" uri="/WEB-INF/tld/sparx.tld"%>
<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>

<app:page title="The Sparx Collection" heading="Search Books">

	<sparx:query-select-dialog source="searchBooks" name="searchDialog"/>
	
</app:page>