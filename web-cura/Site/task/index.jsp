<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>
<%@ taglib prefix="xaf" uri="/WEB-INF/tld/xaf.tld"%>

<app:page title="Browse Tasks" heading="Browse Tasks">

    <app:link url="/task/create.jsp">Add New Task</app:link><br/>
    <xaf:query-select-dialog source="Task" name="task_search"/>

</app:page>