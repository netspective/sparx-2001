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
 * $Id: DialogField.java,v 1.10 2002-08-30 00:28:14 shahid.shah Exp $
 */

package com.netspective.sparx.xaf.form;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.netspective.sparx.xaf.form.field.SelectField;
import com.netspective.sparx.xaf.form.conditional.DialogFieldConditionalApplyFlag;
import com.netspective.sparx.xaf.form.conditional.DialogFieldConditionalData;
import com.netspective.sparx.xaf.form.conditional.DialogFieldConditionalDisplay;
import com.netspective.sparx.util.value.SingleValueSource;
import com.netspective.sparx.util.value.ValueSourceFactory;

/**
 * A <code>DialogField</code> object represents a data field of a form/dialog. It contains functionalities
 * such as data validation rules, dynamic data binding, HTML rendering, and conditional logics.
 * It provides the default behavior and functionality for all types of dialog fields.
 * All dialog classes representing specialized  fields such as text fields, numerical fields, and phone fields subclass
 * the <code>DialogField</code> class.
 */
public class DialogField
{
    // all these values are also defined in dialog.js (make sure they are always in sync)
    static public final int FLDFLAG_REQUIRED = 1;
    static public final int FLDFLAG_PRIMARYKEY = FLDFLAG_REQUIRED * 2;
    static public final int FLDFLAG_INVISIBLE = FLDFLAG_PRIMARYKEY * 2;
    static public final int FLDFLAG_READONLY = FLDFLAG_INVISIBLE * 2;
    static public final int FLDFLAG_INITIAL_FOCUS = FLDFLAG_READONLY * 2;
    static public final int FLDFLAG_PERSIST = FLDFLAG_INITIAL_FOCUS * 2;
    static public final int FLDFLAG_CREATEADJACENTAREA = FLDFLAG_PERSIST * 2;
    static public final int FLDFLAG_SHOWCAPTIONASCHILD = FLDFLAG_CREATEADJACENTAREA * 2;
    static public final int FLDFLAG_INPUT_HIDDEN = FLDFLAG_SHOWCAPTIONASCHILD * 2;
    static public final int FLDFLAG_HAS_CONDITIONAL_DATA = FLDFLAG_INPUT_HIDDEN * 2;
    static public final int FLDFLAG_COLUMN_BREAK_BEFORE = FLDFLAG_HAS_CONDITIONAL_DATA * 2;
    static public final int FLDFLAG_COLUMN_BREAK_AFTER = FLDFLAG_COLUMN_BREAK_BEFORE * 2;
    static public final int FLDFLAG_BROWSER_READONLY = FLDFLAG_COLUMN_BREAK_AFTER * 2;
    public static final int FLDFLAG_IDENTIFIER = FLDFLAG_BROWSER_READONLY * 2;
    static public final int FLDFLAG_STARTCUSTOM = FLDFLAG_IDENTIFIER * 2; // all DialogField "children" will use this

    // flags used to describe what kind of formatting needs to be done to the dialog field
    public static final int DISPLAY_FORMAT = 1;
    public static final int SUBMIT_FORMAT = 2;

    static public int[] CHILD_CARRY_FLAGS = new int[]{FLDFLAG_REQUIRED, FLDFLAG_INVISIBLE, FLDFLAG_READONLY, FLDFLAG_PERSIST, FLDFLAG_CREATEADJACENTAREA, FLDFLAG_SHOWCAPTIONASCHILD};

    static public String CUSTOM_CAPTION = new String();
    static public String GENERATE_CAPTION = "*";

    static public String FIELDTAGPREFIX = "field.";

    static public int fieldCounter = 0;

    private DialogField parent;
    private int arrayIndex = -1;
    private String id;
    private String simpleName;
    private String qualifiedName;
    private SingleValueSource caption;
    private String cookieName;
    private String errorMessage;
    private SingleValueSource defaultValue;
    private List errors;
    private List children;
    private List conditionalActions;
    private List dependentConditions;
    private List clientJavascripts;
    private long flags;
    private DialogFieldPopup popup;
    private SingleValueSource hint;

    /**
     * Creates a dialog field
     */
    public DialogField()
    {
        defaultValue = null;
        errorMessage = null;
        flags = 0;
    }

    /**
     * Creates a dialog field
     *
     * @param aName field name
     * @param aCaption field caption
     */
    public DialogField(String aName, String aCaption)
    {
        this();
        setSimpleName(aName);
        caption = aCaption != null ? ValueSourceFactory.getSingleOrStaticValueSource(aCaption) : null;
    }

    /**
     * Checks to see if the field requires multi-part endcoding
     *
     * @return boolean
     */
    public boolean requiresMultiPartEncoding()
    {
        // if any child requires multi part encoding, then return true (this will take of things recursively)
        if(children != null)
        {
            Iterator c = children.iterator();
            while(c.hasNext())
            {
                DialogField field = (DialogField) c.next();
                if(field.requiresMultiPartEncoding())
                    return true;
            }
        }

        // no child requires it and we don't require it by default, either
        return false;
    }

