<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html"/>

<xsl:param name="root-url"/>
<xsl:param name="test-url"/>
<xsl:param name="detail-type"/>
<xsl:param name="detail-name"/>
<xsl:param name="sub-detail-name"/>

<!-- all of the appConfig variables are passed in, so we can use them -->
<xsl:param name="framework.shared.images-url"/>
<xsl:param name="app.ace.images-root-url"/>

<xsl:param name="sql-images-root-url"><xsl:value-of select="$framework.shared.images-url"/>/dbdd</xsl:param>

<xsl:template match="xaf">
	<xsl:choose>
		<xsl:when test="$detail-type = 'statement' and $detail-name">
			<xsl:apply-templates select="sql-statements/statement[@qualified-name = $detail-name]" mode="detail"/>
		</xsl:when>
		<xsl:when test="$detail-type = 'query-defn' and $detail-name">
			<xsl:apply-templates select="query-defn[@id = $detail-name]" mode="detail"/>
		</xsl:when>
		<xsl:otherwise>
			<table class="heading" border="0" cellspacing="0" cellpadding='5'>
			<tr class="heading">
				<td class="heading">
				<img border="0">
					<xsl:attribute name="src"><xsl:value-of select="$sql-images-root-url"/>/table-icon.gif</xsl:attribute>
				</img>
				&#160;SQL Components
				</td>
				<td align="right">
				</td>
			</tr>
			<tr class="heading_rule"><td height="1" colspan="2"></td></tr>
			</table>
			<table>
				<tr>
					<td>
					<h1>Statements</h1>
					<table cellspacing="0" cellpadding="2">
					<tr bgcolor="beige">
						<th>ID</th>
						<th>Package</th>
						<th>Name</th>
						<th>Parameters</th>
					</tr>
					<tr><td colspan="4"><img width="100%" height="2"><xsl:attribute name="src"><xsl:value-of select="$framework.shared.images-url"/>/design/bar.gif</xsl:attribute></img></td></tr>
					<xsl:apply-templates select="sql-statements/statement" mode="toc">
						<xsl:sort select="@qualified-name"/>
					</xsl:apply-templates>
					</table>
					
					<h1>Query Definitions</h1>
					<table cellspacing="0" cellpadding="2">
					<tr bgcolor="beige">
						<th>ID</th>
						<th>Fields</th>
						<th>Joins</th>
						<th>Selects</th>
						<th>Dialogs</th>
						<th></th>
					</tr>
					<tr><td colspan="6"><img width="100%" height="2"><xsl:attribute name="src"><xsl:value-of select="$framework.shared.images-url"/>/design/bar.gif</xsl:attribute></img></td></tr>
					<xsl:apply-templates select="query-defn" mode="toc">
						<xsl:sort select="@id"/>
					</xsl:apply-templates>
					</table>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<p/><br/>
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
	<li><b><a><xsl:attribute name="href"><xsl:value-of select="concat($root-url,'/','dialogs','/',@package)"/></xsl:attribute><xsl:value-of select="@package"/></a></b></li>
</xsl:template>

<xsl:template match="dialogs" mode="toc-select">
	<option>
		<xsl:if test="@package = $detail-name"><xsl:attribute name="selected">yes</xsl:attribute></xsl:if>
		<xsl:attribute name="value"><xsl:value-of select="concat($root-url,'/','dialogs','/',@package)"/>
		</xsl:attribute><xsl:value-of select="@package"/>
	</option>
</xsl:template>

<xsl:template match="register-field" mode="toc">
	<li><b><xsl:value-of select="@tag-name"/></b> (<xsl:value-of select="@class"/>)</li>
</xsl:template>

<xsl:template match="register-skin" mode="toc">
	<li><b><xsl:value-of select="@name"/></b> (<xsl:value-of select="@class"/>)</li>
</xsl:template>

<xsl:template match="statement" mode="toc">
	<tr>
		<td><a><xsl:attribute name="href"><xsl:value-of select="concat($root-url, '/', 'statement', '/', @qualified-name)"/></xsl:attribute><xsl:value-of select="@qualified-name"/></a></td>
		<td><xsl:value-of select="@package"/></td>
		<td><a target="ace-sql-test"><xsl:attribute name="href"><xsl:value-of select="concat($test-url, '/statement/', @qualified-name)"/></xsl:attribute><xsl:value-of select="@name"/></a></td>
		<td align="right">
			<font color="red">
			<xsl:param name="pcount"><xsl:value-of select="count(params/*)"/></xsl:param>
			<xsl:if test="$pcount > 0">
				<font color="green">
				<xsl:value-of select="$pcount"/>
				</font>
			</xsl:if>
			</font>
		</td>		
	</tr>
	<tr><td colspan="4"><img width="100%" height="1"><xsl:attribute name="src"><xsl:value-of select="$framework.shared.images-url"/>/design/bar.gif</xsl:attribute></img></td></tr>
</xsl:template>

