<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>
<%@ taglib prefix="xaf" uri="/WEB-INF/tld/xaf.tld"%>

<app:page title="Browse Contacts" heading="Browse Contacts">

	<xaf:query-select-dialog source="Person" name="person_search"/>

</app:page>