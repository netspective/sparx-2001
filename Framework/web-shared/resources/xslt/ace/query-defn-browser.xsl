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

<xsl:param name="sql-images-root-url"><xsl:value-of select="$framework.shared.images-url"/>/dbdd</xsl:param>

<xsl:template match="xaf">
	<xsl:choose>
		<xsl:when test="$detail-type = 'describe' and $detail-name">
			<xsl:apply-templates select="query-defn[@id = $detail-name]" mode="detail"/>
		</xsl:when>
		<xsl:otherwise>
			<div class="content">
				<div class="content_head">Query Definitions</div>
				<table class="data_table" cellspacing="0" cellpadding="2" border="0">
					<tr class="data_table_header">
						<th class="data_table">Actions</th>
						<th class="data_table">ID</th>
						<th class="data_table">Fields</th>
						<th class="data_table">Joins</th>
						<th class="data_table">Selects</th>
						<th class="data_table">Dialogs</th>
					</tr>
					<xsl:apply-templates select="query-defn" mode="toc">
						<xsl:sort select="@id"/>
					</xsl:apply-templates>
				</table>

				<div class="content_head">Options</div>
				<table class="data_table" cellspacing="0" cellpadding="2" border="0">
					<tr class="data_table_header">
						<th class="data_table">Name</th>
						<th class="data_table">Value</th>
					</tr>
					<xsl:for-each select="meta-info/options">
						<xsl:sort select="@name"/>
						<tr valign="top" class="data_table">
							<td class="data_table"><xsl:value-of select="@name"/></td>
							<td class="data_table"><font color="green"><xsl:value-of select="@value"/></font></td>
						</tr>
					</xsl:for-each>
				</table>
	
				<div class="content_head">Source Files</div>
				<table class="data_table" cellspacing="0" cellpadding="2" border="0">
				<tr class="data_table_header">
					<th class="data_table">File</th>
					<th class="data_table">Included-from</th>
				</tr>
				<xsl:for-each select="meta-info/source-files/source-file">
					<tr class="data_table">
						<td class="data_table">
						<a class="data_table">
						<xsl:attribute name="href"><xsl:value-of select="@abs-path"/></xsl:attribute>
						<xsl:value-of select="@abs-path"/>
						</a>
						</td>
						<td class="data_table">
							<xsl:value-of select="@included-from"/>
							<xsl:if test="not(@included-from)">&#160;</xsl:if>
						</td>
					</tr>
				</xsl:for-each>
				</table>

				<p/>
				<xsl:if test="meta-info/errors">
					<div class="content_head">Errors</div>
					<ol>
					<xsl:for-each select="meta-info/errors/error">
						<li><xsl:value-of select="."/></li>
					</xsl:for-each>
					</ol>
				</xsl:if>
			</div>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template match="query-defn" mode="toc">
	<tr>
		<td class="data_table">
			<a title="Click here to see test statement">
				<xsl:attribute name="href"><xsl:value-of select="concat($root-url, '/test/query-defn/', @id)"/></xsl:attribute>
				<xsl:attribute name="target"><xsl:value-of select="concat('query-defn.', @id)"/></xsl:attribute>
				<img border="0">
					<xsl:attribute name="src"><xsl:value-of select="$framework.ace.images-root-url"/>/icons/exec_query_defn.gif</xsl:attribute>
				</img>
			</a>
			<img border="0"><xsl:attribute name="src"><xsl:value-of select="$framework.ace.images-root-url"/>/icons/spacer.gif</xsl:attribute></img>
			<a title="Click here to review functional specifications"><xsl:attribute name="href"><xsl:value-of select="concat($root-url, '/', 'describe', '/', @id)"/></xsl:attribute><img border="0"><xsl:attribute name="src"><xsl:value-of select="$framework.ace.images-root-url"/>/icons/describe_query_defn.gif</xsl:attribute></img></a>
		</td>
		<td class="data_table"><b><a class="data_table" title="Click here to review functional specifications"><xsl:attribute name="href"><xsl:value-of select="concat($root-url, '/', 'describe', '/', @id)"/></xsl:attribute><xsl:value-of select="@id"/></a></b></td>
		<td class="data_table" align="right"><xsl:value-of select="count(field)"/></td>
		<td class="data_table" align="right"><xsl:value-of select="count(join)"/></td>
		<td class="data_table" align="right"><xsl:value-of select="count(select)"/></td>
		<td class="data_table" align="right"><xsl:value-of select="count(select-dialog)"/></td>
	</tr>
</xsl:template>

<xsl:template match="query-defn" mode="toc-select">
	<option>
		<xsl:if test="@id = $detail-name"><xsl:attribute name="selected">yes</xsl:attribute></xsl:if>
		<xsl:attribute name="value"><xsl:value-of select="concat($root-url,'/','query-defn','/',@id)"/>
		</xsl:attribute><xsl:value-of select="@id"/>
	</option>
</xsl:template>

<xsl:template name="data-or-blank">
	<xsl:param name="value"/>
	<xsl:if test="$value">
		<xsl:value-of select="$value"/>
	</xsl:if>
	<xsl:if test="not($value)">
		&#160;
	</xsl:if>
</xsl:template>

