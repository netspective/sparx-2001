<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html"/>

<xsl:param name="root-url"/>
<xsl:param name="page-heading"/>
<xsl:param name="detail-type"/>
<xsl:param name="detail-name"/>
<xsl:param name="sub-detail-name"/>

<!-- all of the appConfig variables are passed in, so we can use them -->
<xsl:param name="sparx.shared.images-url"/>
<xsl:param name="sparx.ace.images-root-url"/>

<xsl:param name="sql-images-root-url"><xsl:value-of select="$sparx.shared.images-url"/>/dbdd</xsl:param>

<xsl:template match="xaf">
	<xsl:choose>
		<xsl:when test="$detail-type = 'describe' and $detail-name">
			<xsl:apply-templates select="sql-statements/statement[@qualified-name = $detail-name]" mode="detail"/>
		</xsl:when>
		<xsl:otherwise>
			<div class="content">
				<div class="content_head">Statements</div>
				<table class="data_table" cellspacing="0" cellpadding="2" border="0">
					<tr class="data_table_header">
						<th class="data_table">Actions</th>
						<th class="data_table">ID</th>
						<th class="data_table">Parameters</th>
						<th class="data_table" title="Number of times query was executed">Executed</th>
						<th class="data_table" title="Average time (in milliseconds) query took to run">Avg</th>
						<th class="data_table" title="Maximum time (in milliseconds) query took to run">Max</th>
						<th class="data_table" title="Average time (in milliseconds) query took to connect to db">Conn</th>
						<th class="data_table" title="Average time (in milliseconds) query took to bind parameters">Bind</th>
						<th class="data_table" title="Average time (in milliseconds) query took to execute SQL">SQL</th>
						<th class="data_table" title="Number of times query failed to run">Failed</th>
					</tr>
					<xsl:apply-templates select="sql-statements/statement" mode="toc">
						<xsl:sort select="@qualified-name"/>
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
						<xsl:attribute name="href"><xsl:value-of select="concat($root-url, '../../../documents?browseDoc=', @abs-path)"/></xsl:attribute>
						<xsl:attribute name="target"><xsl:value-of select="@abs-path"/></xsl:attribute>
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

<xsl:template name="blank-if-zero">
	<xsl:param name="value"/>
	<xsl:if test="$value > 0">
		<xsl:value-of select="$value"/>
	</xsl:if>
	<xsl:if test="$value = 0">
		&#160;
	</xsl:if>
</xsl:template>

<xsl:template match="statement" mode="toc">
	<xsl:param name="pcount"><xsl:value-of select="count(params/*)"/></xsl:param>
	<tr class="data_table">
		<td class="data_table">
			<a title="Click here to see test statement">
				<xsl:attribute name="href"><xsl:value-of select="concat($root-url,'/test/',@qualified-name)"/></xsl:attribute>
				<xsl:attribute name="target"><xsl:value-of select="concat('statement.',@qualified-name)"/></xsl:attribute>
				<img border="0">
					<xsl:attribute name="src"><xsl:value-of select="$sparx.ace.images-root-url"/>/icons/exec_sql.gif</xsl:attribute>
				</img>
			</a>
			<img border="0"><xsl:attribute name="src"><xsl:value-of select="$sparx.ace.images-root-url"/>/icons/spacer.gif</xsl:attribute></img>
			<a title="Click here to review functional specifications"><xsl:attribute name="href"><xsl:value-of select="concat($root-url,'/describe/',@qualified-name)"/></xsl:attribute><img border="0"><xsl:attribute name="src"><xsl:value-of select="$sparx.ace.images-root-url"/>/icons/describe_sql.gif</xsl:attribute></img></a>
		</td>
		<td class="data_table"><b><a class="data_table" title="Click here to review functional specifications"><xsl:attribute name="href"><xsl:value-of select="concat($root-url, '/', 'describe', '/', @qualified-name)"/></xsl:attribute><xsl:value-of select="@qualified-name"/></a></b></td>
		<td class="data_table" align="right">
			<font color="red">
			<xsl:call-template name="blank-if-zero"><xsl:with-param name="value" select="$pcount"/></xsl:call-template>
			</font>
		</td>		
		<td class="data_table" align="right"><xsl:call-template name="blank-if-zero"><xsl:with-param name="value" select="@stat-total-executions"/></xsl:call-template></td>
		<td class="data_table" align="right"><xsl:call-template name="blank-if-zero"><xsl:with-param name="value" select="@stat-total-avg-time"/></xsl:call-template></td>
		<td class="data_table" align="right"><xsl:call-template name="blank-if-zero"><xsl:with-param name="value" select="@stat-total-max-time"/></xsl:call-template></td>
		<td class="data_table" align="right"><xsl:call-template name="blank-if-zero"><xsl:with-param name="value" select="@stat-connection-avg-time"/></xsl:call-template></td>
		<td class="data_table" align="right"><xsl:call-template name="blank-if-zero"><xsl:with-param name="value" select="@stat-bind-params-avg-time"/></xsl:call-template></td>
		<td class="data_table" align="right"><xsl:call-template name="blank-if-zero"><xsl:with-param name="value" select="@stat-sql-exec-avg-time"/></xsl:call-template></td>
		<td class="data_table" align="right"><xsl:call-template name="blank-if-zero"><xsl:with-param name="value" select="@stat-total-failed"/></xsl:call-template></td>
	</tr>
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
		<img border="0"><xsl:attribute name="src"><xsl:value-of select="$sparx.ace.images-root-url"/>/icons/describe_sql.gif</xsl:attribute></img>
		<font color="navy">Statement</font>&#160;<xsl:value-of select="@qualified-name"/>
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
	<h1>Execution Log</h1>
	<table>
		<tr>
			<th>Source</th>
			<th>Run</th>
			<th>Conn</th>
			<th>Bind</th>
			<th>SQL</th>
			<th>Total</th>
		</tr>
		<xsl:for-each select="exec-log/*">
		<tr>
			<td><font color="green"><xsl:value-of select="@src"/></font></td>
			<td><font color="red"><xsl:value-of select="@init-date"/></font></td>
			<td align="right"><xsl:value-of select="@conn-time"/></td>
			<td align="right"><xsl:value-of select="@bind-time"/></td>
			<td align="right"><xsl:value-of select="@sql-time"/></td>
			<td align="right"><xsl:value-of select="@total-time"/></td>
		</tr>
		</xsl:for-each>
	</table>

	</td></tr>
	</table>	
</xsl:template>

</xsl:stylesheet>
