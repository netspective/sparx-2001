/*
 * Copyright (c) 2000-2002 Netspective Corporation -- all rights reserved
 *
 * Netspective Corporation permits redistribution, modification and use
 * of this file in source and binary form ("The Software") under the
 * Netspective Source License ("NSL" or "The License"). The following
 * conditions are provided as a summary of the NSL but the NSL remains the
 * canonical license and must be accepted before using The Software. Any use of
 * The Software indicates agreement with the NSL.
 *
 * 1. Each copy or derived work of The Software must preserve the copyright
 *    notice and this notice unmodified.
 *
 * 2. Redistribution of The Software is allowed in object code form only
 *    (as Java .class files or a .jar file containing the .class files) and only
 *    as part of an application that uses The Software as part of its primary
 *    functionality. No distribution of the package is allowed as part of a software
 *    development kit, other library, or development tool without written consent of
 *    Netspective Corporation. Any modified form of The Software is bound by
 *    these same restrictions.
 *
 * 3. Redistributions of The Software in any form must include an unmodified copy of
 *    The License, normally in a plain ASCII text file unless otherwise agreed to,
 *    in writing, by Netspective Corporation.
 *
 * 4. The names "Sparx" and "Netspective" are trademarks of Netspective
 *    Corporation and may not be used to endorse products derived from The
 *    Software without without written consent of Netspective Corporation. "Sparx"
 *    and "Netspective" may not appear in the names of products derived from The
 *    Software without written consent of Netspective Corporation.
 *
 * 5. Please attribute functionality to Sparx where possible. We suggest using the
 *    "powered by Sparx" button or creating a "powered by Sparx(tm)" link to
 *    http://www.netspective.com for each application using Sparx.
 *
 * The Software is provided "AS IS," without a warranty of any kind.
 * ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY DISCLAIMED.
 *
 * NETSPECTIVE CORPORATION AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE OR ANY THIRD PARTY AS A RESULT OF USING OR DISTRIBUTING
 * THE SOFTWARE. IN NO EVENT WILL NETSPECTIVE OR ITS LICENSORS BE LIABLE
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THE SOFTWARE, EVEN IF HE HAS BEEN ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGES.
 *
 * @author Shahid N. Shah
 */

/**
 * $Id: Dialog.java,v 1.2 2002-02-08 21:44:39 snshah Exp $
 */

package com.netspective.sparx.xaf.form;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.netspective.sparx.xaf.form.field.FileField;
import com.netspective.sparx.xaf.form.field.SelectField;
import com.netspective.sparx.xaf.form.field.StaticField;
import com.netspective.sparx.xaf.task.BasicTask;
import com.netspective.sparx.xaf.task.Task;
import com.netspective.sparx.xaf.task.TaskContext;
import com.netspective.sparx.xaf.task.TaskExecuteException;
import com.netspective.sparx.xaf.task.TaskInitializeException;
import com.netspective.sparx.util.value.SingleValueSource;
import com.netspective.sparx.util.value.ValueSourceFactory;

public class Dialog
{
    // NOTE: when adding new flags, make sure to create them before the
    // last DLGFLAG_CUSTOM_START entry. This is because QueryBuilderDialog
    // extends this class and has additional flags that is based on the value
    // of DLGFLAG_CUSTOM_START.
    static public final int DLGFLAG_CONTENTS_FINALIZED = 1;
    static public final int DLGFLAG_RETAIN_ALL_REQUEST_PARAMS = DLGFLAG_CONTENTS_FINALIZED * 2;
    static public final int DLGFLAG_LOOP_DATA_ENTRY = DLGFLAG_RETAIN_ALL_REQUEST_PARAMS * 2;
    static public final int DLGFLAG_APPEND_WHEN_LOOPING = DLGFLAG_LOOP_DATA_ENTRY * 2;
    static public final int DLGFLAG_HIDE_READONLY_HINTS = DLGFLAG_APPEND_WHEN_LOOPING * 2;
    static public final int DLGFLAG_ENCTYPE_MULTIPART_FORMDATA = DLGFLAG_HIDE_READONLY_HINTS * 2;
    static public final int DLGFLAG_CUSTOM_START = DLGFLAG_ENCTYPE_MULTIPART_FORMDATA * 2;