<xsl:template match="statement" mode="toc-select">
	<option>
		<xsl:if test="@qualified-name = $detail-name"><xsl:attribute name="selected">yes</xsl:attribute></xsl:if>
		<xsl:attribute name="value"><xsl:value-of select="concat($root-url,'/','statement','/',@qualified-name)"/>
		</xsl:attribute><xsl:value-of select="@qualified-name"/>
	</option>
</xsl:template>

<xsl:template match="statement" mode="detail">
	<table class="heading" border="0" cellspacing="0" cellpadding='5'>
	<tr class="heading">
		<td class="heading">
		<img border="0">
			<xsl:attribute name="src"><xsl:value-of select="$sql-images-root-url"/>/table-icon.gif</xsl:attribute>
		</img>
		&#160;Statement <font color="black"><xsl:value-of select="@qualified-name"/></font>
		</td>
		<td align="right">
			Statements:
			<select onchange="window.location.href = this.options[this.selectedIndex].value">
				<xsl:apply-templates select="/xaf/sql-statements/statement" mode="toc-select">
					<xsl:sort select="@qualified-name"/>
				</xsl:apply-templates>
			</select>
		</td>
	</tr>
	<tr class="heading_rule"><td height="1" colspan="2"></td></tr>
	<tr><td colspan="2">
	<h1>Statement Attributes</h1>
	<table>
		<xsl:for-each select="@*">
			<tr>
				<td class="param_name"><xsl:value-of select="name()"/>:</td>
				<td class="param_value"><xsl:value-of select="."/></td>
			</tr>
		</xsl:for-each>
	</table>
	<h1>SQL</h1>
	<pre>
		<xsl:value-of select="text()"/>
	</pre>
	<xsl:if test="params">
	<h1>Bind Parameters</h1>
	<ol>
	<xsl:for-each select="params/param">
		<li>
			<xsl:if test="@name">
			<b><xsl:value-of select="@name"/></b>: 
			</xsl:if>
			<xsl:value-of select="@value"/> 
			<xsl:if test="@type">
			[<xsl:value-of select="@type"/> ]
			</xsl:if>
		</li>
	</xsl:for-each>
	</ol>
	</xsl:if>
	</td></tr>
	</table>
</xsl:template>

<xsl:template match="query-defn" mode="toc">
	<tr>
		<td><a><xsl:attribute name="href"><xsl:value-of select="concat($root-url, '/', 'query-defn', '/', @id)"/></xsl:attribute><xsl:value-of select="@id"/></a></td>
		<td align="right"><xsl:value-of select="count(field)"/></td>
		<td align="right"><xsl:value-of select="count(join)"/></td>
		<td align="right"><xsl:value-of select="count(select)"/></td>
		<td align="right"><xsl:value-of select="count(select-dialog)"/></td>
		<td><a target="ace-query-defn-test"><xsl:attribute name="href"><xsl:value-of select="concat($test-url, '/query-defn/', @id)"/></xsl:attribute>Test</a></td>
	</tr>
	<tr><td colspan="6"><img width="100%" height="1"><xsl:attribute name="src"><xsl:value-of select="$framework.shared.images-url"/>/design/bar.gif</xsl:attribute></img></td></tr>
</xsl:template>

<xsl:template match="query-defn" mode="toc-select">
	<option>
		<xsl:if test="@id = $detail-name"><xsl:attribute name="selected">yes</xsl:attribute></xsl:if>
		<xsl:attribute name="value"><xsl:value-of select="concat($root-url,'/','query-defn','/',@id)"/>
		</xsl:attribute><xsl:value-of select="@id"/>
	</option>
</xsl:template>

