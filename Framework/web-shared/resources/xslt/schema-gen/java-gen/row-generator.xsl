<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="text"/>

<xsl:param name="entire-schema"/>
<xsl:param name="package-name"/>
<xsl:param name="row-name"/>
<xsl:variable name="default-text-size">32</xsl:variable>

<xsl:template match="table">
<xsl:variable name="_gen-table-class-name"><xsl:value-of select="@_gen-table-class-name"/></xsl:variable>
<xsl:variable name="_gen-table-method-name"><xsl:value-of select="@_gen-table-method-name"/></xsl:variable>
package <xsl:value-of select="$package-name"/>;

import java.io.*;
import java.util.*;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Connection;
import javax.naming.NamingException;

import com.xaf.db.*;
import com.xaf.db.schema.*;
import com.xaf.form.*;
import com.xaf.sql.*;
import com.xaf.value.*;

public class <xsl:value-of select="$row-name"/> extends AbstractRow implements <xsl:value-of select="@_gen-domain-class-name"/>
{
	/* COLNAME_XX (column_name_xx) is the name of the XX column in the database */
<xsl:for-each select="column">	public static final String COLNAME_<xsl:value-of select="@_gen-constant-name"/> = &quot;<xsl:value-of select="@name"/>&quot;;
</xsl:for-each>

	/* COLRSI_XX (column result set index) is the index of the XX column in a 
	   ResultSet when all columns are selected from a table (1-based) */
<xsl:for-each select="column">	public static final int COLRSI_<xsl:value-of select="@_gen-constant-name"/> = <xsl:value-of select="position()"/>;
</xsl:for-each>

	/* COLAI_XX (column array index) is the index of the XX column in a Java 
	   array when all columns are selected from a table (0-based) */
<xsl:for-each select="column">	public static final int COLAI_<xsl:value-of select="@_gen-constant-name"/> = <xsl:value-of select="position()-1"/>;
</xsl:for-each>

	/* Member variables that manage the actual data */
<xsl:for-each select="column">	protected <xsl:value-of select="java-class/@package"/>.<xsl:value-of select="java-class"/><xsl:text> </xsl:text><xsl:value-of select="@_gen-member-name"/>;
</xsl:for-each>
	
	public <xsl:value-of select="$row-name"/>(<xsl:value-of select="@_gen-table-class-name"/> table)
	{
		super(table);
<xsl:for-each select="column">
<xsl:variable name="member-name"><xsl:value-of select="@_gen-member-name"/></xsl:variable>
<xsl:variable name="constant-name"><xsl:value-of select="@_gen-constant-name"/></xsl:variable>
<xsl:if test="@default-java or default[@type = 'java']">
	<xsl:text>		</xsl:text><xsl:value-of select="$member-name"/> = table.get<xsl:value-of select="@_gen-method-name"/>Column().getDefaultValue();
</xsl:if>
<xsl:if test="@default or default">
	<xsl:text>		</xsl:text>setCustomSqlExpr(COLAI_<xsl:value-of select="$constant-name"/>, table.get<xsl:value-of select="@_gen-method-name"/>Column().getDefaultSqlExprValue());
</xsl:if>
</xsl:for-each>
	}

<xsl:if test="column[@primarykey='yes']">
	public Object getActivePrimaryKeyValue()
	{
		return get<xsl:value-of select="column[@primarykey='yes']/@_gen-method-name"/>();
	}
</xsl:if>

	public Object[] getData()
	{
		return new Object[] {
<xsl:for-each select="column"><xsl:text>			</xsl:text><xsl:value-of select="@_gen-member-name"/><xsl:if test="position() != last()"><xsl:text>, 
</xsl:text></xsl:if>
</xsl:for-each>		
		};
	}

	public boolean equals(Object row)
	{
		if(this == row) return true;
		if(row == null) return false;
		if(! (row instanceof <xsl:value-of select="$row-name"/>)) return false;
		
		<xsl:value-of select="$row-name"/> compareRow = (<xsl:value-of select="$row-name"/>) row;		
<xsl:for-each select="column">		if(! valuesAreEqual(get<xsl:value-of select="@_gen-method-name"/>(), compareRow.get<xsl:value-of select="@_gen-method-name"/>())) return false;
</xsl:for-each>
		return true;
	}

	public List getDataForDmlStatement()
	{
		List data = new ArrayList();
<xsl:for-each select="column">		data.add(haveSqlExprData[COLAI_<xsl:value-of select="@_gen-constant-name"/>] ? ((Object) new DmlStatement.CustomSql(sqlExprData[COLAI_<xsl:value-of select="@_gen-constant-name"/>])) : <xsl:value-of select="@_gen-member-name"/>);
</xsl:for-each>
		return data;
	}
	
