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
 * $Id: GenerateJavaDALTask.java,v 1.4 2002-08-31 00:18:04 shahid.shah Exp $
 */

package com.netspective.sparx.util.ant;

import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.netspective.sparx.xif.SchemaDocFactory;
import com.netspective.sparx.xif.SchemaDocument;

public class GenerateJavaDALTask extends Task
{
    private String schemaDocFile;
    private String destRoot;
    private String styleSheetRoot;

    private String dataTypesPkg = "app.dal.column";
    private String tableTypesPkg = "app.dal.table.type";
    private String tablesPkg = "app.dal.table";
    private String domainsPkg = "app.dal.domain";
    private String listenersPkg = "app.dal.listener";
    private String rowsPkg = "app.dal.domain.row";
    private String rowsListPkg = "app.dal.domain.rows";
    private String schemaPkg = "app.dal";
    private String schemaClassName = "DataAccessLayer";

    private String dataTypesGeneratorStyleSheet = "data-type-generator.xsl";
    private String tableTypesGeneratorStyleSheet = "table-type-generator.xsl";
    private String tablesGeneratorStyleSheet = "table-generator.xsl";
    private String domainsGeneratorStyleSheet = "domain-generator.xsl";
    private String listenersGeneratorStyleSheet = "listener-generator.xsl";
    private String rowsGeneratorStyleSheet = "row-generator.xsl";
    private String rowsListGeneratorStyleSheet = "rows-generator.xsl";
    private String schemaGeneratorStyleSheet = "schema-generator.xsl";

    public GenerateJavaDALTask()
    {
    }

    public void setSchema(String schemaDocFile)
    {
        this.schemaDocFile = schemaDocFile;
    }

    public void setDest(String destRoot)
    {
        this.destRoot = destRoot;
    }

    public void setStylesheetroot(String styleSheetRoot)
    {
        this.styleSheetRoot = styleSheetRoot;
    }

    public void setDatatypespkg(String dataTypesPkg)
    {
        this.dataTypesPkg = dataTypesPkg;
    }

    public void setTabletypespkg(String tableTypesPkg)
    {
        this.tableTypesPkg = tableTypesPkg;
    }

    public void setTablespkg(String tablesPkg)
    {
        this.tablesPkg = tablesPkg;
    }

    public void setDomainspkg(String domainsPkg)
    {
        this.domainsPkg = domainsPkg;
    }

    public void setRowspkg(String rowsPkg)
    {
        this.rowsPkg = rowsPkg;
    }

    public void setRowslistpkg(String rowsListPkg)
    {
        this.rowsListPkg = rowsListPkg;
    }

    public void setSchemapkg(String schemaPkg)
    {
        this.schemaPkg = schemaPkg;
    }

    public void setSchemaclassname(String schemaClassName)
    {
        this.schemaClassName = schemaClassName;
    }

    public void setDatatypesgeneratorstylesheet(String dataTypesGeneratorStyleSheet)
    {
        this.dataTypesGeneratorStyleSheet = dataTypesGeneratorStyleSheet;
    }

    public void setTabletypesgeneratorstylesheet(String tableTypesGeneratorStyleSheet)
    {
        this.tableTypesGeneratorStyleSheet = tableTypesGeneratorStyleSheet;
    }

    public void setTablesgeneratorstylesheet(String tablesGeneratorStyleSheet)
    {
        this.tablesGeneratorStyleSheet = tablesGeneratorStyleSheet;
    }

    public void setDomainsgeneratorstylesheet(String domainsGeneratorStyleSheet)
    {
        this.domainsGeneratorStyleSheet = domainsGeneratorStyleSheet;
    }

    public void setRowsgeneratorstylesheet(String rowsGeneratorStyleSheet)
    {
        this.rowsGeneratorStyleSheet = rowsGeneratorStyleSheet;
    }

    public void setRowslistgeneratorstylesheet(String rowsListGeneratorStyleSheet)
    {
        this.rowsListGeneratorStyleSheet = rowsListGeneratorStyleSheet;
    }

    public void setSchemageneratorstylesheet(String schemaGeneratorStyleSheet)
    {
        this.schemaGeneratorStyleSheet = schemaGeneratorStyleSheet;
    }

    public void execute() throws BuildException
    {
        log("Opening SchemaDoc (XML) file " + schemaDocFile + "...");
        SchemaDocument schemaDoc = SchemaDocFactory.getDoc(schemaDocFile);
        if(schemaDoc == null)
            throw new BuildException("Unable to open SchemaDoc file '" + schemaDocFile + "'");

        List errors = schemaDoc.getErrors();
        if(errors.size() > 0)
        {
            for(Iterator ei = errors.iterator(); ei.hasNext();)
                log("SchemaDoc Warning: " + (String) ei.next());
        }

        SchemaDocument.ObjectRelationalGenerator orGenerator = new SchemaDocument.ObjectRelationalGenerator();
        orGenerator.setDestRoot(destRoot);

        orGenerator.setDataTypesPkg(dataTypesPkg);
        orGenerator.setTableTypesPkg(tableTypesPkg);
        orGenerator.setTablesPkg(tablesPkg);
        orGenerator.setDomainsPkg(domainsPkg);
        orGenerator.setListenersPkg(listenersPkg);
        orGenerator.setRowsPkg(rowsPkg);
        orGenerator.setRowsListPkg(rowsListPkg);
        orGenerator.setSchemaPkg(schemaPkg);
        orGenerator.setSchemaClassName(schemaClassName);

        orGenerator.setDataTypesGeneratorStyleSheet(styleSheetRoot + "/" + dataTypesGeneratorStyleSheet);
        orGenerator.setTableTypesGeneratorStyleSheet(styleSheetRoot + "/" + tableTypesGeneratorStyleSheet);
        orGenerator.setTablesGeneratorStyleSheet(styleSheetRoot + "/" + tablesGeneratorStyleSheet);
        orGenerator.setDomainsGeneratorStyleSheet(styleSheetRoot + "/" + domainsGeneratorStyleSheet);
        orGenerator.setListenersGeneratorStyleSheet(styleSheetRoot + "/" + listenersGeneratorStyleSheet);
        orGenerator.setRowsGeneratorStyleSheet(styleSheetRoot + "/" + rowsGeneratorStyleSheet);
        orGenerator.setRowsListGeneratorStyleSheet(styleSheetRoot + "/" + rowsListGeneratorStyleSheet);
        orGenerator.setSchemaGeneratorStyleSheet(styleSheetRoot + "/" + schemaGeneratorStyleSheet);

        try
        {
            log("Generating Data Access Layer...");
            orGenerator.generate(schemaDoc);
            log("Generated Java Data Access Layer in " + destRoot);
            log("Generated " + orGenerator.getDataTypesGeneratedCount() + " data types");
            log("Generated " + orGenerator.getTableTypesGeneratedCount() + " table types");
            log("Generated " + orGenerator.getTablesGeneratedCount() + " tables");
        }
        catch(Exception e)
        {
            List messages = orGenerator.getMessages();
            if(messages.size() > 0)
            {
                for(int i = 0; i < messages.size(); i++)
                    log((String) messages.get(i));
            }

            throw new BuildException(e);
        }
    }
}