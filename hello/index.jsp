<jsp:directive.include file="/resources/include/site-header.jsp"/>

	<center>
		<br>
		<font size="5">
			<b>Welcome to Tutorial I, Part I</b>
			<br>Hello World
		</font>
		<p>
		<sparx:dialog name="tutorial.hello_first"/>
		<p>
		<font size="2">
		Click <a href="<%= request.getContextPath() + "/ace/documents/project?_dc.user_id=ace&_dc.password=ace" %>" target="hello-ace">here</a> to view the Sparx administration console (ACE) source browser for this app.<br>
		Click <a href="index2.jsp">here</a> to try Sparx Hello World Tutorial II.
		</font>
	</center>

<jsp:directive.include file="/resources/include/site-footer.jsp"/>
