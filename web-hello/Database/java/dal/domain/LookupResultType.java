
package dal.domain;

/**
 * Represents a single row of the <code>Lookup_Result_Type</code> table.
 * <p>Because this interface defines the
 * row of a table that is an Enumeration tabletype, it is recommended that this
 * interface <b>not</b> be used for simple reads from the database. 
 * Instead, one should use the dal.table.LookupResultTypeTable.EnumeratedItem class 
 * so that a join in the database will not be required for static data.
 * <p>
 * Some relevant facts about this interface:
 * <ul>
 *   <li> It represents the data that can be stored and retrieved from the <code>Lookup_Result_Type</code> table in the database.
 *   <li> It has 3 columns, all of which have getter and setter methods.
 * </ul>
 * </p>
 */
public interface LookupResultType
{

	/**
	 * The value associated with a single enum
	 * @param defaultValue The value to return if id is null
	 **/
	public int getIdInt(int defaultValue);

	/**
	 * The value associated with a single enum
	 **/
	public int getIdInt();

	/**
	 * The value associated with a single enum
	 **/
	public void setId(int value);
	
	/**
	 * The value associated with a single enum
	 * @param defaultValue The value to return if id is null
	 **/
	public java.lang.Integer getId(java.lang.Integer defaultValue);

	/**
	 * The value associated with a single enum
	 **/
	public java.lang.Integer getId();

	/**
	 * The value associated with a single enum
	 **/
	public void setId(java.lang.Integer value);
	
	/**
	 * The name/text an end-user would see
	 * @param defaultValue The value to return if caption is null
	 **/
	public java.lang.String getCaption(java.lang.String defaultValue);

	/**
	 * The name/text an end-user would see
	 **/
	public java.lang.String getCaption();

	/**
	 * The name/text an end-user would see
	 **/
	public void setCaption(java.lang.String value);
	
	/**
	 * An abbreviated form of the caption
	 * @param defaultValue The value to return if abbrev is null
	 **/
	public java.lang.String getAbbrev(java.lang.String defaultValue);

	/**
	 * An abbreviated form of the caption
	 **/
	public java.lang.String getAbbrev();

	/**
	 * An abbreviated form of the caption
	 **/
	public void setAbbrev(java.lang.String value);

}
