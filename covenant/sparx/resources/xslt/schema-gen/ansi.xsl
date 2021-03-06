<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xalan="http://xml.apache.org/xalan"
                   exclude-result-prefixes="xalan">
<xsl:output method="text"/>

<xsl:param name="generate-drop-table"/>
<xsl:param name="generate-drop-seq"/>

<!-- these can be overriden in the child stylesheets (for specific databases) -->

<xsl:variable name="dbms-id">ansi</xsl:variable>
<xsl:variable name="generate-constraints">yes</xsl:variable>
<xsl:variable name="generate-seq">yes</xsl:variable>
<xsl:variable name="statement-terminator">;
</xsl:variable>
<xsl:variable name="system-date-function">sysdate</xsl:variable>
<xsl:variable name="system-boolean-false">0</xsl:variable>
<xsl:variable name="system-boolean-true">1</xsl:variable>

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
	<xsl:if test="$generate-constraints = 'yes'">
		<xsl:for-each select="table">
			<xsl:variable name="table" select="."/>
			<xsl:for-each select="column[@reftype]">
				<xsl:call-template name="fkey-ref">
					<xsl:with-param name="table" select="$table"/>
					<xsl:with-param name="column" select="."/>
				</xsl:call-template>
			</xsl:for-each>
		</xsl:for-each>
	</xsl:if>
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

<xsl:template name="table-definition">
	<xsl:param name="table"/>

<xsl:if test="$generate-drop-table">
drop table <xsl:value-of select="$table/@name"/><xsl:value-of select="$statement-terminator"/>
</xsl:if>
<xsl:if test="$generate-seq = 'yes'">
<xsl:for-each select="$table/column[@_gen-create-id = 'autoinc']">
	<xsl:call-template name="sequence-definition">
		<xsl:with-param name="table" select="$table"/>
		<xsl:with-param name="column" select="."/>
	</xsl:call-template>
</xsl:for-each>
</xsl:if>

<xsl:variable name="table-modifiers">
	<xsl:call-template name="table-modifiers">
		<xsl:with-param name="table" select="$table"/>
	</xsl:call-template>
</xsl:variable>

create<xsl:value-of select="$table-modifiers"/> table <xsl:value-of select="$table/@name"/>
(
<xsl:for-each select="$table/column">
	<xsl:call-template name="column-definition">
		<xsl:with-param name="table" select="$table"/>
		<xsl:with-param name="column" select="."/>
	</xsl:call-template>
</xsl:for-each>)<xsl:value-of select="$statement-terminator"/>
<xsl:for-each select="$table/index">
	<xsl:call-template name="index-definition">
		<xsl:with-param name="table" select="$table"/>
		<xsl:with-param name="index" select="."/>
	</xsl:call-template>
</xsl:for-each>
</xsl:template>

<xsl:template name="table-modifiers">
</xsl:template>

<xsl:template name="sequence-definition">
	<xsl:param name="table"/>
	<xsl:param name="column"/>

	<xsl:if test="$generate-drop-seq">
		<xsl:text>drop sequence </xsl:text>
		<xsl:value-of select="$column/@_gen-sequence-name"/>
		<xsl:value-of select="$statement-terminator"/>
	</xsl:if>

	<xsl:text>create sequence </xsl:text>
	<xsl:value-of select="$column/@_gen-sequence-name"/>
	<xsl:text> increment by 1 start with 1 nomaxvalue nocache nocycle</xsl:text>
	<xsl:value-of select="$statement-terminator"/>
</xsl:template>

<xsl:template name="column-definition">
	<xsl:param name="table"/>
	<xsl:param name="column"/>

	<xsl:value-of select="'	'"/>
	<xsl:value-of select="$column/@name"/>
	<xsl:call-template name="column-sql-defn">
		<xsl:with-param name="table" select="$table"/>
		<xsl:with-param name="column" select="$column"/>
	</xsl:call-template>
	<xsl:call-template name="column-sql-modifiers">
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
<xsl:value-of select="'
'"/>
</xsl:template>

<xsl:template name="column-sql-defn">
	<xsl:param name="table"/>
	<xsl:param name="column"/>

	<xsl:value-of select="' '"/>
	<xsl:choose>
		<xsl:when test="$column/sqldefn[@dbms = $dbms-id]">
			<xsl:value-of select="$column/sqldefn[@dbms = $dbms-id]"/>
		</xsl:when>
		<xsl:when test="$column/sqldefn[@dbms = 'ansi']">
			<xsl:value-of select="$column/sqldefn[@dbms = 'ansi']"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$column/sqldefn"/>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template name="column-sql-modifiers">
	<xsl:param name="table"/>
	<xsl:param name="column"/>

	<xsl:if test="$generate-constraints != 'yes'">
		<xsl:if test="@primarykey='yes'">
			<xsl:text> PRIMARY KEY</xsl:text>
		</xsl:if>
		<xsl:if test="@required='yes'">
			<xsl:text> NOT NULL</xsl:text>
		</xsl:if>
	</xsl:if>
</xsl:template>

