<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="text"/>

<xsl:param name="package-name"/>
<xsl:param name="row-name"/>
<xsl:param name="rows-name"/>

<xsl:template match="table">
<xsl:variable name="_gen-table-class-name"><xsl:value-of select="@_gen-table-class-name"/></xsl:variable>
<xsl:variable name="_gen-table-method-name"><xsl:value-of select="@_gen-table-method-name"/></xsl:variable>
package <xsl:value-of select="$package-name"/>;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import com.xaf.db.*;
import com.xaf.db.schema.*;

public class <xsl:value-of select="$rows-name"/> extends AbstractRows
{
	private <xsl:value-of select="@_gen-table-class-name"/> table;
	
	public <xsl:value-of select="$rows-name"/>(<xsl:value-of select="@_gen-table-class-name"/> table)
	{
		super();
		this.table = table;
	}
	
	public void populateDataByIndexes(ResultSet resultSet) throws SQLException
	{
		super.populateDataByIndexes(resultSet);
		<xsl:value-of select="@_gen-row-class-name"/> row = null;
		while(resultSet.next())
		{
			row = table.create<xsl:value-of select="$row-name"/>();
			row.populateDataByIndexes(resultSet);
			add(row);
		}
	}

	public void populateDataByNames(ResultSet resultSet) throws SQLException
	{
		super.populateDataByNames(resultSet);
		Map colNameIndexMap = AbstractRow.getColumnNamesIndexMap(resultSet);
		<xsl:value-of select="@_gen-row-class-name"/> row = null;
		while(resultSet.next())
		{
			row = table.create<xsl:value-of select="$row-name"/>();
			row.populateDataByNames(resultSet, colNameIndexMap);
			add(row);
		}
	}
	
	public <xsl:value-of select="@_gen-domain-class-name"/> get<xsl:value-of select="@_gen-domain-method-name"/>(int rowNum)
	{
		return (<xsl:value-of select="@_gen-domain-class-name"/>) get(rowNum);
	}	
	
	public <xsl:value-of select="@_gen-row-class-name"/> get<xsl:value-of select="$row-name"/>(int rowNum)
	{
		return (<xsl:value-of select="@_gen-row-class-name"/>) get(rowNum);
	}	
}
</xsl:template>

</xsl:stylesheet>