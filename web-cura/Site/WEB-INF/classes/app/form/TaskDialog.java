/*
 * Description: app.form.TaskDialog
 * @author ThuA
 * @created Dec 30, 2001 4:01:21 PM
 * @version 
 */
package app.form;

import com.xaf.form.Dialog;
import com.xaf.form.DialogContext;
import com.xaf.security.AuthenticatedUser;
import com.xaf.db.ConnectionContext;
import com.xaf.db.DatabaseContextFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.math.BigDecimal;

import dal.table.TaskTable;
import dal.domain.row.TaskRow;

public class TaskDialog extends Dialog
{
    public void populateValues(DialogContext dc, int i)
    {
        // make sure to call the parent method to ensure default behavior
        super.populateValues(dc, i);

        // you should almost always call dc.isInitialEntry() to ensure that you're not
        // populating data unless the user is seeing the data for the first time
        //if (!dc.isInitialEntry())
        //    return;

        // now do the populating using DialogContext methods
        //if (dc.editingData())
        //{
            //String personId = dc.getRequest().getParameter("project_id");
            //dc.populateValuesFromStatement("person.information", new Object[] {personId});
            //dc.populateValuesFromStatement("person.address-by-id", new Object[] {personId});
        //}
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
    public String execute(DialogContext dc)
    {
        // if you call super.execute(dc) then you would execute the <execute-tasks> in the XML; leave it out
        // to override
        // super.execute(dc);
        if (dc.addingData())
        {
            // dialog is in the add data command mode
            this.processAddData(dc);
            HttpServletRequest request = (HttpServletRequest)dc.getRequest();
            String url = request.getContextPath() + "/task/home.jsp?task_id=" + request.getAttribute("task_id");
            try
            {
                ((HttpServletResponse)dc.getResponse()).sendRedirect(url);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return "Failed to create response URL.";
            }
        }

        /*
        if (dc.editingData())
        {
            // dialog is in the edit data command mode
        }
        */
        return "";
    }
    /**
     * Process the new data
     */
    protected void processAddData(DialogContext dc)
    {
        HttpSession session = dc.getSession();
        AuthenticatedUser user =
                (AuthenticatedUser) session.getAttribute(com.xaf.security.LoginDialog.DEFAULT_ATTRNAME_USERINFO);
        Map personMap = (Map) user.getAttribute("registration");
        BigDecimal personId = (BigDecimal) personMap.get("person_id");

        try
        {
            ConnectionContext cc =  ConnectionContext.getConnectionContext(DatabaseContextFactory.getSystemContext(),
                dc.getServletContext().getInitParameter("default-data-source"), ConnectionContext.CONNCTXTYPE_TRANSACTION);

            cc.beginTransaction();
            TaskTable taskTable = dal.DataAccessLayer.instance.getTaskTable();
            TaskRow taskRow = taskTable.createTaskRow();
            taskRow.populateDataByNames(dc);

            taskTable.insert(cc, taskRow);
            cc.endTransaction();
            dc.getRequest().setAttribute("project_id", taskRow.getTaskId());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("Done");
    }
}