	public void populateDataByIndexes(ResultSet rs) throws SQLException
	{
	<xsl:for-each select="column">
<xsl:variable name="member-name"><xsl:value-of select="@_gen-member-name"/></xsl:variable>
<xsl:variable name="result-set-index">COLRSI_<xsl:value-of select="@_gen-constant-name"/></xsl:variable>
<xsl:variable name="java-type-init-cap"><xsl:value-of select="@_gen-java-type-init-cap"/></xsl:variable>
<xsl:variable name="java-class-spec"><xsl:value-of select="java-class/@package"/>.<xsl:value-of select="java-class"/></xsl:variable>
<xsl:choose>
	<xsl:when test="java-type">
<xsl:text>	</xsl:text><xsl:value-of select="$java-class-spec"/><xsl:text> </xsl:text><xsl:value-of select="@_gen-member-name"/>Value = new <xsl:value-of select="$java-class-spec"/>(rs.get<xsl:value-of select="$java-type-init-cap"/>(<xsl:value-of select="$result-set-index"/>));
<xsl:text>		</xsl:text>set<xsl:value-of select="@_gen-method-name"/>(rs.wasNull() ? null : <xsl:value-of select="@_gen-member-name"/>Value);
	</xsl:when>
	<xsl:when test="$java-class-spec = 'java.lang.String' ">
<xsl:text>	</xsl:text>set<xsl:value-of select="@_gen-method-name"/>(rs.getString(<xsl:value-of select="$result-set-index"/>));
	</xsl:when>
	<xsl:otherwise>
<xsl:text>	</xsl:text>set<xsl:value-of select="@_gen-method-name"/>((<xsl:value-of select="$java-class-spec"/>) rs.getObject(<xsl:value-of select="$result-set-index"/>));
	</xsl:otherwise>
</xsl:choose>
</xsl:for-each>
	}

	public void populateDataByNames(ResultSet rs, Map colNameIndexMap) throws SQLException
	{
		if(colNameIndexMap == null) colNameIndexMap = getColumnNamesIndexMap(rs);
		Integer colIndex = null;
<xsl:for-each select="column">
<xsl:variable name="member-name"><xsl:value-of select="@_gen-member-name"/></xsl:variable>
<xsl:variable name="constant-name">COLNAME_<xsl:value-of select="@_gen-constant-name"/></xsl:variable>
<xsl:variable name="java-type-init-cap"><xsl:value-of select="@_gen-java-type-init-cap"/></xsl:variable>
<xsl:variable name="java-class-spec"><xsl:value-of select="java-class/@package"/>.<xsl:value-of select="java-class"/></xsl:variable>
		colIndex = (Integer) colNameIndexMap.get(<xsl:value-of select="$constant-name"/>);
<xsl:text>		</xsl:text>if(colIndex != null) {<xsl:text>		
		</xsl:text>
<xsl:choose>
	<xsl:when test="java-type">
<xsl:text>	</xsl:text><xsl:value-of select="$java-class-spec"/><xsl:text> </xsl:text><xsl:value-of select="@_gen-member-name"/>Value = new <xsl:value-of select="$java-class-spec"/>(rs.get<xsl:value-of select="$java-type-init-cap"/>(colIndex.intValue()));
<xsl:text>			</xsl:text>set<xsl:value-of select="@_gen-method-name"/>(rs.wasNull() ? null : <xsl:value-of select="@_gen-member-name"/>Value);
	</xsl:when>
	<xsl:when test="$java-class-spec = 'java.lang.String' ">
<xsl:text>	</xsl:text>set<xsl:value-of select="@_gen-method-name"/>(rs.getString(colIndex.intValue()));
	</xsl:when>
	<xsl:otherwise>
<xsl:text>	</xsl:text>set<xsl:value-of select="@_gen-method-name"/>((<xsl:value-of select="$java-class-spec"/>) rs.getObject(colIndex.intValue()));
	</xsl:otherwise>
</xsl:choose>	}
</xsl:for-each>
	}
	
	public void populateDataByNames(DialogContext dc)
	{
		Map fieldStates = dc.getFieldStates();
		DialogContext.DialogFieldState state = null;
<xsl:for-each select="column">		state = (DialogContext.DialogFieldState) fieldStates.get(COLNAME_<xsl:value-of select="@_gen-constant-name"/>);
<xsl:text>		</xsl:text>if(state != null) set<xsl:value-of select="@_gen-method-name"/>((<xsl:value-of select="java-class/@package"/>.<xsl:value-of select="java-class"/>) state.getValueAsObject());
</xsl:for-each>
	}

