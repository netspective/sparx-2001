package com.xaf.form;

import java.util.*;
import org.w3c.dom.*;

public interface DialogProcessAction
{
    public boolean isExecuteAction();
    public boolean isPopulateDataAction();

    public void initializeProcessAction(Element elem) throws DialogProcessActionInitializeException;
    public void populateDialogValues(Dialog dialog, DialogContext dc);
    public String executeDialog(Dialog dialog, DialogContext dc);
}