<xsl:template match="query-defn" mode="detail">
	<table class="heading" border="0" cellspacing="0" cellpadding='5'>
	<tr class="heading">
		<td class="heading">
		<img border="0">
			<xsl:attribute name="src"><xsl:value-of select="$sql-images-root-url"/>/table-icon.gif</xsl:attribute>
		</img>
		&#160;Query Definition <font color="black"><xsl:value-of select="@id"/></font>
		</td>
		<td align="right">
			Query Definitions:
			<select onchange="window.location.href = this.options[this.selectedIndex].value">
				<xsl:apply-templates select="/xaf/query-defn" mode="toc-select">
					<xsl:sort select="@id"/>
				</xsl:apply-templates>
			</select>
		</td>
	</tr>
	<tr class="heading_rule"><td height="1" colspan="2"></td></tr>
	<tr><td colspan="2">
	<h1>Fields</h1>
	<table cellspacing="0" cellpadding="2">
		<tr bgcolor="beige">
			<th>ID</th>
			<th>Caption</th>
			<th>Join</th>
			<th>Column</th>
			<th>Column-expr</th>
			<th>Where-expr</th>
			<th>Order-by-expr</th>
		</tr>
		<tr><td colspan="7"><img width="100%" height="2"><xsl:attribute name="src"><xsl:value-of select="$framework.shared.images-url"/>/design/bar.gif</xsl:attribute></img></td></tr>
		<xsl:for-each select="field">
			<tr>
			<td><font color="green"><xsl:value-of select="@id"/></font></td>
			<td><font color="blue"><xsl:value-of select="@caption"/></font></td>
			<td><font color="red"><xsl:value-of select="@join"/></font></td>
			<td><xsl:value-of select="@column"/></td>
			<td><xsl:value-of select="@column-expr"/></td>
			<td><xsl:value-of select="@where-expr"/></td>
			<td><xsl:value-of select="@order-by-expr"/></td>
			</tr>
			<tr><td colspan="7"><img width="100%" height="1"><xsl:attribute name="src"><xsl:value-of select="$framework.shared.images-url"/>/design/bar.gif</xsl:attribute></img></td></tr>
		</xsl:for-each>
	</table>

	<h1>Joins</h1>
	<table cellspacing="0" cellpadding="2">
		<tr bgcolor="beige">
			<th>ID</th>
			<th>Table</th>
			<th>Condition</th>
			<th>Auto-Inc</th>
			<th>Weight</th>
		</tr>
		<tr><td colspan="5"><img width="100%" height="2"><xsl:attribute name="src"><xsl:value-of select="$framework.shared.images-url"/>/design/bar.gif</xsl:attribute></img></td></tr>
		<xsl:for-each select="join">
			<tr>
			<td><font color="green"><xsl:value-of select="@id"/></font></td>
			<td><xsl:value-of select="@table"/></td>
			<td><xsl:value-of select="@condition"/></td>
			<td><xsl:value-of select="@auto-include"/></td>
			<td><xsl:value-of select="@weight"/></td>
			</tr>
			<tr><td colspan="5"><img width="100%" height="1"><xsl:attribute name="src"><xsl:value-of select="$framework.shared.images-url"/>/design/bar.gif</xsl:attribute></img></td></tr>
		</xsl:for-each>
	</table>

	<h1>Selects</h1>
	<table cellspacing="0" cellpadding="2">
		<tr bgcolor="beige">
			<th>ID</th>
			<th>Caption</th>
			<th>Items</th>
			<th>Distinct</th>
		</tr>
		<tr><td colspan="4"><img width="100%" height="2"><xsl:attribute name="src"><xsl:value-of select="$framework.shared.images-url"/>/design/bar.gif</xsl:attribute></img></td></tr>
		<xsl:for-each select="select">
			<tr valign="top">
			<td><font color="green"><xsl:value-of select="@id"/></font></td>
			<td><font color="blue"><xsl:value-of select="@caption"/></font></td>
			<td><xsl:apply-templates select="." mode="detail"/></td>
			<td><xsl:value-of select="@distinct"/></td>
			</tr>
			<tr><td colspan="4"><img width="100%" height="1"><xsl:attribute name="src"><xsl:value-of select="$framework.shared.images-url"/>/design/bar.gif</xsl:attribute></img></td></tr>
		</xsl:for-each>
	</table>

	<h1>Select Dialogs</h1>
	<table cellspacing="0" cellpadding="2">
		<tr bgcolor="beige">
			<th>Name</th>
			<th>Heading</th>
			<th>Select</th>
			<td></td>
		</tr>
		<tr><td colspan="4"><img width="100%" height="2"><xsl:attribute name="src"><xsl:value-of select="$framework.shared.images-url"/>/design/bar.gif</xsl:attribute></img></td></tr>
		<xsl:for-each select="select-dialog">
			<tr valign="top">
			<td><font color="green"><xsl:value-of select="@name"/></font></td>
			<td><font color="blue"><xsl:value-of select="@heading"/></font></td>
			<td>
				<xsl:choose>
					<xsl:when test="select">
						<xsl:apply-templates select="select" mode="detail"/>
					</xsl:when>
					<xsl:otherwise>
					</xsl:otherwise>
				</xsl:choose>
			</td>
			<td><a target="ace-query-defn-test"><xsl:attribute name="href"><xsl:value-of select="concat($test-url, '/query-defn-dlg/', ../@id, '/', @name)"/></xsl:attribute>Test</a></td>
			</tr>
			<tr><td colspan="4"><img width="100%" height="1"><xsl:attribute name="src"><xsl:value-of select="$framework.shared.images-url"/>/design/bar.gif</xsl:attribute></img></td></tr>
		</xsl:for-each>
	</table>
	</td></tr>
	</table>
</xsl:template>

<xsl:template match="select" mode="detail">
	<xsl:for-each select="display">
		Display <b><xsl:value-of select="@field"/></b><br/>
	</xsl:for-each>
	<xsl:for-each select="condition">
		Condition 
		<b>
		<xsl:value-of select="@field"/>&#160;
		<xsl:value-of select="@comparison"/>&#160;
		<xsl:value-of select="@value"/>&#160;
		<xsl:value-of select="@connector"/>
		</b><br/>
	</xsl:for-each>
	<xsl:for-each select="where-expr">
		<xsl:value-of select="@value"/>&#160;
		<xsl:value-of select="@connector"/>
	</xsl:for-each>
	<xsl:for-each select="order-by">
		Order-by <b><xsl:value-of select="@field"/></b><br/>
	</xsl:for-each>
</xsl:template>

</xsl:stylesheet>
