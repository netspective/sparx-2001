
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
 * Represents the <code>Marital_Status</code> table.
 * <p>Because this table is an Enumeration tabletype, it automatically defines a
 * EnumeratedItem nested class that represents all of the values contained in the original SchemaDoc
 * specification. Therefore, to get enumeration captions and abbreviations for tables that are
 * referencing this table as a foreign key, a join in the database is not required.</p>
 * <p>
 * Some relevant facts about this class:
 * <ul>
 *   <li> It represents the <code>Marital_Status</code> in the database.
  * <li> It is a child of the <code>Reference_Item</code> table in the database.
  *   <li> It has 3 columns, all of which are represented as Column class instances as protected member variables and have public getter methods.
 *   <li> Wrappers are provided around the <code>createRow()</code> and <code>createRows()</code> methods to allow typed retrieval of the MaritalStatusRow and MaritalStatusRows objects.
 * <li> The <code>getMaritalStatusById()</code> method is provided to retrieve a single row by the primary key
 * </ul>
 * </p>
 */
public class MaritalStatusTable extends AbstractTable implements dal.table.type.Enumeration
{
	/** The <code>id</code> column definition **/
	protected dal.column.IntegerColumn id;
	/** The <code>caption</code> column definition **/
	protected dal.column.TextColumn caption;
	/** The <code>abbrev</code> column definition **/
	protected dal.column.TextColumn abbrev;

	/**
	 * This class represents a cache of the enumerated values from the <code>Marital_Status</code> table.
	 * It is designed to allow access to the actual values stored in the database without having to retrieve
	 * the data from the database each time it's needed (for example, you would use this class to check the
	 * caption of an enumerated item without doing a join in the database).
	 */
	public static class EnumeratedItem implements com.netspective.sparx.xif.dal.EnumeratedItem
	{
		protected static final Map captionsMap = new HashMap();
		protected static final Map abbrevsMap = new HashMap();
		public static final EnumeratedItem UNKNOWN = new EnumeratedItem(0, "Unknown");
		public static final EnumeratedItem SINGLE = new EnumeratedItem(1, "Single");
		public static final EnumeratedItem MARRIED = new EnumeratedItem(2, "Married");
		public static final EnumeratedItem PARTNER = new EnumeratedItem(3, "Partner");
		public static final EnumeratedItem LEGALLY_SEPARATED = new EnumeratedItem(4, "Legally Separated");
		public static final EnumeratedItem DIVORCED = new EnumeratedItem(5, "Divorced");
		public static final EnumeratedItem WIDOWED = new EnumeratedItem(6, "Widowed");
		public static final EnumeratedItem NOT_APPLICABLE = new EnumeratedItem(7, "Not applicable");

		static
		{
			captionsMap.put(UNKNOWN.getCaption().toUpperCase(), UNKNOWN);
			captionsMap.put(SINGLE.getCaption().toUpperCase(), SINGLE);
			captionsMap.put(MARRIED.getCaption().toUpperCase(), MARRIED);
			captionsMap.put(PARTNER.getCaption().toUpperCase(), PARTNER);
			captionsMap.put(LEGALLY_SEPARATED.getCaption().toUpperCase(), LEGALLY_SEPARATED);
			captionsMap.put(DIVORCED.getCaption().toUpperCase(), DIVORCED);
			captionsMap.put(WIDOWED.getCaption().toUpperCase(), WIDOWED);
			captionsMap.put(NOT_APPLICABLE.getCaption().toUpperCase(), NOT_APPLICABLE);
		};

		public static EnumeratedItem getEnum(int id)
		{
			switch(id)
			{
				case 0: return UNKNOWN;
				case 1: return SINGLE;
				case 2: return MARRIED;
				case 3: return PARTNER;
				case 4: return LEGALLY_SEPARATED;
				case 5: return DIVORCED;
				case 6: return WIDOWED;
				case 7: return NOT_APPLICABLE;
			}
			return null;
		}

		public static EnumeratedItem getItemById(Integer id)
		{
			if(id == null) return null;
			return getEnum(id.intValue());
		}

