/*
 * Created by IntelliJ IDEA.
 * User: Aye Thu
 * Date: Jan 18, 2002
 * Time: 12:08:39 AM
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

import dialog.context.task.RegistrationContext;
import dal.table.TaskTable;
import dal.table.TaskPersonRelationTable;
import dal.domain.row.TaskRow;
import dal.domain.row.TaskPersonRelationRow;
import dal.domain.row.ProjectPersonRelationRow;
import dal.domain.rows.TaskPersonRelationRows;

import java.io.Writer;

public class TaskMemberDialog   extends Dialog
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

        if (dc.editingData())
        {
            // dialog is in the edit data command mode
            this.processEditData(dc);
        }
        HttpServletRequest request = (HttpServletRequest)dc.getRequest();
        String url = request.getContextPath() + "/task/home.jsp?project_id=" + dc.getValue("project_id")+
                "&task_id=" + dc.getValue("task_id");
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
            ConnectionContext cc =  dc.getConnectionContext();
            String[] personList = (String[])dc.getValues("rel_person_id");

            cc.beginTransaction();
            dialog.context.task.PersonRegistrationContext rc = (dialog.context.task.PersonRegistrationContext) dc;
            // update the Task Person Relationship table
            TaskPersonRelationTable tprTable = dal.DataAccessLayer.instance.getTaskPersonRelationTable();
            TaskPersonRelationRows tprRows = tprTable.getTaskPersonRelationRowsByParentId(cc, new Long(task_id));
            if (tprRows == null || tprRows.size() == 0)
            {
                // no relations exist. Incoming data is all new
                for (int i=0; i< personList.length; i++)
                {
                    TaskPersonRelationRow tpr = tprTable.createTaskPersonRelationRow();
                    tpr.setCrOrgId(new Long(dc.getValue("cr_org_id")));
                    tpr.setCrPersonId(new Long(dc.getValue("cr_person_id")));
                    tpr.setCrStampSqlExpr("sysdate");
                    tpr.setParentId(new Long(dc.getValue("task_id")));
                    tpr.setRelPersonId(new Long(personList[i]));
                    tpr.setRelBeginSqlExpr("sysdate");
                    tpr.setRelType(dal.table.TaskPersonRelationTypeTable.EnumeratedItem.MEMBER);
                    tprTable.insert(cc, tpr);
                }
            }
            else
            {
                // relations already exist (NOTE: Need to improve this code to be more efficient)
                // First step: delete all rows that no longer exists
                for (int i=0; i < tprRows.size(); i++)
                {
                    boolean deleteRow = true;
                    TaskPersonRelationRow tprRow = tprRows.getTaskPersonRelationRow(i);
                    for (int j=0; j < personList.length; j++)
                    {
                        if (tprRow.getRelPersonId().toString().equals(personList[j]))
                        {
                            // record already exists. No need to do anything
                            deleteRow = false;
                            break;
                        }
                    }
                    if (deleteRow)
                    {
                        // maybe instead of deleting, just set the relationship end date?
                        tprTable.delete(cc, tprRow);
                    }
                }
                // Second step: insert all new rows
                for (int i=0; i < personList.length; i++)
                {
                    boolean addRow = true;
                    for (int j=0; j < tprRows.size(); j++)
                    {

                        TaskPersonRelationRow tprRow = tprRows.getTaskPersonRelationRow(j);
                        if (personList[i].equals(tprRow.getRelPersonId().toString()))
                        {
                            // record exists
                            addRow = false;
                            break;
                        }
                    }
                    if (addRow)
                    {
                        TaskPersonRelationRow tpr = tprTable.createTaskPersonRelationRow();
                        tpr.setCrOrgId(new Long(dc.getValue("cr_org_id")));
                        tpr.setCrPersonId(new Long(dc.getValue("cr_person_id")));
                        tpr.setCrStampSqlExpr("sysdate");
                        tpr.setParentId(new Long(dc.getValue("project_id")));
                        tpr.setRelPersonId(new Long(personList[i]));
                        tpr.setRelBeginSqlExpr("sysdate");
                        tpr.setRelType(dal.table.TaskPersonRelationTypeTable.EnumeratedItem.MEMBER);
                        tprTable.insert(cc, tpr);
                    }
                }

            }

            cc.commitTransaction();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