    /**
     * Checks to see if the default value is a list value source. Returns <code>false</code>
     * always for a <code>DialogField</code> object but child classes requiring a default
     * list value source will return <code>true</code>.
     *
     * @return boolean
     */
    public boolean defaultIsListValueSource()
    {
        return false;
    }

    /**
     * Import dialog XML declaration
     *
     * @param elem DOM Document element representing a dialog
     */
    public void importFromXml(Element elem)
    {
        simpleName = elem.getAttribute("name");
        if(simpleName.length() == 0) simpleName = null;
        setSimpleName(simpleName);

        String captionStr = elem.getAttribute("caption");
        if(captionStr.length() > 0)
            caption = ValueSourceFactory.getSingleOrStaticValueSource(captionStr);

        if(!defaultIsListValueSource())
        {
            String defaultv = elem.getAttribute("default");
            if(defaultv.length() > 0)
            {
                defaultValue = ValueSourceFactory.getSingleOrStaticValueSource(defaultv);
            }
            else
                defaultValue = null;
        }

        String hintStr = elem.getAttribute("hint");
        if(hintStr.length() > 0)
            hint = ValueSourceFactory.getSingleOrStaticValueSource(hintStr);

        if(elem.getAttribute("required").equalsIgnoreCase("yes"))
            setFlag(DialogField.FLDFLAG_REQUIRED);

        if(elem.getAttribute("primary-key").equalsIgnoreCase("yes"))
            setFlag(DialogField.FLDFLAG_PRIMARYKEY);

        if(elem.getAttribute("initial-focus").equalsIgnoreCase("yes"))
            setFlag(DialogField.FLDFLAG_INITIAL_FOCUS);

        // 1. read-only flag of 'yes' will display the field value as a static string
        // within SPAN tags and the INPUT will be hidden.
        // 2. read-only flag of 'browser' will display the field value as
        // an INPUT with readonly flag set.
        String readonly = elem.getAttribute("read-only");
        if(readonly.equalsIgnoreCase("yes"))
            setFlag(DialogField.FLDFLAG_READONLY);
        else if(readonly.equalsIgnoreCase("browser"))
            setFlag(DialogField.FLDFLAG_BROWSER_READONLY);

        if(elem.getAttribute("visible").equalsIgnoreCase("no"))
            setFlag(DialogField.FLDFLAG_INVISIBLE);

        if(elem.getAttribute("hidden").equalsIgnoreCase("yes"))
            setFlag(DialogField.FLDFLAG_INPUT_HIDDEN);

        if(elem.getAttribute("persist").equalsIgnoreCase("yes"))
            setFlag(DialogField.FLDFLAG_PERSIST);

        if(elem.getAttribute("show-child-caption").equalsIgnoreCase("yes"))
            setFlag(DialogField.FLDFLAG_SHOWCAPTIONASCHILD);

        if(elem.getAttribute("create-adjacent-area").equalsIgnoreCase("yes"))
            setFlag(DialogField.FLDFLAG_CREATEADJACENTAREA);

        if(elem.getAttribute("identifier").equalsIgnoreCase("yes"))
            setFlag(DialogField.FLDFLAG_IDENTIFIER);

        String colBreak = elem.getAttribute("col-break");
        if(colBreak.length() > 0)
        {
            if(colBreak.equals("before"))
                setFlag(DialogField.FLDFLAG_COLUMN_BREAK_BEFORE);
            else if(colBreak.equals("after") || colBreak.equals("yes"))
                setFlag(DialogField.FLDFLAG_COLUMN_BREAK_AFTER);
        }

        importChildrenFromXml(elem);
    }

    /**
     * Import children nodes of a dialog element
     *
     * @param elem dialog element
     */
    public void importChildrenFromXml(Element elem)
    {
        NodeList children = elem.getChildNodes();
        for(int n = 0; n < children.getLength(); n++)
        {
            Node node = children.item(n);
            if(node.getNodeType() != Node.ELEMENT_NODE)
                continue;

            String childName = node.getNodeName();
            if(childName.startsWith(FIELDTAGPREFIX))
            {
                Element fieldElem = (Element) node;
                DialogField field = DialogFieldFactory.createField(childName);
                if(field != null)
                    field.importFromXml(fieldElem);
                else
                    field = new SelectField("error", "Unable to create field of type '" + childName, SelectField.SELECTSTYLE_COMBO, ValueSourceFactory.getListValueSource("dialog-field-types:"));
                addChildField(field);
            }
            else if(childName.equals("conditional"))
            {
                importConditionalFromXml((Element) node);
            }
            else if(childName.equals("popup"))
            {
                importPopupFromXml((Element) node);
            }
            else if(childName.equals("client-js"))
            {
                importCustomJavaScriptFromXml((Element) node);
            }
        }
    }

