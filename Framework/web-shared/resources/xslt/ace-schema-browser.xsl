<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html"/>

<xsl:param name="root-url"/>
<xsl:param name="detail-type"/>
<xsl:param name="detail-name"/>
<xsl:param name="sub-detail-name"/>
<xsl:param name="images-root-url">/shared/resources/images</xsl:param>
<xsl:param name="dbdd-images-root-url">/shared/resources/images/dbdd</xsl:param>

<xsl:template match="schema">
	<xsl:choose>
		<xsl:when test="$detail-type = 'table' and not($sub-detail-name)">
			<xsl:apply-templates select="table[@name = $detail-name]" mode="detail"/>
		</xsl:when>
		<xsl:when test="$detail-type = 'table' and $sub-detail-name">
			<xsl:apply-templates select="table[@name = $detail-name]/column[@name = $sub-detail-name]"/>
		</xsl:when>
		<xsl:when test="$detail-type = 'erd'">
			<xsl:apply-templates select="table-structure"/>
		</xsl:when>
		<xsl:when test="$detail-type = 'generate'">
		</xsl:when>
		<xsl:when test="$detail-type = 'graphviz'">
			<code>
			digraph G
			{
			<xsl:apply-templates select="table[@is-audit != 'yes']" mode="graphviz-node">
				<xsl:sort select="@name"/>
			</xsl:apply-templates>	
			<xsl:apply-templates select="table[@is-audit != 'yes']" mode="graphviz-edge">
				<xsl:sort select="@name"/>
			</xsl:apply-templates>	
			}
			</code>
		</xsl:when>
		<xsl:otherwise>
		<table class="heading" border="0" cellspacing="0" cellpadding='5'>
		<tr class="heading">
			<td class="heading">Schema: <xsl:value-of select="@name"/></td>
		</tr>
		<tr class="heading_rule">
			<td height="1" ></td>
		</tr>
		</table>
		<table cellpadding="10">
			<tr>
				<td>
					Data-types (<xsl:value-of select="count(datatype)"/>): <br/>
					<select size="1">
						<xsl:apply-templates select="datatype" mode="toc">
							<xsl:sort select="@name"/>
						</xsl:apply-templates>
					</select>
				</td>
				<td>
					Table-types (<xsl:value-of select="count(tabletype)"/>): <br/>
					<select size="1">
						<xsl:apply-templates select="tabletype" mode="toc">
							<xsl:sort select="@name"/>
						</xsl:apply-templates>
					</select>
				</td>
			</tr>
			<tr valign="top">
				<td>
					Tables (<xsl:value-of select="count(table)"/>): <br/>
					<select size="15" onchange="window.location.href = this.options[this.selectedIndex].value">
						<xsl:apply-templates select="table" mode="toc">
							<xsl:sort select="@name"/>
						</xsl:apply-templates>
					</select>
				</td>
				<td>
					Table Structure (ERD):
					<!--<br/>
					<a><xsl:attribute name="href"><xsl:value-of select="$root-url"/>/erd</xsl:attribute>View ERD (Table Structure)</a>-->
					<xsl:apply-templates select="table-structure"/>
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
		<p/>
		<p/>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template match="datatype" mode="toc">
	<option><xsl:attribute name="value"><xsl:value-of select="$root-url"/>/datatype/<xsl:value-of select="@name"/></xsl:attribute><xsl:value-of select="@name"/></option>
</xsl:template>

<xsl:template match="tabletype" mode="toc">
	<option><xsl:attribute name="value"><xsl:value-of select="$root-url"/>/tabletype/<xsl:value-of select="@name"/></xsl:attribute><xsl:value-of select="@name"/></option>
</xsl:template>

<xsl:template match="table" mode="toc">
	<option><xsl:if test="@name = $detail-name"><xsl:attribute name="selected">yes</xsl:attribute></xsl:if><xsl:attribute name="value"><xsl:value-of select="$root-url"/>/table/<xsl:value-of select="@name"/></xsl:attribute><xsl:value-of select="@name"/></option>
</xsl:template>

