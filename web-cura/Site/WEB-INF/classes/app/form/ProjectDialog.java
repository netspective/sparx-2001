/*
 * Description: app.form.ProjectDialog
 * @author ThuA
 * @created Dec 29, 2001 3:30:12 PM
 * @version
 */
package app.form;

import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.form.Dialog;
import com.netspective.sparx.xaf.security.AuthenticatedUser;
import com.netspective.sparx.xif.dal.ConnectionContext;
import com.netspective.sparx.xif.db.DatabaseContextFactory;
import dal.domain.Project;
import dal.domain.rows.ProjectOrgRelationRows;
import dal.domain.rows.TaskRows;
import dal.domain.row.ProjectRow;
import dal.domain.row.ProjectOrgRelationRow;
import dal.domain.row.TaskRow;
import dal.table.ProjectTable;
import dal.table.RecordStatusTable;
import dal.table.ProjectOrgRelationTable;
import dal.table.TaskTable;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Iterator;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.io.Writer;

public class ProjectDialog extends Dialog
{
    public static final int MAINPROJECT_TYPE = 1;
    public static final int SUBPROJECT_TYPE = 2;

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
            String projectId = dc.getRequest().getParameter("project_id");
            dc.populateValuesFromStatement("project.information", new Object[] {new Long(projectId)});
            //dc.populateValuesFromStatement("person.address-by-id", new Object[] {personId});
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

        if (dc.deletingData())
        {
            // dialog is in the delete data command mode
            this.processDeleteData(dc);
        }