    static public final String PARAMNAME_AUTOEXECUTE = "_d_exec";
    static public final String PARAMNAME_OVERRIDE_SKIN = "_d_skin";
    static public final String PARAMNAME_DIALOGPREFIX = "_d.";
    static public final String PARAMNAME_CONTROLPREFIX = "_dc.";
    static public final String PARAMNAME_DIALOGQNAME = "_d.dialog_qname";
    static public final String PARAMNAME_IGNORE_VALIDATION = PARAMNAME_CONTROLPREFIX + "ignore_val";

    static public final String PARAMNAME_ACTIVEMODE = ".active_mode";
    static public final String PARAMNAME_NEXTMODE = ".next_mode";
    static public final String PARAMNAME_RUNSEQ = ".run_sequence";
    static public final String PARAMNAME_EXECSEQ = ".exec_sequence";
    static public final String PARAMNAME_ORIG_REFERER = ".orig_referer";
    static public final String PARAMNAME_TRANSACTIONID = ".transaction_id";
    static public final String PARAMNAME_RESETCONTEXT = ".reset_context";

    /*
	   the Data Command when first passed in (start of dialog, run seq == 1)
	   is passed using the parameter "data_cmd" (INITIAL). When the dialog is
	   in run sequence > 1 (after submit) the data command is passed in as a
	   hidden "pass-thru" variable with the suffix PARAMNAME_DATA_CMD
    */
    static public final String PARAMNAME_DATA_CMD_INITIAL = "data_cmd";
    static public final String PARAMNAME_DATA_CMD = ".data_cmd";
    static public final String PARAMVALUE_DATA_CMD_ADD = "add";
    static public final String PARAMVALUE_DATA_CMD_EDIT = "edit";
    static public final String PARAMVALUE_DATA_CMD_DELETE = "delete";
    static public final String PARAMVALUE_DATA_CMD_CONFIRM = "confirm";
    static public final String PARAMVALUE_DATA_CMD_PRINT = "print";

    // This flag is used to search for a session parameter indicating whether or not
    // the dialog is being used in an application or ACE.
    public static final String ENV_PARAMNAME = "dialog_environment";

    private ArrayList fields = new ArrayList();
    private int flags;
    private Task populateTasks;
    private Task executeTasks;
    private DialogDirector director;
    private String pkgName;
    private String nameFromXml;
    private String name;
    private SingleValueSource heading;
    private String actionURL;
    private String loopSeparator = "<p>";
    private int layoutColumnsCount = 1;
    private String[] retainRequestParams;
    private Class dcClass = DialogContext.class;
    private Class directorClass = DialogDirector.class;
    private SingleValueSource includeJSFile = null;

    public Dialog()
    {
        layoutColumnsCount = 1;
        flags = (DLGFLAG_LOOP_DATA_ENTRY | DLGFLAG_APPEND_WHEN_LOOPING);
    }

    public Dialog(String aName, String aHeading)
    {
        name = aName;
        setHeading(aHeading);
        layoutColumnsCount = 1;
        flags = (DLGFLAG_LOOP_DATA_ENTRY | DLGFLAG_APPEND_WHEN_LOOPING);
    }

    public String getName()
    {
        return name;
    }

    public String getNameFromXml()
    {
        return nameFromXml;
    }

    public void setName(String newName)
    {
        name = newName;
    }

    public final long getFlags()
    {
        return flags;
    }

    public final boolean flagIsSet(long flag)
    {
        return (flags & flag) == 0 ? false : true;
    }

    public final void setFlag(long flag)
    {
        flags |= flag;
    }

    public final void clearFlag(long flag)
    {
        flags &= ~flag;
    }

    public SingleValueSource getHeading()
    {
        return heading;
    }

    public void setHeading(String value)
    {
        heading = ValueSourceFactory.getSingleOrStaticValueSource(value);
    }

    public void setHeading(SingleValueSource vs)
    {
        heading = vs;
    }

    public final boolean loopEntries()
    {
        return flagIsSet(DLGFLAG_LOOP_DATA_ENTRY);
    }

    public final void setLoopEntries(boolean value)
    {
        if(value) setFlag(DLGFLAG_LOOP_DATA_ENTRY); else clearFlag(DLGFLAG_LOOP_DATA_ENTRY);
    }

    public final boolean appendAfterLoop()
    {
        return flagIsSet(DLGFLAG_APPEND_WHEN_LOOPING);
    }

    public final void setAppendAfterLoop(boolean value)
    {
        if(value) setFlag(DLGFLAG_APPEND_WHEN_LOOPING); else clearFlag(DLGFLAG_APPEND_WHEN_LOOPING);
    }

    public final int getLayoutColumnsCount()
    {
        return layoutColumnsCount;
    }

