
package dal.domain.row;

import java.io.*;
import java.util.*;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Connection;
import java.text.ParseException;
import javax.naming.NamingException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.netspective.sparx.xif.db.*;
import com.netspective.sparx.xif.dal.*;
import com.netspective.sparx.xaf.form.*;
import com.netspective.sparx.xaf.sql.*;
import com.netspective.sparx.util.value.*;

/**
 * Represents a single row of the <code>Lookup_Result_Type</code> table.
 * <p>Because this interface defines the
 * row of a table that is an Enumeration tabletype, it is recommended that this
 * interface <b>not</b> be used for simple reads from the database.
 * Instead, one should use the dal.table.LookupResultTypeTable.EnumeratedItem class
 * so that a join in the database will not be required for static data.
 * <p>
 * Some relevant facts about this class:
 * <ul>
 *   <li> It represents the data that can be stored and retrieved from the <code>Lookup_Result_Type</code> table in the database.
  * <li> The row is a child of the <code>Reference_Item</code> table in the database.
  *   <li> It has 3 columns, all of which have getter and setter methods.
 * </ul>
 * </p>
 */
public class LookupResultTypeRow extends AbstractRow implements dal.domain.LookupResultType
{
	/** The name of the <code>id</code> column in the database **/
	public static final String COLNAME_ID = "id";
	/** The name of the <code>caption</code> column in the database **/
	public static final String COLNAME_CAPTION = "caption";
	/** The name of the <code>abbrev</code> column in the database **/
	public static final String COLNAME_ABBREV = "abbrev";
	/** The name of the <code>id</code> column suitable for use as an XML node/element **/
	public static final String NODENAME_ID = "id";
	/** The name of the <code>caption</code> column suitable for use as an XML node/element **/
	public static final String NODENAME_CAPTION = "caption";
	/** The name of the <code>abbrev</code> column suitable for use as an XML node/element **/
	public static final String NODENAME_ABBREV = "abbrev";
	/** The index of the <code>id</code> column in a ResultSet when all columns are selected from a table (1-based) **/
	public static final int COLRSI_ID = 1;
	/** The index of the <code>caption</code> column in a ResultSet when all columns are selected from a table (1-based) **/
	public static final int COLRSI_CAPTION = 2;
	/** The index of the <code>abbrev</code> column in a ResultSet when all columns are selected from a table (1-based) **/
	public static final int COLRSI_ABBREV = 3;
	/** The index of the <code>id</code> column in an array when all columns are selected from a table (0-based) **/
	public static final int COLAI_ID = 0;
	/** The index of the <code>caption</code> column in an array when all columns are selected from a table (0-based) **/
	public static final int COLAI_CAPTION = 1;
	/** The index of the <code>abbrev</code> column in an array when all columns are selected from a table (0-based) **/
	public static final int COLAI_ABBREV = 2;
	/** The <code>id</code> column data **/
	protected java.lang.Integer id;
	/** The <code>caption</code> column data **/
	protected java.lang.String caption;
	/** The <code>abbrev</code> column data **/
	protected java.lang.String abbrev;


	public LookupResultTypeRow(dal.table.LookupResultTypeTable table)
	{
		super(table);

	}


	/**
	 * Return the value of the id column
	 **/
	public Object getActivePrimaryKeyValue()
	{
		return getId();
	}


	public Object[] getData()
	{
		return new Object[] {
			id,
			caption,
			abbrev
		};
	}

	/**
	 * Return true if the row is an instance of the LookupResultTypeRow class
	 * and each of the columns in the row parameter matches the data contained in this
	 * row. The values of two columns are considered equal when either the object is the
	 * same, both are null, or the equals() method for the two objects when compared with
	 * other returns true.
	 **/
	public boolean equals(Object row)
	{
		if(this == row) return true;
		if(row == null) return false;
		if(! (row instanceof LookupResultTypeRow)) return false;

		LookupResultTypeRow compareRow = (LookupResultTypeRow) row;
		if(! valuesAreEqual(getId(), compareRow.getId())) return false;
		if(! valuesAreEqual(getCaption(), compareRow.getCaption())) return false;
		if(! valuesAreEqual(getAbbrev(), compareRow.getAbbrev())) return false;

		return true;
	}