<xsl:template name="column-default">
	<xsl:param name="table"/>
	<xsl:param name="column"/>

	<xsl:text> DEFAULT </xsl:text>
	<xsl:choose>
		<xsl:when test="($column/@default = 'sysdate') and ($system-date-function)">
			<xsl:value-of select="$system-date-function"/>
		</xsl:when>
		<!-- Added by Shahbaz Javeed for PostgreSQL's boolean defaults -->
		<xsl:when test="($column/@default = '0') and ($column/@type = 'boolean')">
			<xsl:value-of select="$system-boolean-false"/>
		</xsl:when>
		<xsl:when test="($column/@default = '1') and ($column/@type = 'boolean')">
			<xsl:value-of select="$system-boolean-true"/>
		</xsl:when>
		<!-- End of Addition -->
		<xsl:otherwise>
			<xsl:value-of select="$column/@default"/>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template name="index-definition">
	<xsl:param name="table"/>
	<xsl:param name="index"/>

	<xsl:text>create </xsl:text>
	<xsl:if test="$index/@type"><xsl:value-of select="$index/@type"/><xsl:value-of select="' '"/></xsl:if>
	<xsl:text>index </xsl:text>
	<xsl:value-of select="$table/@abbrev"/>
	<xsl:text>_</xsl:text>
	<xsl:value-of select="$index/@name"/>
	<xsl:text> on </xsl:text>
	<xsl:value-of select="$table/@name"/>
	<xsl:text>(</xsl:text>
	<xsl:value-of select="$index/@columns"/>
	<!-- line break -->
	<xsl:text>)</xsl:text>
	<xsl:value-of select="$statement-terminator"/>
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
	<xsl:text>))</xsl:text>
	<xsl:value-of select="$statement-terminator"/>
</xsl:template>

<xsl:template name="required">
	<xsl:param name="table"/>
	<xsl:param name="column"/>

	<xsl:text>alter table </xsl:text>
	<xsl:value-of select="$table/@name"/>
	<xsl:text> modify (</xsl:text>
	<xsl:value-of select="$column/@name"/>
	<xsl:text> constraint </xsl:text>
	<xsl:value-of select="$table/@abbrev"/>
	<xsl:text>_</xsl:text>
	<xsl:value-of select="$column/@name"/>
	<xsl:text>_REQ NOT NULL</xsl:text>
	<!-- line break -->
	<xsl:text>)</xsl:text>
	<xsl:value-of select="$statement-terminator"/>
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
	<xsl:text>))</xsl:text>
	<xsl:value-of select="$statement-terminator"/>
</xsl:template>

<xsl:template name="get-data-elems">
    <xsl:param name="data-elem"/>
    <xsl:param name="table"/>
    <xsl:for-each select="$data-elem/@*">
        <xsl:variable name="attr-name"><xsl:value-of select="name()"/></xsl:variable>
        <!-- if the attribute matches one of our column names, we want it -->
        <xsl:if test="$table/column[@name = $attr-name]">
            <xsl:element name="{$attr-name}"><xsl:value-of select="."/></xsl:element>
        </xsl:if>
    </xsl:for-each>

    <xsl:choose>
        <!-- when child elements exist, there should be no text node of the  enum element.
             This means the caption  should be explicitly defined either as an attribute
             or a child element.
         -->
        <xsl:when test="$data-elem/*">
            <xsl:for-each select="$data-elem/*">
                <xsl:variable name="child-name"><xsl:value-of select="name()"/></xsl:variable>
                <!-- if the attribute matches one of our column names, we want it -->
                <xsl:if test="$table/column[@name = $child-name]">
                    <xsl:copy-of select="."/>
                </xsl:if>
            </xsl:for-each>
        </xsl:when>
        <xsl:otherwise>
            <!-- Expecting a HARD CODED column called 'caption' to exist -->
            <xsl:variable name="value" select="$data-elem/text()"/>
            <xsl:if test="$value != ''">
                <xsl:element name="caption"><xsl:value-of select="$value"/></xsl:element>
            </xsl:if>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>

<xsl:template name="static-data">
	<xsl:param name="table"/>
    <xsl:param name="static-data"/>
	<xsl:for-each select="$static-data">
        <xsl:variable name="column-data-elems">
            <xsl:call-template name="get-data-elems">
                <xsl:with-param name="data-elem" select="."/>
                <xsl:with-param name="table" select="$table"/>
            </xsl:call-template>
        </xsl:variable>

		<xsl:text>insert into </xsl:text>
		<xsl:value-of select="$table/@name"/>
		<xsl:text>(</xsl:text>
		<xsl:for-each select="xalan:nodeset($column-data-elems)/*">
				<xsl:value-of select="name()"/><xsl:if test="position() != last()">, </xsl:if>
		</xsl:for-each>
		<xsl:text>) values (</xsl:text>
		<xsl:for-each select="xalan:nodeset($column-data-elems)/*">
            <xsl:variable name="elem-name"><xsl:value-of select="name()"/></xsl:variable>

            <xsl:choose>
            <xsl:when test="$table/column[@name = $elem-name]/@type = 'text'">
                <xsl:text>'</xsl:text><xsl:value-of select="."/><xsl:text>'</xsl:text><xsl:if test="position() != last()">, </xsl:if>
            </xsl:when>
            <xsl:otherwise>
                    <xsl:value-of select="."/><xsl:if test="position() != last()">, </xsl:if>
            </xsl:otherwise>
            </xsl:choose>
		</xsl:for-each>
		<xsl:text>)</xsl:text>
	<!-- line break -->
	<xsl:value-of select="$statement-terminator"/>
	</xsl:for-each>
</xsl:template>


</xsl:stylesheet>
