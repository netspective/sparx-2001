package com.xaf.ant;

import org.apache.tools.ant.*;
import com.xaf.*;

public class ClassPathTask extends Task
{
    private boolean listAll;
    private String showLocOfClass;

    public ClassPathTask()
    {
    }

    public void init() throws BuildException
    {
        listAll = false;
        showLocOfClass = null;
    }

    public void setListall(boolean list)
    {
        listAll = list;
    }

    public void setClass(String className)
    {
        showLocOfClass = className;
    }

	public void execute() throws BuildException
	{
        if(listAll)
        {
            BuildConfiguration.ClassPathInfo[] cpi = BuildConfiguration.getClassPaths();
            for(int i = 0; i < cpi.length; i++)
            {
                BuildConfiguration.ClassPathInfo info = cpi[i];
                log(info.getClassPath().getAbsolutePath() + (info.isValid() ? " (valid)" : " (not found)"));
            }
        }

        if(showLocOfClass != null)
        {
            log(showLocOfClass + " is " + BuildConfiguration.getClassFileName(showLocOfClass));
        }
	}
}