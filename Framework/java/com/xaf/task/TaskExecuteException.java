package com.xaf.task;

import java.io.*;

public class TaskExecuteException extends Exception
{
	private Throwable rootCause;

    public TaskExecuteException(String msg)
    {
		super(msg);
    }

	public TaskExecuteException(Throwable root)
	{
		this(root.getMessage());
		rootCause = root;
	}

    public TaskExecuteException(Throwable root, String prependMessage)
	{
		this(prependMessage + "\n\n" + root.getMessage());
		rootCause = root;
	}

	public Throwable getRootCause() { return rootCause; }

	public String getDetailedMessage()
	{
		if(rootCause == null)
			return getMessage();

		StringWriter stack = new StringWriter();
        rootCause.printStackTrace(new PrintWriter(stack));
		return getMessage() + "\n" + stack.toString();
	}
}