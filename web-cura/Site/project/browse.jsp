<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>
<%@ taglib prefix="xaf" uri="/WEB-INF/tld/xaf.tld"%>

<app:page title="Browse Projects" heading="Browse Projects">

	<xaf:query-select-dialog source="Project" name="project_search"/>

</app:page>