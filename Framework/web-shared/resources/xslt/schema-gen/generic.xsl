<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="text"/>

<xsl:param name="generate"/>
<xsl:param name="table-name"/>
<xsl:param name="generate-drop-table"/>
<xsl:param name="generate-drop-seq"/>
<xsl:param name="generate-seq">yes</xsl:param>

<xsl:param name="app.database-root-path"/>

<xsl:template match="schema">
	<xsl:for-each select="table">
		<xsl:variable name="table" select="."/>
		<xsl:if test="column">
			<xsl:call-template name="table-definition">
				<xsl:with-param name="table" select="."/>
			</xsl:call-template>
		</xsl:if>
		<!-- This only supports primary keys with single columns -->
		<xsl:for-each select="column[not(@default) and (@required='yes' or @primarykey='yes')]">
			<xsl:call-template name="required">
				<xsl:with-param name="table" select="$table"/>
				<xsl:with-param name="column" select="."/>
			</xsl:call-template>
		</xsl:for-each>
		<!-- This only supports primary keys with single columns -->
		<xsl:for-each select="column[@primarykey='yes']">
			<xsl:call-template name="pkey-ref">
				<xsl:with-param name="table" select="$table"/>
				<xsl:with-param name="column" select="."/>
			</xsl:call-template>
		</xsl:for-each>
	</xsl:for-each>
	<xsl:for-each select="table">
		<xsl:variable name="table" select="."/>
		<xsl:for-each select="column[@reftype]">
			<xsl:call-template name="fkey-ref">
				<xsl:with-param name="table" select="$table"/>
				<xsl:with-param name="column" select="."/>
			</xsl:call-template>
		</xsl:for-each>
	</xsl:for-each>
	<xsl:for-each select="table[enum]">
		<xsl:call-template name="enum-data">
			<xsl:with-param name="table" select="."/>
		</xsl:call-template>
	</xsl:for-each>
</xsl:template>

<xsl:template name="table-definition">
	<xsl:param name="table"/>

<xsl:if test="$generate-drop-table">
drop table <xsl:value-of select="$table/@name"/>;
</xsl:if>
<xsl:if test="$generate-seq">
<xsl:for-each select="$table/column[@type = 'autoinc']">
	<xsl:call-template name="sequence-definition">
		<xsl:with-param name="table" select="$table"/>
		<xsl:with-param name="column" select="."/>
	</xsl:call-template>
</xsl:for-each>
</xsl:if>
create table <xsl:value-of select="$table/@name"/>
(
<xsl:for-each select="$table/column">
	<xsl:call-template name="column-definition">
		<xsl:with-param name="table" select="$table"/>
		<xsl:with-param name="column" select="."/>
	</xsl:call-template>
</xsl:for-each>);
<xsl:for-each select="$table/index">
	<xsl:call-template name="index-definition">
		<xsl:with-param name="table" select="$table"/>
		<xsl:with-param name="index" select="."/>
	</xsl:call-template>
</xsl:for-each>
</xsl:template>

<xsl:template name="sequence-definition">
	<xsl:param name="table"/>
	<xsl:param name="column"/>

	<xsl:if test="$generate-drop-seq">
	<xsl:text>drop sequence </xsl:text>
	<xsl:value-of select="$table/@abbrev"/>
	<xsl:text>_</xsl:text>
	<xsl:value-of select="$column/@name"/>
	<xsl:text>_SEQ;
</xsl:text>
	</xsl:if>

	<xsl:text>create sequence </xsl:text>
	<xsl:value-of select="$table/@abbrev"/>
	<xsl:text>_</xsl:text>
	<xsl:value-of select="$column/@name"/>
	<xsl:text>_SEQ increment by 1 start with 1 nomaxvalue nocache nocycle;
</xsl:text>
</xsl:template>

<xsl:template name="column-definition">
	<xsl:param name="table"/>
	<xsl:param name="column"/>

	<xsl:text>	</xsl:text>
	<xsl:value-of select="$column/@name"/>
	<xsl:call-template name="column-sql-defn">
		<xsl:with-param name="table" select="$table"/>
		<xsl:with-param name="column" select="$column"/>
	</xsl:call-template>
	<xsl:if test="$column/@default">
		<xsl:call-template name="column-default">
			<xsl:with-param name="table" select="$table"/>
			<xsl:with-param name="column" select="$column"/>
		</xsl:call-template>
	</xsl:if>
	<xsl:if test="not($column/@is-last)">,</xsl:if>
