<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html"/>

<xsl:param name="root-url"/>
<xsl:param name="page-heading"/>
<xsl:param name="detail-type"/>
<xsl:param name="detail-name"/>
<xsl:param name="sub-detail-name"/>

<!-- all of the appConfig variables are passed in, so we can use them -->
<xsl:param name="framework.shared.images-url"/>
<xsl:param name="framework.ace.images-root-url"/>

<xsl:param name="ui-images-root-url"><xsl:value-of select="$framework.shared.images-url"/>/dbdd</xsl:param>

<xsl:template match="xaf">
	<xsl:choose>
		<xsl:when test="$detail-type = 'dialogs' and $detail-name">
			<table class="heading" border="0" cellspacing="0" cellpadding='5'>
			<tr class="heading">
				<td class="heading">
				<img border="0">
					<xsl:attribute name="src"><xsl:value-of select="$ui-images-root-url"/>/table-icon.gif</xsl:attribute>
				</img>
				&#160;Dialogs Package <font color="black"><xsl:value-of select="$detail-name"/></font>
				</td>
				<td align="right">
					Packages:
					<select onchange="window.location.href = this.options[this.selectedIndex].value">
						<xsl:apply-templates select="dialogs" mode="toc-select">
							<xsl:sort select="@name"/>
						</xsl:apply-templates>
					</select>
				</td>
			</tr>
			<tr class="heading_rule"><td height="1" colspan="2"></td></tr>
			</table>
			<table>
				<tr>
					<td>
					<h1>Dialog Packages</h1>
					<ol>
					<xsl:apply-templates select="dialogs[@package = $detail-name]/dialog" mode="toc"/>
					</ol>
					</td>
				</tr>
			</table>
		</xsl:when>
		<xsl:when test="$detail-type = 'describe' and $detail-name">
			<xsl:apply-templates select="dialogs/dialog[@qualified-name = $detail-name]" mode="detail"/>
		</xsl:when>
		<xsl:otherwise>
			<table class="heading" border="0" cellspacing="0" cellpadding='5'>
			<tr class="heading">
				<td class="heading">
				<img border="0">
					<xsl:attribute name="src"><xsl:value-of select="$ui-images-root-url"/>/table-icon.gif</xsl:attribute>
				</img>
				&#160;<xsl:value-of select="$page-heading"/>
				</td>
				<td align="right">
				</td>
			</tr>
			<tr class="heading_rule"><td height="1" colspan="2"></td></tr>
			</table>
			<table>
				<tr>
					<td>
					<h1>Dialogs</h1>
					<table cellspacing="0" cellpadding="2">
					<tr bgcolor="beige">
						<th>ID</th>
						<th>Heading</th>
						<th>Package</th>
						<th>Name</th>
						<th title="Retain Request Parameters">Retain</th>
						<th>Fields</th>
					</tr>
					<tr><td colspan="6"><img width="100%" height="2"><xsl:attribute name="src"><xsl:value-of select="$framework.shared.images-url"/>/design/bar.gif</xsl:attribute></img></td></tr>
					<xsl:apply-templates select="dialogs/dialog" mode="toc">
						<xsl:sort select="@qualified-name"/>
					</xsl:apply-templates>
					</table>					
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<h1>Source Files</h1>
						<ol>
						<xsl:for-each select="meta-info/source-files/source-file">
							<li>
								<a>
								<xsl:attribute name="href"><xsl:value-of select="@abs-path"/></xsl:attribute>
								<xsl:value-of select="@abs-path"/>
								</a>
								<xsl:if test="@included-from">
									(from <xsl:value-of select="@included-from"/>)
								</xsl:if>
							</li>
						</xsl:for-each>
						</ol>

						<p/>
						<xsl:if test="meta-info/errors">
							<h1>Schema Errors</h1>
							<ol>
							<xsl:for-each select="meta-info/errors/error">
								<li><xsl:value-of select="."/></li>
							</xsl:for-each>
							</ol>
						</xsl:if>
					</td>
				</tr>
			</table>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template match="dialogs" mode="toc">
	<li><b><a><xsl:attribute name="href"><xsl:value-of select="concat($root-url,'/','describe','/',@package)"/></xsl:attribute><xsl:value-of select="@package"/></a></b></li>
</xsl:template>

<xsl:template match="dialogs" mode="toc-select">
	<option>
		<xsl:if test="@package = $detail-name"><xsl:attribute name="selected">yes</xsl:attribute></xsl:if>
		<xsl:attribute name="value"><xsl:value-of select="concat($root-url,'/describe/',@package)"/>
		</xsl:attribute><xsl:value-of select="@package"/>
	</option>
