<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="text"/>

<xsl:param name="package-name"/>
<xsl:param name="class-name"/>

<xsl:template match="schema">
package <xsl:value-of select="$package-name"/>;

import java.io.*;
import java.util.*;

import com.netspective.sparx.xif.dal.*;

/**
 * The class that contains all of the table definitions for the Schema. This class
 * defines a static variable called &lt;code&gt;instance&lt;/code&gt; that is automatically initialized 
 * at startup. Using this instance, the developer has complete access to all of the
 * tables, columns, row types, rows types, and domains contained in the schema.
 * &lt;p&gt;
 * Here are some usage patterns (assuming there is a Person table and
 * a PersonAddress child table in the database.):
 * &lt;/p&gt;
 * &lt;pre&gt;
 * 	PersonTable personTable = <xsl:value-of select="$class-name"/>.instance.getPersonTable();
 *	PersonAddressTable personAddressTable = <xsl:value-of select="$class-name"/>.instance.getPersonAddressTable();
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
 * &lt;/pre&gt;
 */
public class <xsl:value-of select="$class-name"/> extends AbstractSchema
{
	/** The singleton instance for this schema **/
	public final static <xsl:value-of select="$class-name"/> instance = new <xsl:value-of select="$class-name"/>();

<xsl:for-each select="table">	/** The <xsl:value-of select="@name"/> table definition **/
	protected <xsl:value-of select="@_gen-table-class-name"/><xsl:text> </xsl:text><xsl:value-of select="@_gen-table-member-name"/>;
</xsl:for-each>
	
	public <xsl:value-of select="$class-name"/>()
	{
		super();
		initializeDefn();
	}

	public void initializeDefn()
	{
<xsl:for-each select="table">	
<xsl:text>		</xsl:text><xsl:value-of select="@_gen-table-member-name"/> = new <xsl:value-of select="@_gen-table-class-name"/>(this);
</xsl:for-each>	
		finalizeDefn();
	}

<xsl:for-each select="table">	/** Returns the <xsl:value-of select="@name"/> table definition **/
	public <xsl:value-of select="@_gen-table-class-name"/><xsl:text> </xsl:text>get<xsl:value-of select="@_gen-table-method-name"/>Table() { return <xsl:value-of select="@_gen-table-member-name"/>; }
</xsl:for-each>
}
</xsl:template>

</xsl:stylesheet>