<xsl:template match="table" mode="structure">
	<div class="table_struct">
		<xsl:attribute name="id"><xsl:value-of select="@name"/></xsl:attribute>
		<!--
		<a class="table_struct"><xsl:attribute name="href"><xsl:value-of select="$root-url"/>/table/<xsl:value-of select="@name"/></xsl:attribute><xsl:value-of select="@name"/></a>
		-->
		<xsl:if test="not(table)">
		<xsl:value-of select="@name"/>
		<span class="table_struct_menu"><a class="column_references"><xsl:attribute name="href"><xsl:value-of select="concat($root-url, '/table/', @name)"/></xsl:attribute>4</a></span>
		</xsl:if>
		<xsl:if test="table">
		<span class="table_struct_name">
		<xsl:attribute name="onclick">var children = document.all('<xsl:value-of select="concat(@name, '_children')"/>'); children.className = (children.className == 'table_struct_open' ? 'table_struct_closed' : 'table_struct_open') ;</xsl:attribute>
		<b><xsl:value-of select="@name"/></b>
		<xsl:if test="@parent-col">
			<font color="skyblue">
			(join: parent.<xsl:value-of select="@parent-col"/> =
			this.<xsl:value-of select="@child-col"/>)
			</font>
		</xsl:if>
		</span>
		<span class="table_struct_menu"><a class="column_references"><xsl:attribute name="href"><xsl:value-of select="concat($root-url, '/table/', @name)"/></xsl:attribute>4</a></span>
		<div class="table_struct_closed">
		<xsl:attribute name="id"><xsl:value-of select="concat(@name, '_children')"/></xsl:attribute>
		<xsl:apply-templates select="table" mode="structure">
			<xsl:sort select="@name"/>
		</xsl:apply-templates>
		</div>
		</xsl:if>
	</div>
</xsl:template>

<xsl:template match="table" mode="graphviz-node">
	<xsl:value-of select="@name"/>;
</xsl:template>

<xsl:template match="table" mode="graphviz-edge">
	<xsl:param name="table-name"><xsl:value-of select="@name"/></xsl:param>
	<xsl:for-each select="column">
		<xsl:if test="@reftype = 'parent'">
			<xsl:value-of select="$table-name"/> -&gt; <xsl:value-of select="@reftbl"/> [label="<xsl:value-of select="@refcol"/>"];
		</xsl:if>
		<!--
		<xsl:if test="@reftype = 'lookup'">
			<xsl:value-of select="$table-name"/> -&gt; <xsl:value-of select="@reftbl"/> [label="<xsl:value-of select="@refcol"/>"];
		</xsl:if>
		-->
	</xsl:for-each>
</xsl:template>

<!--
	<xsl:param name="parent-table"/>
	
	<xsl:if test="@parent-col and $parent-table">
		<xsl:value-of select="@name"/> -&gt; <xsl:value-of select="$parent-table"/> 
		<xsl:if test="@parent-col = @child-col">
		[label="<xsl:value-of select="@child-col"/>"];
		</xsl:if>
		<xsl:if test="@parent-col != @child-col">
		[label="<xsl:value-of select="@child-col"/>=<xsl:value-of select="@parent-col"/>"];
		</xsl:if>
	</xsl:if>
	
	<xsl:apply-templates select="table" mode="graphviz">
		<xsl:with-param name="parent-table"><xsl:value-of select="@name"/></xsl:with-param>
		<xsl:sort select="@name"/>
	</xsl:apply-templates>	
-->

<xsl:template match="table-structure">
	<div class="table_struct_open">
	<xsl:apply-templates select="table" mode="structure">
		<xsl:sort select="@name"/>
	</xsl:apply-templates>
	</div>
</xsl:template>

