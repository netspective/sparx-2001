/*
 * Created by IntelliJ IDEA.
 * User: Aye Thu
 * Date: Jan 20, 2002
 * Time: 3:35:52 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package app.form;

import com.netspective.sparx.xaf.form.Dialog;
import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xif.dal.ConnectionContext;
import com.netspective.sparx.xif.db.DatabaseContextFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dal.table.ProjectOrgRelationTable;
import dal.table.ProjectTable;
import dal.table.TaskTable;
import dal.domain.row.ProjectRow;
import dal.domain.row.TaskRow;

import java.io.Writer;
import java.net.URLEncoder;

public class DeleteTaskDialog extends Dialog
{
    /**
     *  This is where you perform all your actions. Whatever you return as the function result will be shown
     * in the HTML
     */
    public void execute(Writer writer, DialogContext dc)
    {
        // if you call super.execute(dc) then you would execute the <execute-tasks> in the XML; leave it out
        // to override
        // super.execute(dc);

        this.processExecution(dc);

        HttpServletRequest request = (HttpServletRequest)dc.getRequest();
        String url = "";
        if (request.getParameter("project_id") != null)
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
     * Execute the dialog actions
     *
     * @param dc DialogContext
     */
    protected void processExecution(DialogContext dc)
    {
        try
        {
            ConnectionContext cc =  dc.getConnectionContext();

            cc.beginTransaction();
            // the dialog's context is represented by its own custom bean class
            dialog.context.project.DeleteTaskContext rc = (dialog.context.project.DeleteTaskContext) dc;

            // check to see which task ids were selected for removal
            String[] taskList = rc.getRequest().getParameterValues("_dc.task_id.checkbox");
            TaskTable taskTable = dal.DataAccessLayer.instance.getTaskTable();
            for (int i=0; i < taskList.length; i++)
            {
                TaskRow taskRow = taskTable.getTaskByTaskId(cc, new Long(taskList[i]));
                if (taskRow != null)
                    taskTable.delete(cc, taskRow);
            }
            cc.endTransaction();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
