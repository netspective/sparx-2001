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
 * $Id: DatabaseMetaDataToSchemaDocDialog.java,v 1.2 2002-08-09 11:53:45 shahid.shah Exp $
 */

package com.netspective.sparx.ace.page;

import com.netspective.sparx.xaf.form.Dialog;
import com.netspective.sparx.xaf.form.DialogDirector;
import com.netspective.sparx.xaf.form.DialogField;
import com.netspective.sparx.xaf.form.field.TextField;
import com.netspective.sparx.util.value.StaticValue;
import com.netspective.sparx.util.value.ValueSourceFactory;

public class DatabaseMetaDataToSchemaDocDialog extends Dialog
{
    protected TextField dsDriverField;
    protected TextField dsUrlField;
    protected TextField dsUserNameField;
    protected TextField dsPasswordField;
    protected TextField dsCatalogField;
    protected TextField dsSchemaField;
    protected TextField outFileName;

    public DatabaseMetaDataToSchemaDocDialog()
    {
        super("schemadocgen", "Generate SchemaDoc from DataSource MetaData");

        dsDriverField = new TextField("ds_driver_name", "Driver Name");
        dsDriverField.setDefaultValue(new StaticValue("oracle.jdbc.driver.OracleDriver"));
        dsDriverField.setFlag(DialogField.FLDFLAG_REQUIRED | DialogField.FLDFLAG_PERSIST);

        dsUrlField = new TextField("ds_url", "Database URL");
        dsUrlField.setDefaultValue(new StaticValue("jdbc:oracle:thin:@aruba:1521:127.0.0.1"));
        dsUrlField.setFlag(DialogField.FLDFLAG_REQUIRED | DialogField.FLDFLAG_PERSIST);

        dsUserNameField = new TextField("ds_username", "User Name");
        dsUserNameField.setFlag(DialogField.FLDFLAG_PERSIST);

        dsPasswordField = new TextField("ds_password", "Password");
        dsPasswordField.setFlag(TextField.FLDFLAG_MASKENTRY);
        dsPasswordField.setFlag(DialogField.FLDFLAG_COLUMN_BREAK_AFTER);

        dsCatalogField = new TextField("ds_catalog", "Catalog");
        dsCatalogField.setFlag(DialogField.FLDFLAG_PERSIST);

        dsSchemaField = new TextField("ds_schema", "Schema");
        dsSchemaField.setFlag(DialogField.FLDFLAG_PERSIST);

        outFileName = new TextField("out_file_name", "Output File");
        outFileName.setDefaultValue(ValueSourceFactory.getSingleValueSource("config-expr:${config:app.schema.root-path}/generated-schema.xml"));
        outFileName.setFlag(DialogField.FLDFLAG_REQUIRED | DialogField.FLDFLAG_PERSIST);

        addField(dsDriverField);
        addField(dsUrlField);
        addField(dsUserNameField);
        addField(dsPasswordField);

        addField(dsCatalogField);
        addField(dsSchemaField);
        addField(outFileName);

        setDirector(new DialogDirector());
    }
}