        HttpServletRequest request = (HttpServletRequest)dc.getRequest();
        String url = "";
        if (request.getParameter("org_id") != null)
        {
            url = request.getContextPath() + "/account/home.jsp?org_id=" + request.getParameter("org_id") +
                "&org_name=" + request.getParameter("org_name");
        }
        else
        {
            url = request.getContextPath() + "/project/home.jsp?project_id=" + request.getAttribute("project_id") +
                "&project_name=" + request.getAttribute("project_name");
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
    protected void processDeleteData(DialogContext dc)
    {
        try
        {
            ConnectionContext cc =  ConnectionContext.getConnectionContext(DatabaseContextFactory.getSystemContext(),
                 dc.getServletContext().getInitParameter("default-data-source"), ConnectionContext.CONNCTXTYPE_TRANSACTION);

            cc.beginTransaction();
            // the dialog's context is represented by its own custom bean class
            dialog.context.project.RegistrationContext rc = (dialog.context.project.RegistrationContext) dc;

            // remove all the relationships assigned to this project
            ProjectOrgRelationTable projRelTable = dal.DataAccessLayer.instance.getProjectOrgRelationTable();
            projRelTable.deleteProjectOrgRelationRowsUsingParentId(cc, rc.getProjectId());

            ProjectTable projectTable = dal.DataAccessLayer.instance.getProjectTable();
            ProjectRow projectRow = projectTable.getProjectByProjectId(cc, rc.getProjectId());

            // remove all tasks assigned to this project
            dal.table.TaskTable taskTable = dal.DataAccessLayer.instance.getTaskTable();
            TaskRows taskRows = taskTable.getTaskRowsByOwnerProjectId(cc, rc.getProjectId());
            if (taskRows != null && taskRows.size() > 0)
            {
                taskTable.deleteTaskRowsUsingOwnerProjectId(cc, projectRow.getProjectId());
            }

            // delete the row in the project table
            projectTable.delete(cc, projectRow);
            cc.commitTransaction();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Process the edit action
     */
    protected void processEditData(DialogContext dc)
    {
        ConnectionContext cc = null;
        try
        {
            cc =  dc.getConnectionContext();
            cc.beginTransaction();

            // the dialog's context is represented by its own custom bean class
            dialog.context.project.RegistrationContext rc = (dialog.context.project.RegistrationContext) dc;

            ProjectTable projectTable = dal.DataAccessLayer.instance.getProjectTable();
            ProjectRow projectRow = projectTable.getProjectByProjectId(cc, rc.getProjectId());
            projectRow.setProjectName(rc.getProjectName());
            projectRow.setProjectDescr(rc.getProjectDescr());
            projectRow.setProjectStatus(rc.getProjectStatusInt());
            if (rc.getActualEndDate() != null)
                projectRow.setActualEndDate(new java.sql.Date(rc.getActualEndDate().getTime()));

            projectTable.update(cc, projectRow);

			// every project added needs a 'owner' organization
            ProjectOrgRelationTable projRelTable = dal.DataAccessLayer.instance.getProjectOrgRelationTable();
            ProjectOrgRelationRows projRelRows =  projectRow.getProjectOrgRelationRows(cc);

            Iterator list = projRelRows.listIterator();
            while (list.hasNext())
            {
                ProjectOrgRelationRow projRelRow = (ProjectOrgRelationRow)list.next();
                // NOTE: relationships have not been defined yet. Using 1 as a dummy
                if (projRelRow.getRelType().intValue() == 1)
                {
                    //projRelRow.setNotifyEmail(rc.getNotifyEmail());
                    projRelTable.update(cc, projRelRow);
                }
            }


		    cc.commitTransaction();
            dc.getRequest().setAttribute("project_id", projectRow.getProjectId());
            dc.getRequest().setAttribute("project_name", projectRow.getProjectName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            if (cc != null)
            {
                try
                {
                    cc.rollbackTransaction();
                }
                catch (SQLException e1)
                {
                    e1.printStackTrace();
                }
            }
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
		// the getValue() method retrieves the dialog field values according
		// to the passed in field name
        String projectDescr = (String) dc.getValue("project_descr");
        String projectName = (String) dc.getValue("project_name");
        String projectCode = (String) dc.getValue("project_code");
        String organization = (String) dc.getValue("organization_id");

        long mainProject = 0;
        try
        {
            ConnectionContext cc =  dc.getConnectionContext();

			// begin the transaction
            cc.beginTransaction();

            ProjectTable projectTable = dal.DataAccessLayer.instance.getProjectTable();
            ProjectRow projectRow = projectTable.createProjectRow();
            projectRow.setCrPersonId(personId.longValue());
            projectRow.setCrStampSqlExpr("sysdate");
            projectRow.setProjectName(projectName);
            projectRow.setProjectDescr(projectDescr);
            projectRow.setProjectCode(projectCode);
            projectRow.setProjectStatus(new Integer(dc.getValue("project_status")));
            projectRow.setStartDate((Date)dc.getValueForSqlBindParam("start_date"));
            projectRow.setTargetEndDate((Date)dc.getValueForSqlBindParam("target_end_date"));
            // by default, set the record to be active
            projectRow.setRecordStatusId(RecordStatusTable.EnumeratedItem.ACTIVE);
            if (mainProject != 0)
                projectRow.setParentId(mainProject);
			// insert a new project row
            projectTable.insert(cc, projectRow);

            // insert the project relationships
            ProjectOrgRelationTable projRelTable = dal.DataAccessLayer.instance.getProjectOrgRelationTable();
            ProjectOrgRelationRow projRelRow = projRelTable.createProjectOrgRelationRow();
            projRelRow.setParentId(projectRow.getProjectId());
            projRelRow.setCrPersonId(personId.longValue());
            projRelRow.setCrStampSqlExpr("sysdate");
            projRelRow.setRelBeginSqlExpr("sysdate");
            if (dc.getValue("org_id") != null && dc.getValue("org_id").length() > 0)
                projRelRow.setRelOrgId(Long.parseLong((String)dc.getValue("org_id")));
            else
                projRelRow.setRelOrgId(new Long((String)session.getAttribute("organization")));
            // assigning a project to person. Relationship not deifned yet so assign the creating user
            // NOTE: relationship types have not been defined yet. Insert dummy value
            projRelRow.setRelType(dal.table.ProjectOrgRelationTypeTable.EnumeratedItem.OWNER);
            if (dc.getValue("notify_email") != null)
                projRelRow.setNotifyEmail(dc.getValue("notify_email"));
			// insert a new project relationship row
            projRelTable.insert(cc, projRelRow);
            // end the transaction
            cc.commitTransaction();
            dc.getRequest().setAttribute("project_id", projectRow.getProjectId());
            dc.getRequest().setAttribute("project_name", projectRow.getProjectName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