    /**
     * Reads the XML for Custom Javascript configuration assigned to a dialog field
     *
     * @param elem client-js node
     * @return none
     */
    public void importCustomJavaScriptFromXml(Element elem)
    {
        // what time of event should this custom JS respond to
        String event = elem.getAttribute("event");
        if(event == null || event.length() == 0)
        {
            addErrorMessage("No 'event' specified for custom Javascript.");
            return;
        }
        else if(!DialogFieldClientJavascript.isValidEvent(event))
        {
            addErrorMessage("Invalid 'event' specified for custom Javascript.");
            return;
        }

        // whether or not if this JS script should overwrite or extend the existing default JS
        // assigned to the event
        String type = elem.getAttribute("type");
        if(type == null || type.length() == 0)
        {
            addErrorMessage("No 'type' specified for custom Javascript.");
            return;
        }
        else if(!type.equals("extends") && !type.equals("override"))
        {
            addErrorMessage("Invalid 'type' specified for custom Javascript.");
            return;
        }

        // get the custom script
        String script = elem.getAttribute("js-expr");
        if(script == null || script.length() == 0)
        {
            addErrorMessage("No custom Javascript defined.");
            return;
        }
        DialogFieldClientJavascript customJS = new DialogFieldClientJavascript();
        customJS.setEvent(event);
        customJS.setType(type);
        customJS.setScript(script);

        this.addClientJavascript(customJS);
    }

    /**
     * Imports XML nodes representing conditional logic for a dialog element
     *
     * @param elem dialog element
     */
    public void importConditionalFromXml(Element elem)
    {
        String action = elem.getAttribute("action");
        if(action == null || action.length() == 0)
        {
            addErrorMessage("No 'action' specified for conditional.");
            return;
        }

        DialogFieldConditionalAction actionInst = DialogFieldFactory.createConditional(action);
        if(actionInst != null)
        {
            int conditionalItem = (conditionalActions == null ? 0 : conditionalActions.size()) + 1;
            if(actionInst.importFromXml(this, elem, conditionalItem))
                addConditionalAction(actionInst);
        }
        else
        {
            addErrorMessage("Conditional action '" + action + "' unknown.");
        }
    }
    /**
     * Imports XML nodes representing popup window logic for a dialog element
     *
     * @param elem dialog element
     */
    public void importPopupFromXml(Element elem)
    {
        String action = elem.getAttribute("action");
        if(action.length() == 0)
        {
            addErrorMessage("Popup has no associated 'action' (URL).");
            return;
        }

        String[] fillFields = null;
        String fill = elem.getAttribute("fill");
        if(fill.length() == 0)
        {
            fillFields = new String[]{getQualifiedName()};
        }
        else
        {
            if(fill.indexOf(",") > 0)
            {
                int fillCount = 0;
                StringTokenizer st = new StringTokenizer(fill, ",");
                while(st.hasMoreTokens())
                {
                    st.nextToken();
                    fillCount++;
                }

                int fillIndex = 0;
                fillFields = new String[fillCount];
                st = new StringTokenizer(fill, ",");
                while(st.hasMoreTokens())
                {
                    fillFields[fillIndex] = st.nextToken();
                    fillIndex++;
                }
            }
            else
                fillFields = new String[]{fill};
        }

        popup = new DialogFieldPopup(action, fillFields);
        String imgsrc = elem.getAttribute("image-src");
        if(imgsrc.length() > 0)
            popup.setImageUrl(imgsrc);
    }

    public void invalidate(DialogContext dc, String message)
    {
        dc.addErrorMessage(parent != null ? parent : this, message);
    }

    /**
     * Gets the parent dialog field
     *
     * @return DialogField
     */
    public final DialogField getParent()
    {
        return parent;
    }

    /**
     * Sets the parent dialog field
     *
     * @param newParent the parent field
     */
    public void setParent(DialogField newParent)
    {
        parent = newParent;
    }

    public final String getId()
    {
        return id;
    }

    /**
     * Gets the simple name of the dialog
     *
     * @return String
     */
    public final String getSimpleName()
    {
        return simpleName;
    }

    /**
     * Gets the qualified name of the dialog
     *
     * @return String
     */
    public final String getQualifiedName()
    {
        return qualifiedName;
    }

    /**
     * Sets the simple name of the dialog
     *
     * @param newName new simple name
     */
    public void setSimpleName(String newName)
    {
        simpleName = newName;
        if(simpleName != null)
        {
            id = Dialog.PARAMNAME_CONTROLPREFIX + simpleName;
            setQualifiedName(simpleName);
        }
    }

