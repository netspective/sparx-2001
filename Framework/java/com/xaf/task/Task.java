package com.xaf.task;

import java.util.*;
import org.w3c.dom.*;

import com.xaf.form.*;
import com.xaf.value.*;

public interface Task
{
	public final static long TASKFLAG_DEBUG = 1;
	public final static long TASKFLAG_LAST = TASKFLAG_DEBUG;

	public void initialize(Element elem) throws TaskInitializeException;
	public void reset();

	public long getFlags();
	public boolean flagIsSet(long flag);
	public void setFlag(long flag);
	public void clearFlag(long flag);

	public void execute(TaskContext tc) throws TaskExecuteException;
}