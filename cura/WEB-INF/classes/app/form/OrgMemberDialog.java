/*
 * Class: app.form.OrgMemberDialog
 * Created on Feb 10, 2002 11:43:36 PM
 * @author Aye Thu
 * @version
 */
package app.form;

import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.form.Dialog;
import com.netspective.sparx.xif.dal.ConnectionContext;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;
import java.sql.SQLException;

import app.dal.table.PersonOrgRelationshipTable;
import app.dal.DataAccessLayer;
import app.dal.domain.row.PersonOrgRelationshipRow;

public class OrgMemberDialog  extends Dialog
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
        if (dc.addingData())
        {
            // dialog is in the add data command mode
            this.processAddAction(dc);
        }
        else if (dc.deletingData())
        {
            this.processDeleteAction(dc);
        }
        HttpServletRequest request = (HttpServletRequest)dc.getRequest();
        String url = request.getContextPath() + "/account/home.jsp?org_id=" + request.getParameter("org_id") +
            "&org_name=" + request.getParameter("org_name");
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
     * Process the delete action by removing contacts from the organization
     *
     * @param dc dialog context
     */
    protected void processDeleteAction(DialogContext dc)
    {
        ConnectionContext cc = null;
        try
        {
            String org_id = dc.getRequest().getParameter("org_id");
            cc = dc.getConnectionContext();
            cc.beginTransaction();
            String[] personList = dc.getRequest().getParameterValues("_dc.rel_person_id.checkbox");
            app.form.context.org.PersonUnregistrationContext rc = (app.form.context.org.PersonUnregistrationContext) dc;
            PersonOrgRelationshipTable porTable = app.dal.DataAccessLayer.instance.getPersonOrgRelationshipTable();
            for (int j=0; j < personList.length; j++)
            {
                porTable.delete(cc, porTable.createPersonOrgRelationshipRow(), "rel_org_id = ? and parent_id = ?",
                        new Object[] {new Long(org_id), new Long(personList[j])});
            }
            cc.commitTransaction();

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
     * Process the new members for this organization
     *
     * @param dc dialog context
     */
    protected void processAddAction(DialogContext dc)
    {
        ConnectionContext cc = null;
        try
        {
            String org_id = dc.getRequest().getParameter("org_id");
            cc =  dc.getConnectionContext();
            String[] personList = dc.getRequest().getParameterValues("_dc.rel_person_id.checkbox");

            cc.beginTransaction();
            app.form.context.org.PersonRegistrationContext rc = (app.form.context.org.PersonRegistrationContext) dc;
            PersonOrgRelationshipTable porTable = app.dal.DataAccessLayer.instance.getPersonOrgRelationshipTable();
            for (int j=0; j < personList.length; j++)
            {
                PersonOrgRelationshipRow porRow = porTable.createPersonOrgRelationshipRow();
                porRow.setCrOrgId(rc.getCrOrgId());
                porRow.setCrPersonId(rc.getCrPersonId());
                porRow.setCrStampSqlExpr("sysdate");
                porRow.setParentId(new Long(personList[j]));
                porRow.setRelOrgId(new Long(org_id));
                porRow.setRelBeginSqlExpr("sysdate");
                porRow.setRelType(1);
                porTable.insert(cc, porRow);
            }

            cc.commitTransaction();

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
                catch (SQLException sqle)
                {
                    sqle.printStackTrace();
                }
            }
        }
    }
}
