
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
 * Represents a single row of the <code>Reference_Item</code> table.

 * <p>
 * Some relevant facts about this class:
 * <ul>
 *   <li> It represents the data that can be stored and retrieved from the <code>Reference_Item</code> table in the database.
  *   <li> It has 0 columns, all of which have getter and setter methods.
 *   <li> It has children which can be retrieved using:
 *              <ol>
 *                <li> <code>getMaritalStatusRows()</code> (join condition is <code>parent. = this.</code>)
 *                <li> <code>getGenderRows()</code> (join condition is <code>parent. = this.</code>)
 *                <li> <code>getLookupResultTypeRows()</code> (join condition is <code>parent. = this.</code>)
 *              </ol>
 * </ul>
 * </p>
 */
public class ReferenceItemRow extends AbstractRow implements dal.domain.ReferenceItem
{
	/** The children rows from Marital_Status connected to this row by parent. = this. (null if children not retrieved) **/
	protected dal.domain.rows.MaritalStatusRows maritalStatusRows;
	/** The children rows from Gender connected to this row by parent. = this. (null if children not retrieved) **/
	protected dal.domain.rows.GenderRows genderRows;
	/** The children rows from Lookup_Result_Type connected to this row by parent. = this. (null if children not retrieved) **/
	protected dal.domain.rows.LookupResultTypeRows lookupResultTypeRows;


	public ReferenceItemRow(dal.table.ReferenceItemTable table)
	{
		super(table);

	}



	public Object[] getData()
	{
		return new Object[] {

		};
	}

	/**
	 * Return true if the row is an instance of the ReferenceItemRow class
	 * and each of the columns in the row parameter matches the data contained in this
	 * row. The values of two columns are considered equal when either the object is the
	 * same, both are null, or the equals() method for the two objects when compared with
	 * other returns true.
	 **/
	public boolean equals(Object row)
	{
		if(this == row) return true;
		if(row == null) return false;
		if(! (row instanceof ReferenceItemRow)) return false;

		ReferenceItemRow compareRow = (ReferenceItemRow) row;

		return true;
	}

	public List getDataForDmlStatement()
	{
		List data = new ArrayList();

		return data;
	}

	public void populateDataByIndexes(ResultSet rs) throws SQLException
	{
	
	}

	public void populateDataByNames(ResultSet rs, Map colNameIndexMap) throws SQLException
	{
		if(colNameIndexMap == null) colNameIndexMap = getColumnNamesIndexMap(rs);
		Integer colIndex = null;

	}

	public void populateDataByNames(Element element) throws ParseException, DOMException
	{
		dal.table.ReferenceItemTable table = (dal.table.ReferenceItemTable) getTable();

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
		}
	}

	public void populateDataByNames(DialogContext dc)
	{
		dal.table.ReferenceItemTable table = (dal.table.ReferenceItemTable) getTable();
		Map fieldStates = dc.getFieldStates();
		DialogContext.DialogFieldState state = null;

	}

	public void populateDataByNames(DialogContext dc, Map colNameFieldNameMap)
	{
		dal.table.ReferenceItemTable table = (dal.table.ReferenceItemTable) getTable();
		Map fieldStates = dc.getFieldStates();
		String fieldName = null;
		DialogContext.DialogFieldState state = null;

	}

	public void setData(DialogContext dc)
	{
		dal.table.ReferenceItemTable table = (dal.table.ReferenceItemTable) getTable();
	}

	public void setData(DialogContext dc, Map colNameFieldNameMap)
	{
		dal.table.ReferenceItemTable table = (dal.table.ReferenceItemTable) getTable();
		String fieldName = null;
	}


	public String toString()
	{
		StringBuffer str = new StringBuffer();
		str.append("Primary Key = ");
		str.append(getActivePrimaryKeyValue());
		str.append("\n");

		return str.toString();
	}
}
