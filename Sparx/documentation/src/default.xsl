<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="1.0"
				xmlns:xalan="http://xml.apache.org/xalan" exclude-result-prefixes="xalan">

<xsl:output method="html"/>

<xsl:param name="masthead-bgcolor">#333333</xsl:param>
<xsl:param name="menu-tab-color">#660000</xsl:param>
<xsl:param name="active-menu-tab-color">#EEEEEE</xsl:param>
<xsl:param name="channel-color">#660000</xsl:param>
<xsl:param name="channel-frame-color">#E7B89F</xsl:param>

<xsl:variable name="structure" select="document('structure.xml')/structure"/>
<xsl:variable name="home-page" select="$structure/page[@name = 'index']"/>
<xsl:variable name="xaf-xsd" select="document('xaf.xsd')/xsd:schema"/>
<xsl:variable name="xaf-xsdn" select="document('xaf.xsdn')"/>
<xsl:variable name="xif-xsd" select="document('xif.xsd')/xsd:schema"/>
<xsl:variable name="xif-xsdn" select="document('xif.xsdn')"/>

<xsl:template match="*">
	<xsl:param name="active-page"/>
	<xsl:param name="root-dir"/>
	<xsl:param name="resources-dir"/>
	<xsl:param name="images-dir"/>
	<xsl:copy>
		<xsl:for-each select="@*">
			<xsl:copy/>
		</xsl:for-each>
		<xsl:apply-templates>
			<xsl:with-param name="active-page" select="$active-page"/>
			<xsl:with-param name="root-dir" select="$root-dir"/>
			<xsl:with-param name="resources-dir" select="$resources-dir"/>
			<xsl:with-param name="images-dir" select="$images-dir"/>
		</xsl:apply-templates>
	</xsl:copy>
</xsl:template>

<xsl:template match="space"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></xsl:template>

<xsl:template name="structure">
	<xsl:param name="active-page"/>
	<xsl:param name="root-dir"/>
	<xsl:param name="resources-dir"/>
	<xsl:param name="images-dir"/>
	<xsl:param name="page"/>
	<ol>
		<xsl:for-each select="$page/page">
			<li>
				<xsl:variable name="heading">
					<xsl:choose>
						<xsl:when test="@heading"><xsl:value-of select="@heading"/></xsl:when>
						<xsl:otherwise><xsl:value-of select="@caption"/></xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<a class="navigation" href="{concat($root-dir, @name, '.html')}"><xsl:value-of select="$heading"/></a>
				<xsl:call-template name="structure">
					<xsl:with-param name="page" select="."/>
					<xsl:with-param name="active-page" select="$active-page"/>
					<xsl:with-param name="root-dir" select="$root-dir"/>
					<xsl:with-param name="resources-dir" select="$resources-dir"/>
					<xsl:with-param name="images-dir" select="$images-dir"/>
				</xsl:call-template>
			</li>
		</xsl:for-each>
	</ol>
</xsl:template>

<xsl:template match="sitemap">
	<xsl:param name="active-page"/>
	<xsl:param name="root-dir"/>
	<xsl:param name="resources-dir"/>
	<xsl:param name="images-dir"/>
	<xsl:call-template name="structure">
		<xsl:with-param name="page" select="$structure/page"/>
		<xsl:with-param name="active-page" select="$active-page"/>
		<xsl:with-param name="root-dir" select="$root-dir"/>
		<xsl:with-param name="resources-dir" select="$resources-dir"/>
		<xsl:with-param name="images-dir" select="$images-dir"/>
	</xsl:call-template>
</xsl:template>

