package com.xaf.task.navigate;

import java.io.*;
import org.w3c.dom.*;

import com.xaf.task.*;
import com.xaf.value.*;

public class RedirectTask extends AbstractTask
{
	private SingleValueSource url;

    public RedirectTask()
    {
		super();
    }

    public void initialize(Element elem) throws TaskInitializeException
    {
		super.initialize(elem);
		url = ValueSourceFactory.getSingleOrStaticValueSource(elem.getAttribute("url"));
    }

	public void reset()
	{
		url = null;
	}

    public void execute(TaskContext tc) throws TaskExecuteException
    {
		try
		{
			tc.getResponse().sendRedirect(url.getValue(tc));
			tc.setFlag(TaskContext.TCFLAG_HALTPROCESSING);
		}
		catch(IOException e)
		{
			throw new TaskExecuteException(e);
		}
    }
}