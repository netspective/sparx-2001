/*
 * Description: app.form.ProjectDialog
 * @author ThuA
 * @created Dec 29, 2001 3:30:12 PM
 * @version 
 */
package app.form;

import com.xaf.form.DialogContext;
import com.xaf.form.Dialog;
import com.xaf.security.AuthenticatedUser;
import com.xaf.db.ConnectionContext;
import com.xaf.db.DatabaseContextFactory;
import dal.domain.Project;
import dal.domain.row.ProjectRow;
import dal.domain.row.ProjectRelationRow;
import dal.table.ProjectTable;
import dal.table.RecordStatusTable;
import dal.table.ProjectRelationTable;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.math.BigDecimal;

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
            String url = request.getContextPath() + "/project/home.jsp?project_id=" + request.getAttribute("project_id");
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

        String projectDescr = (String) dc.getValue("project_descr");
        String projectName = (String) dc.getValue("project_name");
        String projectCode = (String) dc.getValue("project_code");
        String organization = (String) dc.getValue("organization_id");
        int projectType = Integer.parseInt((String)dc.getValue("project_type"));

        long mainProject = 0;
        if (projectType == SUBPROJECT_TYPE)
            mainProject = Long.parseLong((String)dc.getValue("main_project"));

        try
        {
            ConnectionContext cc =  ConnectionContext.getConnectionContext(DatabaseContextFactory.getSystemContext(),
                dc.getServletContext().getInitParameter("default-data-source"), ConnectionContext.CONNCTXTYPE_TRANSACTION);

            cc.beginTransaction();

            ProjectTable projectTable = dal.DataAccessLayer.instance.getProjectTable();
            ProjectRow projectRow = projectTable.createProjectRow();
            projectRow.setCrPersonId(personId.longValue());
            projectRow.setCrStampSqlExpr("sysdate");
            projectRow.setProjectName(projectName);
            projectRow.setProjectDescr(projectDescr);
            projectRow.setProjectCode(projectCode);
            // by default, set the record to be active
            projectRow.setRecordStatusId(RecordStatusTable.EnumeratedItem.ACTIVE);
            if (mainProject != 0)
                projectRow.setParentId(mainProject);

            projectTable.insert(cc, projectRow);


            // insert the project relationships
            ProjectRelationTable projRelTable = dal.DataAccessLayer.instance.getProjectRelationTable();
            ProjectRelationRow projRelRow = projRelTable.createProjectRelationRow();
            projRelRow.setParentId(projectRow.getProjectId());
            projRelRow.setCrPersonId(personId.longValue());
            projRelRow.setCrStampSqlExpr("sysdate");
            projRelRow.setRelBeginSqlExpr("sysdate");
            projRelRow.setRelOrgId(Long.parseLong((String)dc.getValue("organization")));
            // assigning a project to person. Relationship not deifned yet so assign the creating user
            projRelRow.setRelPersonId(personId.longValue());
            // relationship types have not been defined yet. Insert dummy value
            projRelRow.setRelType(1);
            if (dc.getValue("notify_email") != null)
                projRelRow.setNotifyEmail(dc.getValue("notify_email"));

            projRelTable.insert(cc, projRelRow);
            cc.endTransaction();

            dc.getRequest().setAttribute("project_id", projectRow.getProjectId());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