    /**
     * Sets the qualified name of the dialog
     *
     * @param newName new qualified name
     */
    public void setQualifiedName(String newName)
    {
        qualifiedName = newName;
        if(qualifiedName != null)
            id = Dialog.PARAMNAME_CONTROLPREFIX + qualifiedName;
    }

    /**
     * Gets the cookie name associated with the dialog
     *
     * @return String cookie name
     */
    public final String getCookieName()
    {
        return "DLG_" + parent.getSimpleName() + "_FLD_" + (cookieName.length() > 0 ? cookieName : simpleName);
    }

    /**
     * Sets the cookie name associated with the dialog
     *
     * @param name cookie name
     */
    public void setCookieName(String name)
    {
        cookieName = name;
    }

    /**
     * Gets the caption of the dialog as a single value source
     *
     * @return SingleValueSource
     */
    public SingleValueSource getCaptionSource()
    {
        return caption;
    }

    /**
     * Gets the caption string of the dialog
     *
     * @param dc dialog context
     * @return String
     */
    public String getCaption(DialogContext dc)
    {
        return caption != null ? caption.getValue(dc) : null;
    }

    /**
     * Sets the caption of the dialog from a value source
     *
     * @param value value source object from which the caption is being extracted
     */
    public void setCaption(SingleValueSource value)
    {
        caption = value;
    }

    /**
     * Sets the caption of the dialog from a value source
     *
     * @param value value source from which the caption string is being extracted
     */
    public void setCaption(String value)
    {
        setCaption(value != null ? ValueSourceFactory.getSingleOrStaticValueSource(value) : null);
    }

    /**
     * Gets the hint string associated with the dialog field
     *
     * @return String
     */
    public final String getHint(DialogContext dc)
    {
        return hint != null ? hint.getValue(dc) : null;
    }

    /**
     * Sets the hint string associated with the dialog field
     *
     * @param value hint string
     */
    public void setHint(SingleValueSource value)
    {
        hint = value;
    }

    /**
     * Sets the hint string associated with the dialog field
     *
     * @param value hint string
     */
    public void setHint(String value)
    {
        setHint(value != null ? ValueSourceFactory.getSingleOrStaticValueSource(value) : null);
    }

    /**
     * Gets the display error message when a validation fails
     *
     * @return String
     */
    public final String getErrorMessage()
    {
        return errorMessage;
    }

    /**
     * Sets the error message for display when a validation fails
     *
     * @param newMessage error message string
     */
    public void setErrorMessage(String newMessage)
    {
        errorMessage = newMessage;
    }

    /**
     * Gets the default value of the field as a value source
     *
     * @return SingleValueSource    value source containing the field's value
     */
    public final SingleValueSource getDefaultValue()
    {
        return defaultValue;
    }

    /**
     * Sets the default value for the field
     *
     * @param value value source containing the value
     */
    public void setDefaultValue(SingleValueSource value)
    {
        defaultValue = value;
    }

    public final DialogFieldPopup getPopup()
    {
        return popup;
    }

    public void setPopup(DialogFieldPopup value)
    {
        popup = value;
    }

    /**
     * Gets a list of children fields
     *
     * @return List list of children fields
     */
    public final List getChildren()
    {
        return children;
    }

    /**
     * Adds a child field.
     *
     * @param field child field
     */
    public void addChildField(DialogField field)
    {
        for(int i = 0; i < CHILD_CARRY_FLAGS.length; i++)
        {
            int flag = CHILD_CARRY_FLAGS[i];
            if((flags & flag) != 0)
                field.setFlag(flag);
        }

        if(children == null) children = new ArrayList();
        children.add(field);

        field.setParent(this);
        if(qualifiedName != null)
            field.setQualifiedName(qualifiedName + "." + field.getSimpleName());
    }

    /**
     * Gets a list of errors
     *
     * @return List list of errors
     */
    public final List getErrors()
    {
        return errors;
    }

    /**
     * Adds a error message for the field
     *
     * @param msg error message
     */
    public final void addErrorMessage(String msg)
    {
        if(errors == null) errors = new ArrayList();
        errors.add(msg);
    }

    /**
     * Get a list of conditional actions
     *
     * @return List a list of conditional actions
     */
    public final List getConditionalActions()
    {
        return conditionalActions;
    }

    /**
     * Adds a conditional action
     *
     * @param action conditional action object
     */
    public void addConditionalAction(DialogFieldConditionalAction action)
    {
        if(conditionalActions == null) conditionalActions = new ArrayList();

        if(action instanceof DialogFieldConditionalData || action instanceof DialogFieldConditionalApplyFlag)
            setFlag(FLDFLAG_HAS_CONDITIONAL_DATA);

        conditionalActions.add(action);
    }

