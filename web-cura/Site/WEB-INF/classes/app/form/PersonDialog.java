/*
 * Description: app.dialog.PersonDialog
 * @author ThuA
 * @created Dec 27, 2001 3:22:08 PM
 * @version 
 */
package app.form;

import com.xaf.form.DialogContext;
import com.xaf.task.TaskExecuteException;
import com.xaf.sql.StatementManager;
import com.xaf.sql.StatementNotFoundException;
import com.xaf.db.DatabaseContext;
import com.xaf.db.DatabaseContextFactory;

import javax.naming.NamingException;
import java.math.BigDecimal;
import java.sql.SQLException;

public class PersonDialog   extends com.xaf.form.Dialog
{
    public void populateValues(DialogContext dc, int i)
    {
        // make sure to call the parent method to ensure default behavior
        super.populateValues(dc, i);
    }

    public void makeStateChanges(DialogContext dc, int i)
    {
        // make sure to call the parent method to ensure default behavior
        super.makeStateChanges(dc, i);
    }

    /**
     * This is the class that you do your entire dialog validation with
     */
    public boolean isValid(DialogContext dc)
    {
        return super.isValid(dc);
    }

    /**
     *  This is where you perform all your actions. Whatever you return as the function result will be shown
     * in the HTML
     */
    public String execute(DialogContext dc)
    {
        // if you call super.execute(dc) then you would execute the <execute-tasks> in the XML; leave it out
        // to override
        // super.execute(dc);
        if (dc.addingData())
        {
            // dialog is in the add data command mode
            this.processAddData(dc);

        }

        if (dc.editingData())
        {
            // dialog is in the edit data command mode
        }

        return "";
    }

    /**
     * Process the new data
     */
    protected void processAddData(DialogContext dc)
    {
        // these field names must match the ones defined in the XML
        /*
        String last_name = dc.getValue("last_name");
        String first_name = dc.getValue("first_name");
        String middle_name = dc.getValue("middle_name");
        String prefix = dc.getValue("prefix");
        String ssn = dc.getValue("ssn");
        String dob = dc.getValue("dob");
        String org_id = dc.getValue("org");
        */
        try
        {
            // begin the transaction
            dc.beginSqlTransaction();

            StatementManager sm = dc.getStatementManager();
            DatabaseContext dbContext = DatabaseContextFactory.getContext(dc.getRequest(), dc.getServletContext());
            BigDecimal person_id = (BigDecimal) sm.executeStmtGetValue(dbContext, dc, null, "person.next-id", new Object[0]);

            // fields represent a mapping for dialog fields to database column names
            String fields = "last_name=name_last,first_name=name_first,middle_name=name_middle,prefix=name_prefix," +
                    "ssn,gender,organization=cr_org_id";
            // columns represent a mapping of database column names to literal values?
            String columns = "person_id=custom-sql:" + person_id + ",cr_stamp=custom-sql:sysdate";
            dc.executeSqlInsert("person", fields, columns);

            String method = dc.getValue("method");
            if (method != null && method.length() > 0)
            {
                // process the contact information if exists
                int methodId = Integer.parseInt(method);

                if (methodId == dal.table.ContactMethodTypeTable.EnumeratedItem.PHYSICAL_ADDRESS.getId())
                {
                    // process address information
                    BigDecimal address_id = (BigDecimal) sm.executeStmtGetValue(dbContext, dc, null, "person.next-address-id", new Object[0]);
                    fields = "address1=line1,address2=line2,city,state,zip,country";
                    columns = "system_id=custom-sql:" + address_id + ",parent_id=custom-sql:" + person_id + ",cr_stamp=custom-sql:sysdate";;
                    dc.executeSqlInsert("person_address", fields, columns);
                }
                else if (methodId == dal.table.ContactMethodTypeTable.EnumeratedItem.E_MAIL.getId())
                {
                    // process email information
                    BigDecimal contact_id = (BigDecimal) sm.executeStmtGetValue(dbContext, dc, null, "person.next-contact-id", new Object[0]);
                    fields = "method=method_type,email=method_value,organization=cr_org_id";
                    columns = "system_id=custom-sql:" + contact_id + ",parent_id=custom-sql:" + person_id + ",cr_stamp=custom-sql:sysdate";;
                    dc.executeSqlInsert("person_contact", fields, columns);
                }
                else if (methodId == dal.table.ContactMethodTypeTable.EnumeratedItem.URL.getId())
                {
                    // process url information
                    BigDecimal contact_id = (BigDecimal) sm.executeStmtGetValue(dbContext, dc, null, "person.next-contact-id", new Object[0]);
                    fields = "method=method_type,url=method_value,organization=cr_org_id";
                    columns = "system_id=custom-sql:" + contact_id + ",parent_id=custom-sql:" + person_id + ",cr_stamp=custom-sql:sysdate";;
                    dc.executeSqlInsert("person_contact", fields, columns);
                }

            }
            // end transaction
            dc.endSqlTransaction();
        }
        catch (TaskExecuteException tee)
        {
            tee.printStackTrace();
        }
        catch (StatementNotFoundException se)
        {
            se.printStackTrace();
        }
        catch (NamingException ne)
        {
            ne.printStackTrace();
        }
        catch (SQLException sqle)
        {
            sqle.printStackTrace();
        }
    }
}
