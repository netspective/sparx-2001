/*
 * Copyright (c) 2000-2002 Netspective Corporation -- all rights reserved
 *
 * Netspective Corporation permits redistribution, modification and use
 * of this file in source and binary form ("The Software") under the
 * Netspective Source License ("NSL" or "The License"). The following
 * conditions are provided as a summary of the NSL but the NSL remains the
 * canonical license and must be accepted before using The Software. Any use of
 * The Software indicates agreement with the NSL.
 *
 * 1. Each copy or derived work of The Software must preserve the copyright
 *    notice and this notice unmodified.
 *
 * 2. Redistribution of The Software is allowed in object code form only
 *    (as Java .class files or a .jar file containing the .class files) and only
 *    as part of an application that uses The Software as part of its primary
 *    functionality. No distribution of the package is allowed as part of a software
 *    development kit, other library, or development tool without written consent of
 *    Netspective Corporation. Any modified form of The Software is bound by
 *    these same restrictions.
 *
 * 3. Redistributions of The Software in any form must include an unmodified copy of
 *    The License, normally in a plain ASCII text file unless otherwise agreed to,
 *    in writing, by Netspective Corporation.
 *
 * 4. The names "Sparx" and "Netspective" are trademarks of Netspective
 *    Corporation and may not be used to endorse products derived from The
 *    Software without without written consent of Netspective Corporation. "Sparx"
 *    and "Netspective" may not appear in the names of products derived from The
 *    Software without written consent of Netspective Corporation.
 *
 * 5. Please attribute functionality to Sparx where possible. We suggest using the
 *    "powered by Sparx" button or creating a "powered by Sparx(tm)" link to
 *    http://www.netspective.com for each application using Sparx.
 *
 * The Software is provided "AS IS," without a warranty of any kind.
 * ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 *
 * NETSPECTIVE CORPORATION AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE OR ANY THIRD PARTY AS A RESULT OF USING OR DISTRIBUTING
 * THE SOFTWARE. IN NO EVENT WILL NETSPECTIVE OR ITS LICENSORS BE LIABLE
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THE SOFTWARE, EVEN IF HE HAS BEEN ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGES.
 *
 * @author Shahid N. Shah
 */

/**
 * $Id: TableDialog.java,v 1.4 2003-02-04 05:07:18 shahid.shah Exp $
 */

package com.netspective.sparx.xif.dal;

import java.sql.SQLException;
import java.io.Writer;
import java.io.IOException;

import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.w3c.dom.Element;

import com.netspective.sparx.xaf.form.Dialog;
import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xif.SchemaDocument;
import com.netspective.sparx.xif.SchemaDocFactory;
import com.netspective.sparx.util.log.LogManager;

public class TableDialog extends Dialog
{
    public static final int DLGFLAG_ALLOW_INSERT_IF_EDITPK_NOT_FOUND = DLGFLAG_CUSTOM_START * 2;
    public static final String PARAMNAME_PRIMARYKEY = "pk";
    public static final String SESSATTRNAMEPREFIX_ACTIVE_PRIMARYKEY = "table-dialog-active-pk-";

    private String schemaName;
    private Schema schema;
    private String tableName;
    private Table table;
    private String primaryKeyColName;

    public void finalizeContents(ServletContext context)
    {
        super.finalizeContents(context);

        SchemaDocument schemaDoc =
                schemaName == null ? SchemaDocFactory.getDoc(context) : SchemaDocFactory.getDoc(schemaName);

        if(schemaDoc != null)
        {
            schema = schemaDoc.getSchema();
            if(schema != null)
            {
                table = schema.getTable(tableName);
                if(table != null)
                {
                    Column primaryKey = table.getPrimaryKeyColumn();
                    primaryKeyColName = primaryKey != null ? primaryKey.getNameForServletParameter() : null;
                }
            }
        }
        else
            schema = null;
    }

    public boolean isValid(DialogContext dc)
    {
        if(! super.isValid(dc))
            return false;

        if(table == null)
        {
            dc.addErrorMessage("Unable to find table '"+ tableName +"' in schema '"+ schemaName +"'.");
            return false;
        }

        if (primaryKeyColName == null)
        {
            dc.addErrorMessage("Unable to find primary key in table '"+ tableName +"' in schema '"+ schemaName +"'.");
            return false;
        }

        return true;
    }

    public String getSchemaName()
    {
        return schemaName;
    }

    public void setSchemaName(String schemaName)
    {
        this.schemaName = schemaName;
        schema = null;
    }

    public String getTableName()
    {
        return tableName;
    }