		public static EnumeratedItem getItemById(Long id)
		{
			if(id == null) return null;
			return getEnum((int) id.longValue());
		}

		public static EnumeratedItem getItemByCaption(String caption)
		{
			return (EnumeratedItem) captionsMap.get(caption.toUpperCase());
		}

		public static EnumeratedItem getItemByAbbrev(String abbrev)
		{
			return (EnumeratedItem) abbrevsMap.get(abbrev.toUpperCase());
		}

		private int id;
		private Integer idObject;
		private String caption;
		private String abbrev;

		private EnumeratedItem(int id, String caption, String abbrev)
		{
			this.id = id;
			this.idObject = new Integer(id);
			this.caption = caption;
			this.abbrev = abbrev;
		}

		private EnumeratedItem(int id, String caption)
		{
			this(id, caption, null);
		}

		public int getId() { return id; }
		public Integer getIdAsInteger() { return idObject; }
		public String getCaption() { return caption; }
		public String getAbbrev() { return abbrev; }
		public String getAbbrevOrCaption() { return abbrev != null ? abbrev : caption; }
	}


	public MaritalStatusTable(Schema schema)
	{
		super(schema, "Marital_Status");
		initializeDefn();
	}

	public void initializeDefn()
	{
		id = new dal.column.IntegerColumn(this, dal.domain.row.MaritalStatusRow.COLNAME_ID);
		id.setIsNaturalPrimaryKey(true);
		caption = new dal.column.TextColumn(this, dal.domain.row.MaritalStatusRow.COLNAME_CAPTION);
		caption.setIsRequired(true);
		caption.setSize(96);
		abbrev = new dal.column.TextColumn(this, dal.domain.row.MaritalStatusRow.COLNAME_ABBREV);
		abbrev.setIsUnique(true);

		setAllColumns(new Column[] {
			id,
			caption,
			abbrev
		});
	}

	/**
	* Create the MaritalStatusRow class that should
	* be used to store a single row of this table's data.
	*/
	public Row createRow()
	{
		return new dal.domain.row.MaritalStatusRow(this);
	}

	/**
	* Create the MaritalStatusRows class that should
	* be used to store multiple rows of this table's data.
	*/
	public Rows createRows()
	{
		return new dal.domain.rows.MaritalStatusRows(this);
	}

	/**
	* Create the MaritalStatusRow class that should
	* be used to store a single row of this table's data.
	*/
	public dal.domain.row.MaritalStatusRow createMaritalStatusRow()
	{
		return new dal.domain.row.MaritalStatusRow(this);
	}

	/**
	* Create the MaritalStatusRows class that should
	* be used to store multiple rows of this table's data.
	*/
	public dal.domain.rows.MaritalStatusRows createMaritalStatusRows()
	{
		return new dal.domain.rows.MaritalStatusRows(this);
	}

	/** Returns the <code>id</code> column definition **/
	public dal.column.IntegerColumn getIdColumn() { return id; }
	/** Returns the <code>caption</code> column definition **/
	public dal.column.TextColumn getCaptionColumn() { return caption; }
	/** Returns the <code>abbrev</code> column definition **/
	public dal.column.TextColumn getAbbrevColumn() { return abbrev; }

    /**
     * Updates the dal.domain.row.MaritalStatusRow identified by the primary key as a primitive type
     */
    public boolean updateById(ConnectionContext cc, Row row, int value) throws NamingException, SQLException
    {
        return update(cc, row, "id = ?", new java.lang.Integer(value));
    }

	/** Returns the dal.domain.row.MaritalStatusRow identified by the primary key as a primitive type **/
	public dal.domain.row.MaritalStatusRow getMaritalStatusById(ConnectionContext cc, int value) throws NamingException, SQLException
	{
		return getMaritalStatusById(cc, new java.lang.Integer(value));
	}

	/** Returns the dal.domain.row.MaritalStatusRow identified by the primary key **/
	public dal.domain.row.MaritalStatusRow getMaritalStatusById(ConnectionContext cc, java.lang.Integer value) throws NamingException, SQLException
	{
		return (dal.domain.row.MaritalStatusRow) getRecordByPrimaryKey(cc, value, null);
	}


}
