
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
 * Represents the <code>Lookup_Result_Type</code> table.
 * <p>Because this table is an Enumeration tabletype, it automatically defines a
 * EnumeratedItem nested class that represents all of the values contained in the original SchemaDoc
 * specification. Therefore, to get enumeration captions and abbreviations for tables that are
 * referencing this table as a foreign key, a join in the database is not required.</p>
 * <p>
 * Some relevant facts about this class:
 * <ul>
 *   <li> It represents the <code>Lookup_Result_Type</code> in the database.
  * <li> It is a child of the <code>Reference_Item</code> table in the database.
  *   <li> It has 3 columns, all of which are represented as Column class instances as protected member variables and have public getter methods.
 *   <li> Wrappers are provided around the <code>createRow()</code> and <code>createRows()</code> methods to allow typed retrieval of the LookupResultTypeRow and LookupResultTypeRows objects.
 * <li> The <code>getLookupResultTypeById()</code> method is provided to retrieve a single row by the primary key
 * </ul>
 * </p>
 */
public class LookupResultTypeTable extends AbstractTable implements dal.table.type.Enumeration
{
	/** The <code>id</code> column definition **/
	protected dal.column.IntegerColumn id;
	/** The <code>caption</code> column definition **/
	protected dal.column.TextColumn caption;
	/** The <code>abbrev</code> column definition **/
	protected dal.column.TextColumn abbrev;

	/**
	 * This class represents a cache of the enumerated values from the <code>Lookup_Result_Type</code> table.
	 * It is designed to allow access to the actual values stored in the database without having to retrieve
	 * the data from the database each time it's needed (for example, you would use this class to check the
	 * caption of an enumerated item without doing a join in the database).
	 */
	public static class EnumeratedItem implements com.netspective.sparx.xif.dal.EnumeratedItem
	{
		protected static final Map captionsMap = new HashMap();
		protected static final Map abbrevsMap = new HashMap();
		public static final EnumeratedItem ID = new EnumeratedItem(0, "ID");
		public static final EnumeratedItem CAPTION = new EnumeratedItem(1, "Caption");
		public static final EnumeratedItem ABBREVIATION = new EnumeratedItem(2, "Abbreviation");

		static
		{
			captionsMap.put(ID.getCaption().toUpperCase(), ID);
			captionsMap.put(CAPTION.getCaption().toUpperCase(), CAPTION);
			captionsMap.put(ABBREVIATION.getCaption().toUpperCase(), ABBREVIATION);
		};

		public static EnumeratedItem getEnum(int id)
		{
			switch(id)
			{
				case 0: return ID;
				case 1: return CAPTION;
				case 2: return ABBREVIATION;
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


	public LookupResultTypeTable(Schema schema)
	{
		super(schema, "Lookup_Result_Type");
		initializeDefn();
	}

	public void initializeDefn()
	{
		id = new dal.column.IntegerColumn(this, dal.domain.row.LookupResultTypeRow.COLNAME_ID);
		id.setIsNaturalPrimaryKey(true);
		caption = new dal.column.TextColumn(this, dal.domain.row.LookupResultTypeRow.COLNAME_CAPTION);
		caption.setIsRequired(true);
		caption.setSize(96);
		abbrev = new dal.column.TextColumn(this, dal.domain.row.LookupResultTypeRow.COLNAME_ABBREV);
		abbrev.setIsUnique(true);

		setAllColumns(new Column[] {
			id,
			caption,
			abbrev
		});
	}

	/**
	* Create the LookupResultTypeRow class that should
	* be used to store a single row of this table's data.
	*/
	public Row createRow()
	{
		return new dal.domain.row.LookupResultTypeRow(this);
	}

	/**
	* Create the LookupResultTypeRows class that should
	* be used to store multiple rows of this table's data.
	*/
	public Rows createRows()
	{
		return new dal.domain.rows.LookupResultTypeRows(this);
	}

	/**
	* Create the LookupResultTypeRow class that should
	* be used to store a single row of this table's data.
	*/
	public dal.domain.row.LookupResultTypeRow createLookupResultTypeRow()
	{
		return new dal.domain.row.LookupResultTypeRow(this);
	}

	/**
	* Create the LookupResultTypeRows class that should
	* be used to store multiple rows of this table's data.
	*/
	public dal.domain.rows.LookupResultTypeRows createLookupResultTypeRows()
	{
		return new dal.domain.rows.LookupResultTypeRows(this);
	}

	/** Returns the <code>id</code> column definition **/
	public dal.column.IntegerColumn getIdColumn() { return id; }
	/** Returns the <code>caption</code> column definition **/
	public dal.column.TextColumn getCaptionColumn() { return caption; }
	/** Returns the <code>abbrev</code> column definition **/
	public dal.column.TextColumn getAbbrevColumn() { return abbrev; }

    /**
     * Updates the dal.domain.row.LookupResultTypeRow identified by the primary key as a primitive type
     */
    public boolean updateById(ConnectionContext cc, Row row, int value) throws NamingException, SQLException
    {
        return update(cc, row, "id = ?", new java.lang.Integer(value));
    }

	/** Returns the dal.domain.row.LookupResultTypeRow identified by the primary key as a primitive type **/
	public dal.domain.row.LookupResultTypeRow getLookupResultTypeById(ConnectionContext cc, int value) throws NamingException, SQLException
	{
		return getLookupResultTypeById(cc, new java.lang.Integer(value));
	}

	/** Returns the dal.domain.row.LookupResultTypeRow identified by the primary key **/
	public dal.domain.row.LookupResultTypeRow getLookupResultTypeById(ConnectionContext cc, java.lang.Integer value) throws NamingException, SQLException
	{
		return (dal.domain.row.LookupResultTypeRow) getRecordByPrimaryKey(cc, value, null);
	}


}