<xsl:template match="page">
	<xsl:variable name="page-name" select="@name"/>
	<xsl:variable name="active-page" select="$structure//page[@name = $page-name]"/>
	<xsl:variable name="active-level" select="$active-page/@level"/>
	<xsl:variable name="active-heading"><xsl:choose><xsl:when test="$active-page/@heading"><xsl:value-of select="$active-page/@heading"/></xsl:when><xsl:otherwise><xsl:value-of select="$active-page/@caption"/></xsl:otherwise></xsl:choose></xsl:variable>
	<xsl:variable name="root-dir">
		<xsl:choose>
			<xsl:when test="$active-level = 1 or $active-level = 0">
				<xsl:value-of select="'./'"/>
			</xsl:when>
			<xsl:when test="$active-level = 2">
				<xsl:value-of select="'../'"/>
			</xsl:when>
			<xsl:when test="$active-level = 3">
				<xsl:value-of select="'../../'"/>
			</xsl:when>
			<xsl:when test="$active-level = 4">
				<xsl:value-of select="'../../../'"/>
			</xsl:when>
			<xsl:when test="$active-level = 5">
				<xsl:value-of select="'../../../../'"/>
			</xsl:when>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="resources-dir"><xsl:value-of select="concat($root-dir, 'resources/')"/></xsl:variable>
	<xsl:variable name="images-dir"><xsl:value-of select="concat($resources-dir, 'images/')"/></xsl:variable>
	<html>
		<head>
			<title>
				<xsl:choose>
					<xsl:when test="$active-page/@title">
						<xsl:value-of select="$active-page/@title"/>
					</xsl:when>
					<xsl:otherwise>
						Netspective Sparx
						<xsl:if test="$active-level > 0">
							<xsl:text disable-output-escaping="yes"> -- </xsl:text>
							<xsl:for-each select="$active-page/ancestor::*">
								<xsl:if test="@caption != 'Home'">
									<xsl:value-of select="@caption"/>
									<xsl:text disable-output-escaping="yes"> -- </xsl:text>
								</xsl:if>
							</xsl:for-each>
							<xsl:value-of select="$active-page/@caption"/>
						</xsl:if>
					</xsl:otherwise>
				</xsl:choose>
			</title>
			<style>
				h2 { font-family: arial, helvetica; font-size: 12pt; font-weight: bold; color: darkred}
				table.data { }
				tr.data { }
				tr.data_head { background: #EEEEEE; }
				th.data { font-family: tahoma; font-size: 8pt; border-bottom: 1 solid #999999; font-weight: normal; }
				td.data { font-family: tahoma; font-size: 8pt; border-bottom: 1 solid #CCCCCC; }
				td.data_code { font-family: lucida sans, courier; font-size: 8pt; border-bottom: 1 solid #CCCCCC; }
				td.data_check { font-family: tahoma; font-size: 8pt; text-align: center; border-bottom: 1 solid #CCCCCC; }
				table.tab { }
				tr.tab { }
				td.tab { font-family: tahoma; font-size: 8pt; border-bottom: 1 solid white; }
				td.tab_active { font-family: tahoma; font-size: 8pt; font-weight: bold; }
				a.navigation { text-decoration: none; }
				a.navigation:hover { text-decoration: underline; color: #660000; }
			</style>
		</head>
		<body topmargin="0" leftmargin="0" rightmargin="0" border="0">

			<!-- start of page master table -->
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<!-- the main masthead row and search box -->
				<tr bgcolor="{$masthead-bgcolor}" height="60">
					<td width="150"><img src="{concat($images-dir, 'masthead/logo-top-left.gif')}"/></td>
					<td><img src="{concat($images-dir, 'masthead/logo-top-middle.gif')}"/><img src="{concat($images-dir, 'masthead/logo-top-right.gif')}"/></td>
					<td valign="center"><font face="Times" color="#DCDCDC"><i>Sparx Application Platform Developer's Guide</i></font></td>
				</tr>
			</table>

			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<!-- the second masthead row and main menu tabs -->
				<tr bgcolor="{$masthead-bgcolor}" height="20">
					<td width="150"><img src="{concat($images-dir, 'masthead/logo-bottom-left.gif')}"/></td>
					<td valign="bottom" background="{concat($images-dir, 'design/stripes-dark-grey.gif')}">
						<table bgcolor="{$menu-tab-color}" border="0" cellspacing="0" cellpadding="0" height="20"><tr>
							<xsl:for-each select="$structure/page/*">
								<xsl:choose>
									<xsl:when test="descendant-or-self::*[@name = $active-page/@name]">
										<td align="center" width="100" bgcolor="{$active-menu-tab-color}"><font face="verdana,Arial,Helvetical" size="2" style="font-size:8pt"><b><a href="{concat($root-dir, @name, '.html')}" class="navigation" style="color:black"><xsl:value-of select="@caption"/></a></b></font></td>
									</xsl:when>
									<xsl:otherwise>
										<td align="center" width="100"><font face="verdana,Arial,Helvetical" size="2" style="font-size:8pt"><b><a href="{concat($root-dir, @name, '.html')}" class="navigation" style="color:white"><xsl:value-of select="@caption"/></a></b></font></td>
									</xsl:otherwise>
								</xsl:choose>
								<xsl:if test="position() != last()"><td bgcolor="white" width="1"><img width="1" src="{concat($images-dir, 'spacer.gif')}"/></td></xsl:if>
							</xsl:for-each>
						</tr></table>
					</td>
				</tr>
			</table>

			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<!-- the third masthead row and cookie crumbs area -->
				<tr height="20">
					<td bgcolor="black"><font face="tahoma,arial" size="2" color="black" style="font-size:8pt"><img width="150" height="20" src="{concat($images-dir, 'spacer.gif')}"/></font></td>
					<td bgcolor="{$active-menu-tab-color}"><font size="2" face="tahoma,arial,helvetica">
						<xsl:if test="$active-level > 1">
							<font face="tahoma,arial" size="2" color="#660000" style="font-size:8pt">
							<xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:text>
							<xsl:for-each select="$active-page/ancestor::*">
								<a class="navigation" style="color:#660000" href="{concat($root-dir, @name, '.html')}"><xsl:value-of select="@caption"/></a>
								<xsl:if test="@caption">
									<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
									&gt;
									<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
								</xsl:if>
							</xsl:for-each>
							<b><a class="navigation" style="color:#660000" href="{concat($root-dir, $active-page/@name, '.html')}"><xsl:value-of select="$active-page/@caption"/></a></b>
							</font>
						</xsl:if>
					</font></td>
				</tr>

				<!-- start of navigation and body row -->
				<tr valign="top">
					<td background="{concat($images-dir, 'design/stripes-dark-grey.gif')}" bgcolor="#555555" width="150">
						<br/>
						<table border="0" cellpadding="2" cellspacing="0" width="100%">
						<!-- the navigation area -->
						<xsl:call-template name="navigation">
							<xsl:with-param name="active-page" select="$active-page"/>
							<xsl:with-param name="root-dir" select="$root-dir"/>
							<xsl:with-param name="resources-dir" select="$resources-dir"/>
							<xsl:with-param name="images-dir" select="$images-dir"/>
						</xsl:call-template>
						</table>
						<p/>
						<img src="{concat($images-dir, 'spacer.gif')}"/>
					</td>
					<td bgcolor="white">
						<table cellspacing="10" cellpadding="8" width="100%">
						<xsl:if test="summary">
						<tr>
							<td>
								<table border="0" cellspacing="0" cellpadding="4">
									<tr><td bgcolor="#993333"><font face="Arial,helvetica" size="2" color="white"><b><xsl:value-of select="$active-heading"/></b></font></td></tr>
									<tr>
									<td bgcolor="#EEEEEE" style="border:1px solid #660000;">
										<!-- the page summary-->
										<font face="verdana" size="2" style="font-size: 10pt">
										<xsl:apply-templates select="summary">
											<xsl:with-param name="active-page" select="$active-page"/>
											<xsl:with-param name="root-dir" select="$root-dir"/>
											<xsl:with-param name="resources-dir" select="$resources-dir"/>
											<xsl:with-param name="images-dir" select="$images-dir"/>
										</xsl:apply-templates>
										</font>
									</td>
									</tr>
								</table>
							</td>
						</tr>
						</xsl:if>

						<!-- each main page element MUST start and end its own <TR> (table row) -->
						<xsl:apply-templates select="*[name() != 'summary']">
							<xsl:with-param name="active-page" select="$active-page"/>
							<xsl:with-param name="root-dir" select="$root-dir"/>
							<xsl:with-param name="resources-dir" select="$resources-dir"/>
							<xsl:with-param name="images-dir" select="$images-dir"/>
						</xsl:apply-templates>
						</table>
					</td> <!-- end of body area -->
				</tr> <!-- end of navigation and body row -->

				<!-- the footer row -->
				<tr bgcolor="#333333" align="center"><td colspan="2">
					<table>
						<tr>
							<td><font face="tahoma,Arial,helvetica" size="1" color="#DCDCDC">
								<xsl:text disable-output-escaping="yes">&amp;copy;</xsl:text> Copyright 2000-2002, Netspective LLC. All rights reserved.
							</font></td>
							<td><font face="tahoma,Arial,helvetica" size="1" color="#DCDCDC">|</font></td>
							<td><font face="tahoma,Arial,helvetica" size="1" color="#DCDCDC">
								<a class="navigation" href="http://www.netspective.com" style="color:#DCDCDC">www.netspective.com</a>
							</font></td>
							<td><font face="tahoma,Arial,helvetica" size="1" color="#DCDCDC">|</font></td>
							<td><font face="tahoma,Arial,helvetica" size="1" color="#DCDCDC">
								<a class="navigation" href="mailto:support@netspective.com" style="color:#DCDCDC">support@netspective.com</a>
							</font></td>
						</tr>
					</table>
				</td></tr>

			</table> <!-- end of page table -->
		</body>
	</html>
</xsl:template>

<xsl:template name="navigation-indent">
	<xsl:param name="active-level"/>
	<xsl:param name="last-level"/>

	<xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text>
	<xsl:if test="$active-level &lt; $last-level">
		<xsl:call-template name="navigation-indent">
			<xsl:with-param name="active-level" select="$active-level + 1"/>
			<xsl:with-param name="last-level" select="$last-level"/>
		</xsl:call-template>
	</xsl:if>
</xsl:template>

<xsl:template name="navigation">
	<xsl:param name="active-page"/>
	<xsl:param name="root-dir"/>
	<xsl:param name="resources-dir"/>
	<xsl:param name="images-dir"/>

	<xsl:if test="$active-page/quick-links">
		<xsl:for-each select="$active-page/quick-links/quick-link">
			<tr><td><font face="arial,helvetica" size="2" style="font-size:9pt">
				<b><a class="navigation" style="color:white" href="{concat($root-dir, @page, '.html')}"><xsl:value-of select="@caption"/></a></b>
			</font></td></tr>
		</xsl:for-each>
		<tr><td><font face="arial,helvetica" size="2" style="font-size:9pt"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></font></td></tr>
	</xsl:if>

	<xsl:choose>
		<xsl:when test="$active-page/@level > 1">
			<xsl:for-each select="$active-page/ancestor::*[@level > 1]">
				<tr>
				<td><font face="arial,helvetica" size="2" style="font-size:9pt">
					<xsl:call-template name="navigation-indent">
						<xsl:with-param name="active-level" select="1"/>
						<xsl:with-param name="last-level" select="@level"/>
					</xsl:call-template>
					<a class="navigation" style="color:white" href="{concat($root-dir, @name, '.html')}"><xsl:value-of select="@caption"/></a>
				</font></td>
				</tr>
			</xsl:for-each>

			<xsl:for-each select="$active-page/../page">
				<xsl:choose>
					<xsl:when test="@name = $active-page/@name">
						<tr><td bgcolor="white"><font face="arial,helvetica" size="2" style="font-size:9pt">
						<xsl:call-template name="navigation-indent">
							<xsl:with-param name="active-level" select="1"/>
							<xsl:with-param name="last-level" select="@level"/>
						</xsl:call-template>
						<b><a class="navigation" style="color:black" href="{concat($root-dir, @name, '.html')}"><xsl:value-of select="@caption"/></a></b>
						</font></td></tr>
						<xsl:for-each select="page">
							<tr><td><font face="arial,helvetica" size="2" style="font-size:9pt">
							<xsl:call-template name="navigation-indent">
								<xsl:with-param name="active-level" select="1"/>
								<xsl:with-param name="last-level" select="@level"/>
							</xsl:call-template>
							<a class="navigation" style="color:white" href="{concat($root-dir, @name, '.html')}"><xsl:value-of select="@caption"/></a>
							</font></td></tr>
						</xsl:for-each>
					</xsl:when>
					<xsl:otherwise>
						<tr><td><font face="arial,helvetica" size="2" style="font-size:9pt">
						<xsl:call-template name="navigation-indent">
							<xsl:with-param name="active-level" select="1"/>
							<xsl:with-param name="last-level" select="@level"/>
						</xsl:call-template>
						<a class="navigation" style="color:white" href="{concat($root-dir, @name, '.html')}"><xsl:value-of select="@caption"/></a>
						</font></td></tr>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
		</xsl:when>

		<xsl:otherwise>
			<xsl:for-each select="$active-page/page">
				<tr><td><font face="arial,helvetica" size="2" style="font-size:9pt">
				<xsl:call-template name="navigation-indent">
					<xsl:with-param name="active-level" select="1"/>
					<xsl:with-param name="last-level" select="@level"/>
				</xsl:call-template>
				<a class="navigation" style="color:white" href="{concat($root-dir, @name, '.html')}"><xsl:value-of select="@caption"/></a>
				</font></td></tr>
			</xsl:for-each>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template match="td[not(@class)]">
	<xsl:param name="active-page"/>
	<xsl:param name="root-dir"/>
	<xsl:param name="resources-dir"/>
	<xsl:param name="images-dir"/>
	<xsl:copy>
		<xsl:for-each select="@*">
			<xsl:copy/>
		</xsl:for-each>
		<font face="verdana,arial,helvetica" size="2" style="font-size:10pt">
			<xsl:apply-templates>
				<xsl:with-param name="active-page" select="$active-page"/>
				<xsl:with-param name="root-dir" select="$root-dir"/>
				<xsl:with-param name="resources-dir" select="$resources-dir"/>
				<xsl:with-param name="images-dir" select="$images-dir"/>
			</xsl:apply-templates>
		</font>
	</xsl:copy>
</xsl:template>

<xsl:template match="link">
	<xsl:param name="root-dir"/>
	<xsl:param name="resources-dir"/>
	<xsl:param name="images-dir"/>
	<xsl:choose>
		<xsl:when test="@page">
			<a class="body"><xsl:attribute name="href"><xsl:value-of select="concat($root-dir, @page)"/>.html</xsl:attribute><xsl:if test="@image"><img border="0"><xsl:attribute name="src"><xsl:value-of select="concat($images-dir, @image)"/></xsl:attribute></img></xsl:if><xsl:apply-templates/></a>
		</xsl:when>
		<xsl:when test="@resource">
			<a class="body"><xsl:attribute name="href"><xsl:value-of select="concat($resources-dir, @resource)"/></xsl:attribute><xsl:if test="@image"><img border="0"><xsl:attribute name="src"><xsl:value-of select="concat($images-dir, @image)"/></xsl:attribute></img></xsl:if><xsl:apply-templates/></a>
		</xsl:when>
		<xsl:when test="@http">
			<a class="body"><xsl:attribute name="href"><xsl:value-of select="concat('http://', @http)"/></xsl:attribute><xsl:if test="@image"><img border="0"><xsl:attribute name="src"><xsl:value-of select="concat($images-dir, @image)"/></xsl:attribute></img></xsl:if><xsl:apply-templates/></a>
		</xsl:when>
	</xsl:choose>
</xsl:template>

<xsl:template match="image">
	<xsl:param name="active-page"/>
	<xsl:param name="root-dir"/>
	<xsl:param name="resources-dir"/>
	<xsl:param name="images-dir"/>

	<xsl:choose>
		<xsl:when test="@src">
			<img border="0"><xsl:for-each select="@*"><xsl:copy/></xsl:for-each><xsl:attribute name="src"><xsl:value-of select="concat($images-dir, @src)"/></xsl:attribute></img>
		</xsl:when>
		<xsl:when test="@name">
			<xsl:variable name="image-name" select="@name"/>
			<xsl:variable name="image-info" select="$structure/images//image[@name = $image-name]"/>
			<xsl:choose>
				<xsl:when test="$image-info/@preview">
					<a class="preview"><xsl:attribute name="href"><xsl:value-of select="concat($images-dir, $image-info/@src)"/></xsl:attribute>
					<img border="0"><xsl:for-each select="@*"><xsl:copy/></xsl:for-each><xsl:attribute name="src"><xsl:value-of select="concat($images-dir, $image-info/@preview)"/></xsl:attribute></img>
					</a>
				</xsl:when>
				<xsl:otherwise>
					<img border="0"><xsl:for-each select="@*"><xsl:copy/></xsl:for-each><xsl:attribute name="src"><xsl:value-of select="concat($images-dir, $image-info/@src)"/></xsl:attribute></img>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:when>
	</xsl:choose>
</xsl:template>

<xsl:template name="tab">
	<xsl:param name="heading"/>
	<xsl:param name="color"/>
	<xsl:param name="active-page"/>
	<xsl:param name="root-dir"/>
	<xsl:param name="resources-dir"/>
	<xsl:param name="images-dir"/>

	<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td valign="top" width="3" height="17"><img src="{concat($images-dir, 'design/tabs/arrow-top.gif')}"/></td>
			<td height="17" bgcolor="white" style="border-top:1px solid black; border-right:1px solid black; border-left: 1px solid black;"><font face="tahoma,arial,helvetica" size="2" style="font-size:8pt">
				<nobr>
					<xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text>
					<b><xsl:value-of select="$heading"/></b>
					<xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text>
				</nobr>
			</font></td>
		</tr>
	</table>
	<table height="6" width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr height="6">
			<td width="12"><img src="{concat($images-dir, 'design/tabs/arrow-bottom.gif')}"/></td>
			<td background="{concat($images-dir, 'design/tabs/horiz-bar.gif')}" bgcolor="#DDDDDD"><img src="{concat($images-dir, 'spacer.gif')}"/></td>
			<td width="6"><img src="{concat($images-dir, 'design/tabs/horiz-bar-end.gif')}"/></td>
		</tr>
	</table>
</xsl:template>

<xsl:template match="section">
	<xsl:param name="active-page"/>
	<xsl:param name="root-dir"/>
	<xsl:param name="resources-dir"/>
	<xsl:param name="images-dir"/>

	<xsl:variable name="at-root"><xsl:if test="parent::node()[local-name() = 'page']">yes</xsl:if></xsl:variable>

	<!-- if we're at the page level (instead of inside a section, then make sure we start a row -->
	<xsl:if test="$at-root = 'yes'">
		<xsl:text disable-output-escaping="yes"><![CDATA[<tr><td>]]></xsl:text>
	</xsl:if>

	<xsl:if test="@heading | @xsdn-element">
		<xsl:variable name="heading">
			<xsl:choose>
				<xsl:when test="@xsdn-element and not(@heading)"><xsl:value-of select="@xsdn-element"/> Element</xsl:when>
				<xsl:otherwise><xsl:value-of select="@heading"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="tab">
			<xsl:with-param name="active-page" select="$active-page"/>
			<xsl:with-param name="root-dir" select="$root-dir"/>
			<xsl:with-param name="resources-dir" select="$resources-dir"/>
			<xsl:with-param name="images-dir" select="$images-dir"/>
			<xsl:with-param name="heading" select="$heading"/>
		</xsl:call-template>
	</xsl:if>
	<table cellspacing="0" cellpadding="3" width="100%" bgcolor="white">
		<xsl:if test="@heading | @xsdn-element">
			<xsl:attribute name="style">border:1px solid black</xsl:attribute>
		</xsl:if>
		<tr>
			<td>
				<font face="verdana,arial,helvetica" size="2" style="font-size:9pt">
					<xsl:apply-templates>
						<xsl:with-param name="active-page" select="$active-page"/>
						<xsl:with-param name="root-dir" select="$root-dir"/>
						<xsl:with-param name="resources-dir" select="$resources-dir"/>
						<xsl:with-param name="images-dir" select="$images-dir"/>
					</xsl:apply-templates>
				</font>
			</td>
		</tr>
	</table>

	<xsl:if test="$at-root = 'yes'">
		<xsl:text disable-output-escaping="yes"><![CDATA[</td></tr>]]></xsl:text>
	</xsl:if>
</xsl:template>

<xsl:template match="channel">
	<xsl:param name="active-page"/>
	<xsl:param name="root-dir"/>
	<xsl:param name="resources-dir"/>
	<xsl:param name="images-dir"/>

	<xsl:variable name="at-root"><xsl:if test="parent::node()[local-name() = 'page']">yes</xsl:if></xsl:variable>

	<!-- if we're at the page level (instead of inside a section, then make sure we start a row -->
	<xsl:if test="$at-root = 'yes'">
		<xsl:text disable-output-escaping="yes"><![CDATA[<tr><td>]]></xsl:text>
	</xsl:if>

	<xsl:if test="@heading | @xsdn-element">
		<xsl:variable name="heading">
			<xsl:choose>
				<xsl:when test="@xsdn-element and not(@heading)"><xsl:value-of select="@xsdn-element"/> Element</xsl:when>
				<xsl:otherwise><xsl:value-of select="@heading"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="tab">
			<xsl:with-param name="active-page" select="$active-page"/>
			<xsl:with-param name="root-dir" select="$root-dir"/>
			<xsl:with-param name="resources-dir" select="$resources-dir"/>
			<xsl:with-param name="images-dir" select="$images-dir"/>
			<xsl:with-param name="heading" select="$heading"/>
		</xsl:call-template>
	</xsl:if>

	<table cellspacing="0" cellpadding="3" width="100%" style="border:1px solid black;">
		<xsl:attribute name="bgcolor"><xsl:choose><xsl:when test="@border-color"><xsl:value-of select="@border-color"/></xsl:when><xsl:otherwise><xsl:value-of select="$channel-frame-color"/></xsl:otherwise></xsl:choose></xsl:attribute>
		<xsl:choose>
		<xsl:when test="description">
			<tr>
				<xsl:attribute name="bgcolor"><xsl:choose><xsl:when test="@content-color"><xsl:value-of select="@content-color"/></xsl:when><xsl:otherwise>#FFFFFF</xsl:otherwise></xsl:choose></xsl:attribute>
				<td colspan="3">
					<font face="tahoma,arial,helvetica" size="2" style="font-size:8pt">
						<xsl:apply-templates select="text() | *[name() != 'description']">
							<xsl:with-param name="active-page" select="$active-page"/>
							<xsl:with-param name="root-dir" select="$root-dir"/>
							<xsl:with-param name="resources-dir" select="$resources-dir"/>
							<xsl:with-param name="images-dir" select="$images-dir"/>
						</xsl:apply-templates>
					</font>
				</td>
			</tr>
			<xsl:for-each select="description">
			<tr>
				<xsl:attribute name="bgcolor"><xsl:choose><xsl:when test="@content-color"><xsl:value-of select="@content-color"/></xsl:when><xsl:otherwise>#FFFFFF</xsl:otherwise></xsl:choose></xsl:attribute>
				<td valign="top">
					<font face="tahoma,arial,helvetica" size="2" style="font-size:8pt">
					<b><xsl:value-of select="@heading"/></b>
					</font>
				</td>
				<td valign="top">
					<font face="tahoma,arial,helvetica" size="2" style="font-size:8pt">
						<xsl:apply-templates>
							<xsl:with-param name="active-page" select="$active-page"/>
							<xsl:with-param name="root-dir" select="$root-dir"/>
							<xsl:with-param name="resources-dir" select="$resources-dir"/>
							<xsl:with-param name="images-dir" select="$images-dir"/>
						</xsl:apply-templates>
					</font>
				</td>
			</tr>
			</xsl:for-each>
		</xsl:when>
		<xsl:when test="@type = 'xml-source'">
			<tr>
				<xsl:attribute name="bgcolor"><xsl:choose><xsl:when test="@content-color"><xsl:value-of select="@content-color"/></xsl:when><xsl:otherwise>#FFFFFF</xsl:otherwise></xsl:choose></xsl:attribute>
				<td>
					<font face="courier,arial,helvetica" size="2" style="font-size:8pt">
					<xsl:choose>
						<xsl:when test="@include and not(@element)">
							<xsl:call-template name="xml-element">
								<xsl:with-param name="element" select="document(@include)"/>
							</xsl:call-template>
						</xsl:when>
						<xsl:when test="@include and @element">
							<xsl:variable name="xml-element" select="xalan:evaluate(concat('document(@include)', @element))"/>
							<DIV STYLE="margin-left:2em;text-indent:-2em">
								&lt;<font color="navy"><xsl:value-of select="name($xml-element)"/></font>
								<xsl:if test="$xml-element/@*"><xsl:text> </xsl:text></xsl:if>
								<xsl:for-each select="$xml-element/@*">
									<font color="red"><xsl:value-of select="name()"/></font>=&quot;<font color="green"><xsl:value-of select="."/></font>&quot;
									<xsl:if test="position() != last()"><xsl:text> </xsl:text></xsl:if>
								</xsl:for-each><xsl:if test="count(*) = 0">/</xsl:if>&gt;
								<xsl:call-template name="xml-element">
									<xsl:with-param name="element" select="$xml-element"/>
								</xsl:call-template>
								<xsl:if test="count($xml-element/*) > 0">&lt;/<font color="navy"><xsl:value-of select="name($xml-element)"/></font>&gt;</xsl:if>
							</DIV>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="xml-element">
								<xsl:with-param name="element" select="."/>
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
					</font>
				</td>
			</tr>
		</xsl:when>
		<xsl:when test="@xsdn-element">
			<xsl:variable name="xsdn-source"><xsl:choose><xsl:when test="@xsdn-src"><xsl:value-of select="@xsdn-src"/></xsl:when><xsl:otherwise>xaf-xsdn</xsl:otherwise></xsl:choose></xsl:variable>
			<tr>
				<xsl:attribute name="bgcolor"><xsl:choose><xsl:when test="@content-color"><xsl:value-of select="@content-color"/></xsl:when><xsl:otherwise>#FFFFFF</xsl:otherwise></xsl:choose></xsl:attribute>
				<td>
					<font face="verdana" size="2" style="font-size:9pt">
						<xsl:call-template name="element-documentation">
							<xsl:with-param name="elem-defn" select="xalan:evaluate(concat('$', $xsdn-source, @xsdn-element))"/>
							<xsl:with-param name="show-name" select="'no'"/>
							<xsl:with-param name="active-page" select="$active-page"/>
							<xsl:with-param name="root-dir" select="$root-dir"/>
							<xsl:with-param name="resources-dir" select="$resources-dir"/>
							<xsl:with-param name="images-dir" select="$images-dir"/>
						</xsl:call-template>
						<xsl:if test="@describe-children">
							<xsl:for-each select="xalan:evaluate(concat('$', $xsdn-source, @xsdn-element, @describe-children))">
								<xsl:sort select="name()"/>
								<xsl:if test="not(starts-with(name(), 'elem.'))">
									<p/>
									<xsl:call-template name="element-documentation">
										<xsl:with-param name="elem-defn" select="."/>
										<xsl:with-param name="active-page" select="$active-page"/>
										<xsl:with-param name="root-dir" select="$root-dir"/>
										<xsl:with-param name="resources-dir" select="$resources-dir"/>
										<xsl:with-param name="images-dir" select="$images-dir"/>
									</xsl:call-template>
								</xsl:if>
							</xsl:for-each>
						</xsl:if>
					</font>
				</td>
			</tr>
		</xsl:when>
		<xsl:otherwise>
			<tr>
				<xsl:attribute name="bgcolor"><xsl:choose><xsl:when test="@content-color"><xsl:value-of select="@content-color"/></xsl:when><xsl:otherwise>#FFFFFF</xsl:otherwise></xsl:choose></xsl:attribute>
				<td>
					<font face="tahoma,arial,helvetica" size="2" style="font-size:8pt">
						<xsl:apply-templates>
							<xsl:with-param name="active-page" select="$active-page"/>
							<xsl:with-param name="root-dir" select="$root-dir"/>
							<xsl:with-param name="resources-dir" select="$resources-dir"/>
							<xsl:with-param name="images-dir" select="$images-dir"/>
						</xsl:apply-templates>
					</font>
				</td>
			</tr>
		</xsl:otherwise>
		</xsl:choose>
	</table>

	<xsl:if test="$at-root = 'yes'">
		<xsl:text disable-output-escaping="yes"><![CDATA[</td></tr>]]></xsl:text>
	</xsl:if>
</xsl:template>

<!-- find the children of the active-page using the structure.xml file and collect all the <summary> tags -->
<xsl:template match="summarize-children">
	<xsl:param name="active-page"/>
	<xsl:param name="root-dir"/>
	<xsl:param name="resources-dir"/>
	<xsl:param name="images-dir"/>

	<xsl:variable name="at-root"><xsl:if test="parent::node()[local-name() = 'page']">yes</xsl:if></xsl:variable>

	<!-- if we're at the page level (instead of inside a section, then make sure we start a row -->
	<xsl:if test="$at-root = 'yes'">
		<xsl:text disable-output-escaping="yes"><![CDATA[<tr><td>]]></xsl:text>
	</xsl:if>

	<xsl:if test="@heading">
		<xsl:call-template name="tab">
			<xsl:with-param name="active-page" select="$active-page"/>
			<xsl:with-param name="root-dir" select="$root-dir"/>
			<xsl:with-param name="resources-dir" select="$resources-dir"/>
			<xsl:with-param name="images-dir" select="$images-dir"/>
			<xsl:with-param name="heading" select="@heading"/>
			<xsl:with-param name="color" select="white"/>
		</xsl:call-template>
	</xsl:if>

	<table border="0" cellspacing="0" cellpadding="4">
		<xsl:if test="@heading"><xsl:attribute name="style">border:1px solid black;</xsl:attribute></xsl:if>
		<xsl:for-each select="$active-page/page">
		<xsl:variable name="page-heading"><xsl:choose><xsl:when test="@heading"><xsl:value-of select="@heading"/></xsl:when><xsl:otherwise><xsl:value-of select="@caption"/></xsl:otherwise></xsl:choose></xsl:variable>
		<tr>
			<xsl:attribute name="bgcolor"><xsl:choose><xsl:when test="@content-color"><xsl:value-of select="@content-color"/></xsl:when><xsl:otherwise>#FFFFFF</xsl:otherwise></xsl:choose></xsl:attribute>
			<td valign="top">
				<font face="verdana,arial,helvetica" size="2" style="font-size:10pt">
				<a class="body"><xsl:attribute name="href"><xsl:value-of select="concat($root-dir, @name, '.html')"/></xsl:attribute><xsl:value-of select="$page-heading"/></a>
				</font>
			</td>
			<td valign="top">
				<font face="verdana,arial,helvetica" size="2" style="font-size:10pt">
					<xsl:if test="not(document(concat(@name, '.xml'))/page/summary)">
					Coming soon...
					</xsl:if>
					<xsl:apply-templates select="document(concat(@name, '.xml'))/page/summary">
						<xsl:with-param name="active-page" select="$active-page"/>
						<xsl:with-param name="root-dir" select="$root-dir"/>
						<xsl:with-param name="resources-dir" select="$resources-dir"/>
						<xsl:with-param name="images-dir" select="$images-dir"/>
					</xsl:apply-templates>
				</font>
			</td>
		</tr>
		</xsl:for-each>
	</table>

	<xsl:if test="$at-root = 'yes'">
		<xsl:text disable-output-escaping="yes"><![CDATA[</td></tr>]]></xsl:text>
	</xsl:if>
</xsl:template>

<!-- use the elements and content of the given element and syntax-highlight it as XML -->
<xsl:template name="xml-element">
	<xsl:param name="element"/>
	<xsl:variable name="elem-text"><xsl:value-of select="normalize-space(string($element/text()))"/></xsl:variable>

	<xsl:if test="string-length($elem-text) > 0">
	<font color="green"><DIV STYLE="margin-left:2em;text-indent:-2em"><xsl:value-of select="$elem-text"/></DIV></font>
	</xsl:if>

	<xsl:for-each select="$element/* | $element/comment()">
		<xsl:choose>
			<xsl:when test=". = $element/comment()">
				<DIV>
					<br/>
					<font face="arial,helvetica" size="2" color="#777777">&lt;!-- <xsl:value-of select="."/> --&gt;</font>
					<br/>
				</DIV>
			</xsl:when>
			<xsl:when test="self::*">
				<DIV STYLE="margin-left:2em;text-indent:-2em">
					&lt;<font color="navy"><xsl:value-of select="name()"/></font>
					<xsl:for-each select="@*"><xsl:text> </xsl:text><font color="darkred"><xsl:value-of select="name()"/></font>=&quot;<font color="green"><xsl:value-of select="."/></font>&quot;</xsl:for-each><xsl:if test="(count(*) = 0) and not(text())">/</xsl:if>&gt;
					<xsl:call-template name="xml-element">
						<xsl:with-param name="element" select="."/>
					</xsl:call-template>
					<xsl:if test="(count(*) > 0) or text()">&lt;/<font color="navy"><xsl:value-of select="name()"/></font>&gt;</xsl:if>
				</DIV>
			</xsl:when>
		</xsl:choose>
	</xsl:for-each>
</xsl:template>

<!-- use the elements and content of the given element and syntax-highlight it as XML -->
<xsl:template match="xml-source">
	<xsl:call-template name="xml-element">
		<xsl:with-param name="element" select="."/>
	</xsl:call-template>
</xsl:template>

<!-- include the contents of another XML file and transform it using this stylesheet -->
<xsl:template match="include">
	<xsl:param name="active-page"/>
	<xsl:param name="root-dir"/>
	<xsl:param name="resources-dir"/>
	<xsl:param name="images-dir"/>
	<xsl:apply-templates select="document(@file)">
		<xsl:with-param name="active-page" select="$active-page"/>
		<xsl:with-param name="root-dir" select="$root-dir"/>
		<xsl:with-param name="resources-dir" select="$resources-dir"/>
		<xsl:with-param name="images-dir" select="$images-dir"/>
	</xsl:apply-templates>
</xsl:template>


<xsl:template name="element-documentation">
	<xsl:param name="elem-defn"/>
	<xsl:param name="show-name"/>
	<xsl:param name="active-page"/>
	<xsl:param name="root-dir"/>
	<xsl:param name="resources-dir"/>
	<xsl:param name="images-dir"/>

	<xsl:if test="not($show-name = 'no')">
	<font color="navy" size="2" face="arial,helvetica"><a class="xml-element" name="{local-name()}">&lt;<b><xsl:value-of select="local-name()"/></b>&gt;</a> element</font><br/>
	</xsl:if>
	<font face="tahoma,arial,helvetica" size="2">
	<xsl:value-of select="$elem-defn/elem.remarks"/><br/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text><br/>
	</font>
	<xsl:if test="$elem-defn/*[not(starts-with(local-name(), 'elem.'))] | $elem-defn/elem.attributes/attribute">
	<table class="data" cellspacing="0" cellpadding="3">
		<tr class="data_head">
			<th class="data">Name</th>
			<th class="data">Req</th>
			<th class="data">Type</th>
			<th class="data">Options</th>
			<th class="data">Default</th>
			<th class="data">Description</th>
		</tr>
		<xsl:for-each select="$elem-defn/elem.attributes/attribute">
			<xsl:variable name="type-name" select="type"/>
			<xsl:variable name="attr-type-defn" select="$xaf-xsd/xsd:simpleType[@name = $type-name]"/>
			<tr class="data">
				<td class="data">
					<xsl:choose>
						<xsl:when test="@inheritance-depth = 0"><b><nobr><xsl:value-of select="name"/></nobr></b></xsl:when>
						<xsl:otherwise><nobr><xsl:value-of select="name"/></nobr></xsl:otherwise>
					</xsl:choose>
				</td>
				<td class="data">
					<xsl:choose>
						<xsl:when test="use = 'required'"><font color="red">yes</font></xsl:when>
						<xsl:otherwise><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></xsl:otherwise>
					</xsl:choose>
				</td>
				<td class="data"><xsl:value-of select="type"/></td>
				<td class="data">
					<xsl:choose>
						<xsl:when test="$attr-type-defn/xsd:restriction/xsd:enumeration">
							<font color="green">
							<xsl:for-each select="$attr-type-defn/xsd:restriction/xsd:enumeration">
								<nobr><xsl:value-of select="@value"/></nobr>
								<xsl:if test="position() != last()"><xsl:text> | </xsl:text></xsl:if>
							</xsl:for-each>
							</font>
						</xsl:when>
						<xsl:when test="enums">
							<font color="green">
							<xsl:for-each select="enums/enum">
								<nobr><xsl:value-of select="."/></nobr>
								<xsl:if test="position() != last()"><xsl:text> | </xsl:text></xsl:if>
							</xsl:for-each>
							</font>
						</xsl:when>
						<xsl:otherwise><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></xsl:otherwise>
					</xsl:choose>
				</td>
				<td class="data">
					<xsl:choose>
						<xsl:when test="default"><xsl:value-of select="default"/></xsl:when>
						<xsl:otherwise><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></xsl:otherwise>
					</xsl:choose>
				</td>
				<td class="data">
					<xsl:choose>
						<xsl:when test="remarks/text() != ''"><xsl:value-of select="remarks"/></xsl:when>
						<xsl:otherwise><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></xsl:otherwise>
					</xsl:choose>
				</td>
			</tr>
		</xsl:for-each>
		<xsl:for-each select="$elem-defn/*[not(starts-with(local-name(), 'elem.'))]">
			<xsl:sort select="local-name()"/>
			<tr class="data">
				<td class="data">
					<xsl:choose>
						<xsl:when test="starts-with(local-name(), 'field.')">
							<a class="xml-element" href="{concat($root-dir, 'products/xaf/dialogs/controls.html#', local-name())}"><nobr>&lt;<xsl:value-of select="local-name()"/>&gt;</nobr></a>
						</xsl:when>
						<xsl:when test="local-name() = 'dialogs'">
							<a class="xml-element" href="{concat($root-dir, 'products/xaf/dialogs/xml.html')}"><nobr>&lt;<xsl:value-of select="local-name()"/>&gt;</nobr></a>
						</xsl:when>
						<xsl:when test="local-name() = 'configuration'">
							<a class="xml-element" href="{concat($root-dir, 'products/xaf/configuration.html')}"><nobr>&lt;<xsl:value-of select="local-name()"/>&gt;</nobr></a>
						</xsl:when>
						<xsl:when test="local-name() = 'sql-statements'">
							<a class="xml-element" href="{concat($root-dir, 'products/xaf/sql/xml.html')}"><nobr>&lt;<xsl:value-of select="local-name()"/>&gt;</nobr></a>
						</xsl:when>
						<xsl:when test="local-name() = 'query-defn'">
							<a class="xml-element" href="{concat($root-dir, 'products/xaf/dynamic-queries/xml.html')}"><nobr>&lt;<xsl:value-of select="local-name()"/>&gt;</nobr></a>
						</xsl:when>
						<xsl:when test="local-name() = 'datatype'">
							<a class="xml-element" href="{concat($root-dir, 'products/xif/datatypes.html')}"><nobr>&lt;<xsl:value-of select="local-name()"/>&gt;</nobr></a>
						</xsl:when>
						<xsl:when test="local-name() = 'tabletype'">
							<a class="xml-element" href="{concat($root-dir, 'products/xif/tabletypes.html')}"><nobr>&lt;<xsl:value-of select="local-name()"/>&gt;</nobr></a>
						</xsl:when>
						<xsl:when test="local-name() = 'table'">
							<a class="xml-element" href="{concat($root-dir, 'products/xif/tabletypes.html')}"><nobr>&lt;<xsl:value-of select="local-name()"/>&gt;</nobr></a>
						</xsl:when>
						<xsl:otherwise>
							<font color="blue"><nobr>&lt;<xsl:value-of select="local-name()"/>&gt;</nobr></font>
						</xsl:otherwise>
					</xsl:choose>
				</td>
				<td class="data"><xsl:value-of select="elem.use"/></td>
				<td class="data"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
				<td class="data"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
				<td class="data"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
				<td class="data"><xsl:value-of select="elem.summary"/></td>
			</tr>
		</xsl:for-each>
	</table>
	</xsl:if>
</xsl:template>

<xsl:template match="xaf-fields-table">
	<table class="data" cellspacing="0" cellpadding="3">
		<tr class="data_head">
			<th class="data">Name</th>
			<th class="data">Description</th>
			<th class="data">Children</th>
			<th class="data">Options</th>
		</tr>
		<xsl:for-each select="$xaf-xsdn/xaf/dialogs/dialog/*[starts-with(name(), 'field.')]">
			<xsl:sort select="local-name()"/>
			<tr class="data">
				<td class="data"><a href="#{name()}">&lt;<b><xsl:value-of select="name()"/></b>&gt;</a></td>
				<td class="data"><xsl:value-of select="elem.summary"/></td>
				<td class="data" align="right"><xsl:value-of select="count(*[not(starts-with(name(), 'elem.'))])"/></td>
				<td class="data" align="right"><xsl:value-of select="count(elem.attributes/attribute)"/></td>
			</tr>
		</xsl:for-each>
	</table>
</xsl:template>

<xsl:template match="xaf-fields-attr-types-table">
	<table class="data" cellspacing="0" cellpadding="3">
		<tr class="data_head">
			<th class="data">Name</th>
			<th class="data">Type</th>
			<th class="data">Options</th>
			<th class="data">Description</th>
		</tr>
		<xsl:variable name="dialog-field-attr-types" select="$xaf-xsdn/xaf/dialogs/dialog/*[starts-with(name(), 'field.')]/elem.attributes//attribute/type"/>
		<xsl:for-each select="xalan:nodeset($dialog-field-attr-types)/type[not(. = preceding-sibling::type)]">
			<xsl:variable name="type-name" select="."/>
			<xsl:variable name="attr-type-defn" select="$xaf-xsd/xsd:simpleType[@name = $type-name]"/>
			<tr class="data">
				<td class="data"><a class="xml-element-attr"  name="attr-type-{.}"><b><nobr><xsl:value-of select="."/></nobr></b></a></td>
				<td class="data">
					<xsl:choose>
						<xsl:when test="$attr-type-defn/xsd:restriction/@base"><font color="red"><xsl:value-of select="$attr-type-defn/xsd:restriction/@base"/></font></xsl:when>
						<xsl:otherwise><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></xsl:otherwise>
					</xsl:choose>
				</td>
				<td class="data">
					<xsl:choose>
						<xsl:when test="$attr-type-defn/xsd:restriction/xsd:enumeration">
							<font color="green">
							<xsl:for-each select="$attr-type-defn/xsd:restriction/xsd:enumeration">
								<nobr><xsl:value-of select="@value"/></nobr>
								<xsl:if test="position() != last()"><xsl:text> | </xsl:text></xsl:if>
							</xsl:for-each>
							</font>
						</xsl:when>
						<xsl:otherwise><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></xsl:otherwise>
					</xsl:choose>
				</td>
				<td class="data">
					<xsl:choose>
						<xsl:when test="$attr-type-defn/xsd:annotation"><xsl:value-of select="$attr-type-defn/xsd:annotation"/></xsl:when>
						<xsl:otherwise><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></xsl:otherwise>
					</xsl:choose>
				</td>
			</tr>
		</xsl:for-each>
	</table>
</xsl:template>

<xsl:template match="xaf-fields-attrs-tables">
	<xsl:param name="active-page"/>
	<xsl:param name="root-dir"/>
	<xsl:param name="resources-dir"/>
	<xsl:param name="images-dir"/>

	<xsl:for-each select="$xaf-xsdn/xaf/dialogs/dialog/*[starts-with(name(), 'field.')]">
		<xsl:sort select="local-name()"/>
		<p/>
		<xsl:call-template name="element-documentation">
			<xsl:with-param name="elem-defn" select="."/>
			<xsl:with-param name="active-page" select="$active-page"/>
			<xsl:with-param name="root-dir" select="$root-dir"/>
			<xsl:with-param name="resources-dir" select="$resources-dir"/>
			<xsl:with-param name="images-dir" select="$images-dir"/>
		</xsl:call-template>
	</xsl:for-each>
</xsl:template>

<!-- **********************************************************************************************************
     project management tags like <project> copied from ACE article-transform.xsl
     ********************************************************************************************************** -->

<xsl:template match="project">
	<style>
		.projectHead { font-family: arial; font-size: 14pt; font-weight: bold; color: navy; margin-bottom: 5pt; border-bottom: solid 1px red; padding-top: 2pt; }
		.projectSumm { font-family: 'arial,arial'; font-size: 10pt; padding-bottom: 5pt; }
		.projectAttrName { font-family: 'arial'; font-size: 10pt; color: navy; padding-bottom: 1; }
		.projectAttrValue { font-family: 'arial'; font-size: 10pt; border-bottom: solid 1px silver; padding-bottom: 1; }
		h1 { font-family: arial; font-size: 12pt; font-weight: bold; color: darkred; }
		h2 { font-family: arial; font-size: 10pt; font-weight: bold; color: navy; }
		td { font-family: 'tahoma,arial'; font-size: 8pt; }
		.tableColHead { font-family: 'tahoma,arial'; font-size: 8pt; color: navy; border-bottom: solid 2px black; }
		ol { padding : 0; margin: 0; margin-left: 15pt }
	</style>
	<xsl:if test="@name">
		<div class="projectHead"><xsl:value-of select="@name"/></div>
	</xsl:if>
	<xsl:if test="summary">
		<div class="projectSumm"><xsl:apply-templates select="summary"/></div>
	</xsl:if>
	<xsl:if test="project-attribute">
		<table border="0" cellspacing="2" cellpadding="0">
			<xsl:apply-templates select="project-attribute">
				<xsl:with-param name="allResources">
						<xsl:copy-of select="xalan:distinct(.//resource)"/>
				</xsl:with-param>
			</xsl:apply-templates>
		</table>
		<p/>
		<xsl:if test=".//needinfo">
			<b>Need Information</b>
			<table>
				<xsl:for-each select=".//needinfo">
					<xsl:sort select="@source"/>
					<tr valign="top">
						<td style="font-family:tahoma;size:8pt;color:red"><xsl:value-of select="@urgency"/></td>
						<td style="font-family:tahoma;size:8pt"><NOBR>Task</NOBR></td>
						<td style="font-family:tahoma;size:8pt">[<B><xsl:value-of select="@source"/></B>]</td>
						<td style="font-family:tahoma;size:8pt"><xsl:value-of select="."/></td>
					</tr>
				</xsl:for-each>
			</table>
		</xsl:if>
		<p/>
	</xsl:if>
	<xsl:if test="task">
	<table border="0" cellspacing="0" cellpadding="2" >
		<tr bgcolor="#EEEEEE">
			<th class="tableColHead" align="left">No.</th>
			<xsl:if test=".//priority">
			<th class="tableColHead" title="Priority">P</th>
			</xsl:if>
			<th class="tableColHead">Task</th>
			<xsl:if test=".//resource">
			<th class="tableColHead"><nobr>Resource(s)</nobr></th>
			</xsl:if>
			<xsl:if test=".//completed">
			<th class="tableColHead">Completed</th>
			</xsl:if>
			<xsl:if test=".//duration">
			<th class="tableColHead">Duration</th>
			</xsl:if>
			<xsl:if test=".//start">
			<th class="tableColHead">Start</th>
			</xsl:if>
			<xsl:if test=".//finish">
			<th class="tableColHead">Finish</th>
			</xsl:if>
			<xsl:if test=".//remarks">
			<th class="tableColHead">Remarks</th>
			</xsl:if>
		</tr>
		<xsl:apply-templates select="task">
			<xsl:with-param name="haveAnyPriority" select="boolean(.//priority)"/>
			<xsl:with-param name="haveAnyResource" select="boolean(.//resource)"/>
			<xsl:with-param name="haveAnyCompleted" select="boolean(.//completed)"/>
			<xsl:with-param name="haveAnyDuration" select="boolean(.//duration)"/>
			<xsl:with-param name="haveAnyStart" select="boolean(.//start)"/>
			<xsl:with-param name="haveAnyFinish" select="boolean(.//finish)"/>
			<xsl:with-param name="haveAnyRemarks" select="boolean(.//remarks)"/>
		</xsl:apply-templates>
	</table>
	</xsl:if>
</xsl:template>

<xsl:template match="project-attribute">
	<xsl:param name="allResources"/>

	<tr valign="top">
		<td align="right" class="projectAttrName">
			<xsl:value-of select="@name"/>:
		</td>
		<td class="projectAttrValue">
			<xsl:if test="@name = 'Resources'">
			<ol>
			<xsl:for-each select="xalan:nodeset($allResources)/*">
				<xsl:sort select="."/>
				<li><xsl:value-of select="."/></li>
			</xsl:for-each>
			</ol>
			</xsl:if>
			<xsl:if test="@name = 'Last Update'">
			<xsl:value-of select="$file-date"/>
			</xsl:if>
			<xsl:apply-templates/>
		</td>
	</tr>
</xsl:template>

<xsl:template match="task">
	<xsl:param name="haveAnyPriority"/>
	<xsl:param name="haveAnyResource"/>
	<xsl:param name="haveAnyCompleted"/>
	<xsl:param name="haveAnyDuration"/>
	<xsl:param name="haveAnyStart"/>
	<xsl:param name="haveAnyFinish"/>
	<xsl:param name="haveAnyRemarks"/>
	<xsl:variable name="taskNum"><xsl:number level="multiple"/></xsl:variable>
	<tr valign="top">
		<td align="left" style="color: silver">
			<xsl:value-of select="$taskNum"/>
		</td>
		<xsl:if test="$haveAnyPriority">
		<td align="center" style=""><div style="color: red"><xsl:value-of select="priority"/></div></td>
		</xsl:if>
		<td>
			<xsl:if test="task">
				<xsl:attribute name="COLSPAN">0</xsl:attribute>
			</xsl:if>
			<xsl:attribute name="STYLE">
				padding-left: <xsl:value-of select="count(ancestor::*) * 25"/>;
				<xsl:if test="task">
				font-weight: bold;
				color: navy;
				</xsl:if>
			</xsl:attribute>
			<xsl:choose>
				<xsl:when test="name">
					<xsl:if test="new">
						<font color="red"><u>[<B>NEW</B>]</u>:
						</font>
					</xsl:if>
					<xsl:choose>
						<xsl:when test="completed = 100">
							<font color="silver">
							<strike>
								<xsl:apply-templates select="name"/>
							</strike>
							</font>
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates select="name"/>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:if test="comments">
					<DIV>
						<xsl:attribute name="STYLE">
							font-family: arial,helvetica;
							font-size: 9pt;
							font-weight: normal;
							color: green;
						</xsl:attribute>
						<xsl:apply-templates select="comments"/>
					</DIV>
					</xsl:if>
					<xsl:if test="needinfo">
					<DIV>
						<xsl:attribute name="STYLE">
							font-family: arial,helvetica;
							font-size: 9pt;
							font-weight: normal;
							color: red;
						</xsl:attribute>
						<font color="darkred">[Waiting for <B><xsl:value-of select="needinfo/@source"/></B>]</font>
						<xsl:apply-templates select="needinfo"/>
					</DIV>
					</xsl:if>
				</xsl:when>
				<xsl:when test="project/@href">
					<a><xsl:attribute name="href">/lib/page.asp?src=<xsl:value-of select="project/@href"/>&amp;sty=/stylesheets/project.xsl</xsl:attribute><xsl:value-of select="project"/></a>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates/>
				</xsl:otherwise>
			</xsl:choose>
		</td>
		<xsl:if test="$haveAnyResource">
		<td style="color:green">
			<xsl:for-each select="resource">
				<xsl:value-of select="."/>
				<xsl:if test="position() != last()">, </xsl:if>
			</xsl:for-each>
		</td>
		</xsl:if>
		<xsl:if test="$haveAnyCompleted">
		<td align="center">
			<xsl:if test="completed">
			<DIV STYLE="text-align:left; position:relative; border:1px solid black; width:50px; background-color: white">
			  <DIV>
				<xsl:attribute name="STYLE">
				  text-align:center;
				  position:relative;
				  background-color:#389cce;
				  width: <xsl:value-of select="number(completed) div 2"/>px;
				</xsl:attribute>
			  </DIV>
			</DIV>
			</xsl:if>
		</td>
		</xsl:if>
		<xsl:if test="$haveAnyDuration">
		<td align="center"><xsl:value-of select="duration"/></td>
		</xsl:if>
		<xsl:if test="$haveAnyStart">
		<td><xsl:value-of select="start"/></td>
		</xsl:if>
		<xsl:if test="$haveAnyFinish">
		<td><xsl:value-of select="finish"/></td>
		</xsl:if>
		<xsl:if test="$haveAnyRemarks">
		<td><xsl:value-of select="remarks"/></td>
		</xsl:if>
	</tr>
	<xsl:apply-templates select="task">
		<xsl:with-param name="haveAnyPriority" select="$haveAnyPriority"/>
		<xsl:with-param name="haveAnyResource" select="$haveAnyResource"/>
		<xsl:with-param name="haveAnyCompleted" select="$haveAnyCompleted"/>
		<xsl:with-param name="haveAnyDuration" select="$haveAnyDuration"/>
		<xsl:with-param name="haveAnyStart" select="$haveAnyStart"/>
		<xsl:with-param name="haveAnyFinish" select="$haveAnyFinish"/>
		<xsl:with-param name="haveAnyRemarks" select="$haveAnyRemarks"/>
	</xsl:apply-templates>
</xsl:template>

</xsl:stylesheet>
