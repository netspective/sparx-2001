<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:import href="ansi.xsl"/>

<xsl:variable name="dbms-id">hsqldb</xsl:variable>
<xsl:variable name="generate-constraints">no</xsl:variable>
<xsl:variable name="generate-seq">no</xsl:variable>
<!--<xsl:variable name="generate-drop-table">yes</xsl:variable>-->
<xsl:variable name="system-date-function">curdate()</xsl:variable>


<xsl:template match="schema">
	<xsl:for-each select="table">
		<xsl:variable name="table" select="."/>
		<xsl:if test="column">
			<xsl:call-template name="table-definition">
				<xsl:with-param name="table" select="."/>
			</xsl:call-template>
		</xsl:if>

		<xsl:if test="$generate-constraints = 'yes'">
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
		</xsl:if>
	</xsl:for-each>

<!--
    ** HSqlDB requires the foreign key to be created in the column sytax of the CREATE TABLE
    ** I'm leaving this out for now because I'm not sure how to implement that
    <xsl:for-each select="table">
		<xsl:variable name="table" select="."/>
		<xsl:for-each select="column[@reftype]">
			<xsl:call-template name="fkey-ref">
				<xsl:with-param name="table" select="$table"/>
				<xsl:with-param name="column" select="."/>
			</xsl:call-template>
		</xsl:for-each>
	</xsl:for-each>
-->
	<xsl:for-each select="table[enum]">
		<xsl:call-template name="static-data">
			<xsl:with-param name="table" select="."/>
            <xsl:with-param name="static-data" select="enum"/>
		</xsl:call-template>
	</xsl:for-each>
	<xsl:for-each select="table[data]">
		<xsl:call-template name="static-data">
			<xsl:with-param name="table" select="."/>
            <xsl:with-param name="static-data" select="data"/>
		</xsl:call-template>
	</xsl:for-each>
</xsl:template>

<!-- Minor syntax differences between ANSI/Oracle and here -->
<xsl:template name="fkey-ref">
	<xsl:param name="table"/>
	<xsl:param name="column"/>
	<xsl:text>alter table </xsl:text>
	<xsl:value-of select="$table/@name"/>
	<xsl:text> add constraint </xsl:text>
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
	<xsl:text>)</xsl:text>
	<xsl:value-of select="$statement-terminator"/>
</xsl:template>

<!-- HSqlDB does not support default values in columns -->
<xsl:template name="column-default">
	<xsl:param name="table"/>
	<xsl:param name="column"/>

	<xsl:text> </xsl:text>
</xsl:template>
<xsl:template name="column-sql-modifiers">
	<xsl:param name="table"/>
	<xsl:param name="column"/>

	<xsl:if test="$generate-constraints != 'yes'">
		<xsl:if test="@primarykey='yes' and @type='autoinc'">
			<xsl:text> IDENTITY PRIMARY KEY</xsl:text>
		</xsl:if>
		<xsl:if test="@primarykey='yes' and @type='guid32'">
			<xsl:text>  PRIMARY KEY</xsl:text>
		</xsl:if>
		<xsl:if test="@required='yes'">
			<xsl:text> NOT NULL</xsl:text>
		</xsl:if>
	</xsl:if>
</xsl:template>
</xsl:stylesheet>