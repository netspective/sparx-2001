/*
 * Description: app.form.TaskDialog
 * @author ThuA
 * @created Dec 30, 2001 4:01:21 PM
 * @version
 */
package app.form;

import com.netspective.sparx.xaf.form.Dialog;
import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.security.AuthenticatedUser;
import com.netspective.sparx.xif.dal.ConnectionContext;
import com.netspective.sparx.xif.db.DatabaseContextFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.math.BigDecimal;
import java.io.Writer;

import dal.table.TaskTable;
import dal.domain.row.TaskRow;
import dal.DataAccessLayer;
import dialog.context.task.RegistrationContext;

public class TaskDialog extends Dialog
{
    public void populateValues(DialogContext dc, int i)
    {
        // make sure to call the parent method to ensure default behavior
        super.populateValues(dc, i);

        // you should almost always call dc.isInitialEntry() to ensure that you're not
        // populating data unless the user is seeing the data for the first time
        if (!dc.isInitialEntry())
            return;

        // now do the populating using DialogContext methods
        if (dc.editingData())
        {
            String taskId = dc.getRequest().getParameter("task_id");
            dc.populateValuesFromStatement("task.information", new Object[] {new Long(taskId)});
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
        if (dc.addingData())
        {
            // dialog is in the add data command mode
            this.processAddData(dc);
        }


        if (dc.editingData())
        {
            // dialog is in the edit data command mode
            this.processEditData(dc);
        }
        HttpServletRequest request = (HttpServletRequest)dc.getRequest();
        String url = "";
        if (request.getAttribute("project_id") != null)
        {
            url = request.getContextPath() + "/project/home.jsp?project_id=" + request.getAttribute("project_id");
        }
        else
        {
            url = request.getContextPath() + "/task/home.jsp?task_id=" + request.getAttribute("task_id");
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
     * Process the new updated data
     */
    protected void processEditData(DialogContext dc)
    {
        try
        {
            String task_id = dc.getRequest().getParameter("task_id");
            ConnectionContext cc =  ConnectionContext.getConnectionContext(DatabaseContextFactory.getSystemContext(),
                dc.getServletContext().getInitParameter("default-data-source"), ConnectionContext.CONNCTXTYPE_TRANSACTION);

            cc.beginTransaction();
            dialog.context.task.RegistrationContext rc = (RegistrationContext) dc;
            // update the Task table
            TaskTable taskTable = dal.DataAccessLayer.instance.getTaskTable();
            TaskRow taskRow = taskTable.getTaskByTaskId(cc, new Long(task_id));
            if (rc.getTaskDescr() != null)
                taskRow.setTaskDescr(rc.getTaskDescr());
            taskRow.setPriorityId(new Integer(rc.getPriorityId()));
            taskRow.setTaskStatus(new Integer(rc.getTaskStatus()));
            //taskRow.setTaskResolution(new Integer(rc.getTaskResolution()));
            //taskRow.setOwnerOrgId(new Long(rc.getOwnerOrgId()));
            //taskRow.setOwnerPersonId(new Long());
            taskTable.update(cc, taskRow);

            cc.endTransaction();
            dc.getRequest().setAttribute("task_id", taskRow.getTaskId());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Process the new data
     */
    protected void processAddData(DialogContext dc)
    {
        HttpSession session = dc.getSession();
        AuthenticatedUser user =
                (AuthenticatedUser) session.getAttribute(com.netspective.sparx.xaf.security.LoginDialog.DEFAULT_ATTRNAME_USERINFO);
        Map personMap = (Map) user.getAttribute("registration");
        BigDecimal personId = (BigDecimal) personMap.get("person_id");

        try
        {
            RegistrationContext rc = (RegistrationContext)dc;
            ConnectionContext cc =  rc.getConnectionContext();

            cc.beginTransaction();
            TaskTable taskTable = dal.DataAccessLayer.instance.getTaskTable();
            TaskRow taskRow = taskTable.createTaskRow();
            // this will extract the values from the dialog fields and populate
            // them into the row.
            taskRow.populateDataByNames(rc);
			// insert a row into the Task table
            taskTable.insert(cc, taskRow);
            cc.endTransaction();

            if (rc.getOwnerProjectIdRequestParam() != null)
            {
                dc.getRequest().setAttribute("project_name","");
                dc.getRequest().setAttribute("project_id", rc.getOwnerProjectIdRequestParam());
            }
            else
            {
                dc.getRequest().setAttribute("task_id", taskRow.getTaskId());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