	public List getDataForDmlStatement()
	{
		List data = new ArrayList();
		data.add(haveSqlExprData[COLAI_ID] ? ((Object) new DmlStatement.CustomSql(sqlExprData[COLAI_ID])) : id);
		data.add(haveSqlExprData[COLAI_CAPTION] ? ((Object) new DmlStatement.CustomSql(sqlExprData[COLAI_CAPTION])) : caption);
		data.add(haveSqlExprData[COLAI_ABBREV] ? ((Object) new DmlStatement.CustomSql(sqlExprData[COLAI_ABBREV])) : abbrev);

		return data;
	}

	public void populateDataByIndexes(ResultSet rs) throws SQLException
	{
		java.lang.Integer idValue = new java.lang.Integer(rs.getInt(COLRSI_ID));
		setId(rs.wasNull() ? null : idValue);
		setCaption(rs.getString(COLRSI_CAPTION));
		setAbbrev(rs.getString(COLRSI_ABBREV));
	
	}

	public void populateDataByNames(ResultSet rs, Map colNameIndexMap) throws SQLException
	{
		if(colNameIndexMap == null) colNameIndexMap = getColumnNamesIndexMap(rs);
		Integer colIndex = null;

		colIndex = (Integer) colNameIndexMap.get(COLNAME_ID);
		if(colIndex != null) {
			java.lang.Integer idValue = new java.lang.Integer(rs.getInt(colIndex.intValue()));
			setId(rs.wasNull() ? null : idValue);
		}

		colIndex = (Integer) colNameIndexMap.get(COLNAME_CAPTION);
		if(colIndex != null) {
			setCaption(rs.getString(colIndex.intValue()));
		}

		colIndex = (Integer) colNameIndexMap.get(COLNAME_ABBREV);
		if(colIndex != null) {
			setAbbrev(rs.getString(colIndex.intValue()));
		}

	}

	public void populateDataByNames(Element element) throws ParseException, DOMException
	{
		dal.table.LookupResultTypeTable table = (dal.table.LookupResultTypeTable) getTable();

		NodeList rowChildren = element.getChildNodes();
		int rowChildrenCount = rowChildren.getLength();
		for(int i = 0; i < rowChildrenCount; i++)
		{
			Node rowChildNode = rowChildren.item(i);
			if(rowChildNode.getNodeType() != Node.ELEMENT_NODE)
				continue;

			Element columnDataElem = (Element) rowChildNode;
			String columnName = columnDataElem.getNodeName();
			String columnValue = columnDataElem.getFirstChild().getNodeValue();
			if(NODENAME_ID.equals(columnName)) {
				setId(table.getIdColumn().parse(columnValue));
				break;
			}
	
			if(NODENAME_CAPTION.equals(columnName)) {
				setCaption(columnValue);
				break;
			}
	
			if(NODENAME_ABBREV.equals(columnName)) {
				setAbbrev(columnValue);
				break;
			}
			}
	}

	public void populateDataByNames(DialogContext dc)
	{
		dal.table.LookupResultTypeTable table = (dal.table.LookupResultTypeTable) getTable();
		Map fieldStates = dc.getFieldStates();
		DialogContext.DialogFieldState state = null;
		state = (DialogContext.DialogFieldState) fieldStates.get(COLNAME_ID);
      if(state != null && state.value != null && state.value.length() > 0) setId(table.getIdColumn().parse(state.value));
		state = (DialogContext.DialogFieldState) fieldStates.get(COLNAME_CAPTION);
      if(state != null && state.value != null && state.value.length() > 0) setCaption(state.value);
		state = (DialogContext.DialogFieldState) fieldStates.get(COLNAME_ABBREV);
      if(state != null && state.value != null && state.value.length() > 0) setAbbrev(state.value);

	}

	public void populateDataByNames(DialogContext dc, Map colNameFieldNameMap)
	{
		dal.table.LookupResultTypeTable table = (dal.table.LookupResultTypeTable) getTable();
		Map fieldStates = dc.getFieldStates();
		String fieldName = null;
		DialogContext.DialogFieldState state = null;
		fieldName = (String) colNameFieldNameMap.get(COLNAME_ID);
		state = (DialogContext.DialogFieldState) fieldStates.get(fieldName != null ? fieldName : COLNAME_ID);
		if(state != null && state.value != null && state.value.length() > 0) setId(table.getIdColumn().parse(state.value));
		fieldName = (String) colNameFieldNameMap.get(COLNAME_CAPTION);
		state = (DialogContext.DialogFieldState) fieldStates.get(fieldName != null ? fieldName : COLNAME_CAPTION);
		if(state != null && state.value != null && state.value.length() > 0) setCaption(state.value);
		fieldName = (String) colNameFieldNameMap.get(COLNAME_ABBREV);
		state = (DialogContext.DialogFieldState) fieldStates.get(fieldName != null ? fieldName : COLNAME_ABBREV);
		if(state != null && state.value != null && state.value.length() > 0) setAbbrev(state.value);

	}