<xsl:template match="table" mode="detail">
	<xsl:param name="table-name" select="@name"/>
	<table class="heading" border="0" cellspacing="0" cellpadding='5'>
	<tr class="heading">
		<td class="heading">
		<img border="0">
			<xsl:attribute name="src"><xsl:value-of select="$dbdd-images-root-url"/>/table-icon.gif</xsl:attribute>
		</img>
		&#160;<xsl:value-of select="@name"/> Table
		</td>
		<td align="right">
			Tables:
			<select onchange="window.location.href = this.options[this.selectedIndex].value">
				<xsl:apply-templates select="../table" mode="toc">
					<xsl:sort select="@name"/>
				</xsl:apply-templates>
			</select>
		</td>
	</tr>
	<tr class="heading_rule"><td height="1" colspan="2"></td></tr>
	</table>
	<table cellspacing="10"><tr><td>
	<table>
		<tr valign="top">
			<td class="table_info_caption">Description:</td>
			<td class="table_description">
				<xsl:value-of select="@descr | description"/>
			</td>
		</tr>
		<xsl:if test="@parent | parent">
		<tr>
			<td class="table_info_caption">Parent:</td>
			<td class="table_info_value">
			<a class="column_references"><xsl:attribute name="href"><xsl:value-of select="concat($root-url, '/table/', @parent | parent)"/></xsl:attribute><xsl:value-of select="@parent | parent"/></a>
			</td>
		</tr>
		</xsl:if>
		<tr valign="top">
			<td class="table_info_caption">Children:</td>
			<td class="table_info_value">
				<ol>
				<xsl:for-each select="../table[@parent = $table-name or parent = $table-name]">
					<xsl:sort select="@name"/>
					<li><a class="column_references"><xsl:attribute name="href"><xsl:value-of select="concat($root-url, '/table/', @name)"/></xsl:attribute><xsl:value-of select="@name"/></a></li>
				</xsl:for-each>
				</ol>
			</td>
		</tr>
		<tr>
			<td class="table_info_caption">Abbreviation:</td>
			<td class="table_info_value">
				<xsl:value-of select="@abbrev"/>
			</td>
		</tr>
		<tr>
			<td class="table_info_caption">Extends (Inherits):</td>
			<td class="table_info_value"><xsl:value-of select="@type"/></td>
		</tr>
	</table>
	<p/>
	<table border="0" cellspacing="0">
	<tr bgcolor="beige">
		<th></th>
		<th></th>
		<th>Column Name</th>
		<th></th>
		<th>Datatype</th>
		<th></th>
		<th>Default</th>
		<th></th>
		<th>SQL Defn</th>
		<th></th>
		<th>Inherits</th>
		<th></th>
		<th>References</th>
		<th></th>
	</tr>
	<tr><td colspan="14"><img width="100%" height="3"><xsl:attribute name="src"><xsl:value-of select="$images-root-url"/>/design/bar.gif</xsl:attribute></img></td></tr>
	<xsl:for-each select="column">
	<tr>
		<xsl:if test="@primarykey='yes'">
			<xsl:attribute name="bgcolor">#EEEEEE</xsl:attribute>
		</xsl:if>
		<td rowspan="2">
			<xsl:if test="@primarykey = 'yes'">
			<img>
				<xsl:attribute name="src"><xsl:value-of select="$dbdd-images-root-url"/>/primary-key.gif</xsl:attribute>
				<xsl:attribute name="title"><xsl:value-of select="@name"/> is a primary key</xsl:attribute>
			</img>
			</xsl:if>
			<xsl:if test="@required = 'yes'">
			<img>
				<xsl:attribute name="src"><xsl:value-of select="$dbdd-images-root-url"/>/value-required.gif</xsl:attribute>
				<xsl:attribute name="title"><xsl:value-of select="@name"/> is a required column</xsl:attribute>
			</img>
			</xsl:if>
			<xsl:if test="@required = 'dbms'">
			<img>
				<xsl:attribute name="src"><xsl:value-of select="$dbdd-images-root-url"/>/value-required-dbms.gif</xsl:attribute>
				<xsl:attribute name="title"><xsl:value-of select="@name"/> is required by the dbms but not necessarily the UI</xsl:attribute>
			</img>
			</xsl:if>
			<xsl:if test="@reftype = 'lookup'">
			<img>
				<xsl:attribute name="src"><xsl:value-of select="$dbdd-images-root-url"/>/foreign-key.gif</xsl:attribute>
				<xsl:attribute name="title"><xsl:value-of select="@name"/> is a foreign-key reference</xsl:attribute>
			</img>
			</xsl:if>
			<xsl:if test="@reftype = 'self'">
			<img>
				<xsl:attribute name="src"><xsl:value-of select="$dbdd-images-root-url"/>/self-ref-key.gif</xsl:attribute>
				<xsl:attribute name="title"><xsl:value-of select="@name"/> is a self-key reference</xsl:attribute>
			</img>
			</xsl:if>
			<xsl:if test="@reftype = 'parent'">
			<img>
				<xsl:attribute name="src"><xsl:value-of select="$dbdd-images-root-url"/>/parent-ref-key.gif</xsl:attribute>
				<xsl:attribute name="title"><xsl:value-of select="@name"/> is a parent-key reference</xsl:attribute>
			</img>
			</xsl:if>
			<xsl:if test="@reftype = 'usetype'">
			<img>
				<xsl:attribute name="src"><xsl:value-of select="$dbdd-images-root-url"/>/use-type-ref.gif</xsl:attribute>
				<xsl:attribute name="title"><xsl:value-of select="@name"/> is a use-type reference</xsl:attribute>
			</img>
			</xsl:if>
			<xsl:if test="@unique = 'yes'">
			<img>
				<xsl:attribute name="src"><xsl:value-of select="$dbdd-images-root-url"/>/value-unique.gif</xsl:attribute>
				<xsl:attribute name="title"><xsl:value-of select="@name"/> is a unique-value column</xsl:attribute>
			</img>
			</xsl:if>
			<xsl:if test="referenced-by">
			<img>
				<xsl:attribute name="src"><xsl:value-of select="$dbdd-images-root-url"/>/foreign-key-elsewhere.gif</xsl:attribute>
				<xsl:attribute name="title"><xsl:value-of select="@name"/> is referenced by other columns elsewhere</xsl:attribute>
			</img>
			</xsl:if>
		</td>
		<td width="4" rowspan="2"></td>
		<td class="column_name">
			<a class="column_name"><xsl:attribute name="href"><xsl:value-of select="concat($root-url, '/table/', $table-name)"/>/<xsl:value-of select="@name"/></xsl:attribute><xsl:value-of select="@name"/></a>
		</td>
		<td width="4"></td>
		<td class="column_domain"><xsl:value-of select="@type"/></td>
		<td width="4"></td>
		<td class="column_default"><xsl:value-of select="@default"/></td>
		<td width="4"></td>
		<td class="column_sql"><xsl:value-of select="sqldefn | @sqldefn"/></td>
		<td width="4"></td>
		<td class="column_inherits"><xsl:value-of select="@_inherited-from"/></td>
		<td width="4"></td>
		<td class="column_references">
			<xsl:if test="@reftbl">
			<a class="column_references"><xsl:attribute name="href"><xsl:value-of select="concat($root-url, '/table/', @reftbl)"/></xsl:attribute><xsl:value-of select="concat(@reftbl, '.', @refcol, ' (', @reftype, ')')"/></a>
			</xsl:if>
		</td>
		<td width="4"></td>
	</tr>
	<tr>
		<xsl:if test="@primarykey='yes'">
			<xsl:attribute name="bgcolor">#EEEEEE</xsl:attribute>
		</xsl:if>
		<td colspan="12" class="column_descr"><xsl:value-of select="descr | @descr"/></td>
	</tr>
	<tr><td colspan="14"><img width="100%" height="1"><xsl:attribute name="src"><xsl:value-of select="$images-root-url"/>/design/bar.gif</xsl:attribute></img></td></tr>
	</xsl:for-each>
	</table>
	</td></tr></table>