    /**
     * Gets all the javascripts defined for this field
     *
     * @return ArrayList
     */
    public final List getClientJavascripts()
    {
        return this.clientJavascripts;
    }

    /**
     * Adds a javascript to the list of scripts defined for this field
     *
     * @param script custom js object
     */
    public void addClientJavascript(DialogFieldClientJavascript script)
    {
        if(this.clientJavascripts == null)
            this.clientJavascripts = new ArrayList();
        this.clientJavascripts.add(script);

    }

    /**
     * Returns the current dialog field or one of its children which has the passed in qualified name
     *
     * @param qualifiedName qualified name
     * @return DialogField
     */
    public DialogField findField(String qualifiedName)
    {
        if(this.qualifiedName != null && this.qualifiedName.equals(qualifiedName))
            return this;

        if(children != null)
        {
            Iterator i = children.iterator();
            while(i.hasNext())
            {
                DialogField field = (DialogField) i.next();
                DialogField found = field.findField(qualifiedName);
                if(found != null)
                    return found;
            }
        }

        return null;
    }

    /**
     * Set the qualified name of the field and its' children fields. The qualified name of a field is a sum of
     * the parent field's qualified name and the field's simple name.
     *
     * @param dialog parent dialog
     */
    public void finalizeQualifiedName(Dialog dialog)
    {
        String newQName = simpleName;
        if(parent != null && simpleName != null)
            newQName = parent.getQualifiedName() + "." + simpleName;
        if(newQName != null)
            setQualifiedName(newQName);

        if(children != null)
        {
            Iterator i = children.iterator();
            while(i.hasNext())
            {
                DialogField field = (DialogField) i.next();
                field.finalizeQualifiedName(dialog);
            }
        }
    }

    /**
     * Finalize the dialog field's contents: loops through each conditional action of the field to
     * assign partner fields and loops through each child field to finalize their contents.
     *
     * @param dialog parent dialog
     */
    public void finalizeContents(Dialog dialog)
    {
        if(conditionalActions != null)
        {
            Iterator i = conditionalActions.iterator();
            while(i.hasNext())
            {
                DialogFieldConditionalAction action = (DialogFieldConditionalAction) i.next();
                DialogField partnerField = dialog.findField(action.getPartnerFieldName());
                if(partnerField != null)
                    action.setPartnerField(partnerField);
            }
        }

        if(children != null)
        {
            Iterator c = children.iterator();
            while(c.hasNext())
            {
                DialogField field = (DialogField) c.next();
                field.finalizeContents(dialog);
            }
        }
    }

    public final List getDependentConditions()
    {
        return dependentConditions;
    }

    public void addDependentCondition(DialogFieldConditionalAction action)
    {
        if(dependentConditions == null) dependentConditions = new ArrayList();
        dependentConditions.add(action);
    }

    public final long getFlags()
    {
        return flags;
    }
    /**
     * Check if a flag is set
     *
     * @param flag      bit-mapped flag
     * @return boolean  True if the passed in flag is set, else False
     */
    public final boolean flagIsSet(long flag)
    {
        return (flags & flag) == 0 ? false : true;
    }

    /**
     * Set a flag
     *
     * @param flag bit-mapped flag
     */
    public final void setFlag(long flag)
    {
        flags |= flag;
        if(children != null)
        {
            Iterator i = children.iterator();
            while(i.hasNext())
            {
                ((DialogField) i.next()).setFlag(flag);
            }
        }
    }

    /**
     * Cleat a flag
     *
     * @param flag bit-mapped flag
     */
    public final void clearFlag(long flag)
    {
        flags &= ~flag;
        if(children != null)
        {
            Iterator i = children.iterator();
            while(i.hasNext())
            {
                ((DialogField) i.next()).clearFlag(flag);
            }
        }
    }

    /**
     * Indicates whether or not the field is a required field. It checks the  <code>FLDFLAG_REQUIRED</code>
     * flag of the field and its' children.
     *
     * @param dc  dialog context
     */
    public final boolean isRequired(DialogContext dc)
    {
        String qName = getQualifiedName();
        if(qName != null)
        {
            if(dc.flagIsSet(qName, FLDFLAG_REQUIRED)) return true;
        }
        else
        {
            if(flagIsSet(FLDFLAG_REQUIRED)) return true;
        }

        if(children != null)
        {
            Iterator i = children.iterator();
            while(i.hasNext())
            {
                if(((DialogField) i.next()).isRequired(dc))
                    return true;
            }
        }
        return false;
    }

