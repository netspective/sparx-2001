/*
 * Description: app.dialog.PersonDialog
 * @author ThuA
 * @created Dec 27, 2001 3:22:08 PM
 * @version
 */
package app.form;

import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.task.TaskExecuteException;
import com.netspective.sparx.xaf.sql.StatementManager;
import com.netspective.sparx.xaf.sql.StatementNotFoundException;
import com.netspective.sparx.xif.db.DatabaseContext;
import com.netspective.sparx.xif.db.DatabaseContextFactory;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Map;
import java.net.URL;
import java.net.URLEncoder;
import java.io.Writer;

public class PersonDialog   extends com.netspective.sparx.xaf.form.Dialog
{
    public static final int CONTACT_METHOD_TYPE_ADDRESS = 0;
    public static final int CONTACT_METHOD_TYPE_PHONE = 1;
    public static final int CONTACT_METHOD_TYPE_EMAIL = 3;
    public static final int CONTACT_METHOD_TYPE_URL = 4;


    public static final String CONTACT_METHOD_NAME_ADDRESS = "Physical Address";
    public static final String CONTACT_METHOD_NAME_PHONE = "Phone/Fax";
    public static final String CONTACT_METHOD_NAME_EMAIL = "E-mail";
    public static final String CONTACT_METHOD_NAME_URL = "URL";

    public void populateValues(DialogContext dc, int i)
    {
        // make sure to call the parent method to ensure default behavior
        super.populateValues(dc, i);

        // you should almost always call dc.isInitialEntry() to ensure that you're not
        // populating data unless the user is seeing the data for the first time
        if (!dc.isInitialEntry())
            return;

        // now do the populating using DialogContext methods
        if (dc.editingData() || dc.deletingData())
        {
            String personId = dc.getRequest().getParameter("person_id");
            dc.populateValuesFromStatement("person.information", new Object[] {personId});
            dc.populateValuesFromStatement("person.active-org-memberships", new Object[] {personId});
            dc.populateValuesFromStatement("person.address-by-id", new Object[] {personId});
        }
    }

