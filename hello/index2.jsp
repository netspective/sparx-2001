<jsp:directive.include file="/resources/include/site-header.jsp"/>

	<center>
		<br>
		<font size="5">
			<b>Welcome to Tutorial I, Part II</b>
			<br>Hello World
		</font>
		<p>

		<!-- create the dialog state machine and form HTML -->		
		<sparx:dialog name="tutorial.hello_second"/>

		<p>&nbsp;<p>
		<table width='75%'>
			<tr valign=top>
				<td width='50%' align=center>
					<sparx:panel heading="Browse Source" headingExtra="ACE User is 'ace', Password is 'ace'">
						Try the Sparx <a href="<%= request.getContextPath() + "/ace?_dc.user_id=ace&_dc.password=ace" %>" target="hello-ace">administration console</a> (ACE).<br>
						View the ACE <a href="<%= request.getContextPath() + "/ace/documents/project" %>" target="hello-files">files browser</a> for this app.<br>
						View the <a href="<%= request.getContextPath() + "/ace/documents/project/WEB-INF/ui/dialogs.xml" %>" target="hello-form">Hello World Form XML</a>.<br>
						View the <a href="<%= request.getContextPath() + "/ace/documents/project/WEB-INF/schema/schema.xml" %>" target="hello-schema">Hellow World Schema XML</a>.<br>
						View this <a href="<%= request.getContextPath() + "/ace/documents?browseDoc=" + application.getRealPath(request.getServletPath()) %>" target="hello-jsp">JSP file</a>.<br>
					</sparx:panel>
				</td>
				<td width='50%' align=center>
					<sparx:panel heading="See more examples">
						Try the Sparx <a href="index.jsp">Hello World Tutorial Part I</a>.<br>
						Try the Sparx <a href="../library">Library Tutorial</a>.
					</sparx:panel>
				</td>
			</tr>
		</table>
	</center>

<jsp:directive.include file="/resources/include/site-footer.jsp"/>