    /**
     * Checks whether or not the field is visible. The check is done by seeing if the invisible flag, <code>FLDFLAG_INVISIBLE</code>
     * is set or not and by making sure each partner field of its' conditionals have a value or not.
     *
     * @param dc dialog context
     * @return boolean True if the field is visible
     */
    public final boolean isVisible(DialogContext dc)
    {
        if(flagIsSet(FLDFLAG_HAS_CONDITIONAL_DATA))
        {
            Iterator i = conditionalActions.iterator();
            while(i.hasNext())
            {
                DialogFieldConditionalAction action = (DialogFieldConditionalAction) i.next();
                if(action instanceof DialogFieldConditionalData)
                {
                    // if the partner field doesn't have data yet, hide this field
                    if(isRequired(dc))
                    {
                        String value = dc.getValue(action.getPartnerField());
                        if(value == null || value.length() == 0)
                            return false;
                    }
                }
            }
        }
        String qName = getQualifiedName();
        if(qName != null)
        {
            return dc.flagIsSet(qName, FLDFLAG_INVISIBLE) ? false : true;
        }
        else
        {
            return flagIsSet(FLDFLAG_INVISIBLE) ? false : true;
        }
    }

    public final boolean isReadOnly(DialogContext dc)
    {
        String qName = getQualifiedName();
        if(qName != null)
            return dc.flagIsSet(qName, FLDFLAG_READONLY);
        else
            return flagIsSet(FLDFLAG_READONLY);
    }

    public final boolean isBrowserReadOnly(DialogContext dc)
    {
        String qName = getQualifiedName();
        if(qName != null)
            return dc.flagIsSet(qName, FLDFLAG_BROWSER_READONLY);
        else
            return flagIsSet(FLDFLAG_BROWSER_READONLY);
    }

    public final boolean isInputHidden(DialogContext dc)
    {
        if(simpleName != null)
            return dc.flagIsSet(getQualifiedName(), FLDFLAG_INPUT_HIDDEN);
        else
            return flagIsSet(FLDFLAG_INPUT_HIDDEN);
    }

    public final boolean persistValue()
    {
        return (flags & FLDFLAG_PERSIST) == 0 ? false : true;
    }

    public final boolean showCaptionAsChild()
    {
        return (flags & FLDFLAG_SHOWCAPTIONASCHILD) == 0 ? false : true;
    }

    public static final String escapeHTML(String s)
    {
        if(s == null) return null;
        StringBuffer sb = new StringBuffer();
        int n = s.length();
        for (int i = 0; i < n; i++)
        {
            char c = s.charAt(i);
            switch (c)
            {
                case '<': sb.append("&lt;"); break;
                case '>': sb.append("&gt;"); break;
                case '&': sb.append("&amp;"); break;
                case '"': sb.append("&quot;"); break;
                case 'à': sb.append("&agrave;");break;
                case 'À': sb.append("&Agrave;");break;
                case 'â': sb.append("&acirc;");break;
                case 'Â': sb.append("&Acirc;");break;
                case 'ä': sb.append("&auml;");break;
                case 'Ä': sb.append("&Auml;");break;
                case 'å': sb.append("&aring;");break;
                case 'Å': sb.append("&Aring;");break;
                case 'æ': sb.append("&aelig;");break;
                case 'Æ': sb.append("&AElig;");break;
                case 'ç': sb.append("&ccedil;");break;
                case 'Ç': sb.append("&Ccedil;");break;
                case 'é': sb.append("&eacute;");break;
                case 'É': sb.append("&Eacute;");break;
                case 'è': sb.append("&egrave;");break;
                case 'È': sb.append("&Egrave;");break;
                case 'ê': sb.append("&ecirc;");break;
                case 'Ê': sb.append("&Ecirc;");break;
                case 'ë': sb.append("&euml;");break;
                case 'Ë': sb.append("&Euml;");break;
                case 'ï': sb.append("&iuml;");break;
                case 'Ï': sb.append("&Iuml;");break;
                case 'ô': sb.append("&ocirc;");break;
                case 'Ô': sb.append("&Ocirc;");break;
                case 'ö': sb.append("&ouml;");break;
                case 'Ö': sb.append("&Ouml;");break;
                case 'ø': sb.append("&oslash;");break;
                case 'Ø': sb.append("&Oslash;");break;
                case 'ß': sb.append("&szlig;");break;
                case 'ù': sb.append("&ugrave;");break;
                case 'Ù': sb.append("&Ugrave;");break;
                case 'û': sb.append("&ucirc;");break;
                case 'Û': sb.append("&Ucirc;");break;
                case 'ü': sb.append("&uuml;");break;
                case 'Ü': sb.append("&Uuml;");break;
                case '®': sb.append("&reg;");break;
                case '©': sb.append("&copy;");break;
                case '€': sb.append("&euro;"); break;

                default: sb.append(c); break;
            }
        }
        return sb.toString();
    }

    public String getHiddenControlHtml(DialogContext dc)
    {
        String value = dc.getValue(this);
        return "<input type='hidden' name='" + getId() + "' value=\"" + (value != null ? escapeHTML(value) : "") + "\">";
    }

