
package dal.domain.rows;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.netspective.sparx.xif.db.*;
import com.netspective.sparx.xif.dal.*;

public class MaritalStatusRows extends AbstractRows
{
	private dal.table.MaritalStatusTable table;
	
	public MaritalStatusRows(dal.table.MaritalStatusTable table)
	{
		super();
		this.table = table;
	}
	
	public void populateDataByIndexes(ResultSet resultSet) throws SQLException
	{
		super.populateDataByIndexes(resultSet);
		dal.domain.row.MaritalStatusRow row = null;
		while(resultSet.next())
		{
			row = table.createMaritalStatusRow();
			row.populateDataByIndexes(resultSet);
			add(row);
		}
	}

	public void populateDataByNames(ResultSet resultSet) throws SQLException
	{
		super.populateDataByNames(resultSet);
		Map colNameIndexMap = AbstractRow.getColumnNamesIndexMap(resultSet);
		dal.domain.row.MaritalStatusRow row = null;
		while(resultSet.next())
		{
			row = table.createMaritalStatusRow();
			row.populateDataByNames(resultSet, colNameIndexMap);
			add(row);
		}
	}

	public void populateDataByNames(Element element) throws ParseException, DOMException
	{
		super.populateDataByNames(element);
		dal.domain.row.MaritalStatusRow row = null;
		NodeList dataChildren = element.getChildNodes();
		int dataChildrenCount = dataChildren.getLength();
		String rowNodeName = table.getNameForXmlNode();
		for(int i = 0; i < dataChildrenCount; i++)
		{
			Node dataChildNode = dataChildren.item(i);
			if(! dataChildNode.getNodeName().equals(rowNodeName))
				continue;

			row = table.createMaritalStatusRow();
			row.populateDataByNames((Element) dataChildNode);
			add(row);
		}
	}
	
	public dal.domain.MaritalStatus getMaritalStatus(int rowNum)
	{
		return (dal.domain.MaritalStatus) get(rowNum);
	}	
	
	public dal.domain.row.MaritalStatusRow getMaritalStatusRow(int rowNum)
	{
		return (dal.domain.row.MaritalStatusRow) get(rowNum);
	}	
}