    public void makeStateChanges(DialogContext dc, int stage)
    {
        // make sure to call the parent method to ensure default behavior
        super.makeStateChanges(dc, stage);
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
    public void execute(Writer writer, DialogContext dc)
    {
        // if you call super.execute(dc) then you would execute the <execute-tasks> in the XML; leave it out
        // to override
        // super.execute(dc);
        String person_id = "";
        String person_name = "";
        String url = "";
        HttpServletRequest request = (HttpServletRequest)dc.getRequest();

        if (dc.addingData())
        {
            // dialog is in the add data command mode
            this.processAddAction(dc);
            person_id = (String)request.getAttribute("person_id");
            person_name = (String) request.getAttribute("person_name");
            // if an error occurred, return user to the contact report
            if (person_id == null)
                url = request.getContextPath() + "/contact/index.jsp?_d_exec=1";
            else
                url = request.getContextPath() + "/contact/home.jsp?person_id=" +  person_id +
                "&person_name=" + URLEncoder.encode(person_name);

        }

        if (dc.editingData())
        {
            // dialog is in the edit data command mode
            this.processEditAction(dc);
            person_id = request.getParameter("person_id");
            person_name = (String)request.getParameter("person_name");
            url = request.getContextPath() + "/contact/home.jsp?person_id=" +  person_id +
                "&person_name=" + URLEncoder.encode(person_name);

        }

        if (dc.deletingData())
        {
            // dialog is in the delete data command mode
            this.processDeleteAction(dc);
            url = request.getContextPath() + "/contact/index.jsp?_d_exec=1";

        }

        try
        {
            ((HttpServletResponse)dc.getResponse()).sendRedirect(url);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Process the delete action
     */
    protected void processDeleteAction(DialogContext dc)
    {
        try
        {
            dc.beginSqlTransaction();
            StatementManager sm = dc.getStatementManager();
            DatabaseContext dbContext = DatabaseContextFactory.getContext(dc.getRequest(), dc.getServletContext());

            String whereExpr = "parent_id = ?";
            dc.executeSqlRemove("personorg_relationship", null, null, whereExpr, "request:person_id");
            dc.executeSqlRemove("person_address", null, null, whereExpr, "request:person_id");
            dc.executeSqlRemove("person_contact", null, null, whereExpr, "request:person_id");

            // create the WHERE clause for the delete SQL statement
            whereExpr = "person_id = ?";
            dc.executeSqlRemove("person", null, null, whereExpr, "request:person_id");
            dc.commitSqlTransaction();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Process the update action
     */
	protected void processEditAction(DialogContext dc)
	{
		try
		{
            //String personId = (String)dc.getRequest().getParameter("person_id");
            // begin the transaction
            dc.beginSqlTransaction();

            StatementManager sm = dc.getStatementManager();
            DatabaseContext dbContext = DatabaseContextFactory.getContext(dc.getRequest(), dc.getServletContext());
            // fields represent a mapping for dialog fields to database column names
            String fields = "name_last,name_first,name_middle,name_prefix," +
                    "ssn,gender,date_of_birth";
            String complete_name = dc.getValue("name_first") + " " + dc.getValue("name_last");
            // columns represent a mapping of database column names to literal values?
            String columns = "complete_name=custom-sql:'" + complete_name + "'";
            // create the WHERE clause for the update SQL statement
            String whereExpr = "person_id = ?";
            dc.executeSqlUpdate("person", fields, columns, whereExpr, "request:person_id");

            // process address information
            // 1. Check to see if the entry already exists
            Map result = sm.executeStmtGetValuesMap(dbContext, dc, null, "person.address-by-id", new Object[]{new Long(dc.getRequest().getParameter("person_id"))});
            if (result != null)
            {
                fields = "line1,line2,city,state,zip,country";
                columns = "";
                // create the WHERE clause for the update SQL statement
                whereExpr = "parent_id = ?";
                dc.executeSqlUpdate("person_address", fields, columns, whereExpr, "request:person_id");
            }
            else
            {
                fields = "line1,line2,city,state,zip,country";
                columns = "parent_id=request:person_id,cr_stamp=custom-sql:sysdate";
                String autoinc = "system_id,PerAddr_system_id_SEQ";
                String autoincStore = "request-attr:new_address_id";
                dc.executeSqlInsert("person_address", fields, columns, autoinc, autoincStore);
            }
            dc.commitSqlTransaction();

		}
		catch (Exception e)
		{
            e.printStackTrace();
		}
	}

    /**
     * Process the new data
     */
    protected void processAddAction(DialogContext dc)
    {
        try
        {
            String cr_person_id = (String)dc.getRequest().getAttribute("personId");
            // begin the transaction
            dc.beginSqlTransaction();

            StatementManager sm = dc.getStatementManager();
            DatabaseContext dbContext = DatabaseContextFactory.getContext(dc.getRequest(), dc.getServletContext());

            String fullName = dc.getValue("name_first") + " " + dc.getValue("name_middle") + " " + dc.getValue("name_last");

            // fields represent a mapping for dialog fields to database column names
            String fields = "name_last,name_first,name_middle,name_prefix," +
                    "ssn,gender";
            // columns represent a mapping of database column names to literal values?
            String columns = "cr_stamp=custom-sql:sysdate,complete_name=custom-sql:'" + fullName + "'";
            String autoinc = "person_id,per_person_id_seq";
            String autoincStore = "request-attr:new_person_id";
            dc.executeSqlInsert("person", fields, columns, autoinc, autoincStore);
            BigDecimal person_id = (BigDecimal)dc.getRequest().getAttribute("new_person_id");

            // associate this new contact with an organization
            fields = "organization=rel_org_id,organization_relation=rel_type";
            columns = "cr_stamp=custom-sql:sysdate,cr_person_id=custom-sql:" + cr_person_id + ",parent_id=custom-sql:" + person_id +
                ",rel_begin=custom-sql:sysdate,record_status_id=custom-sql:1" ;
            autoinc = "system_id,PerRel_system_id_seq";
            autoincStore = "request-attr:new_personorg_rel_id";
            dc.executeSqlInsert("personorg_relationship", fields, columns, autoinc, autoincStore);


            // process address information
            fields = "line1,line2,city,state,zip,country";
            columns = "parent_id=custom-sql:" + person_id +
                    ",cr_stamp=custom-sql:sysdate";
            autoinc = "system_id,PerAddr_system_id_SEQ";
            autoincStore = "request-attr:new_address_id";
            dc.executeSqlInsert("person_address", fields, columns, autoinc, autoincStore);



            // process email information
            if (dc.getValue("email") != null && dc.getValue("email").length() > 0)
            {
                fields = "email=method_value,organization=cr_org_id";
                columns = "method_name=custom-sql:'" + CONTACT_METHOD_NAME_EMAIL + "',method_type=custom-sql:" +
                        CONTACT_METHOD_TYPE_EMAIL + ",parent_id=custom-sql:" + person_id + ",cr_stamp=custom-sql:sysdate";
                autoinc = "system_id,PerCont_system_id_SEQ";
                autoincStore = "request-attr:new_contact_id";
                dc.executeSqlInsert("person_contact", fields, columns, autoinc, autoincStore);
            }
            if (dc.getValue("url") != null && dc.getValue("url").length() > 0)
            {
                // process url information
                fields = "url=method_value,organization=cr_org_id";
                columns = "method_name=custom-sql:'" + CONTACT_METHOD_NAME_URL +  "',method_type=custom-sql:" +
                        CONTACT_METHOD_TYPE_URL + ",parent_id=custom-sql:" + person_id + ",cr_stamp=custom-sql:sysdate";
                autoinc = "system_id,PerCont_system_id_SEQ";
                autoincStore = "request-attr:new_contact_id";
                dc.executeSqlInsert("person_contact", fields, columns, autoinc, autoincStore);
            }
            if (dc.getValue("phone") != null && dc.getValue("phone").length() > 0)
            {
                // process url information
                fields = "phone=method_value,organization=cr_org_id";
                columns = "method_name=custom-sql:'" + CONTACT_METHOD_NAME_PHONE + "',method_type=custom-sql:" +
                        CONTACT_METHOD_TYPE_PHONE + ",parent_id=custom-sql:" + person_id + ",cr_stamp=custom-sql:sysdate";
                autoinc = "system_id,PerCont_system_id_SEQ";
                autoincStore = "request-attr:new_contact_id";
                dc.executeSqlInsert("person_contact", fields, columns, autoinc, autoincStore);
            }
            // end transaction
            dc.commitSqlTransaction();
            dc.getRequest().setAttribute("person_id", person_id.toString());
            dc.getRequest().setAttribute("person_name", fullName);
        }
        catch (TaskExecuteException tee)
        {
            tee.printStackTrace();
            try
            {
                System.out.println("calling rollback!!");
                dc.rollbackSqlTransaction();
            }
            catch (TaskExecuteException e)
            {
                e.printStackTrace();
            }
        }
    }
}
