/*
 * Class: app.form.OrgDialog
 * Created on Feb 8, 2002 9:51:11 PM
 * @author Aye Thu
 * @version
 */
package app.form;

import com.netspective.sparx.xaf.form.Dialog;
import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.security.AuthenticatedUser;
import com.netspective.sparx.xif.dal.ConnectionContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.naming.NamingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Map;
import java.math.BigDecimal;

import app.TaskHandler;
import dialog.context.org.RegistrationContext;
import dal.table.OrgTable;
import dal.table.OrgIndustryTable;
import dal.table.OrgTypeTable;
import dal.table.OrgRelationshipTable;
import dal.domain.row.OrgRow;
import dal.domain.row.OrgIndustryRow;
import dal.domain.row.OrgTypeRow;
import dal.domain.row.OrgRelationshipRow;
import dal.domain.rows.OrgRelationshipRows;
import dal.DataAccessLayer;

public class OrgDialog extends Dialog
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
            String orgId = dc.getRequest().getParameter("org_id");
            dc.populateValuesFromStatement("org.registration", new Object[] {new Long(orgId)});
        }
        if (dc.deletingData())
        {
            String orgId = dc.getRequest().getParameter("org_id");
            dc.populateValuesFromStatement("org.registration", new Object[] {new Long(orgId)});
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
        String url = "";
        HttpServletRequest request = (HttpServletRequest)dc.getRequest();
        try
        {
            if (dc.addingData())
            {
                // dialog is in the add data command mode
                this.processAddData(dc);
                url = request.getContextPath() + "/account/home.jsp?org_id=" + request.getAttribute("org_id") +
                    "&org_name=" + URLEncoder.encode((String)request.getAttribute("org_name"));
            }
            if (dc.editingData())
            {
                // dialog is in the edit data command mode
                this.processEditData(dc);
                url = request.getContextPath() + "/account/home.jsp?org_id=" + request.getAttribute("org_id") +
                    "&org_name=" + URLEncoder.encode((String)request.getAttribute("org_name"));
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
     * Delete the organization
     */
    protected void processDeleteData(DialogContext dc) throws SQLException, NamingException
    {
        dialog.context.org.RegistrationContext rc = (dialog.context.org.RegistrationContext) dc;
        ConnectionContext cc = dc.getConnectionContext();
        String org_id = dc.getRequest().getParameter("org_id");
        cc.beginTransaction();

        OrgTypeTable orgTypeTable = dal.DataAccessLayer.instance.getOrgTypeTable();
        orgTypeTable.deleteOrgTypeRowsUsingOrgId(cc, new Long(org_id));
        OrgIndustryTable orgIndustryTable = dal.DataAccessLayer.instance.getOrgIndustryTable();
        orgIndustryTable.deleteOrgIndustryRowsUsingOrgId(cc, new Long(org_id));
        OrgTable orgTable = dal.DataAccessLayer.instance.getOrgTable();
        OrgRow orgRow = orgTable.getOrgByOrgId(cc, new Long(org_id));
        orgTable.delete(cc, orgRow);
        cc.commitTransaction();
    }

    /**
     * Process the new updated data
     */
    protected void processEditData(DialogContext dc) throws SQLException, NamingException
    {
        String org_id = dc.getRequest().getParameter("org_id");
        ConnectionContext cc =  dc.getConnectionContext();

        cc.beginTransaction();
        dialog.context.org.RegistrationContext rc = (RegistrationContext) dc;
        // update the Task table
        OrgTable orgTable = dal.DataAccessLayer.instance.getOrgTable();
        OrgRow orgRow = orgTable.getOrgByOrgId(cc, new Long(org_id));
        orgRow.populateDataByNames(dc);
        orgTable.update(cc, orgRow);

        cc.commitTransaction();
        //dc.getRequest().setAttribute("org_id", orgRow.getTaskId());
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
        ConnectionContext cc = null;

        try
        {
            dialog.context.org.RegistrationContext rc = (RegistrationContext)dc;
            cc =  rc.getConnectionContext();

            cc.beginTransaction();
            OrgTable orgTable = dal.DataAccessLayer.instance.getOrgTable();
            OrgRow orgRow = orgTable.createOrgRow();
            // populate the row with values from the dialog fields
            orgRow.populateDataByNames(rc);
            orgTable.insert(cc, orgRow);

            OrgIndustryTable orgIndustryTable = dal.DataAccessLayer.instance.getOrgIndustryTable();
            OrgIndustryRow orgIndustryRow = orgIndustryTable.createOrgIndustryRow();
            orgIndustryRow.setCrOrgId(rc.getCrOrgId());
            orgIndustryRow.setCrPersonId(rc.getCrPersonId());
            orgIndustryRow.setCrStampSqlExpr("sysdate");
            orgIndustryRow.setOrgIndustry(new Integer(rc.getOrgIndustry()));
            orgIndustryRow.setOrgId(orgRow.getOrgIdLong());
            orgIndustryTable.insert(cc, orgIndustryRow);

            OrgTypeTable orgTypeTable = dal.DataAccessLayer.instance.getOrgTypeTable();
            OrgTypeRow orgTypeRow = orgTypeTable.createOrgTypeRow();
            orgTypeRow.setCrOrgId(rc.getCrOrgId());
            orgTypeRow.setCrPersonId(rc.getCrPersonId());
            orgTypeRow.setCrStampSqlExpr("sysdate");
            orgTypeRow.setOrgId(orgRow.getOrgIdLong());
            orgTypeRow.setOrgType(new Integer(rc.getOrgType()));
            orgTypeTable.insert(cc, orgTypeRow);

            if (rc.getParentOrgId() != null && rc.getParentOrgId().length() > 0)
            {
                // add org relationships
                OrgRelationshipTable orgRelTable = dal.DataAccessLayer.instance.getOrgRelationshipTable();
                // add a relationship row for this new org with the parent org
                OrgRelationshipRow orgRelRow = orgRelTable.createOrgRelationshipRow();
                orgRelRow.setCrOrgId(rc.getCrOrgId());
                orgRelRow.setCrPersonId(rc.getCrPersonId());
                orgRelRow.setCrStampSqlExpr("sysdate");
                orgRelRow.setParentId(orgRow.getOrgIdLong());
                orgRelRow.setRelOrgId(new Long(rc.getParentOrgId()));
                orgRelRow.setRelType(dal.table.OrgRelationshipTypeTable.EnumeratedItem.PARENT_OF_ORG);
                orgRelTable.insert(cc, orgRelRow);

                // add a relationship row for the parent org with the new row as its child
                OrgRelationshipRow parentOrgRow = orgRelTable.createOrgRelationshipRow();
                parentOrgRow.setCrOrgId(rc.getCrOrgId());
                parentOrgRow.setCrPersonId(rc.getCrPersonId());
                parentOrgRow.setCrStampSqlExpr("sysdate");
                parentOrgRow.setParentId(new Long(rc.getParentOrgId()));
                parentOrgRow.setRelOrgId(orgRow.getOrgId());
                parentOrgRow.setRelType(dal.table.OrgRelationshipTypeTable.EnumeratedItem.CHILD_OF_ORG);
                orgRelTable.insert(cc, parentOrgRow);


                // get all the relationships of the parent org
                OrgRelationshipRows orgRelRows = orgRelTable.getOrgRelationshipRowsByParentId(cc, new Long(rc.getParentOrgId()));
                for (int i=0; i < orgRelRows.size(); i++)
                {
                    OrgRelationshipRow tmpRow = (OrgRelationshipRow) orgRelRows.get(i);

                    if (tmpRow.getRelTypeEnum() == dal.table.OrgRelationshipTypeTable.EnumeratedItem.PARENT_OF_ORG)
                    {
                        // parent of the new org's parent should be added as the new org's ancestor
                        OrgRelationshipRow orgAncestorRow = orgRelTable.createOrgRelationshipRow();
                        orgAncestorRow.setCrOrgId(rc.getCrOrgId());
                        orgAncestorRow.setCrPersonId(rc.getCrPersonId());
                        orgAncestorRow.setCrStampSqlExpr("sysdate");
                        orgAncestorRow.setParentId(orgRow.getOrgIdLong());
                        orgAncestorRow.setRelOrgId(tmpRow.getRelOrgId());
                        orgAncestorRow.setRelType(dal.table.OrgRelationshipTypeTable.EnumeratedItem.ANCESTOR_OF_ORG);
                        orgRelTable.insert(cc, orgAncestorRow);

                        // the new org should be added as the descendent of the parent of the new org's parent
                        OrgRelationshipRow ancestorOrgRow = orgRelTable.createOrgRelationshipRow();
                        ancestorOrgRow.setCrOrgId(rc.getCrOrgId());
                        ancestorOrgRow.setCrPersonId(rc.getCrPersonId());
                        ancestorOrgRow.setCrStampSqlExpr("sysdate");
                        ancestorOrgRow.setParentId(tmpRow.getRelOrgId());
                        ancestorOrgRow.setRelOrgId(orgRow.getOrgIdLong());
                        ancestorOrgRow.setRelType(dal.table.OrgRelationshipTypeTable.EnumeratedItem.DESCENDENT_OF_ORG);
                        orgRelTable.insert(cc, ancestorOrgRow);

                    }
                }

            }

            cc.commitTransaction();
            dc.getRequest().setAttribute("org_id", orgRow.getOrgId());
            dc.getRequest().setAttribute("org_name", orgRow.getOrgName());
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
