
package dal.table;

import java.io.*;
import java.util.*;
import java.sql.SQLException;
import java.sql.ResultSet;
import javax.naming.NamingException;

import com.netspective.sparx.xif.db.*;
import com.netspective.sparx.xif.dal.*;
import com.netspective.sparx.util.value.*;

/**
 * Represents the <code>Reference_Item</code> table.

 * <p>
 * Some relevant facts about this class:
 * <ul>
 *   <li> It represents the <code>Reference_Item</code> in the database.
  *   <li> It has 0 columns, all of which are represented as Column class instances as protected member variables and have public getter methods.
 *   <li> Wrappers are provided around the <code>createRow()</code> and <code>createRows()</code> methods to allow typed retrieval of the ReferenceItemRow and ReferenceItemRows objects.
 * </ul>
 * </p>
 */
public class ReferenceItemTable extends AbstractTable 
{


	public ReferenceItemTable(Schema schema)
	{
		super(schema, "Reference_Item");
		initializeDefn();
	}

	public void initializeDefn()
	{

		setAllColumns(new Column[] {

		});
	}

	/**
	* Create the ReferenceItemRow class that should
	* be used to store a single row of this table's data.
	*/
	public Row createRow()
	{
		return new dal.domain.row.ReferenceItemRow(this);
	}

	/**
	* Create the ReferenceItemRows class that should
	* be used to store multiple rows of this table's data.
	*/
	public Rows createRows()
	{
		return new dal.domain.rows.ReferenceItemRows(this);
	}

	/**
	* Create the ReferenceItemRow class that should
	* be used to store a single row of this table's data.
	*/
	public dal.domain.row.ReferenceItemRow createReferenceItemRow()
	{
		return new dal.domain.row.ReferenceItemRow(this);
	}

	/**
	* Create the ReferenceItemRows class that should
	* be used to store multiple rows of this table's data.
	*/
	public dal.domain.rows.ReferenceItemRows createReferenceItemRows()
	{
		return new dal.domain.rows.ReferenceItemRows(this);
	}



}