	public void populateDataByNames(DialogContext dc, Map colNameFieldNameMap)
	{
		Map fieldStates = dc.getFieldStates();
		String fieldName = null;
		DialogContext.DialogFieldState state = null;
<xsl:for-each select="column">		fieldName = (String) colNameFieldNameMap.get(COLNAME_<xsl:value-of select="@_gen-constant-name"/>);
		state = (DialogContext.DialogFieldState) fieldStates.get(fieldName != null ? fieldName : COLNAME_<xsl:value-of select="@_gen-constant-name"/>);
<xsl:text>		</xsl:text>if(state != null) set<xsl:value-of select="@_gen-method-name"/>((<xsl:value-of select="java-class/@package"/>.<xsl:value-of select="java-class"/>) state.getValueAsObject());
</xsl:for-each>
	}

	public void setData(DialogContext dc)
	{
		Object colValue = null;
<xsl:for-each select="column">		colValue = get<xsl:value-of select="@_gen-method-name"/>();
		dc.setValue(COLNAME_<xsl:value-of select="@_gen-constant-name"/>, colValue != null ? colValue.toString() : null);
</xsl:for-each>
	}

	public void setData(DialogContext dc, Map colNameFieldNameMap)
	{
		Object colValue = null;
		String fieldName = null;
<xsl:for-each select="column">		fieldName = (String) colNameFieldNameMap.get(COLNAME_<xsl:value-of select="@_gen-constant-name"/>);
		colValue = get<xsl:value-of select="@_gen-method-name"/>();
		dc.setValue(fieldName != null ? fieldName : COLNAME_<xsl:value-of select="@_gen-constant-name"/>, colValue != null ? colValue.toString() : null);
</xsl:for-each>
	}
	
<xsl:if test="column[@type = 'autoinc']">
	public boolean beforeInsert(ConnectionContext cc, DmlStatement dml) throws NamingException, SQLException
	{
		if(! super.beforeInsert(cc, dml))
			return false;
			
		<xsl:value-of select="$_gen-table-class-name"/> table = (<xsl:value-of select="$_gen-table-class-name"/>) getTable();
		DatabasePolicy databasePolicy = cc.getDatabasePolicy();
		Object value;
<xsl:for-each select="column[@type = 'autoinc']">
<xsl:variable name="java-class-spec"><xsl:value-of select="java-class/@package"/>.<xsl:value-of select="java-class"/></xsl:variable>
<xsl:text>		</xsl:text>Column <xsl:value-of select="@_gen-member-name"/>Col = table.get<xsl:value-of select="@_gen-method-name"/>Column();
<xsl:text>		</xsl:text>value = databasePolicy.handleAutoIncPreDmlExecute(cc.getConnection(), <xsl:value-of select="@_gen-member-name"/>Col.getSequenceName(), <xsl:value-of select="@_gen-member-name"/>Col.getName());
<xsl:text>		</xsl:text>dml.updateValue(COLAI_<xsl:value-of select="@_gen-constant-name"/>, value);
<xsl:text>		</xsl:text>set<xsl:value-of select="@_gen-method-name"/>(value instanceof <xsl:value-of select="$java-class-spec"/> ? (<xsl:value-of select="$java-class-spec"/>) value : new <xsl:value-of select="$java-class-spec"/>(value.toString()));
</xsl:for-each>
		return true;
	}

