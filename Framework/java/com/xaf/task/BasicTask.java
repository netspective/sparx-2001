package com.xaf.task;

import java.util.*;
import org.w3c.dom.*;
import com.xaf.form.*;
import com.xaf.value.*;

public class BasicTask implements Task
{
	private static long taskNum = 0;
	private long flags;
    private int dataCmdCondition = DialogContext.DATA_CMD_NONE;
    private String taskName;
    private List initErrors;
	private List children;

    public BasicTask()
    {
		taskNum++;
    }

	public void reset()
	{
		flags = 0;
        dataCmdCondition = 0;
		children = null;
        taskName = null;
        initErrors = null;
	}

	public long getTaskNum() { return taskNum; }
    public String getTaskName() { return taskName; }
    public List getChildren() { return children; }
    public int getChildrenCount() { return children != null ? children.size() : 0; }

    public void addChildTask(Task task)
    {
        if(children == null)
            children = new ArrayList();
        children.add(task);
    }

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

    public int getDataCmdCondition()
    {
        return dataCmdCondition;
    }

    public void setDataCmdCondition(int condition)
    {
        dataCmdCondition = condition;
    }

    public void setDataCmdCondition(String condition)
    {
        dataCmdCondition = DialogContext.getDataCmdIdForCmdText(condition);
    }

    public boolean isDataCmdConditionTrue(TaskContext tc)
    {
        DialogContext dc = tc.getDialogContext();
        if(dc == null || dataCmdCondition == DialogContext.DATA_CMD_NONE)
            return true;

        return dc.matchesDataCmdCondition(dataCmdCondition);
    }

    public void addInitError(String message)
    {
        if(initErrors == null)
            initErrors = new ArrayList();
        initErrors.add(message);
    }

    public boolean isValid()
    {
        return initErrors == null;
    }

    public List getInitErrors()
    {
        return initErrors;
    }

	public void initialize(Element elem) throws TaskInitializeException
	{
        taskName = elem.getNodeName();

        if(elem.getAttribute("debug").equals("yes"))
			setFlag(TASKFLAG_DEBUG);

        String dataCmd = elem.getAttribute("data-cmd");
        if(dataCmd.length() > 0)
            setDataCmdCondition(dataCmd);

        NodeList childNodes = elem.getChildNodes();
        for(int c = 0;  c < childNodes.getLength(); c++)
        {
            Node childNode = childNodes.item(c);
            if(childNode.getNodeType() == Node.ELEMENT_NODE)
            {
                try
                {
                    Task task = TaskFactory.getTask((Element) childNode, true);
                    addChildTask(task);
                }
                catch(Exception e)
                {
                    addInitError(e.toString());
                }
            }
        }
	}

    public boolean allowExecute(TaskContext tc)
    {
        return isDataCmdConditionTrue(tc);
    }

    public void executeChildren(TaskContext tc) throws TaskExecuteException
    {
        if(children == null || children.size() == 0)
            return;

        for(Iterator i = children.iterator(); i.hasNext(); )
        {
            Task task = (Task) i.next();
            if(task.allowExecute(tc))
                task.execute(tc);
            if(tc.haltProcessing())
                break;
        }
    }

	public void execute(TaskContext tc) throws TaskExecuteException
    {
        tc.registerTaskExecutionBegin(this);
        executeChildren(tc);
        tc.registerTaskExecutionEnd(this);
    }

    public String getDebugHtml(ValueContext vc)
    {
        StringBuffer html = new StringBuffer();
        html.append("Task '"+ getTaskName() +"' ("+ getClass().getName() +") ["+ DialogContext.getDataCmdTextForCmdId(dataCmdCondition) +"]");

        if(children != null)
        {
            html.append("<ol>");
            for(int i = 0; i< children.size(); i++)
            {
                Task child = (Task) children.get(i);
                html.append("<li>");
                html.append(child.getDebugHtml(vc));
                html.append("</li>");
            }
            html.append("</ol>");
        }

        return html.toString();
    }
}