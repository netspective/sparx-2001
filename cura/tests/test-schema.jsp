<jsp:directive.page import="com.xaf.db.*,dal.*,dal.domain.*,dal.domain.row.*,dal.domain.rows.*,dal.table.*"/>

<pre>
<% 
	String dataSourceId = "jdbc/cura";
	PersonTable personTable = DataAccessLayer.instance.getPersonTable();
	PersonAddressTable personAddressTable = DataAccessLayer.instance.getPersonAddressTable();
	ConnectionContext cc = ConnectionContext.getConnectionContext(
		DatabaseContextFactory.getSystemContext(), dataSourceId, 
		ConnectionContext.CONNCTXTYPE_AUTO);
%>

** Create a new person record and insert it into the database:
<%
	PersonRow personRow = personTable.createPersonRow();
	personRow.setNameFirst("Shawhid");
	personRow.setNameLast("Shaw");
	personTable.insert(cc, personRow);
	
	// the person_id column is an auto-inc so we'll automatically
	// get a real ID after the insert
	long personId = personRow.getPersonIdLong();
	
	/* since we're getting the personAddress row from an existing personRow, the join 
	   to the above personRow record is automatic (based on parent/child key) */
	PersonAddressRow personAddressRow = personRow.createPersonAddressRow();
	personAddressRow.setAddressName("Home");
	personAddressRow.setLine1("6 Castle Cliff Court");
	personAddressRow.setCity("Silver Spring");
	personAddressRow.setState("MD");
	personAddressRow.setZip("20904");
	personAddressTable.insert(cc, personAddressRow);
%>
<%= personRow %>
<%= personAddressRow %>

** Now that we've inserted the record, just refresh and see what was really written
   (this uses the table class' getRecordByPrimaryKey method internally)
<% 
	personTable.refreshData(cc, personRow);
	personAddressTable.refreshData(cc, personAddressRow);
%>
<%= personRow %>
<%= personAddressRow %>

** Now modify the same record and save it back to the database (using the existing primary key in the Row)
<%
	personRow.setNameFirst("Shahid");
	personRow.setNameMiddle("Nehal");
	personRow.setNameLast("Shah");
	personTable.update(cc, personRow);
%>
<%= personRow %>

** Now that we've updated the record, just refresh and see what was really written
   (again, this uses the table class' getRecordByPrimaryKey method internally)
<% 
	personTable.refreshData(cc, personRow);
%>
<%= personRow %>

** Now try and bring back all the addresses into memory
<% 
	PersonAddressRows personAddressRows = personAddressTable.getPersonAddressRowsByParentId(cc, personId);
%>
<%= personAddressRows == null ? "No address records" : (personAddressRows.size() + " address records found") %>

** Now delete children and then the primary record
<% 
	personAddressTable.deletePersonAddressRowsUsingParentId(cc, personId);
	personTable.delete(cc, personRow);
%>
** Now try and find the same record (it should be NULL)
<%
	personRow = personTable.getPersonByPersonId(cc, personId);
%>
<%= personRow %>
</pre>
<% 	cc.endTransaction(); %>