	public void setData(DialogContext dc)
	{
		dal.table.LookupResultTypeTable table = (dal.table.LookupResultTypeTable) getTable();
		dc.setValue(COLNAME_ID, table.getIdColumn().format(dc, getId()));
		dc.setValue(COLNAME_CAPTION, getCaption());
		dc.setValue(COLNAME_ABBREV, getAbbrev());
	}

	public void setData(DialogContext dc, Map colNameFieldNameMap)
	{
		dal.table.LookupResultTypeTable table = (dal.table.LookupResultTypeTable) getTable();
		String fieldName = null;
		fieldName = (String) colNameFieldNameMap.get(COLNAME_ID);
		dc.setValue(fieldName != null ? fieldName : COLNAME_ID, table.getIdColumn().format(dc, getId()));
		fieldName = (String) colNameFieldNameMap.get(COLNAME_CAPTION);
		dc.setValue(fieldName != null ? fieldName : COLNAME_CAPTION, getCaption());
		fieldName = (String) colNameFieldNameMap.get(COLNAME_ABBREV);
		dc.setValue(fieldName != null ? fieldName : COLNAME_ABBREV, getAbbrev());
	}


	public int getIdInt(int defaultValue) { return id != null ? id.intValue() : defaultValue; }
	public int getIdInt() { return id.intValue(); }
	public void setId(int value) { setId(new java.lang.Integer(value)); }

	public java.lang.Integer getId(java.lang.Integer defaultValue) { return id != null ? id : defaultValue; }
	public java.lang.Integer getId() { return id; }
	public void setId(java.lang.Integer value) { id = value; haveSqlExprData[COLAI_ID] = false; }
	/** The value associated with a single enum **/
	public void setIdSqlExpr(String value) { setCustomSqlExpr(COLAI_ID, value); }

	public java.lang.String getCaption(java.lang.String defaultValue) { return caption != null ? caption : defaultValue; }
	public java.lang.String getCaption() { return caption; }
	public void setCaption(java.lang.String value) { caption = value; haveSqlExprData[COLAI_CAPTION] = false; }
	/** The name/text an end-user would see **/
	public void setCaptionSqlExpr(String value) { setCustomSqlExpr(COLAI_CAPTION, value); }

	public java.lang.String getAbbrev(java.lang.String defaultValue) { return abbrev != null ? abbrev : defaultValue; }
	public java.lang.String getAbbrev() { return abbrev; }
	public void setAbbrev(java.lang.String value) { abbrev = value; haveSqlExprData[COLAI_ABBREV] = false; }
	/** An abbreviated form of the caption **/
	public void setAbbrevSqlExpr(String value) { setCustomSqlExpr(COLAI_ABBREV, value); }

	public String toString()
	{
		StringBuffer str = new StringBuffer();
		str.append("Primary Key = ");
		str.append(getActivePrimaryKeyValue());
		str.append("\n");
		str.append(COLNAME_ID);
		str.append(" = ");
		str.append(id != null ? (id.toString() + " (" + id.getClass().getName() + ")") : "NULL");
		if(haveSqlExprData[COLAI_ID])
			str.append(" [SQL Expr: [" + sqlExprData[COLAI_ID] + "]]");
		else
			str.append(" [No SQL Expr]");
		str.append("\n");
		str.append(COLNAME_CAPTION);
		str.append(" = ");
		str.append(caption != null ? (caption.toString() + " (" + caption.getClass().getName() + ")") : "NULL");
		if(haveSqlExprData[COLAI_CAPTION])
			str.append(" [SQL Expr: [" + sqlExprData[COLAI_CAPTION] + "]]");
		else
			str.append(" [No SQL Expr]");
		str.append("\n");
		str.append(COLNAME_ABBREV);
		str.append(" = ");
		str.append(abbrev != null ? (abbrev.toString() + " (" + abbrev.getClass().getName() + ")") : "NULL");
		if(haveSqlExprData[COLAI_ABBREV])
			str.append(" [SQL Expr: [" + sqlExprData[COLAI_ABBREV] + "]]");
		else
			str.append(" [No SQL Expr]");
		str.append("\n");

		return str.toString();
	}
}
