<%@ taglib prefix="sparx" uri="/WEB-INF/tld/sparx.tld"%>
<%@ taglib prefix="app" uri="/WEB-INF/tld/page.tld"%>

<app:page title="The Sparx Collection" heading="The Sparx Collection">

	<sparx:query name="library.sel_all_books" skin="record-editor"/>
    <!--
	<sparx:query name="library.sel_all_books" skin="record-viewer"/>
    -->

	<table>
		<tr valign=top>
			<td width='50%' align=center>
				<sparx:panel heading="Browse Source" headingExtra="ACE User is 'ace', Password is 'ace'">
					<nobr>
					Try the Sparx <a href="<%= request.getContextPath() + "/ace?_dc.user_id=ace&_dc.password=ace" %>" target="hello-ace">administration console</a> (ACE).<br>
					View the ACE <a href="<%= request.getContextPath() + "/ace/documents/project" %>" target="library-files">files browser</a> for this app.<br>
					View the <a href="<%= request.getContextPath() + "/ace/documents/project/WEB-INF/ui/dialogs.xml" %>" target="library-form">Library Form XML</a>.<br>
					View the <a href="<%= request.getContextPath() + "/ace/documents/project/WEB-INF/classes/app/form/BookInfo.java" %>" target="library-java">Library Form Java Class</a>.<br>
					View the <a href="<%= request.getContextPath() + "/ace/documents/project/WEB-INF/sql/statements.xml" %>" target="library-sql">Library SQL XML</a>.<br>
					View the <a href="<%= request.getContextPath() + "/ace/documents/project/WEB-INF/schema/schema.xml" %>" target="library-schema">Library Schema XML</a>.<br>
					View this <a href="<%= request.getContextPath() + "/ace/documents?browseDoc=" + application.getRealPath(request.getServletPath()) %>" target="library-jsp">JSP file</a>.<br>
					<nobr>
				</sparx:panel>
			</td>
			<td width='50%' align=center>
				<sparx:panel heading="See more examples">
					<nobr>
					Try the Sparx <a href="../hello">Hello World Tutorial Part I</a>.<br>
					Try the Sparx <a href="../hello/index2.jsp">Hello World Tutorial Part II</a>.
					</nobr>
				</sparx:panel>
			</td>
		</tr>
	</table>
</app:page>