    public void setTableName(String tableName)
    {
        this.tableName = tableName;
    }

    public void importFromXml(String packageName, Element elem)
    {
        super.importFromXml(packageName, elem);

        if(elem.getAttribute("schema").length() > 0)
            setSchemaName(elem.getAttribute("schema"));

        if(elem.getAttribute("table").length() > 0)
            setTableName(elem.getAttribute("table"));

        if(elem.getAttribute("allow-insert-if-edit-pk-not-found").equals("yes"))
            setFlag(DLGFLAG_ALLOW_INSERT_IF_EDITPK_NOT_FOUND);
    }

    public void populateValues(DialogContext dc, int formatType)
    {
        if(dc.isInitialEntry())
        {
            if(dc.addingData())
            {
                dc.populateValuesFromRequestParamsAndAttrs();
            }
            else if((dc.editingData() || dc.deletingData()))
            {
                HttpServletRequest request = (HttpServletRequest) dc.getRequest();
                Object pkValue = request.getAttribute(PARAMNAME_PRIMARYKEY);
                if(pkValue == null)
                {
                    request.getParameter(PARAMNAME_PRIMARYKEY);
                    if(pkValue == null && primaryKeyColName != null)
                    {
                        pkValue = request.getAttribute(primaryKeyColName);
                        if(pkValue == null)
                            pkValue = request.getParameter(primaryKeyColName);
                    }
                }

                if(pkValue != null)
                {
                    ConnectionContext cc = null;
                    try
                    {
                        cc = dc.getConnectionContextAuto();
                        Row row = table.getRecordByPrimaryKey(cc, pkValue, null);
                        if(row != null)
                        {
                            row.setData(dc);
                            request.getSession(true).setAttribute(SESSATTRNAMEPREFIX_ACTIVE_PRIMARYKEY + dc.getTransactionId(), pkValue);
                        }
                        else if(! flagIsSet(DLGFLAG_ALLOW_INSERT_IF_EDITPK_NOT_FOUND))
                            dc.addErrorMessage("Unable to locate primary key '"+ pkValue +"'");
                    }
                    catch (NamingException e)
                    {
                        LogManager.recordException(this.getClass(), "populateValues", "primary key = '"+ pkValue +"'", e);
                    }
                    catch (SQLException e)
                    {
                        LogManager.recordException(this.getClass(), "populateValues", "primary key = '"+ pkValue +"'", e);
                    }
                    finally
                    {
                        try
                        {
                            if(cc != null) cc.returnConnection();
                        }
                        catch (SQLException e)
                        {
                            LogManager.recordException(this.getClass(), "populateValues", "primary key = '"+ pkValue +"'", e);
                        }
                    }
                }
            }
        }

        super.populateValues(dc, formatType);
    }

    public void execute(Writer writer, DialogContext dc) throws IOException
    {
        ConnectionContext cc = null;
        try
        {
            cc = dc.getConnectionContext();
        }
        catch (Exception e)
        {
            handlePostExecuteException(writer, dc, dc.getDataCommandText(true)+ ": unable to establish connection", e);
            return;
        }

        Row row = table.createRow();
        row.populateDataByNames(dc);

        HttpServletRequest request = (HttpServletRequest) dc.getRequest();
        HttpSession session = request.getSession(true);
        String activePrimaryKeyValueSessAttrName = SESSATTRNAMEPREFIX_ACTIVE_PRIMARYKEY + dc.getTransactionId();

        try
        {
            cc.beginTransaction();

            switch(dc.getDataCommand())
            {
                case DialogContext.DATA_CMD_ADD:
                    table.insert(cc, row);
                    dc.setLastRowManipulated(row);
                    break;

                case DialogContext.DATA_CMD_EDIT:
                    if(flagIsSet(DLGFLAG_ALLOW_INSERT_IF_EDITPK_NOT_FOUND))
                    {
                        Object pkValue = session.getAttribute(activePrimaryKeyValueSessAttrName);
                        if(pkValue == null)
                            table.insert(cc, row);
                        else
                            table.update(cc, row);
                    }
                    else
                        table.update(cc, row);
                    dc.setLastRowManipulated(row);
                    break;

                case DialogContext.DATA_CMD_DELETE:
                    table.delete(cc, row);
                    dc.setLastRowManipulated(row);
                    break;
            }

            cc.commitTransaction();
            handlePostExecute(writer, dc);
        }
        catch (Exception e)
        {
            handlePostExecuteException(writer, dc, dc.getDataCommandText(true) + ": " + row, e);
        }
        finally
        {
            session.removeAttribute(activePrimaryKeyValueSessAttrName);
        }
    }
}