</xsl:template>

<xsl:template match="register-field" mode="toc">
	<li><b><xsl:value-of select="@tag-name"/></b> (<xsl:value-of select="@class"/>)</li>
</xsl:template>

<xsl:template match="register-skin" mode="toc">
	<li><b><xsl:value-of select="@name"/></b> (<xsl:value-of select="@class"/>)</li>
</xsl:template>

<xsl:template match="dialog" mode="toc">
	<tr>
		<td><a title="Click here to review functional specifications"><xsl:attribute name="href"><xsl:value-of select="concat($root-url,'/describe/',@qualified-name)"/></xsl:attribute><xsl:value-of select="@qualified-name"/></a></td>
		<td><font color="green"><xsl:value-of select="@heading"/></font></td>
		<td><xsl:value-of select="@package"/></td>
		<td><a title="Click here to see dialog sample" target="ace-dialog-test"><xsl:attribute name="href"><xsl:value-of select="concat($root-url,'/test/',@qualified-name)"/></xsl:attribute><xsl:value-of select="@name"/></a></td>
		<td><font color="red"><xsl:value-of select="@retain-params"/></font></td>
		<td align="right"><font color="red"><xsl:value-of select="count(*)"/></font></td>
	</tr>
	<tr><td colspan="6"><img width="100%" height="1"><xsl:attribute name="src"><xsl:value-of select="$framework.shared.images-url"/>/design/bar.gif</xsl:attribute></img></td></tr>
</xsl:template>

<xsl:template match="dialog" mode="toc-select">
	<option>
		<xsl:if test="@qualified-name = $detail-name"><xsl:attribute name="selected">yes</xsl:attribute></xsl:if>
		<xsl:attribute name="value"><xsl:value-of select="concat($root-url,'/describe/',@qualified-name)"/>
		</xsl:attribute><xsl:value-of select="@qualified-name"/>
	</option>
</xsl:template>

<xsl:template match="dialog" mode="detail">
	<table class="heading" border="0" cellspacing="0" cellpadding='5'>
	<tr class="heading">
		<td class="heading">
		<img border="0">
			<xsl:attribute name="src"><xsl:value-of select="$ui-images-root-url"/>/table-icon.gif</xsl:attribute>
		</img>
		&#160;<font color="black">Dialog</font>&#160;<xsl:value-of select="@qualified-name"/>
		</td>
		<td align="right">
			Dialogs:
			<select onchange="window.location.href = this.options[this.selectedIndex].value">
				<xsl:apply-templates select="/xaf/dialogs/dialog" mode="toc-select">
					<xsl:sort select="@qualified-name"/>
				</xsl:apply-templates>
			</select>
		</td>
	</tr>
	<tr class="heading_rule"><td height="1" colspan="2"></td></tr>
	<tr><td colspan="2">
	<h1>Dialog Attributes</h1>
	<table>
		<xsl:for-each select="@*">
			<tr>
				<td class="param_name"><xsl:value-of select="name()"/>:</td>
				<td class="param_value"><xsl:value-of select="."/></td>
			</tr>
		</xsl:for-each>
	</table>
	<h1>Dialog Fields</h1>
	<table border="0" cellspacing="0" cellpadding="2">
		<tr bgcolor="beige">
			<th>ID</th>
			<th>Name</th>
			<th>Caption</th>
			<th>Type</th>
			<th>Default</th>
			<th>Options</th>
		</tr>
		<tr><td colspan="10"><img width="100%" height="3"><xsl:attribute name="src"><xsl:value-of select="$framework.shared.images-url"/>/design/bar.gif</xsl:attribute></img></td></tr>
	<xsl:for-each select="*">
		<tr valign="top">
			<td></td>
			<td><font color="green"><xsl:value-of select="@name"/></font></td>
			<td><font color="navy"><xsl:value-of select="@caption"/></font></td>
			<td><xsl:value-of select="name()"/></td>
			<td><font color="navy"><xsl:value-of select="@default"/></font></td>
			<td>
				<xsl:for-each select="@*">
					<xsl:if test="name() != 'name' and name() != 'caption' and name() != 'default'">
						<font color="green"><xsl:value-of select="name()"/></font>
						= 
						<font color="navy"><xsl:value-of select="."/></font><br/>
					</xsl:if>
				</xsl:for-each>
			</td>
		</tr>
		<tr><td colspan="10"><img width="100%" height="1"><xsl:attribute name="src"><xsl:value-of select="$framework.shared.images-url"/>/design/bar.gif</xsl:attribute></img></td></tr>
	</xsl:for-each>
	</table>
	</td></tr>
	</table>
</xsl:template>

</xsl:stylesheet>
