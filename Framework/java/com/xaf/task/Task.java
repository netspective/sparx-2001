package com.xaf.task;

import java.util.*;
import org.w3c.dom.*;

import com.xaf.form.*;
import com.xaf.value.*;

public interface Task
{
	public void initialize(Element elem) throws TaskInitializeException;

	public long getFlags();
	public boolean flagIsSet(long flag);
	public void setFlag(long flag);
	public void clearFlag(long flag);

	public void execute(TaskContext tc);
}