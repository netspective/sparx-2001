package com.xaf.value;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import java.io.*;
import java.util.*;
import com.xaf.config.*;
import com.xaf.form.*;
import com.xaf.form.field.*;
import org.apache.oro.text.perl.*;

public class FilesystemEntriesListValue extends ListSource implements FilenameFilter
{
    static public Perl5Util perlUtil = new Perl5Util();
    static public String ALL_FILES_FILTER = "/.*/";

    private boolean includePathInCaption;
    private SingleValueSource rootPathValue;
    private String filter;

    public FilesystemEntriesListValue()
    {
        filter = ALL_FILES_FILTER;
    }

    public boolean isPathInSelection()
    {
        return includePathInCaption;
    }

    public void setIncludePathInSelection(boolean includePathInSelection)
    {
        this.includePathInCaption = includePathInSelection;
    }

    public SingleValueSource getRootPath()
    {
        return rootPathValue;
    }

    public void setRootPath(String rootPath)
    {
        this.rootPathValue = ValueSourceFactory.getSingleOrStaticValueSource(rootPath);
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
        int delimPos = srcParams.indexOf(",");
        if(delimPos > 0)
        {
            setRootPath(srcParams.substring(0, delimPos));
            setFilter(srcParams.substring(delimPos+1));
        }
        else
        {
            setRootPath(srcParams);
            filter = ALL_FILES_FILTER;
        }
    }

    public SelectChoicesList getSelectChoices(ValueContext vc)
    {
		SelectChoicesList choices = new SelectChoicesList();
        File rootPath = new File(rootPathValue.getValue(vc));
        String[] files = rootPath.list(this);

        if(files != null && files.length > 0)
        {
            if(includePathInCaption)
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
                    File file = new File(files[f]);
                    choices.add(new SelectChoice(file.getName(), files[f]));
                }
            }
        }

		return choices;
	}
}