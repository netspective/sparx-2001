package com.xaf.task.navigate;

import java.io.*;
import org.w3c.dom.*;
import com.xaf.task.*;

public class RedirectTask extends AbstractTask
{
	private String url;

    public RedirectTask()
    {
		super();
    }

    public void initialize(Element elem) throws com.xaf.task.TaskInitializeException
    {
		url = elem.getAttribute("url");
    }

	public void reset()
	{
		url = null;
	}

    public void execute(TaskContext tc) throws TaskExecuteException
    {
		try
		{
			tc.getResponse().sendRedirect(url);
			tc.setFlag(TaskContext.TCFLAG_HALTPROCESSING);
		}
		catch(IOException e)
		{
			throw new TaskExecuteException(e);
		}
    }
}