<xsl:template match="query-defn" mode="detail">
	<table class="heading" border="0" cellspacing="0" cellpadding='5'>
	<tr class="heading">
		<td class="heading">
		<img border="0"><xsl:attribute name="src"><xsl:value-of select="$framework.ace.images-root-url"/>/icons/describe_query_defn.gif</xsl:attribute></img>
		<font color="navy">Query Definition</font>&#160;<xsl:value-of select="@id"/>
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
	</table>

	<div class="content">
	<div class="content_head">Fields</div>
	<table class="data_table" cellspacing="0" cellpadding="2" border="0">
		<tr class="data_table_header">
			<th class="data_table">ID</th>
			<th class="data_table">Caption</th>
			<th class="data_table">Join</th>
			<th class="data_table">Column</th>
			<th class="data_table">Column-expr</th>
			<th class="data_table">Where-expr</th>
			<th class="data_table">Order-by-expr</th>
			<th class="data_table">Bind-expr</th>
		</tr>
		<xsl:for-each select="field">
			<tr class="data_table">
			<td class="data_table"><font color="green"><xsl:value-of select="@id"/></font></td>
			<td class="data_table"><font color="blue"><xsl:value-of select="@caption"/></font></td>
			<td class="data_table"><font color="red"><xsl:value-of select="@join"/></font></td>
			<td class="data_table"><xsl:call-template name="data-or-blank"><xsl:with-param name="value" select="@column"/></xsl:call-template></td>
			<td class="data_table"><xsl:call-template name="data-or-blank"><xsl:with-param name="value" select="@column-expr"/></xsl:call-template></td>
			<td class="data_table"><xsl:call-template name="data-or-blank"><xsl:with-param name="value" select="@where-expr"/></xsl:call-template></td>
			<td class="data_table"><xsl:call-template name="data-or-blank"><xsl:with-param name="value" select="@order-by-expr"/></xsl:call-template></td>
			<td class="data_table"><xsl:call-template name="data-or-blank"><xsl:with-param name="value" select="@bind-expr"/></xsl:call-template></td>
			</tr>
		</xsl:for-each>
	</table>

	<div class="content_head">Joins</div>
	<table class="data_table" cellspacing="0" cellpadding="2" border="0">
		<tr class="data_table_header">
			<th class="data_table">ID</th>
			<th class="data_table">Table</th>
			<th class="data_table">Condition</th>
			<th class="data_table">Auto-Inc</th>
			<th class="data_table">Weight</th>
		</tr>
		<xsl:for-each select="join">
			<tr class="data_table">
			<td class="data_table"><font color="green"><xsl:value-of select="@id"/></font></td>
			<td class="data_table"><xsl:call-template name="data-or-blank"><xsl:with-param name="value" select="@table"/></xsl:call-template></td>
			<td class="data_table"><xsl:call-template name="data-or-blank"><xsl:with-param name="value" select="@condition"/></xsl:call-template></td>
			<td class="data_table"><xsl:call-template name="data-or-blank"><xsl:with-param name="value" select="@auto-include"/></xsl:call-template></td>
			<td class="data_table"><xsl:call-template name="data-or-blank"><xsl:with-param name="value" select="@weight"/></xsl:call-template></td>
			</tr>
		</xsl:for-each>
	</table>

	<div class="content_head">Selects</div>
	<table class="data_table" cellspacing="0" cellpadding="2" border="0">
		<tr class="data_table_header">
			<th class="data_table">ID</th>
			<th class="data_table">Caption</th>
			<th class="data_table">Items</th>
			<th class="data_table">Distinct</th>
		</tr>
		<xsl:for-each select="select">
			<tr class="data_table" valign="top">
			<td class="data_table"><font color="green"><xsl:value-of select="@id"/></font></td>
			<td class="data_table"><font color="blue"><xsl:value-of select="@caption"/></font></td>
			<td class="data_table"><xsl:apply-templates select="." mode="detail"/></td>
			<td class="data_table"><xsl:call-template name="data-or-blank"><xsl:with-param name="value" select="@distinct"/></xsl:call-template></td>
			</tr>
		</xsl:for-each>
	</table>

	<div class="content_head">Select Dialogs</div>
	<table class="data_table" cellspacing="0" cellpadding="2" border="0">
		<tr class="data_table_header">
			<th class="data_table">Actions</th>
			<th class="data_table">Name</th>
			<th class="data_table">Heading</th>
			<th class="data_table">Select</th>
		</tr>
		<xsl:for-each select="select-dialog">
			<tr class="data_table" valign="top">
			<td class="data_table">
				<a title="Click here to test dialog"><xsl:attribute name="href"><xsl:value-of select="concat($root-url, '/test/query-defn-dlg/', ../@id, '/', @name)"/></xsl:attribute><img border="0"><xsl:attribute name="src"><xsl:value-of select="$framework.ace.images-root-url"/>/icons/exec_dialog.gif</xsl:attribute></img></a>
			</td>
			<td class="data_table"><font color="green"><xsl:value-of select="@name"/></font></td>
			<td class="data_table"><font color="blue"><xsl:value-of select="@heading"/></font></td>
			<td class="data_table">
				<xsl:choose>
					<xsl:when test="select">
						<xsl:apply-templates select="select" mode="detail"/>
					</xsl:when>
					<xsl:otherwise>
					</xsl:otherwise>
				</xsl:choose>
			</td>
			</tr>
		</xsl:for-each>
	</table>
	</div>
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