    public final String getLoopSeparator()
    {
        return loopSeparator;
    }

    public final String getOriginalRefererParamName()
    {
        return PARAMNAME_DIALOGPREFIX + name + PARAMNAME_ORIG_REFERER;
    }

    public final String getActiveModeParamName()
    {
        return PARAMNAME_DIALOGPREFIX + name + PARAMNAME_ACTIVEMODE;
    }

    public final String getNextModeParamName()
    {
        return PARAMNAME_DIALOGPREFIX + name + PARAMNAME_NEXTMODE;
    }

    public final String getRunSequenceParamName()
    {
        return PARAMNAME_DIALOGPREFIX + name + PARAMNAME_RUNSEQ;
    }

    public final String getExecuteSequenceParamName()
    {
        return PARAMNAME_DIALOGPREFIX + name + PARAMNAME_EXECSEQ;
    }

    public final String getTransactionIdParamName()
    {
        return PARAMNAME_DIALOGPREFIX + name + PARAMNAME_TRANSACTIONID;
    }

    public final String getResetContextParamName()
    {
        return PARAMNAME_DIALOGPREFIX + name + PARAMNAME_RESETCONTEXT;
    }

    public final String getValuesRequestAttrName()
    {
        return "dialog-" + name + "-field-values";
    }

    public final String getDataCmdParamName()
    {
        return PARAMNAME_DIALOGPREFIX + name + PARAMNAME_DATA_CMD;
    }

    public final Task getPopulateTasks()
    {
        return populateTasks;
    }

    public final Task getExecuteTasks()
    {
        return executeTasks;
    }

    public final List getFields()
    {
        return fields;
    }

    public final boolean retainRequestParams()
    {
        return flagIsSet(DLGFLAG_RETAIN_ALL_REQUEST_PARAMS) || (retainRequestParams != null);
    }

    public final String[] getRetainRequestParams()
    {
        return retainRequestParams;
    }

    public final void setRetainRequestParams(String[] value)
    {
        retainRequestParams = value;
    }

    public final void setRetainAllRequestParams(boolean value)
    {
        if(value) setFlag(DLGFLAG_RETAIN_ALL_REQUEST_PARAMS); else clearFlag(DLGFLAG_RETAIN_ALL_REQUEST_PARAMS);
    }

    public final boolean retainAllRequestParams()
    {
        return flagIsSet(DLGFLAG_RETAIN_ALL_REQUEST_PARAMS);
    }

    public final DialogDirector getDirector()
    {
        return director;
    }

    public void setDirector(DialogDirector value)
    {
        director = value;
    }

    public void setDialogDirectorClass(Class cls)
    {
        directorClass = cls;
    }

    public SingleValueSource getIncludeJSFile()
    {
        return includeJSFile;
    }

    public static Class findDialogContextClass(String packageName, Element elem) throws ClassNotFoundException
    {
        Class dcClass = null;

        // see if this dialog is using a special context (bean?) class
        // if a specific dc-class is provided, use it or try and find one
        // with the same name as the dialog in "dialog.context." package
        String dcClassName = elem.getAttribute("dc-class");

        if(dcClassName != null && dcClassName.length() > 0)
            dcClass = Class.forName(dcClassName);
        else
        {
            String elemName = com.netspective.sparx.util.xml.XmlSource.xmlTextToJavaIdentifier(elem.getAttribute("name"), true);
            String dlgName = packageName != null ? (packageName + "." + elemName) : elemName;
            dcClassName = "dialog.context." + dlgName + "Context";
            try
            {
                dcClass = Class.forName(dcClassName);
            }
            catch(ClassNotFoundException e)
            {
                dcClass = DialogContext.class;
            }
        }

        return dcClass;
    }

    public void setDialogContextClass(Class cls)
    {
        dcClass = cls;
    }

