<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="text"/>

<xsl:param name="package-name"/>
<xsl:param name="table-name"/>
<xsl:variable name="default-text-size">32</xsl:variable>

<xsl:template match="table">
<xsl:variable name="_gen-table-method-name"><xsl:value-of select="@_gen-table-method-name"/></xsl:variable>
<xsl:variable name="_gen-table-row-class-name"><xsl:value-of select="@_gen-row-class-name"/></xsl:variable>
<xsl:variable name="table-simple-name"><xsl:value-of select="@name"/></xsl:variable>
<xsl:variable name="table-abbrev"><xsl:value-of select="@abbrev"/></xsl:variable>
package <xsl:value-of select="$package-name"/>;

import java.io.*;
import java.util.*;
import java.sql.SQLException;
import java.sql.ResultSet;
import javax.naming.NamingException;

import com.xaf.db.*;
import com.xaf.db.schema.*;
import com.xaf.value.*;

public class <xsl:value-of select="$table-name"/> extends AbstractTable
{
<xsl:for-each select="column">	protected <xsl:value-of select="@_gen-data-type-class"/><xsl:text> </xsl:text><xsl:value-of select="@_gen-member-name"/>;
</xsl:for-each>
	public <xsl:value-of select="$table-name"/>(Schema schema)
	{
		super(schema, &quot;<xsl:value-of select="@name"/>&quot;);
		initializeDefn();
	}

	public void initializeDefn()
	{
<xsl:for-each select="column">	
	<xsl:variable name="member-name"><xsl:value-of select="@_gen-member-name"/></xsl:variable>
<xsl:text>		</xsl:text><xsl:value-of select="$member-name"/> = new <xsl:value-of select="@_gen-data-type-class"/>(this, &quot;<xsl:value-of select="@name"/>&quot;);
<xsl:if test="@type = 'autoinc' and @primarykey = 'yes'"><xsl:text>		</xsl:text><xsl:value-of select="$member-name"/>.setIsSequencedPrimaryKey(true);<xsl:text>
		</xsl:text><xsl:value-of select="$member-name"/>.setSequenceName(&quot;<xsl:value-of select="$table-abbrev"/>_<xsl:value-of select="@name"/>_SEQ&quot;);
</xsl:if>
<xsl:if test="@type != 'autoinc' and @primarykey = 'yes'"><xsl:text>		</xsl:text><xsl:value-of select="$member-name"/>.setIsNaturalPrimaryKey(true);
</xsl:if>
<xsl:if test="@required = 'yes'"><xsl:text>		</xsl:text><xsl:value-of select="$member-name"/>.setIsRequired(true);
</xsl:if>
<xsl:choose>
	<xsl:when test="@default-java">
		<xsl:text>		</xsl:text><xsl:value-of select="$member-name"/>.setDefaultValue(<xsl:value-of select="@default-java"/>);
	</xsl:when>
	<xsl:when test="default[@type = 'java']">
		<xsl:text>		</xsl:text><xsl:value-of select="$member-name"/>.setDefaultValue(<xsl:value-of select="default"/>);
	</xsl:when>
</xsl:choose>
<xsl:choose>	
	<xsl:when test="@default">
		<xsl:text>		</xsl:text><xsl:value-of select="$member-name"/>.setDefaultSqlExprValue(&quot;<xsl:value-of select="@default"/>&quot;);
	</xsl:when>
	<xsl:when test="default[not(@type)]">
		<xsl:text>		</xsl:text><xsl:value-of select="$member-name"/>.setDefaultSqlExprValue(&quot;<xsl:value-of select="default"/>&quot;);
	</xsl:when>
</xsl:choose>
<xsl:if test="size and size != $default-text-size"><xsl:text>		</xsl:text><xsl:value-of select="$member-name"/>.setSize(<xsl:value-of select="size"/>);
</xsl:if>
<xsl:if test="@size and @size != $default-text-size"><xsl:text>		</xsl:text><xsl:value-of select="$member-name"/>.setSize(<xsl:value-of select="@size"/>);
</xsl:if>
<xsl:if test="selfref"><xsl:text>		</xsl:text><xsl:value-of select="$member-name"/>.setForeignKeyRef(Column.FKEYREF_SELF, &quot;<xsl:value-of select="lookupref"/>&quot;);
</xsl:if>
<xsl:if test="parentref"><xsl:text>		</xsl:text><xsl:value-of select="$member-name"/>.setForeignKeyRef(Column.FKEYREF_PARENT, &quot;<xsl:value-of select="lookupref"/>&quot;);
</xsl:if>
<xsl:if test="lookupref"><xsl:text>		</xsl:text><xsl:value-of select="$member-name"/>.setForeignKeyRef(Column.FKEYREF_LOOKUP, &quot;<xsl:value-of select="lookupref"/>&quot;);
</xsl:if>
</xsl:for-each>	
		setAllColumns(new Column[] {
<xsl:for-each select="column"><xsl:text>			</xsl:text><xsl:value-of select="@_gen-member-name"/><xsl:if test="position() != last()"><xsl:text>, 
</xsl:text></xsl:if>
</xsl:for-each>
		});
	}

	public Row createRow()
	{
		return new <xsl:value-of select="$_gen-table-row-class-name"/>(this);
	}
	
	public <xsl:value-of select="$_gen-table-row-class-name"/> create<xsl:value-of select="$table-simple-name"/>Row()
	{
		return new <xsl:value-of select="$_gen-table-row-class-name"/>(this);
	}
	
<xsl:for-each select="column">	public <xsl:value-of select="@_gen-data-type-class"/><xsl:text> </xsl:text>get<xsl:value-of select="@_gen-method-name"/>Column() { return <xsl:value-of select="@_gen-member-name"/>; }
</xsl:for-each>

<xsl:for-each select="column">	
<xsl:variable name="member-name"><xsl:value-of select="@_gen-member-name"/></xsl:variable>
<xsl:variable name="java-type-init-cap"><xsl:value-of select="@_gen-java-type-init-cap"/></xsl:variable>
<xsl:variable name="java-class-spec"><xsl:value-of select="java-class/@package"/>.<xsl:value-of select="java-class"/></xsl:variable>
<xsl:if test="java-type and @primarykey = 'yes'">
	public <xsl:value-of select="$_gen-table-row-class-name"/> get<xsl:value-of select="$_gen-table-method-name"/>By<xsl:value-of select="@_gen-method-name"/>(ConnectionContext cc, <xsl:value-of select="java-type"/> value) throws NamingException, SQLException { return (<xsl:value-of select="$_gen-table-row-class-name"/>) get<xsl:value-of select="$_gen-table-method-name"/>By<xsl:value-of select="@_gen-method-name"/>(cc, new <xsl:value-of select="$java-class-spec"/>(value)); }
</xsl:if><xsl:if test="@primarykey = 'yes'">
	public <xsl:value-of select="$_gen-table-row-class-name"/> get<xsl:value-of select="$_gen-table-method-name"/>By<xsl:value-of select="@_gen-method-name"/>(ConnectionContext cc, <xsl:value-of select="$java-class-spec"/> value) throws NamingException, SQLException { return (<xsl:value-of select="$_gen-table-row-class-name"/>) getRecordByPrimaryKey(cc, value, null); }
</xsl:if>
</xsl:for-each>
}
</xsl:template>

</xsl:stylesheet>






