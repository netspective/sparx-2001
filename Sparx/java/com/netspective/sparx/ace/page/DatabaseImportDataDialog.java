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
 * $Id: DatabaseImportDataDialog.java,v 1.3 2002-12-23 04:27:51 shahid.shah Exp $
 */

package com.netspective.sparx.ace.page;

import java.io.File;
import java.sql.SQLException;

import javax.naming.NamingException;

import com.netspective.sparx.xaf.form.field.TextField;
import com.netspective.sparx.xaf.form.field.SelectField;
import com.netspective.sparx.xaf.form.DialogField;
import com.netspective.sparx.xaf.form.DialogDirector;
import com.netspective.sparx.xaf.form.Dialog;
import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.form.conditional.DialogFieldConditionalDisplay;
import com.netspective.sparx.util.value.StaticValue;
import com.netspective.sparx.util.value.ValueSourceFactory;
import com.netspective.sparx.util.value.FilesystemEntriesListValue;
import com.netspective.sparx.util.value.ListValueSource;
import com.netspective.sparx.xif.dal.Schema;
import com.netspective.sparx.xif.dal.ConnectionContext;
import com.netspective.sparx.xif.db.context.TestDatabaseContext;
import com.netspective.sparx.xif.db.DatabaseContextFactory;

public class DatabaseImportDataDialog extends Dialog
{
    protected SelectField srcFileNameField;
    protected TextField srcFileNameOtherField;
    protected TextField dalSchemaClassNameField;
    protected SelectField dsIdField;

    protected TextField dsDriverField;
    protected TextField dsUrlField;
    protected TextField dsUserNameField;
    protected TextField dsPasswordField;

    public DatabaseImportDataDialog()
    {
        super("xml_import", "Import XML Data using DAL");

        srcFileNameField = new SelectField("src_file", "Source file", SelectField.SELECTSTYLE_COMBO, "filesystem-entries:simple-expr:${config:app.schema.root-path}/data,.*,1");
        srcFileNameField.setHint("simple-expr:If your file is not in ${config:app.schema.root-path}/data, please enter it below in 'Other file'.");
        srcFileNameField.setFlag(DialogField.FLDFLAG_PERSIST | SelectField.FLDFLAG_PREPENDBLANK);

        srcFileNameOtherField = new TextField("src_file_other", "Other file");
        srcFileNameOtherField.setHint("If you specify a file in this field, it will be used instead of the selection above.");
        srcFileNameOtherField.setFlag(DialogField.FLDFLAG_PERSIST);
        srcFileNameOtherField.setSize(50);
        srcFileNameOtherField.addConditionalAction(new DialogFieldConditionalDisplay(srcFileNameOtherField, "src_file", "control.selectedIndex == 0"));

        dalSchemaClassNameField = new TextField("dal_schema_class", "DAL Schema Class");
        dalSchemaClassNameField.setDefaultValue(new StaticValue("app.dal.DataAccessLayer"));
        dalSchemaClassNameField.setFlag(DialogField.FLDFLAG_REQUIRED | DialogField.FLDFLAG_PERSIST);

        dsIdField = new SelectField("ds_id", "JNDI Datasource", SelectField.SELECTSTYLE_COMBO, "data-source-entries:");
        dsIdField.setHint("If a JNDI data source is not available, specify the Driver/URL parameters below.");
        dsIdField.setFlag(DialogField.FLDFLAG_PERSIST | SelectField.FLDFLAG_PREPENDBLANK);

        dsDriverField = new TextField("ds_driver_name", "Driver Name");
        dsDriverField.setDefaultValue(new StaticValue("oracle.jdbc.driver.OracleDriver"));
        dsDriverField.setFlag(DialogField.FLDFLAG_PERSIST);
        dsDriverField.addConditionalAction(new DialogFieldConditionalDisplay(dsDriverField, "ds_id", "control.selectedIndex == 0"));

        dsUrlField = new TextField("ds_url", "Database URL");
        dsUrlField.setDefaultValue(new StaticValue("jdbc:oracle:thin:@localhost:1521:127.0.0.1"));
        dsUrlField.setFlag(DialogField.FLDFLAG_PERSIST);
        dsUrlField.setSize(50);
        dsUrlField.addConditionalAction(new DialogFieldConditionalDisplay(dsUrlField, "ds_id", "control.selectedIndex == 0"));

        dsUserNameField = new TextField("ds_username", "User Name");
        dsUserNameField.setFlag(DialogField.FLDFLAG_PERSIST);
        dsUserNameField.addConditionalAction(new DialogFieldConditionalDisplay(dsUserNameField, "ds_id", "control.selectedIndex == 0"));

        dsPasswordField = new TextField("ds_password", "Password");
        dsPasswordField.setFlag(TextField.FLDFLAG_MASKENTRY);
        dsPasswordField.addConditionalAction(new DialogFieldConditionalDisplay(dsPasswordField, "ds_id", "control.selectedIndex == 0"));

        addField(srcFileNameField);
        addField(srcFileNameOtherField);
        addField(dalSchemaClassNameField);
        addField(dsIdField);

        addField(dsDriverField);
        addField(dsUrlField);
        addField(dsUserNameField);
        addField(dsPasswordField);

        setDirector(new DialogDirector());
    }