    public void renderControlHtml(Writer writer, DialogContext dc) throws IOException
    {
        if(flagIsSet(FLDFLAG_INPUT_HIDDEN))
        {
            getHiddenControlHtml(dc);
            return;
        }

        if(children == null)
            return;

        dc.getSkin().renderCompositeControlsHtml(writer, dc, this);
    }

    public boolean needsValidation(DialogContext dc)
    {
        if(flagIsSet(FLDFLAG_HAS_CONDITIONAL_DATA))
            return true;

        if(children == null)
            return isRequired(dc);

        int validateFieldsCount = 0;
        Iterator i = children.iterator();
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
        if(flagIsSet(FLDFLAG_HAS_CONDITIONAL_DATA))
        {
            Iterator i = conditionalActions.iterator();
            while(i.hasNext())
            {
                DialogFieldConditionalAction action = (DialogFieldConditionalAction) i.next();
                if(action instanceof DialogFieldConditionalData)
                {
                    // if the partner field doesn't have data, then this field is "invalid"
                    if(isRequired(dc) && dc.getValue(action.getPartnerField()) == null)
                        return false;
                }
            }
        }
        if(children == null)
            return true;

        int invalidFieldsCount = 0;
        Iterator i = children.iterator();
        while(i.hasNext())
        {
            DialogField field = (DialogField) i.next();
            if(field.isVisible(dc) && (!field.isValid(dc)))
                invalidFieldsCount++;
        }
        return invalidFieldsCount == 0 ? true : false;
    }

    /**
     * Format the dialog value after it has been validated and is ready for submission
     *
     * @param value dialog field value
     * @return String
     */
    public String formatSubmitValue(String value)
    {
        return value;
    }

    /**
     * Format the dialog value for every dialog stage except before submission
     *
     * @param value dialog field value
     * @return String
     */
    public String formatDisplayValue(String value)
    {
        return value;
    }


    public Object getValueAsObject(String value)
    {
        return value;
    }

    public Object getValueForSqlBindParam(String value)
    {
        return getValueAsObject(value);
    }

    public void makeStateChanges(DialogContext dc, int stage)
    {
        if(stage == DialogContext.STATECALCSTAGE_INITIAL && conditionalActions != null)
        {
            for(int i = 0; i < conditionalActions.size(); i++)
            {
                DialogFieldConditionalAction action = (DialogFieldConditionalAction) conditionalActions.get(i);
                if(action instanceof DialogFieldConditionalApplyFlag)
                    ((DialogFieldConditionalApplyFlag) action).applyFlags(dc);
            }
        }

        if(children != null)
        {
            Iterator i = children.iterator();
            while(i.hasNext())
            {
                DialogField field = (DialogField) i.next();
                field.makeStateChanges(dc, stage);
            }
        }
    }

    public void populateValue(DialogContext dc, int formatType)
    {
        if(id == null) return;

        String value = dc.getValue(this);
        if(value == null)
            value = dc.getRequest().getParameter(id);
        if(dc.getRunSequence() == 1)
        {
            if((value != null && value.length() == 0 && defaultValue != null) ||
                    (value == null && defaultValue != null))
                value = defaultValue.getValueOrBlank(dc);
        }
        if(formatType == DialogField.DISPLAY_FORMAT)
            dc.setValue(this, this.formatDisplayValue(value));
        else if(formatType == DialogField.SUBMIT_FORMAT)
            dc.setValue(this, this.formatSubmitValue(value));

        if(children == null) return;

        Iterator i = children.iterator();
        while(i.hasNext())
        {
            DialogField field = (DialogField) i.next();
            if(field.isVisible(dc)) field.populateValue(dc, formatType);
        }
    }

    /**
     * Produces JavaScript code to handle Client-side events for the dialog field
     *
     */
    public String getJavaScriptDefn(DialogContext dc)
    {
        String fieldClassName = this.getClass().getName();
        String fieldQualfName = this.getQualifiedName();
        String js =
                "field = new DialogField(\"" + fieldClassName + "\", \"" + this.getId() + "\", \"" + this.getSimpleName() + "\", \"" + fieldQualfName + "\", \"" + this.getCaption(dc) + "\", " + dc.getFieldFlags(fieldQualfName) + ");\n" +
                "dialog.registerField(field);\n";
        String customStr = this.getEventJavaScriptFunctions(dc);
        customStr += this.getCustomJavaScriptDefn(dc);
        if(customStr != null)
            js += customStr;

        List dependentConditions = this.getDependentConditions();
        if(dependentConditions != null)
        {
            StringBuffer dcJs = new StringBuffer();
            Iterator i = dependentConditions.iterator();
            while(i.hasNext())
            {
                DialogFieldConditionalAction o = (DialogFieldConditionalAction) i.next();

                if(o instanceof DialogFieldConditionalDisplay)
                {
                    DialogFieldConditionalDisplay action = (DialogFieldConditionalDisplay) o;
                    if(action.getPartnerField().isVisible(dc))
                        dcJs.append("field.dependentConditions[field.dependentConditions.length] = new DialogFieldConditionalDisplay(\"" + action.getSourceField().getQualifiedName()
                                + "\", \"" + action.getPartnerField().getQualifiedName() + "\", \"" + action.getExpression() + "\");\n");
                }
            }
            js = js + dcJs.toString();
        }

        List children = this.getChildren();
        if(children != null)
        {
            StringBuffer childJs = new StringBuffer();
            Iterator i = children.iterator();
            while(i.hasNext())
            {
                DialogField child = (DialogField) i.next();
                childJs.append(child.getJavaScriptDefn(dc));
            }
            js = js + childJs.toString();
        }

        return js;
    }

