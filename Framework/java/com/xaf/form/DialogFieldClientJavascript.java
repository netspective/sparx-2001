/*
 * Title:       DialogFieldClientJavascript
 * Description: DialogFieldClientJavascript
 * Copyright:   Copyright (c) 2001
 * Company:     
 * @author      ThuA
 * @created     Oct 25, 2001 10:58:36 AM
 * @version     1.0
 */
package com.xaf.form;

import com.xaf.value.*;

public class DialogFieldClientJavascript
{
    private SingleValueSource event;
    private SingleValueSource type;
    private SingleValueSource script;
    public static final String[] eventList= {"is-valid", "value-changed", "click", "key-press", "get-focus", "lose-focus"};

    public DialogFieldClientJavascript()
    {
    }

    public void setEvent(String event)
    {
        this.event = (event != null ? ValueSourceFactory.getSingleOrStaticValueSource(event): null);
    }

    public void setType(String type)
    {
        this.type = (type != null ? ValueSourceFactory.getSingleOrStaticValueSource(type): null);
    }

    public void setScript(String script)
    {
        this.script = (script != null ? ValueSourceFactory.getSingleOrStaticValueSource(script): null);
    }

    /**
     * Check to see if the event is valid
     *
     * @param str event type read from XML
     * @returns true if it is a valid event, else false
     */
    public static boolean isValidEvent(String str)
    {
        if (str == null || str.length() == 0)
            return false;

        for (int i=0; i < DialogFieldClientJavascript.eventList.length; i++)
        {
            if (str.equals(DialogFieldClientJavascript.eventList[i]))
                return true;
        }
        return false;
    }

    public SingleValueSource getType()
    {
        return type;
    }

    public SingleValueSource getEvent()
    {
        return event;
    }

    public SingleValueSource getScript()
    {
        return script;
    }

}