	public void afterInsert(ConnectionContext cc) throws NamingException, SQLException
	{
		<xsl:value-of select="$_gen-table-class-name"/> table = (<xsl:value-of select="$_gen-table-class-name"/>) getTable();
		DatabasePolicy databasePolicy = cc.getDatabasePolicy();
		Object value;
<xsl:for-each select="column[@type = 'autoinc']">
<xsl:variable name="java-class-spec"><xsl:value-of select="java-class/@package"/>.<xsl:value-of select="java-class"/></xsl:variable>
<xsl:text>		</xsl:text>Column <xsl:value-of select="@_gen-member-name"/>Col = table.get<xsl:value-of select="@_gen-method-name"/>Column();
<xsl:text>		</xsl:text>value = databasePolicy.handleAutoIncPostDmlExecute(cc.getConnection(), <xsl:value-of select="@_gen-member-name"/>Col.getSequenceName(), <xsl:value-of select="@_gen-member-name"/>Col.getName(), get<xsl:value-of select="@_gen-method-name"/>());
<xsl:text>		</xsl:text>set<xsl:value-of select="@_gen-method-name"/>(value instanceof <xsl:value-of select="$java-class-spec"/> ? (<xsl:value-of select="$java-class-spec"/>) value : new <xsl:value-of select="$java-class-spec"/>(value.toString()));
</xsl:for-each>
		super.afterInsert(cc);			
	}
</xsl:if>

<xsl:for-each select="column">	
<xsl:variable name="member-name"><xsl:value-of select="@_gen-member-name"/></xsl:variable>
<xsl:variable name="array-index">COLAI_<xsl:value-of select="@_gen-constant-name"/></xsl:variable>
<xsl:variable name="java-type-init-cap"><xsl:value-of select="@_gen-java-type-init-cap"/></xsl:variable>
<xsl:variable name="java-class-spec"><xsl:value-of select="java-class/@package"/>.<xsl:value-of select="java-class"/></xsl:variable>
<xsl:if test="java-type">
	public <xsl:value-of select="java-type"/> get<xsl:value-of select="@_gen-method-name"/><xsl:value-of select="$java-type-init-cap"/>(<xsl:value-of select="java-type"/> defaultValue) { return <xsl:value-of select="$member-name"/> != null ? <xsl:value-of select="$member-name"/>.<xsl:value-of select="java-type"/>Value() : defaultValue; }		
	public <xsl:value-of select="java-type"/> get<xsl:value-of select="@_gen-method-name"/><xsl:value-of select="$java-type-init-cap"/>() { return <xsl:value-of select="$member-name"/>.<xsl:value-of select="java-type"/>Value(); }		
	public void set<xsl:value-of select="@_gen-method-name"/>(<xsl:value-of select="java-type"/> value) { set<xsl:value-of select="@_gen-method-name"/>(new <xsl:value-of select="$java-class-spec"/>(value)); }
</xsl:if>	
	public <xsl:value-of select="$java-class-spec"/> get<xsl:value-of select="@_gen-method-name"/>(<xsl:value-of select="$java-class-spec"/> defaultValue) { return <xsl:value-of select="$member-name"/> != null ? <xsl:value-of select="$member-name"/> : defaultValue; }
	public <xsl:value-of select="$java-class-spec"/> get<xsl:value-of select="@_gen-method-name"/>() { return <xsl:value-of select="$member-name"/>; }
	public void set<xsl:value-of select="@_gen-method-name"/>(<xsl:value-of select="$java-class-spec"/> value) { <xsl:value-of select="$member-name"/> = value; haveSqlExprData[<xsl:value-of select="$array-index"/>] = false; }
	public void set<xsl:value-of select="@_gen-method-name"/>SqlExpr(String value) { setCustomSqlExpr(<xsl:value-of select="$array-index"/>, value); }
<xsl:if	test="@_gen-ref-table-is-enum = 'yes'">
	public <xsl:value-of select="@_gen-ref-table-class-name"/>.EnumeratedItem get<xsl:value-of select="@_gen-method-name"/>Enum() { return <xsl:value-of select="@_gen-ref-table-class-name"/>.EnumeratedItem.getItemById(get<xsl:value-of select="@_gen-method-name"/>()); }
	public void set<xsl:value-of select="@_gen-method-name"/>(<xsl:value-of select="@_gen-ref-table-class-name"/>.EnumeratedItem value) { set<xsl:value-of select="@_gen-method-name"/>(value != null ? value.getIdAsInteger() : null); }
</xsl:if>
</xsl:for-each>
	public String toString()
	{
		StringBuffer str = new StringBuffer();
		str.append("Primary Key = ");
		str.append(getActivePrimaryKeyValue());
		str.append("\n");
<xsl:for-each select="column">		str.append(COLNAME_<xsl:value-of select="@_gen-constant-name"/>);
		str.append(" = ");
		str.append(<xsl:value-of select="@_gen-member-name"/> != null ? (<xsl:value-of select="@_gen-member-name"/>.toString() + " (" + <xsl:value-of select="@_gen-member-name"/>.getClass().getName() + ")") : "NULL");
		if(haveSqlExprData[COLAI_<xsl:value-of select="@_gen-constant-name"/>])
			str.append(" [SQL Expr: [" + sqlExprData[COLAI_<xsl:value-of select="@_gen-constant-name"/>] + "]]");
		else
			str.append(" [No SQL Expr]");
		str.append("\n");
</xsl:for-each>
		return str.toString();
	}
}
</xsl:template>

</xsl:stylesheet>