    public void importFromXml(String packageName, Element elem)
    {
        pkgName = packageName;
        nameFromXml = elem.getAttribute("name");
        name = nameFromXml;

        // we delimit with "_" instead of "." because the dialog name must
        // be a valid single JavaScript token (to make it easy for the skin)
        if(packageName != null)
            name = packageName + "_" + name;

        if(!Character.isJavaIdentifierStart(name.charAt(0)))
            throw new RuntimeException("Dialog name '" + name + " may contain only digits, letters, and _.'");

        for(int c = 1; c < name.length(); c++)
        {
            if(!Character.isJavaIdentifierPart(name.charAt(c)))
                throw new RuntimeException("Dialog name '" + name + " may contain only digits, letters, and _.'");
        }

        String headingVS = elem.getAttribute("heading");
        if(headingVS.length() > 0)
            setHeading(headingVS);

        String loop = elem.getAttribute("loop");
        if(loop.equals("no"))
            clearFlag(DLGFLAG_LOOP_DATA_ENTRY);
        else if(loop.length() > 0)
        {
            setFlag(DLGFLAG_LOOP_DATA_ENTRY);
            if(loop.equals("prepend")) // value can be (yes | append), prepend
                this.setAppendAfterLoop(false);
            else
                this.setAppendAfterLoop(true);
        }

        String loopSep = elem.getAttribute("loop-sep");
        if(loopSep.length() > 0)
            loopSeparator = loopSep;

        String hideHints = elem.getAttribute("hide-readonly-hints");
        if(hideHints.equals("yes"))
            setFlag(DLGFLAG_HIDE_READONLY_HINTS);

        if(director == null)
        {
            try
            {
                director = (DialogDirector) directorClass.newInstance();
            }
            catch(Exception e)
            {
                addField(new StaticField("error", "Dialog director problem: " + e.toString()));
                director = new DialogDirector();
            }
        }

        String retainRequestParamsStr = elem.getAttribute("retain-params");
        if(retainRequestParamsStr.length() > 0)
        {
            if(retainRequestParamsStr.equals("*"))
                setFlag(DLGFLAG_RETAIN_ALL_REQUEST_PARAMS);
            else
            {
                ArrayList paramNames = new ArrayList();
                StringTokenizer st = new StringTokenizer(retainRequestParamsStr, ",");
                while(st.hasMoreTokens())
                    paramNames.add(st.nextToken());
                retainRequestParams = new String[paramNames.size()];
                paramNames.toArray(retainRequestParams);
                paramNames = null;
            }
        }
        else
        {
            clearFlag(DLGFLAG_RETAIN_ALL_REQUEST_PARAMS);
            retainRequestParams = null;
        }

        NodeList children = elem.getChildNodes();
        for(int n = 0; n < children.getLength(); n++)
        {
            Node node = children.item(n);
            if(node.getNodeType() != Node.ELEMENT_NODE)
                continue;

            String childName = node.getNodeName();
            if(childName.startsWith(DialogField.FIELDTAGPREFIX))
            {
                Element fieldElem = (Element) node;
                DialogField field = DialogFieldFactory.createField(childName);
                if(field != null)
                    field.importFromXml(fieldElem);
                else
                    field = new SelectField("error", "Unable to create field of type '" + childName, SelectField.SELECTSTYLE_COMBO, ValueSourceFactory.getListValueSource("dialog-field-types:"));
                addField(field);
            }
            else if(childName.equals("populate-tasks"))
            {
                if(populateTasks == null)
                    populateTasks = new BasicTask();

                try
                {
                    Task populateTask = new BasicTask();
                    populateTask.initialize((Element) node);
                    if(!populateTask.isValid())
                    {
                        for(Iterator i = populateTask.getInitErrors().iterator(); i.hasNext();)
                        {
                            addField(new StaticField("error in populate-tasks", (String) i.next()));
                        }
                    }
                    else
                        populateTasks.addChildTask(populateTask);
                }
                catch(TaskInitializeException e)
                {
                    addField(new StaticField("exception in populate-tasks", e.toString()));
                }
            }
            else if(childName.equals("execute-tasks"))
            {
                if(executeTasks == null)
                    executeTasks = new BasicTask();
                try
                {
                    Task executeTask = new BasicTask();
                    executeTask.initialize((Element) node);
                    if(!executeTask.isValid())
                    {
                        for(Iterator i = executeTask.getInitErrors().iterator(); i.hasNext();)
                        {
                            addField(new StaticField("error in execute-tasks", (String) i.next()));
                        }
                    }
                    else
                        executeTasks.addChildTask(executeTask);
                }
                catch(TaskInitializeException e)
                {
                    addField(new StaticField("exception in execute-tasks", e.toString()));
                }
            }
            else if(childName.equals("director"))
            {
                director.importFromXml((Element) node);
            }
            else if(childName.equals("client-js"))
            {
                Element jsElem = (Element) node;
                String hrefStr = jsElem.getAttribute("href");
                this.includeJSFile = (hrefStr != null ? ValueSourceFactory.getSingleOrStaticValueSource(hrefStr): null);
            }
        }

        Iterator j = fields.iterator();
        while(j.hasNext())
        {
            DialogField field = (DialogField) j.next();
            field.finalizeQualifiedName(this);
        }
    }

