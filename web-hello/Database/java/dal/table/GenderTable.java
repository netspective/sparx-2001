
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
 * Represents the <code>Gender</code> table.
 * <p>Because this table is an Enumeration tabletype, it automatically defines a
 * EnumeratedItem nested class that represents all of the values contained in the original SchemaDoc
 * specification. Therefore, to get enumeration captions and abbreviations for tables that are
 * referencing this table as a foreign key, a join in the database is not required.</p>
 * <p>
 * Some relevant facts about this class:
 * <ul>
 *   <li> It represents the <code>Gender</code> in the database.
  * <li> It is a child of the <code>Reference_Item</code> table in the database.
  *   <li> It has 3 columns, all of which are represented as Column class instances as protected member variables and have public getter methods.
 *   <li> Wrappers are provided around the <code>createRow()</code> and <code>createRows()</code> methods to allow typed retrieval of the GenderRow and GenderRows objects.
 * <li> The <code>getGenderById()</code> method is provided to retrieve a single row by the primary key
 * </ul>
 * </p>
 */
public class GenderTable extends AbstractTable implements dal.table.type.Enumeration
{
	/** The <code>id</code> column definition **/
	protected dal.column.IntegerColumn id;
	/** The <code>caption</code> column definition **/
	protected dal.column.TextColumn caption;
	/** The <code>abbrev</code> column definition **/
	protected dal.column.TextColumn abbrev;

	/**
	 * This class represents a cache of the enumerated values from the <code>Gender</code> table.
	 * It is designed to allow access to the actual values stored in the database without having to retrieve
	 * the data from the database each time it's needed (for example, you would use this class to check the
	 * caption of an enumerated item without doing a join in the database).
	 */
	public static class EnumeratedItem implements com.netspective.sparx.xif.dal.EnumeratedItem
	{
		protected static final Map captionsMap = new HashMap();
		protected static final Map abbrevsMap = new HashMap();
		public static final EnumeratedItem MALE = new EnumeratedItem(0, "Male");
		public static final EnumeratedItem FEMALE = new EnumeratedItem(1, "Female");
		public static final EnumeratedItem NOT_APPLICABLE = new EnumeratedItem(2, "Not applicable");

		static
		{
			captionsMap.put(MALE.getCaption().toUpperCase(), MALE);
			captionsMap.put(FEMALE.getCaption().toUpperCase(), FEMALE);
			captionsMap.put(NOT_APPLICABLE.getCaption().toUpperCase(), NOT_APPLICABLE);
		};

		public static EnumeratedItem getEnum(int id)
		{
			switch(id)
			{
				case 0: return MALE;
				case 1: return FEMALE;
				case 2: return NOT_APPLICABLE;
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


	public GenderTable(Schema schema)
	{
		super(schema, "Gender");
		initializeDefn();
	}

	public void initializeDefn()
	{
		id = new dal.column.IntegerColumn(this, dal.domain.row.GenderRow.COLNAME_ID);
		id.setIsNaturalPrimaryKey(true);
		caption = new dal.column.TextColumn(this, dal.domain.row.GenderRow.COLNAME_CAPTION);
		caption.setIsRequired(true);
		caption.setSize(96);
		abbrev = new dal.column.TextColumn(this, dal.domain.row.GenderRow.COLNAME_ABBREV);
		abbrev.setIsUnique(true);

		setAllColumns(new Column[] {
			id,
			caption,
			abbrev
		});
	}

	/**
	* Create the GenderRow class that should
	* be used to store a single row of this table's data.
	*/
	public Row createRow()
	{
		return new dal.domain.row.GenderRow(this);
	}

	/**
	* Create the GenderRows class that should
	* be used to store multiple rows of this table's data.
	*/
	public Rows createRows()
	{
		return new dal.domain.rows.GenderRows(this);
	}

	/**
	* Create the GenderRow class that should
	* be used to store a single row of this table's data.
	*/
	public dal.domain.row.GenderRow createGenderRow()
	{
		return new dal.domain.row.GenderRow(this);
	}

	/**
	* Create the GenderRows class that should
	* be used to store multiple rows of this table's data.
	*/
	public dal.domain.rows.GenderRows createGenderRows()
	{
		return new dal.domain.rows.GenderRows(this);
	}

	/** Returns the <code>id</code> column definition **/
	public dal.column.IntegerColumn getIdColumn() { return id; }
	/** Returns the <code>caption</code> column definition **/
	public dal.column.TextColumn getCaptionColumn() { return caption; }
	/** Returns the <code>abbrev</code> column definition **/
	public dal.column.TextColumn getAbbrevColumn() { return abbrev; }

    /**
     * Updates the dal.domain.row.GenderRow identified by the primary key as a primitive type
     */
    public boolean updateById(ConnectionContext cc, Row row, int value) throws NamingException, SQLException
    {
        return update(cc, row, "id = ?", new java.lang.Integer(value));
    }

	/** Returns the dal.domain.row.GenderRow identified by the primary key as a primitive type **/
	public dal.domain.row.GenderRow getGenderById(ConnectionContext cc, int value) throws NamingException, SQLException
	{
		return getGenderById(cc, new java.lang.Integer(value));
	}

	/** Returns the dal.domain.row.GenderRow identified by the primary key **/
	public dal.domain.row.GenderRow getGenderById(ConnectionContext cc, java.lang.Integer value) throws NamingException, SQLException
	{
		return (dal.domain.row.GenderRow) getRecordByPrimaryKey(cc, value, null);
	}


}
