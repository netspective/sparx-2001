<!-- this file is included into all pages in the entire site -->

<jsp:directive.taglib prefix="app" uri="/WEB-INF/tld/page.tld"/>
<jsp:directive.taglib prefix="sparx" uri="/WEB-INF/tld/sparx.tld"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%
    com.netspective.sparx.util.config.Configuration sparxConfig = com.netspective.sparx.util.config.ConfigurationManagerFactory.getDefaultConfiguration(pageContext.getServletContext());
    com.netspective.sparx.util.value.ServletValueContext svc = new com.netspective.sparx.util.value.ServletValueContext(pageContext.getServletContext(), (Servlet) pageContext.getPage(), pageContext.getRequest(), pageContext.getResponse());

	String sparxACEUrl = sparxConfig.getTextValue(svc, "sparx.ace.root-url");
    String sparxSampleImagesUrl = sparxConfig.getTextValue(svc, "sparx.shared.images-url") + "/samples";
    
%>	

<html>
	<head>
		<meta http-equiv="content-type" content="text/html;charset=ISO-8859-1">
		<title>Sparx&#153; Sample Application</title>
	</head>

	<body bgcolor="#cccccc" leftmargin="5" marginheight="5" marginwidth="5" topmargin="5">
		<basefont face="Trebuchet MS" size=2>
		<center>
		<table width="100%" border="0" cellspacing="0" cellpadding="0" height="100%">
			<tr height="56">
				<td align="left" valign="top" height="56">
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td align="left" valign="top" width="412"><a href="http://www.netspective.com"><img src="<%= sparxSampleImagesUrl %>/sample-apps-01.gif" alt="" width="412" height="56" border="0"></a></td>
							<td align="left" valign="top" width="100%"><img src="<%= sparxSampleImagesUrl %>/sample-apps-02.gif" alt="" width="100%" height="56" border="0"></td>
							<td align="left" valign="top" width="181">
								<table width="64" border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td align="left" valign="top"><img src="<%= sparxSampleImagesUrl %>/sample-apps-03.gif" alt="" width="181" height="9" border="0"></td>
									</tr>
									<tr>
										<td align="left" valign="top">
											<table width="72" border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td align="left" valign="top"><img src="<%= sparxSampleImagesUrl %>/sample-apps-04.gif" alt="" width="6" height="47" border="0"></td>
													<td align="left" valign="top">
														<table width="64" border="0" cellspacing="0" cellpadding="0">
															<tr height="17">
																<td align="center" valign="middle" bgcolor="white" height="17"><font size=1 face="Arial,Helvetica,Geneva,Swiss,SunSans-Regular"><a href='<%= sparxACEUrl %>'>Admin Console (ACE)</a></font></td>
															</tr>
															<tr>
																<td align="left" valign="top"><img src="<%= sparxSampleImagesUrl %>/sample-apps-07.gif" alt="" width="156" height="30" border="0"></td>
															</tr>
														</table>
													</td>
													<td align="left" valign="top"><img src="<%= sparxSampleImagesUrl %>/sample-apps-06.gif" alt="" width="19" height="47" border="0"></td>
												</tr>
											</table>
										</td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr height="100%">
				<td align="left" valign="top" height="100%">
					<table width="100%" border="0" cellspacing="0" cellpadding="0" height="100%">
						<tr height="100%">
							<td align="left" valign="top" width="15" height="100%" background="<%= sparxSampleImagesUrl %>/sample-apps-08.gif"><img src="<%= sparxSampleImagesUrl %>/sample-apps-spacer.gif" alt="" width="15" height="100%" border="0"></td>
							<td align="left" valign="top" width="100%" height="100%" bgcolor="white">
							<!-- *** START OF BODY CONTENT ** -->