    public void clearFields()
    {
        fields.clear();
        clearFlag(DLGFLAG_CONTENTS_FINALIZED);
    }

    public void addField(DialogField field)
    {
        if(field instanceof FileField)
            setFlag(DLGFLAG_ENCTYPE_MULTIPART_FORMDATA);
        fields.add(field);
    }

    public DialogField findField(String qualifiedName)
    {
        Iterator i = fields.iterator();
        while(i.hasNext())
        {
            DialogField field = (DialogField) i.next();
            DialogField found = field.findField(qualifiedName);
            if(found != null)
                return found;
        }

        return null;
    }

    public void finalizeContents()
    {
        Iterator i = fields.iterator();
        while(i.hasNext())
        {
            DialogField field = (DialogField) i.next();
            field.finalizeContents(this);

            if(field.flagIsSet(DialogField.FLDFLAG_COLUMN_BREAK_BEFORE | DialogField.FLDFLAG_COLUMN_BREAK_AFTER))
            {
                layoutColumnsCount++;
            }
        }
        setFlag(DLGFLAG_CONTENTS_FINALIZED);
    }

    public void processPopulateTasks(DialogContext dc)
    {
        if(populateTasks != null && populateTasks.isValid())
        {
            TaskContext tc = new TaskContext(dc);

            if(populateTasks.allowExecute(tc))
            {
                try
                {
                    populateTasks.execute(tc);
                    if(tc.hasError())
                        dc.addErrorMessage(tc.getErrorMessage());
                }
                catch(TaskExecuteException e)
                {
                    dc.addErrorMessage(e.getMessage());
                }
            }
        }
    }

    public void processExecuteTasks(Writer writer, DialogContext dc) throws IOException
    {
        if(executeTasks != null && executeTasks.isValid())
        {
            TaskContext tc = new TaskContext(dc);

            int numTasksExecuted = 0;
            if(executeTasks.allowExecute(tc))
            {
                try
                {
                    int tasksExecutedBefore = tc.getCountOfTasksExecuted();
                    executeTasks.execute(tc);
                    numTasksExecuted = tc.getCountOfTasksExecuted() - tasksExecutedBefore;
                }
                catch(TaskExecuteException e)
                {
                    dc.setExecuteStageHandled(true);
                    writer.write("<pre>" + e.getDetailedMessage() + "</pre>");
                }
            }

            if(numTasksExecuted > 0)
            {
                dc.setExecuteStageHandled(true);
                if(tc.hasError())
                    writer.write(tc.getErrorMessage());
                else if(tc.hasResultMessage())
                    writer.write(tc.getResultMessage());
            }
        }
    }

    /**
     * Populate the dialog with field values.
     * This should be called everytime the dialog is loaded except when it is ready for
     * execution (validated already)
     */
    public void populateValues(DialogContext dc, int formatType)
    {
        Iterator i = fields.iterator();
        while(i.hasNext())
        {
            DialogField field = (DialogField) i.next();
            if(field.isVisible(dc))
                field.populateValue(dc, formatType);
        }

        if(dc.isInitialEntry())
            processPopulateTasks(dc);
    }

    /**
     * Checks each field to make sure the state of it needs to be changed or not
     * usually based on Conditionals.
     *
     * <b>IMPORTANT</b>: If any changes are made in this class, make sure
     * that they are also reflected in QuerySelectDialog and QueryBuilderDialog classes
     * which extend this class but they overwrite this method and doesn't make a call
     * to this method.
     */
    public void makeStateChanges(DialogContext dc, int stage)
    {
        Iterator i = fields.iterator();
        while(i.hasNext())
        {
            DialogField field = (DialogField) i.next();
            field.makeStateChanges(dc, stage);
        }
    }

    public void execute(Writer writer, DialogContext dc) throws IOException
    {
        processExecuteTasks(writer, dc);

        if(! dc.executeStageHandled())
            writer.write("Need to add Dialog actions, provide listener, or override Dialog.execute(DialogContext)." + dc.getDebugHtml());
    }

    public DialogContext createContext(ServletContext context, Servlet servlet, HttpServletRequest request, HttpServletResponse response, DialogSkin skin)
    {
        DialogContext dc = null;
        try
        {
            dc = (DialogContext) dcClass.newInstance();
        }
        catch(Exception e)
        {
            dc = new DialogContext();
        }
        dc.initialize(context, servlet, request, response, this, skin);
        return dc;
    }

