<jsp:directive.page language="java" import="com.netspective.sparx.xaf.navigate.*"/>
<jsp:directive.taglib prefix="app" uri="/WEB-INF/tld/page.tld"/>

<jsp:scriptlet>
	NavigateFileSystemServlet navigator = (NavigateFileSystemServlet) request.getAttribute("NavigateFileSystemServlet");
	FileSystemContext fsContext = (FileSystemContext) request.getAttribute("NavigateFileSystemServlet.fsContext");
	if(navigator == null)
		navigator = (NavigateFileSystemServlet) session.getAttribute("NavigateFileSystemServlet");
	if(fsContext == null)
		fsContext = (FileSystemContext) session.getAttribute("NavigateFileSystemServlet.fsContext");

	if(navigator == null || fsContext == null)
	{
		throw new Exception("navigator and fsContext are not available in the request-attr or session.");
	}
</jsp:scriptlet>

<app:page title="<%= fsContext.getActivePath().getEntryCaption() %>" heading="<%= fsContext.getActivePath().getEntryCaption() %>">
	<jsp:expression>navigator.getChildrenHtml(request, fsContext)</jsp:expression>
</app:page>