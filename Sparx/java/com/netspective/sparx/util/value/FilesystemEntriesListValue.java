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
 * $Id: FilesystemEntriesListValue.java,v 1.2 2002-08-30 00:24:33 shahid.shah Exp $
 */

package com.netspective.sparx.util.value;

import java.io.File;
import java.io.FilenameFilter;
import java.util.StringTokenizer;

import org.apache.oro.text.perl.Perl5Util;

import com.netspective.sparx.xaf.form.field.SelectChoice;
import com.netspective.sparx.xaf.form.field.SelectChoicesList;

public class FilesystemEntriesListValue extends ListSource implements FilenameFilter
{
    static public Perl5Util perlUtil = new Perl5Util();
    static public String ALL_FILES_FILTER = "/.*/";

    private boolean includePathInValue;
    private SingleValueSource rootPathValue;
    private String filter;

    public FilesystemEntriesListValue()
    {
        filter = ALL_FILES_FILTER;
    }

    public SingleValueSource.Documentation getDocumentation()
    {
        return new SingleValueSource.Documentation(
                "Provides list of files contained in a directory (either all files or by filter). If only a path is " +
                "provided then this LVS returns a list of all the files in the given path. If a regular expression is " +
                "provided (filter-reg-ex) then it must be a Perl5 regular expression that will be used to match the " +
                "files that should be included in the list. If the {include-path} parameter is set to 1 then the full "+
                "path included in the selected value otherwise just the filename is provided.",
                new String[]{ "path", "path,filter-reg-ex", "path,filter-reg-ex,include-path" }
        );
    }

    public boolean isPathInSelection()
    {
        return includePathInValue;
    }

    public void setIncludePathInSelection(boolean includePathInSelection)
    {
        this.includePathInValue = includePathInSelection;
    }

    public SingleValueSource getRootPath()
    {
        return rootPathValue;
    }

    public void setRootPath(String rootPath)
    {
        setRootPath(ValueSourceFactory.getSingleOrStaticValueSource(rootPath));
    }

    public void setRootPath(SingleValueSource rootPath)
    {
        this.rootPathValue = rootPath;
    }

    public String getFilter()
    {
        return filter;
    }

    public void setFilter(String filter)
    {
        this.filter = "/" + filter + "/";
    }

    public boolean accept(File file, String s)
    {
        return perlUtil.match(filter, s);
    }

    public void initializeSource(String srcParams)
    {
        super.initializeSource(srcParams);
        StringTokenizer st = new StringTokenizer(srcParams, ",");
        if(st.hasMoreTokens())
            setRootPath(st.nextToken());
        if(st.hasMoreTokens())
        {
            String filterParam = st.nextToken();
            if(filterParam.equals(""))
                filter = ALL_FILES_FILTER;
            else
                setFilter(filterParam);
        }
        else
            filter = ALL_FILES_FILTER;
        if(st.hasMoreTokens())
            setIncludePathInSelection(st.nextToken().equals("1"));
    }

    public SelectChoicesList getSelectChoices(ValueContext vc)
    {
        SelectChoicesList choices = new SelectChoicesList();
        File rootPath = new File(rootPathValue.getValue(vc));
        String[] files = rootPath.list(this);

        if(files != null && files.length > 0)
        {
            if(!includePathInValue)
            {
                for(int f = 0; f < files.length; f++)
                {
                    choices.add(new SelectChoice(files[f]));
                }
            }
            else
            {
                for(int f = 0; f < files.length; f++)
                {
                    File file = new File(rootPath, files[f]);
                    choices.add(new SelectChoice(file.getName(), file.getAbsolutePath()));
                }
            }
        }
        else
            choices.add(new SelectChoice(rootPath.getAbsolutePath() + " does not contain files matching '"+ filter +"'"));

        return choices;
    }
}