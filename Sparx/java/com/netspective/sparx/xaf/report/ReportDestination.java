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
 * $Id: ReportDestination.java,v 1.2 2002-02-02 00:00:31 snshah Exp $
 */

package com.netspective.sparx.xaf.report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import com.netspective.sparx.util.config.Configuration;
import com.netspective.sparx.util.config.ConfigurationManagerFactory;
import com.netspective.sparx.util.value.ValueContext;

public class ReportDestination
{
    static public final String DESTNAME_BROWSER_MULTI_PAGE = "browser-paged";
    static public final String DESTNAME_BROWSER_SINGLE_PAGE = "browser";
    static public final String DESTNAME_FILE_DOWNLOAD = "file";
    static public final String DESTNAME_FILE_EMAIL = "email";

    static public final int DEST_BROWSER_MULTI_PAGE = 0;
    static public final int DEST_BROWSER_SINGLE_PAGE = 1;
    static public final int DEST_FILE_DOWNLOAD = 2;
    static public final int DEST_FILE_EMAIL = 3;

    private int destination;
    private String storePathName;
    private String downloadUrl;
    private File storePath;
    private File file;
    private Writer writer;

    public ReportDestination(int dest, ValueContext vc, ReportSkin skin) throws IOException
    {
        destination = dest;
        initialize(vc, skin);
    }

    public ReportDestination(String dest, ValueContext vc, ReportSkin skin) throws IOException
    {
        destination = getDestIdFromName(dest);
        if(destination == -1)
            throw new RuntimeException("ReportDestination '" + dest + "' not supported. Use 'file' or 'email'");

        initialize(vc, skin);
    }

    public void initialize(ValueContext vc, ReportSkin skin) throws IOException
    {
        Configuration appConfig = ConfigurationManagerFactory.getDefaultConfiguration(vc.getServletContext());
        storePathName = appConfig.getTextValue(vc, "app.report-file-store-path");
        downloadUrl = appConfig.getTextValue(vc, "app.report-file-download-url");
        if(storePathName == null || downloadUrl == null)
            throw new RuntimeException("Configuration value 'app.report-file-store-path' and 'app.report-file-download-url' are required.");

        storePath = new File(storePathName);
        if(!storePath.exists())
            storePath.mkdirs();

        file = File.createTempFile("report_", skin != null ? skin.getFileExtension() : ".html", storePath);
        file.deleteOnExit();

        writer = new FileWriter(file);
    }

    public String getStorePathName()
    {
        return storePathName;
    }

    public String getDownloadUrl()
    {
        return downloadUrl;
    }

    public File getStorePath()
    {
        return storePath;
    }

    public File getFile()
    {
        return file;
    }

    public Writer getWriter()
    {
        return writer;
    }

    static public int getDestIdFromName(String name)
    {
        int result = -1;
        if(name.equals(DESTNAME_BROWSER_MULTI_PAGE))
            result = DEST_BROWSER_MULTI_PAGE;
        else if(name.equals(DESTNAME_BROWSER_SINGLE_PAGE))
            result = DEST_BROWSER_SINGLE_PAGE;
        else if(name.equals(DESTNAME_FILE_DOWNLOAD))
            result = DEST_FILE_DOWNLOAD;
        else if(name.equals(DESTNAME_FILE_EMAIL))
            result = DEST_FILE_EMAIL;
        return result;
    }

    public String getUserMessage()
    {
        if(destination == DEST_FILE_DOWNLOAD)
            return "Your file is ready for download. Please click <a href='" + downloadUrl + "/" + file.getName() + "'>here</a> to retrieve it.";
        else
            return "E-mail not supported yet; however, your file is ready for download. Please click <a href='" + downloadUrl + "/" + file.getName() + "'>here</a> to retrieve it.";
    }
}