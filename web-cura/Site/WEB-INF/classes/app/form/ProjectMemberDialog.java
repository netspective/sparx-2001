/*
 * Created by IntelliJ IDEA.
 * User: Aye Thu
 * Date: Jan 12, 2002
 * Time: 11:10:49 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package app.form;

import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.form.Dialog;
import com.netspective.sparx.xif.dal.ConnectionContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.naming.NamingException;

import dal.table.ProjectPersonRelationTable;
import dal.domain.ProjectPersonRelation;
import dal.domain.rows.ProjectPersonRelationRows;
import dal.domain.row.ProjectPersonRelationRow;

import java.sql.SQLException;
import java.io.Writer;

public class ProjectMemberDialog extends Dialog
{
    /**
     *  This is where you perform all your actions. Whatever you return as the function result will be shown
     * in the HTML
     */
    public void execute(Writer writer,  DialogContext dc)
    {
        // if you call super.execute(dc) then you would execute the <execute-tasks> in the XML; leave it out
        // to override
        // super.execute(dc);
        try
        {
            if (dc.editingData())
            {
                // dialog is in the edit data command mode
                this.processEditData(dc);
            }
        }
        catch (NamingException e)
        {
            e.printStackTrace();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        HttpServletRequest request = (HttpServletRequest)dc.getRequest();
        String url = request.getContextPath() + "/project/home.jsp?project_id=" + dc.getValue("project_id") +
            "&project_name=" + dc.getValue("project_name");
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
     * Adds new data
     */
    protected void processEditData(DialogContext dc) throws NamingException, SQLException
    {
        HttpSession session = dc.getSession();
        String[] personList = (String[])dc.getValues("rel_person_id");
        ConnectionContext cc = dc.getConnectionContext();
        cc.beginTransaction();

        ProjectPersonRelationTable pprTable = dal.DataAccessLayer.instance.getProjectPersonRelationTable();
        // check to see if there are any relationships already defined
        ProjectPersonRelationRows pprRows = pprTable.getProjectPersonRelationRowsByParentId(cc, new Long(dc.getValue("project_id")));
        if (pprRows == null || pprRows.size() == 0)
        {
            // no relations exist. Incoming data is all new
            for (int i=0; i< personList.length; i++)
            {
                ProjectPersonRelationRow ppr = pprTable.createProjectPersonRelationRow();
                ppr.setCrOrgId(new Long(dc.getValue("cr_org_id")));
                ppr.setCrPersonId(new Long(dc.getValue("cr_person_id")));
                ppr.setCrStampSqlExpr("sysdate");
                ppr.setParentId(new Long(dc.getValue("project_id")));
                ppr.setRelPersonId(new Long(personList[i]));
                ppr.setRelBeginSqlExpr("sysdate");
                ppr.setRelType(dal.table.ProjectPersonRelationTypeTable.EnumeratedItem.MEMBER);
                pprTable.insert(cc, ppr);
            }
        }
        else
        {
            // relations already exist (NOTE: Need to improve this code to be more efficient)
            // First step: delete all rows that no longer exists
            for (int i=0; i < pprRows.size(); i++)
            {
                boolean deleteRow = true;
                ProjectPersonRelationRow pprRow = pprRows.getProjectPersonRelationRow(i);
                for (int j=0; j < personList.length; j++)
                {
                    if (pprRow.getRelPersonId().toString().equals(personList[j]))
                    {
                        // record already exists. No need to do anything
                        deleteRow = false;
                        break;
                    }
                }
                if (deleteRow)
                {
                    // maybe instead of deleting, just set the relationship end date?
                    pprTable.delete(cc, pprRow);
                }
            }
            // Second step: insert all new rows
            for (int i=0; i < personList.length; i++)
            {
                boolean addRow = true;
                for (int j=0; j < pprRows.size(); j++)
                {

                    ProjectPersonRelationRow pprRow = pprRows.getProjectPersonRelationRow(j);
                    if (personList[i].equals(pprRow.getRelPersonId().toString()))
                    {
                        // record exists
                        addRow = false;
                        break;
                    }
                }
                if (addRow)
                {
                    ProjectPersonRelationRow ppr = pprTable.createProjectPersonRelationRow();
                    ppr.setCrOrgId(new Long(dc.getValue("cr_org_id")));
                    ppr.setCrPersonId(new Long(dc.getValue("cr_person_id")));
                    ppr.setCrStampSqlExpr("sysdate");
                    ppr.setParentId(new Long(dc.getValue("project_id")));
                    ppr.setRelPersonId(new Long(personList[i]));
                    ppr.setRelBeginSqlExpr("sysdate");
                    ppr.setRelType(dal.table.ProjectPersonRelationTypeTable.EnumeratedItem.MEMBER);
                    pprTable.insert(cc, ppr);
                }
            }

        }
        cc.endTransaction();
    }

}