    public boolean isValid(DialogContext dc)
    {
        if(! super.isValid(dc))
            return false;

        String sourceFile = getSourceFile(dc);
        if(sourceFile == null || sourceFile.length() == 0)
        {
            dc.addErrorMessage("Please select a source file or enter a value in 'Other file'.");
            return false;
        }

        if(! new File(sourceFile).exists())
        {
            dc.addErrorMessage("Source file '"+ sourceFile +"' does not exist.");
            return false;
        }

        Class schemaClass = getSchemaClass(dc);
        if(schemaClass == null)
        {
            dc.addErrorMessage("Schema class '"+ schemaClass +"' was not found in the CLASSPATH.");
            return false;
        }

        return true;
    }

    public String getSourceFile(DialogContext dc)
    {
        String sourceFile = dc.getValue(srcFileNameOtherField);
        if(sourceFile == null || sourceFile.length() == 0)
            sourceFile = dc.getValue(srcFileNameField);
        return sourceFile;
    }

    public Class getSchemaClass(DialogContext dc)
    {
        String schemaClass = dc.getValue(dalSchemaClassNameField);
        try
        {
            return Class.forName(schemaClass);
        }
        catch(ClassNotFoundException cnfe)
        {
            return null;
        }
    }

    public ConnectionContext getConnectionContext(DialogContext dc) throws NamingException, SQLException
    {
        String dataSourceId = dc.getValue(dsIdField);
        if(dataSourceId != null && dataSourceId.length() > 0)
        {
            return ConnectionContext.getConnectionContext(DatabaseContextFactory.getContext(dc), dataSourceId, ConnectionContext.CONNCTXTYPE_TRANSACTION);
        }
        else
        {
            String driverClassName = dc.getValue(dsDriverField);
            String driverURL = dc.getValue(dsUrlField);
            String userName = dc.getValue(dsUserNameField);
            String password = dc.getValue(dsPasswordField);
            String temporaryDataSourceId = "default-cc";

            TestDatabaseContext testDBC = new TestDatabaseContext();
            testDBC.setDataSourceInfo(temporaryDataSourceId, new TestDatabaseContext.DataSourceInfo(driverClassName, driverURL, userName, password));

            return ConnectionContext.getConnectionContext(testDBC, temporaryDataSourceId, ConnectionContext.CONNCTXTYPE_TRANSACTION);
        }
    }

    public SelectField getSrcFileNameField()
    {
        return srcFileNameField;
    }

    public TextField getSrcFileNameOtherField()
    {
        return srcFileNameOtherField;
    }

    public TextField getDalSchemaClassNameField()
    {
        return dalSchemaClassNameField;
    }

    public SelectField getDsIdField()
    {
        return dsIdField;
    }

    public TextField getDsDriverField()
    {
        return dsDriverField;
    }

    public TextField getDsUrlField()
    {
        return dsUrlField;
    }

    public TextField getDsUserNameField()
    {
        return dsUserNameField;
    }

    public TextField getDsPasswordField()
    {
        return dsPasswordField;
    }
}
