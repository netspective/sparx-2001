package com.xaf.task;

import java.util.*;
import org.w3c.dom.*;
import com.xaf.form.*;
import com.xaf.value.*;

abstract public class AbstractTask implements Task
{
	private static long taskNum = 0;
	private long flags;
	private List children;

    public AbstractTask()
    {
		taskNum++;
    }

	public long getTaskNum() { return taskNum; }
	public final long getFlags() { return flags; }
	public final boolean flagIsSet(long flag) { return (flags & flag) == 0 ? false : true; }
	public final void setFlag(long flag)
	{
		flags |= flag;
		if(children != null)
		{
			Iterator i = children.iterator();
			while(i.hasNext())
			{
				((Task) i.next()).setFlag(flag);
			}
		}
	}

	public final void clearFlag(long flag)
	{
		flags &= ~flag;
		if(children != null)
		{
			Iterator i = children.iterator();
			while(i.hasNext())
			{
				((Task) i.next()).clearFlag(flag);
			}
		}
	}

	abstract public void initialize(Element elem) throws TaskInitializeException;
	abstract public void execute(TaskContext tc);
}