    public void prepareContext(DialogContext dc)
    {
        if(!flagIsSet(DLGFLAG_CONTENTS_FINALIZED))
            finalizeContents();
        populateValues(dc, DialogField.DISPLAY_FORMAT);
        dc.calcState();
        // validated and the dialog is ready for execution
        if(dc.inExecuteMode())
            this.populateValues(dc, DialogField.SUBMIT_FORMAT);

    }

    public void renderHtml(Writer writer, DialogContext dc, boolean contextPreparedAlready) throws IOException
    {
        if(!contextPreparedAlready)
            prepareContext(dc);

        if(dc.inExecuteMode())
        {
            // "looping" means to keep the dialog on the screen even after
            // the "execute" phase has been reached -- useful for lookups, etc
            if(loopEntries())
            {
                if(appendAfterLoop())
                {
                    execute(writer, dc);
                    writer.write(loopSeparator);
                    dc.getSkin().renderHtml(writer, dc);
                }
                else
                {
                    dc.getSkin().renderHtml(writer, dc);
                    writer.write(loopSeparator);
                    execute(writer, dc);
                }
            }
            else
                execute(writer, dc);
        }
        else
        {
            dc.getSkin().renderHtml(writer, dc);
        }
    }

    public void renderHtml(ServletContext context, Servlet servlet, HttpServletRequest request, HttpServletResponse response, DialogSkin skin) throws IOException
    {
        DialogContext dc = createContext(context, servlet, request, response, skin);
        renderHtml(response.getWriter(), dc, false);
    }

    public String getSubclassedDialogContextCode(String pkgPrefix)
    {
        StringBuffer importsCode = new StringBuffer();
        StringBuffer membersCode = new StringBuffer();

        Set modulesImported = new HashSet();

        Iterator i = fields.iterator();
        while(i.hasNext())
        {
            DialogField field = (DialogField) i.next();
            DialogContextMemberInfo mi = field.getDialogContextMemberInfo();
            if(mi != null)
            {
                String[] importModules = mi.getImportModules();
                if(importModules != null)
                {
                    for(int m = 0; m < importModules.length; m++)
                    {
                        String module = importModules[m];
                        if(!modulesImported.contains(module))
                        {
                            modulesImported.add(module);
                            importsCode.append("import " + module + ";\n");
                        }
                    }
                }

                membersCode.append(mi.getCode());
                membersCode.append("\n");
            }
        }

        StringBuffer code = new StringBuffer();
        code.append("\n/* this file is generated by com.netspective.sparx.xaf.form.Dialog.getSubclassedDialogContextCode(), do not modify (you can extend it, though) */\n\n");
        code.append("package " + pkgPrefix + pkgName + ";\n\n");
        if(importsCode.length() > 0)
            code.append(importsCode.toString());
        code.append("import com.netspective.sparx.xaf.form.*;\n\n");
        code.append("public class " + com.netspective.sparx.util.xml.XmlSource.xmlTextToJavaIdentifier(getNameFromXml(), true) + "Context extends DialogContext\n");
        code.append("{\n");
        code.append(membersCode.toString());
        code.append("}\n");
        return code.toString();
    }

    public boolean needsValidation(DialogContext dc)
    {
        int validateFieldsCount = 0;

        Iterator i = fields.iterator();
        while(i.hasNext())
        {
            DialogField field = (DialogField) i.next();
            if(field.isVisible(dc) && field.needsValidation(dc))
                validateFieldsCount++;
        }

        return validateFieldsCount > 0 ? true : false;
    }

    public boolean isValid(DialogContext dc)
    {
        int valStage = dc.getValidationStage();
        if(valStage == DialogContext.VALSTAGE_PERFORMED_SUCCEEDED || valStage == DialogContext.VALSTAGE_IGNORE)
            return true;
        if(valStage == DialogContext.VALSTAGE_PERFORMED_FAILED)
            return false;

        int invalidFieldsCount = 0;

        Iterator i = fields.iterator();
        while(i.hasNext())
        {
            DialogField field = (DialogField) i.next();
            if(field.isVisible(dc) && (!field.isValid(dc)))
                invalidFieldsCount++;
        }

        if(dc.getErrorMessages() != null)
            invalidFieldsCount++;

        boolean isValid = invalidFieldsCount == 0 ? true : false;
        dc.setValidationStage(isValid ? DialogContext.VALSTAGE_PERFORMED_SUCCEEDED : DialogContext.VALSTAGE_PERFORMED_FAILED);
        return isValid;
    }
}