</xsl:template>

<xsl:template match="column">
	<table class="heading" border="0" cellspacing="0" cellpadding='5'>
	<tr class="heading">
		<td class="heading">
		<img border="0">
			<xsl:attribute name="src"><xsl:value-of select="$dbdd-images-root-url"/>/column-icon.gif</xsl:attribute>
		</img>
		&#160;<xsl:value-of select="concat($detail-name, '.', @name)"/> Column
		</td>
		<td align="right">
			Tables:
			<select onchange="window.location.href = this.options[this.selectedIndex].value">
				<xsl:apply-templates select="../../table" mode="toc">
					<xsl:sort select="@name"/>
				</xsl:apply-templates>
			</select>
		</td>
	</tr>
	<tr class="heading_rule"><td height="1" colspan="2"></td></tr>
	</table>

	<table cellspacing="10"><tr><td>
	<a href="." style="text-decoration:none">Return to <b><xsl:value-of select="$detail-name"/></b> table.</a>
	<p/>
	<table>
	<xsl:for-each select="@*">
		<tr><td class="column_attr_caption"><xsl:value-of select="name()"/>:</td><td class="column_attr_value"><xsl:value-of select="."/></td></tr>
	</xsl:for-each>
	<xsl:for-each select="*[name() != 'referenced-by']">
		<tr>
			<td class="column_elem_caption">
			<xsl:value-of select="name()"/>:
			</td>
			<td class="column_elem_value">
			<xsl:value-of select="."/>
			</td>
		</tr>
	</xsl:for-each>
	<xsl:if test="referenced-by">
		<tr valign="top">
			<td class="column_elem_caption">Referenced by:</td>
			<td class="column_elem_value">
				<table>
					<xsl:for-each select="referenced-by">
						<tr>
							<td><xsl:value-of select="@table"/></td>
							<td><xsl:value-of select="@column"/></td>
							<td><xsl:value-of select="@type"/></td>
						</tr>
					</xsl:for-each>
				</table>
			<ol>
			</ol>
			</td>
		</tr>
	</xsl:if>
	</table>	
	</td></tr></table>
</xsl:template>

</xsl:stylesheet>