    /**
     * Retrieves user defined java script strings for this field and creates JS functions
     * out of them
     */
    public String getEventJavaScriptFunctions(DialogContext dc)
    {
        String ret = "";

        List jsList = this.getClientJavascripts();
        if(jsList != null)
        {
            String jsType = "";
            String eventName = "";

            StringBuffer jsBuffer = new StringBuffer();
            Iterator i = jsList.iterator();
            while(i.hasNext())
            {
                DialogFieldClientJavascript jsObject = (DialogFieldClientJavascript) i.next();
                String script = (jsObject.getScript() != null ? jsObject.getScript().getValue(dc) : null);
                eventName = com.netspective.sparx.util.xml.XmlSource.xmlTextToJavaIdentifier(jsObject.getEvent().getValue(dc), false);
                // append function signature
                if(script != null)
                {
                    jsBuffer.append("field.customHandlers." + eventName + " = new Function(\"field\", \"control\", \"" +
                            jsObject.getScript().getValue(dc) + "\");\n");
                    jsBuffer.append("field.customHandlers." + eventName + "Type = '" + jsObject.getType().getValue(dc) + "';\n");
                }
            }
            ret = ret + jsBuffer.toString();
        }
        return ret;
    }

    /**
     * Empty method. Overwritten by extending classes that define extra Javascript definitions.
     */
    public String getCustomJavaScriptDefn(DialogContext dc)
    {
        return "";
    }

    /**
     * Produces Java code when a custom DialogContext is created
     */
    public DialogContextMemberInfo createDialogContextMemberInfo(String dataTypeName)
    {
        DialogContextMemberInfo mi = new DialogContextMemberInfo(this.getQualifiedName(), dataTypeName);

        String memberName = mi.getMemberName();
        String fieldName = mi.getFieldName();

        mi.addJavaCode("\tpublic boolean is" + memberName + "ValueSet() { return hasValue(\"" + fieldName + "\"); }\n");
        mi.addJavaCode("\tpublic boolean is" + memberName + "FlagSet(long flag) { return flagIsSet(\"" + fieldName + "\", flag); }\n");
        mi.addJavaCode("\tpublic void set" + memberName + "Flag(long flag) { setFlag(\"" + fieldName + "\", flag); }\n");
        mi.addJavaCode("\tpublic void clear" + memberName + "Flag(long flag) { clearFlag(\"" + fieldName + "\", flag); }\n");
        mi.addJavaCode("\tpublic String get" + memberName + "RequestParam() { return request.getParameter(\"" + Dialog.PARAMNAME_CONTROLPREFIX + fieldName + "\"); }\n");
        mi.addJavaCode("\tpublic DialogField get" + memberName + "Field() { return getField(\"" + fieldName + "\"); }\n");
        mi.addJavaCode("\tpublic DialogContext.DialogFieldState get" + memberName + "FieldState() { return getFieldState(\"" + fieldName + "\"); }\n");
        mi.addJavaCode("\tpublic void add" + memberName + "ErrorMsg(String msg) { addErrorMessage(\"" + fieldName + "\", msg); }\n");

        return mi;
    }

    /**
     * Produces Java code when a custom DialogContext is created
     * The default method produces nothing; all the subclasses must define what they need.
     */
    public DialogContextMemberInfo getDialogContextMemberInfo()
    {
        if(children == null) return null;

        DialogContextMemberInfo mi = createDialogContextMemberInfo("children");
        Iterator i = children.iterator();
        while(i.hasNext())
        {
            DialogField field = (DialogField) i.next();
            DialogContextMemberInfo childMI = field.getDialogContextMemberInfo();
            if(childMI == null)
                continue;

            String[] childImports = childMI.getImportModules();
            if(childImports != null)
            {
                for(int m = 0; m < childImports.length; m++)
                    mi.addImportModule(childImports[m]);
            }

            mi.addJavaCode(childMI.getCode());
        }

        return mi;
    }
}
