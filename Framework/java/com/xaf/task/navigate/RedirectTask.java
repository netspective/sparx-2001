package com.xaf.task.navigate;

import java.io.*;
import javax.servlet.http.*;

import org.w3c.dom.*;

import com.xaf.task.*;
import com.xaf.value.*;

public class RedirectTask extends BasicTask
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
        tc.registerTaskExecutionBegin(this);
		try
		{
			((HttpServletResponse) tc.getResponse()).sendRedirect(url.getValue(tc));
			tc.setFlag(TaskContext.TCFLAG_HALTPROCESSING);
		}
		catch(IOException e)
		{
			throw new TaskExecuteException(e);
		}
        finally
        {
            tc.registerTaskExecutionEnd(this);
        }
    }
}