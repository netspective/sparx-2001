
package dal;

import java.io.*;
import java.util.*;

import com.netspective.sparx.xif.dal.*;

/**
 * The class that contains all of the table definitions for the Schema. This class
 * defines a static variable called <code>instance</code> that is automatically initialized 
 * at startup. Using this instance, the developer has complete access to all of the
 * tables, columns, row types, rows types, and domains contained in the schema.
 * <p>
 * Here are some usage patterns (assuming there is a Person table and
 * a PersonAddress child table in the database.):
 * </p>
 * <pre>
 * 	PersonTable personTable = DataAccessLayer.instance.getPersonTable();
 *	PersonAddressTable personAddressTable = DataAccessLayer.instance.getPersonAddressTable();
 *	ConnectionContext cc = ConnectionContext.getConnectionContext(
 *		DatabaseContextFactory.getSystemContext(), dataSourceId, 
 *		ConnectionContext.CONNCTXTYPE_AUTO);
 *
 *	// Create a new person record and insert it into the database:
 *	PersonRow personRow = personTable.createPersonRow();
 *	personRow.setNameFirst("First Name");
 *	personRow.setNameLast("Last Name");
 *	personTable.insert(cc, personRow);
 *	
 *	// assuming the the person_id column is an auto-inc, the DAL will automatically
 *	// provide a real ID after the insert
 *	long personIdLong = personRow.getPersonIdLong();
 *	Long personId = personRow.getPersonId();
 *
 *	// If we had any default values assigned by the database or triggers changed values, 
 *	// get the latest values from the database using the existing primary key
 *	personTable.refreshData(cc, personRow);
 *   	
 *	// Update the person record using the existing primary key automatically:
 *	personRow.setNameFirst("A new First Name");
 *	personRow.setNameMiddle("A new Middle Name");
 *	personRow.setNameLast("A new Last Name");
 *	personTable.update(cc, personRow);
 *
 *	// Update the person record using the existing primary key manually:
 *	personTable.update(cc, personRow, "person_id = ?", personId);
 *	personTable.update(cc, personRow, "person_id = ?", new Object[personId]);
 *
 *	// since we're getting the personAddress row from an existing personRow, the join 
 *	//   to the above personRow record is automatic (based on parent/child key)
 *	PersonAddressRow personAddressRow = personRow.createPersonAddressRow();
 *	personAddressRow.setAddressName("Home");
 *	personAddressRow.setLine1("123 Main Street");
 *	personAddressRow.setCity("Anywhere");
 *	personAddressRow.setState("TX");
 *	personAddressRow.setZip("12345");
 *	personAddressTable.insert(cc, personAddressRow);
 *
 *	// Now try and bring back all the addresses into memory
 *	PersonAddressRows personAddressRows = personAddressTable.getPersonAddressRowsByParentId(cc, personId);
 *
 *	// Now delete children and then the primary record
 *	personAddressTable.deletePersonAddressRowsUsingParentId(cc, personId);
 *	personTable.delete(cc, personRow);
 * 
 *	// Or, we can delete the person record using the existing primary key manually:
 *	personTable.delete(cc, personRow, "person_id = ?", personId);
 *	personTable.delete(cc, personRow, "person_id = ?", new Object[personId]);
 * </pre>
 */
public class DataAccessLayer extends AbstractSchema
{
	/** The singleton instance for this schema **/
	public final static DataAccessLayer instance = new DataAccessLayer();

	/** The Reference_Item table definition **/
	protected dal.table.ReferenceItemTable referenceItem;
	/** The Lookup_Result_Type table definition **/
	protected dal.table.LookupResultTypeTable lookupResultType;
	/** The Gender table definition **/
	protected dal.table.GenderTable gender;
	/** The Marital_Status table definition **/
	protected dal.table.MaritalStatusTable maritalStatus;

	
	public DataAccessLayer()
	{
		super();
		initializeDefn();
	}

	public void initializeDefn()
	{
		referenceItem = new dal.table.ReferenceItemTable(this);
		lookupResultType = new dal.table.LookupResultTypeTable(this);
		gender = new dal.table.GenderTable(this);
		maritalStatus = new dal.table.MaritalStatusTable(this);
	
		finalizeDefn();
	}

	/** Returns the Reference_Item table definition **/
	public dal.table.ReferenceItemTable getReferenceItemTable() { return referenceItem; }
	/** Returns the Lookup_Result_Type table definition **/
	public dal.table.LookupResultTypeTable getLookupResultTypeTable() { return lookupResultType; }
	/** Returns the Gender table definition **/
	public dal.table.GenderTable getGenderTable() { return gender; }
	/** Returns the Marital_Status table definition **/
	public dal.table.MaritalStatusTable getMaritalStatusTable() { return maritalStatus; }

}
