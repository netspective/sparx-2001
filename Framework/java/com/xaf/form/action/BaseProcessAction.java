package com.xaf.form.action;

import java.util.*;
import org.w3c.dom.*;
import com.xaf.form.*;

public class BaseProcessAction implements DialogProcessAction
{
    static long actionId = 0;

    public BaseProcessAction()
    {
    }

    public long getUniqueActionId()
    {
        actionId++;
        return actionId;
    }

    public boolean isExecuteAction()
    {
        return false;
    }

    public boolean isPopulateDataAction()
    {
        return false;
    }

    public void initializeProcessAction(Element elem) throws DialogProcessActionInitializeException
    {
    }

    public void populateDialogValues(Dialog dialog, DialogContext dc)
    {
        throw new RuntimeException(this.getClass().getName() + ".populateDialogValues is an abstract method.");
    }

    public String executeDialog(Dialog dialog, DialogContext dc)
    {
        throw new RuntimeException(this.getClass().getName() + ".executeDialog is an abstract method.");
    }
}