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
import javax.naming.NamingException;
import java.util.Map;
import java.math.BigDecimal;
import java.io.Writer;
import java.sql.SQLException;
import java.net.URLEncoder;

import dal.table.TaskTable;
import dal.table.TaskPersonRelationTable;
import dal.table.TaskTypeTable;
import dal.table.TaskDependencyTable;
import dal.domain.row.TaskRow;
import dal.domain.row.TaskDependencyRow;
import dal.domain.TaskPersonRelation;
import dal.domain.TaskType;
import dal.domain.rows.TaskDependencyRows;
import dal.DataAccessLayer;
import dialog.context.task.RegistrationContext;
import app.TaskHandler;

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
        if (dc.deletingData())
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
        boolean isValid = super.isValid(dc);

        if (isValid)
        {
            if (dc.deletingData())
            {
                try
                {
                    ConnectionContext cc = dc.getConnectionContext();
                    TaskHandler taskHandler = new TaskHandler();
                    isValid = taskHandler.checkDeleteStatus(cc, Long.parseLong(dc.getRequest().getParameter("task_id")));
                    if (!isValid)
                        dc.addErrorMessage(taskHandler.getErrMsg());
                    cc.returnConnection();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        return isValid;
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
        try
        {
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
            if (dc.deletingData())
            {
                // dialog is in the edit data command mode
                this.processDeleteData(dc);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        HttpServletRequest request = (HttpServletRequest)dc.getRequest();
        String url = "";
        if (request.getParameter("project_id") != null && request.getParameter("project_name") != null)
        {
            url = request.getContextPath() + "/project/home.jsp?project_id=" + request.getParameter("project_id") +
                "&project_name=" + URLEncoder.encode(request.getParameter("project_name"));
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
     * Delete the task
     */
    protected void processDeleteData(DialogContext dc) throws SQLException, NamingException
    {
        dialog.context.task.RegistrationContext rc = (RegistrationContext) dc;
        ConnectionContext cc = dc.getConnectionContext();
        cc.beginTransaction();

        TaskHandler task = new TaskHandler();
        task.deleteTask(cc, rc.getTaskId());
        cc.commitTransaction();
    }

    /**
     * Process the new updated data
     */
    protected void processEditData(DialogContext dc) throws SQLException, NamingException
    {
        String task_id = dc.getRequest().getParameter("task_id");
        ConnectionContext cc =  dc.getConnectionContext();

        cc.beginTransaction();
        dialog.context.task.RegistrationContext rc = (RegistrationContext) dc;
        // update the Task table
        TaskTable taskTable = dal.DataAccessLayer.instance.getTaskTable();
        TaskRow taskRow = taskTable.getTaskByTaskId(cc, new Long(task_id));
        taskRow.populateDataByNames(dc);
        taskTable.update(cc, taskRow);

        cc.commitTransaction();
        dc.getRequest().setAttribute("task_id", taskRow.getTaskId());
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
            Long taskId = taskRow.getTaskId();


            if (rc.getDependentTask() != null && rc.getDependentTask().length() > 0)
            {
                // include entries into the task dependency table
                TaskDependencyTable tdTable = dal.DataAccessLayer.instance.getTaskDependencyTable();
                TaskDependencyRow tdRow = tdTable.createTaskDependencyRow();
                tdRow.setCrOrgId(rc.getCrOrgId());
                tdRow.setCrPersonId(rc.getCrPersonId());
                tdRow.setCrStampSqlExpr("sysdate");
                tdRow.setParentId(rc.getDependentTaskInt());
                tdRow.setDependentId( taskRow.getTaskId());
                tdTable.insert(cc, tdRow);
            }
            cc.commitTransaction();
            if (rc.getOwnerProjectIdRequestParam() != null)
            {
                dc.getRequest().setAttribute("project_name",rc.getValue("project_name"));
                dc.getRequest().setAttribute("project_id", rc.getOwnerProjectIdRequestParam());
            }
            dc.getRequest().setAttribute("task_id", taskId);


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
