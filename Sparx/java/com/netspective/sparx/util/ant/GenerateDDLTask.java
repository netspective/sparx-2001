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
 * $Id: GenerateDDLTask.java,v 1.1 2002-12-31 19:34:57 shahid.shah Exp $
 */

package com.netspective.sparx.util.ant;

import java.io.File;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;

import com.netspective.sparx.xif.SchemaDocument;
import com.netspective.sparx.xif.SchemaDocFactory;

public class GenerateDDLTask extends Task
{
    private File schemaFile;
    private File dest;
    private File stylesDir;

    public void init() throws BuildException
    {
        schemaFile = null;
        dest = null;
        stylesDir = null;
    }

    public void setDest(File dest)
    {
        this.dest = dest;
    }

    public void setSchema(File schema)
    {
        this.schemaFile = schema;
    }

    public void setStylesDir(File stylesDir)
    {
        this.stylesDir = stylesDir;
    }

    public void execute() throws BuildException
    {
        log("Opening SchemaDoc (XML) file " + schemaFile.getAbsolutePath() + "...");
        SchemaDocument schemaDoc = SchemaDocFactory.getDoc(schemaFile.getAbsolutePath());
        if (schemaDoc == null)
            throw new BuildException("Unable to open SchemaDoc file '" + schemaFile.getAbsolutePath() + "'");

        String[] srcFiles = stylesDir.list();

        for(int f = 0; f < srcFiles.length; f++)
        {
            String styleSheetNameAndExtn = srcFiles[f];
            if(!styleSheetNameAndExtn.endsWith(".xsl"))
                continue;

            try
            {
                File styleSheet = new File(stylesDir, styleSheetNameAndExtn);
                String styleSheetNameOnly = styleSheetNameAndExtn.substring(0, styleSheetNameAndExtn.lastIndexOf("."));
                String destFileName = dest.isDirectory() ? dest.getAbsolutePath() + "/" + styleSheetNameOnly + ".sql" : dest.getAbsolutePath();

                SchemaDocument.SqlDdlGenerator generator = new SchemaDocument.SqlDdlGenerator(styleSheet, destFileName);
                generator.generate(schemaDoc);
                log("Generated DDL file " + generator.getDestFile());
            }
            catch (TransformerConfigurationException e)
            {
                throw new BuildException(e);
            }
            catch (TransformerException e)
            {
                throw new BuildException(e);
            }
        }
    }
}
