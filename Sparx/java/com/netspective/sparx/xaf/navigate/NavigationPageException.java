package com.netspective.sparx.xaf.navigate;

import java.io.StringWriter;
import java.io.PrintWriter;

public class NavigationPageException extends Exception
{
    private Throwable rootCause;

    public NavigationPageException(String msg)
    {
        super(msg);
    }

    public NavigationPageException(Throwable root)
    {
        this(root.getMessage());
        rootCause = root;
    }

    public NavigationPageException(Throwable root, String prependMessage)
    {
        this(prependMessage + "\n\n" + root.getMessage());
        rootCause = root;
    }

    public Throwable getRootCause()
    {
        return rootCause;
    }

    public String getDetailedMessage()
    {
        if(rootCause == null)
            return getMessage();

        StringWriter stack = new StringWriter();
        rootCause.printStackTrace(new PrintWriter(stack));
        return getMessage() + "\n" + stack.toString();
    }
}
