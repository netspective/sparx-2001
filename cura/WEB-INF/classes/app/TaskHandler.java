/*
 * Class: app.TaskHandler
 * Created on Jan 30, 2002 12:51:55 AM
 * @author Aye Thu
 * @version
 */
package app;

import com.netspective.sparx.xif.dal.ConnectionContext;
import app.form.context.task.RegistrationContext;

import javax.naming.NamingException;
import java.sql.SQLException;

import app.dal.table.TaskTable;
import app.dal.table.TaskDependencyTable;
import app.dal.table.TaskPersonRelationTable;
import app.dal.DataAccessLayer;
import app.dal.domain.row.TaskRow;
import app.dal.domain.row.TaskDependencyRow;
import app.dal.domain.rows.TaskDependencyRows;

public class TaskHandler
{
    private String errMsg;

    public String getErrMsg()
    {
        return errMsg;
    }

    /**
     * Delete task and its related objects
     *
     * @param cc ConnectionContext object
     * @param taskId Task ID
     * @exception NamingException
     * @exception SQLException
     */
    public void deleteTask(ConnectionContext cc, long taskId) throws NamingException, SQLException
     {
         TaskTable taskTable = DataAccessLayer.instance.getTaskTable();
         // retrieve the task row
         TaskRow taskRow = taskTable.getTaskByTaskId(cc, taskId);
         if (taskRow != null)
         {
             // remove this task from the task dependency list
             TaskDependencyTable tdTable = DataAccessLayer.instance.getTaskDependencyTable();
             //TaskDependencyRows tdRows = tdTable.getTaskDependencyRowsByDependentId(cc, rc.getTaskId());
             tdTable.deleteTaskDependencyRowsUsingDependentId(cc, taskId);


             if (taskRow.getParentTaskId() != null)
             {
                 // Delete all the sub tasks belonging to this task

             }

             // remove person/task relationships
             TaskPersonRelationTable tpRelationTable = DataAccessLayer.instance.getTaskPersonRelationTable();
             tpRelationTable.deleteTaskPersonRelationRowsUsingParentId(cc, new Long(taskId));

             // delete the task row
             taskTable.delete(cc, taskRow);
         }
     }

    public boolean checkDeleteStatus(ConnectionContext cc, long taskId) throws NamingException, SQLException
    {
        // check to see if there are tasks dependent on this task. If so, don't allow the deletion
        // until the relationship have been cleared
        TaskDependencyTable tdTable = DataAccessLayer.instance.getTaskDependencyTable();
        TaskDependencyRows tdRows = tdTable.getTaskDependencyRowsByParentId(cc, taskId);
        if (tdRows != null && tdRows.size() != 0)
        {
            // there are tasks that are dependent on this task!
            StringBuffer errMsgBuffer = new StringBuffer("The following tasks are dependent on Task " + taskId +
                    ". Remove those relationships before removing this task.<br><ul>");
            for (int i=0; i < tdRows.size(); i++)
            {
                errMsgBuffer.append("<li>ID: " + ((TaskDependencyRow) tdRows.get(i)).getDependentId() + "</li>");
            }
            errMsgBuffer.append("</ul>");
            this.errMsg = errMsgBuffer.toString();
            return false;
        }
        return true;
    }

}