<!-- line break -->
<xsl:text>
</xsl:text>
</xsl:template>

<xsl:template name="column-sql-defn">
	<xsl:param name="table"/>
	<xsl:param name="column"/>

	<xsl:text> </xsl:text>
	<xsl:value-of select="$column/sqldefn"/>
</xsl:template>

<xsl:template name="column-default">
	<xsl:param name="table"/>
	<xsl:param name="column"/>

	<xsl:text> DEFAULT </xsl:text>
	<xsl:value-of select="$column/@default"/>
</xsl:template>

<xsl:template name="index-definition">
	<xsl:param name="table"/>
	<xsl:param name="index"/>

	<xsl:text>create </xsl:text>
	<xsl:if test="$index/@type"><xsl:value-of select="$index/@type"/><xsl:text> </xsl:text></xsl:if>
	<xsl:text>index </xsl:text>
	<xsl:value-of select="$table/@abbrev"/>
	<xsl:text>_</xsl:text>
	<xsl:value-of select="$index/@name"/>
	<xsl:text> on </xsl:text>
	<xsl:value-of select="$table/@name"/>
	<xsl:text>(</xsl:text>
	<xsl:value-of select="$index/@columns"/>
	<!-- line break -->
	<xsl:text>);
</xsl:text>
</xsl:template>

<xsl:template name="pkey-ref">
	<xsl:param name="table"/>
	<xsl:param name="column"/>

	<xsl:text>alter table </xsl:text>
	<xsl:value-of select="$table/@name"/>
	<xsl:text> add (constraint </xsl:text>
	<xsl:value-of select="$table/@name"/>
	<xsl:text>_PK PRIMARY KEY (</xsl:text>
	<xsl:value-of select="$column/@name"/>
	<!-- line break -->
	<xsl:text>));
</xsl:text>
</xsl:template>

<xsl:template name="required">
	<xsl:param name="table"/>
	<xsl:param name="column"/>

	<xsl:text>alter table </xsl:text>
	<xsl:value-of select="$table/@name"/>
	<xsl:text> modify (</xsl:text>
	<xsl:value-of select="$column/@name"/>
	<xsl:text> constraint (</xsl:text>
	<xsl:value-of select="$table/@abbrev"/>
	<xsl:text>_</xsl:text>
	<xsl:value-of select="$column/@name"/>
	<xsl:text>_REQ NOT NULL </xsl:text>
	<!-- line break -->
	<xsl:text>);
</xsl:text>
</xsl:template>

<xsl:template name="fkey-ref">
	<xsl:param name="table"/>
	<xsl:param name="column"/>

	<xsl:text>alter table </xsl:text>
	<xsl:value-of select="$table/@name"/>
	<xsl:text> add (constraint </xsl:text>
	<xsl:value-of select="$table/@abbrev"/>
	<xsl:text>_</xsl:text>
	<xsl:value-of select="$column/@name"/>
	<xsl:text>_FK FOREIGN KEY (</xsl:text>
	<xsl:value-of select="$column/@name"/>
	<xsl:text>) REFERENCES </xsl:text>
	<xsl:value-of select="$column/@reftbl"/>
	<xsl:text>(</xsl:text>
	<xsl:value-of select="$column/@refcol"/>
	<!-- line break -->
	<xsl:text>));
</xsl:text>
</xsl:template>


<xsl:template name="enum-data">
	<xsl:param name="table"/>

	<xsl:for-each select="$table/enum">
		<xsl:text>insert into </xsl:text>
		<xsl:value-of select="$table/@name"/>
		<xsl:if test="@abbrev">
			<xsl:text>(id, abbrev, caption) values (</xsl:text>
			<xsl:value-of select="@id"/><xsl:text>, '</xsl:text>
			<xsl:value-of select="@abbrev"/><xsl:text>', '</xsl:text>
			<xsl:value-of select="."/><xsl:text>'</xsl:text>
		</xsl:if>
		<xsl:if test="not(@abbrev)">
			<xsl:text>(id, caption) values (</xsl:text>
			<xsl:value-of select="@id"/><xsl:text>, '</xsl:text>
			<xsl:value-of select="."/><xsl:text>'</xsl:text>
		</xsl:if>
	<!-- line break -->
	<xsl:text>);
</xsl:text>
	</xsl:for-each>
</xsl:template>


</xsl:stylesheet>
