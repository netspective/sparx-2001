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
 * $Id: dialog.js,v 1.8 2002-10-03 14:55:23 shahid.shah Exp $
 */

var DIALOGFIELD_PREFIX = '_dc';
var FIELDROW_PREFIX = "_dfr.";
var GRIDFIELDROW_PREFIX = "_dgfr.";
var GRIDHEADROW_PREFIX = "_dghr.";
var FIELDNAME_IGNORE_VALIDATION = DIALOGFIELD_PREFIX + ".ignore_val";

var ALLOW_CLIENT_VALIDATION        = true;
var TRANSLATE_ENTER_KEY_TO_TAB_KEY = false;
var ENABLE_KEYPRESS_FILTERS        = true;

function setAllowValidation(value)
{
    ALLOW_CLIENT_VALIDATION = value;
}

// **************************************************************************
// BrowserCheck class
// **************************************************************************
function BrowserCheck()
{
    //Browsercheck (needed)
    this.ver = navigator.appVersion;
    this.agent = navigator.userAgent;
    this.dom = document.getElementById? true : false;
    this.ie5 = (this.ver.indexOf("MSIE 5")>-1 && this.dom)? true : false;
    this.ie6 = (this.ver.indexOf("MSIE 6")>-1 && this.dom)? true : false;
    this.ie4 = (document.all && !this.dom)? true : false;
    this.ie = this.ie4 || this.ie5 || this.ie6;
    this.mac = this.agent.indexOf("Mac") > -1;
    this.opera5 = this.agent.indexOf("Opera 5") > -1;
    this.ns6 = (this.dom && parseInt(this.ver) >= 5) ? true : false;
    this.ns4 = (document.layers && !this.dom)? true : false;
    this.browser = (this.ie6 || this.ie5 || this.ie4 || this.ns4 || this.ns6 || this.opera5 || this.dom);
    return this
}
var browser = new BrowserCheck() //Making browsercheck object

// Get the dialog field control for IE4
function getControl_IE4(dialog, id)
{
    return document.all.item(id);
}

// Get the dialog field control for DOM browsers such as IE5, IE6 and NS6
function getControl_Dom(dialog, id)
{
    if (id.substring(0,3) == DIALOGFIELD_PREFIX)
        return document.getElementById(dialog.name).elements[id];
    else
        return document.getElementById(id);
}

// Get the dialog field control for Netscape 4
function getControl_NS4(dialog, id)
{
    // a dialog field because the ID starts with a PREFIX
    if (id.substring(0,3) == DIALOGFIELD_PREFIX)
        return document.forms[dialog.name].elements[id];
}

// based on which browser is currently running, get the control using the appropriate function
function getControl(dialog, id)
{
    if (browser.ie5 || browser.ie6 || browser.ns6)
    {
        return getControl_Dom(dialog, id);
    }
    else if (browser.ns4)
    {
        return getControl_NS4(dialog, id);
    }
    else if (browser.ie4)
    {
        return getControl_IE4(dialog, id);
    }
}

function radioButtonSelected(fieldName, value)
{
	// radio buttons are named {fieldName}{value} like "_dc.fieldName0" and "_dc.fieldName1", etc
	var fieldId = DIALOGFIELD_PREFIX + "." + fieldName + value;
	var control = getControl(activeDialog, fieldId);
	if(control == null)
	{
		alert("Field '" + fieldId + "' not found in active dialog -- can't check for radio button value");
		return false;
	}

	return control.checked;
}

//****************************************************************************
// FieldType class
//****************************************************************************

function FieldType(name, onFinalizeDefn, onValidate, onChange, onFocus, onBlur, onKeyPress, onClick)
{
    this.type = name;
    this.finalizeDefn = onFinalizeDefn;
    this.isValid = onValidate;
    this.getFocus = onFocus;
    this.valueChanged = onChange;
    this.keyPress = onKeyPress;
    this.loseFocus = onBlur;
    this.click = onClick;
}

var FIELD_TYPES = new Array();

function addFieldType(name, onFinalizeDefn, onValidate, onChange, onFocus, onBlur, onKeyPress, onClick)
{
    FIELD_TYPES[name] = new FieldType(name, onFinalizeDefn, onValidate, onChange, onFocus, onBlur, onKeyPress, onClick);
}

//****************************************************************************
// Dialog class
//****************************************************************************

function Dialog(name)
{
    this.name = name;
    this.fields = new Array();              // straight list (simple array)
    this.fieldsById = new Array();          // hash -- value is field
    this.fieldsByQualName = new Array();    // hash -- value is field

    // the remaining are object-based methods
    this.registerField = Dialog_registerField;
    this.finalizeContents = Dialog_finalizeContents;
    this.isValid = Dialog_isValid;
    this.getFieldControl = Dialog_getFieldControl;
    this.allowValidation = Dialog_allowValidation;
}

function Dialog_registerField(field)
{
    field.fieldIndex = this.fields.length;
    this.fields[field.fieldIndex] = field;
    this.fieldsById[field.controlId] = field;
    this.fieldsByQualName[field.qualifiedName] = field;

    if(field.fieldIndex > 0)
        field.prevFieldIndex = field.fieldIndex-1;
    field.nextFieldIndex = field.fieldIndex+1;
}

function Dialog_finalizeContents()
{
    var dialogFields = this.fields;
    for(var i = 0; i < dialogFields.length; i++)
        dialogFields[i].finalizeContents(this);
}

function Dialog_allowValidation()
{
    return ALLOW_CLIENT_VALIDATION;
}

function Dialog_isValid()
{
    var dialogFields = this.fields;
    for(var i = 0; i < dialogFields.length; i++)
    {
        var field = dialogFields[i];
        if(field.requiresPreSubmit)
            field.doPreSubmit();
    }

    if(! this.allowValidation())
        return true;

    var isValid = true;
    for(var i = 0; i < dialogFields.length; i++)
    {
        var field = dialogFields[i];
        if(! field.isValid())
        {
            isValid = false;
            break;
        }
    }

    return isValid;
}

function Dialog_getFieldControl(qualifiedName)
{
	var field = this.fieldsByQualName[qualifiedName];
	if(field != null)
		return field.getControl(this);
	else
		return null;
}

var activeDialog = null;

function setActiveDialog(dialog)
{
    activeDialog = dialog;
}

//****************************************************************************
// DialogField class
//****************************************************************************

// These constants MUST be kept identical to what is in com.netspective.sparx.form.DialogField

var FLDFLAG_REQUIRED                           = 1;
var FLDFLAG_PRIMARYKEY                         = FLDFLAG_REQUIRED * 2;
var FLDFLAG_INVISIBLE                          = FLDFLAG_PRIMARYKEY * 2;
var FLDFLAG_READONLY                           = FLDFLAG_INVISIBLE * 2;
var FLDFLAG_INITIAL_FOCUS                      = FLDFLAG_READONLY * 2;
var FLDFLAG_PERSIST                            = FLDFLAG_INITIAL_FOCUS * 2;
var FLDFLAG_CREATEADJACENTAREA                 = FLDFLAG_PERSIST * 2;
var FLDFLAG_SHOWCAPTIONASCHILD                 = FLDFLAG_CREATEADJACENTAREA * 2;
var FLDFLAG_INPUT_HIDDEN                       = FLDFLAG_SHOWCAPTIONASCHILD * 2;
var FLDFLAG_HAS_CONDITIONAL_DATA               = FLDFLAG_INPUT_HIDDEN * 2;
var FLDFLAG_COLUMN_BREAK_BEFORE                = FLDFLAG_HAS_CONDITIONAL_DATA * 2;
var FLDFLAG_COLUMN_BREAK_AFTER                 = FLDFLAG_COLUMN_BREAK_BEFORE * 2;
var FLDFLAG_BROWSER_READONLY                   = FLDFLAG_COLUMN_BREAK_AFTER * 2;
var FLDFLAG_IDENTIFIER                         = FLDFLAG_BROWSER_READONLY * 2;
var FLDFLAG_READONLY_HIDDEN_UNLESS_HAS_DATA    = FLDFLAG_IDENTIFIER * 2;
var FLDFLAG_READONLY_INVISIBLE_UNLESS_HAS_DATA = FLDFLAG_READONLY_HIDDEN_UNLESS_HAS_DATA * 2;
var FLDFLAG_STARTCUSTOM                        = FLDFLAG_READONLY_INVISIBLE_UNLESS_HAS_DATA * 2;// all DialogField "children" will use this
// These constants MUST be kept identical to what is in com.netspective.sparx.form.field.SelectField

var SELECTSTYLE_RADIO      = 0;
var SELECTSTYLE_COMBO      = 1;
var SELECTSTYLE_LIST       = 2;
var SELECTSTYLE_MULTICHECK = 3;
var SELECTSTYLE_MULTILIST  = 4;
var SELECTSTYLE_MULTIDUAL  = 5;

var DATE_DTTYPE_DATEONLY = 0;
var DATE_DTTYPE_TIMEONLY = 1;
var DATE_DTTYPE_BOTH     = 2;

function DialogField(type, id, name, qualifiedName, caption, flags)
{
    this.typeName = type;
    this.type = FIELD_TYPES[type];
    if (typeof this.type == "undefined")
        this.type = null;
    this.controlId = id;
    this.name = name;
    this.qualifiedName = qualifiedName;
    this.caption = caption;
    this.customHandlers = new FieldType("Custom", null, null, null, null, null, null, null);
    this.flags = flags;
    this.dependentConditions = new Array();
    this.style = null;
    this.requiresPreSubmit = false;

    this.fieldIndex = -1;
    this.prevFieldIndex = -1;
    this.nextFieldIndex = -1;

    // the remaining are object-based methods
    if (browser.ie5 || browser.ie6 || browser.ns6)
    {
        this.getControl = DialogField_getControl_Dom;
        this.getControlByQualifiedName = DialogField_getControlByQualifiedName_Dom;
        this.getFieldAreaElem = DialogField_getFieldAreaElem_Dom;
    }
    else if (browser.ns4)
    {
        this.getControl = DialogField_getControl_NS4;
        this.getControlByQualifiedName = DialogField_getControlByQualifiedName_NS4;
        this.getFieldAreaElem = DialogField_getFieldAreaElem_NS4;
    }
    else if (browser.ie4)
    {
        this.getControl = DialogField_getControl_IE4;
        this.getControlByQualifiedName = DialogField_getControlByQualifiedName_IE4;
        this.getFieldAreaElem = DialogField_getFieldAreaElem_IE4;
    }

    this.evaluateConditionals = DialogField_evaluateConditionals;
    this.finalizeContents = DialogField_finalizeContents;
    this.isValid = DialogField_isValid;
    this.doPreSubmit = DialogField_doPreSubmit;
    this.focusNext = DialogField_focusNext;
    this.alertRequired = DialogField_alertRequired;
    this.isRequired = DialogField_isRequired;
    this.alertMessage = DialogField_alertMessage;
}

function DialogField_isRequired()
{
    return (this.flags & FLDFLAG_REQUIRED) != 0;
}

/**
 * Get the dialog field control  using its ID for IE4
 */
function DialogField_getControl_IE4(dialog)
{
    return getControl_IE4(dialog, this.controlId);
}

/**
 * Get the dialog field control  using its ID for DOM browsers such as IE5, IE6 and NS6
 */
function DialogField_getControl_Dom(dialog)
{
    return getControl_Dom(dialog, this.controlId);
}

/**
 * Get the dialog field control  using its ID for Netscape 4
 */
function DialogField_getControl_NS4(dialog)
{
    return getControl_NS4(dialog, this.controlId);
}

/**
 * Get the dialog field control  using its qualified name for IE4
 */
function DialogField_getControlByQualifiedName_IE4(dialog)
{
    return getControl_IE4(dialog, this.qualifiedName);
}

/**
 * Get the dialog field control  using its qualified name for DOM browsers such as IE5, IE6 and NS6
 */
function DialogField_getControlByQualifiedName_Dom(dialog)
{
    return getControl_Dom(dialog, this.qualifiedName);
}

/**
 * Get the dialog field control using its qualified name for Netscape 4
 */
function DialogField_getControlByQualifiedName_NS4(dialog)
{
    return getControl_NS4(dialog, this.qualifiedName);
}

function DialogField_finalizeContents(dialog)
{
	if(this.type != null)
	{
		if(this.type.finalizeDefn != null)
			this.type.finalizeDefn(dialog, this);
	}

    if(this.style != null && this.style == SELECTSTYLE_MULTIDUAL)
        this.requiresPreSubmit = true;

    if(this.dependentConditions.length > 0)
        this.evaluateConditionals(dialog);

    if((this.flags & FLDFLAG_INITIAL_FOCUS) != 0)
    {
        var control = this.getControl(dialog);
        if(control == null)
            alert("Unable to find control '"+this.controlId+"' in DialogField.finalizeContents() -- trying to set initial focus");
        else
            control.focus();
    }
}

function DialogField_evaluateConditionals(dialog)
{
    if(((this.flags & FLDFLAG_READONLY) != 0))
        return;

    var control = this.getControl(dialog);
    if(control == null)
    {
        alert("Unable to find control '"+this.controlId+"' in DialogField.evaluateConditionals()");
        return;
    }

    var conditionalFields = this.dependentConditions;
    for(var i = 0; i < conditionalFields.length; i++)
        conditionalFields[i].evaluate(dialog, control);
}

function DialogField_alertMessage(control, message)
{
    if (this.caption == "null")
    {
        alert(message);
    }
    else
    {
        alert(this.caption + ": " + message);
    }
    control.focus();
}

function DialogField_alertRequired(control)
{
    if (this.caption == "null")
    {
	alert("This field is required.");
    }
    else
    {
    	alert(this.caption + " is required.");
    }
    control.focus();
}

function DialogField_isValid()
{
    // perform default validation first
    var control = this.getControl(dialog);
    if (control == null)
        return true;

    // now see if there are any type-specific validations to perform
    var fieldType = this.type;
    if(fieldType != null && fieldType.isValid != null)
    {
        if (this.customHandlers.isValid != null)
        {
            var valid = true;
            if (this.customHandlers.isValidType == 'extends')
                valid = fieldType.isValid(this, control);
            if (valid)
            {
                valid = this.customHandlers.isValid(this, control);
            }
            return valid;
        }
        else
        {
            return fieldType.isValid(this, control);
        }
    }

    // no type-specific validation found so try and do a generic one
    if(this.isRequired())
    {
        if(eval("typeof control.value") != "undefined")
        {
            if(control.value.length == 0)
            {
                this.alertRequired(control);
                return false;
            }
        }
    }

    return true;
}

function DialogField_doPreSubmit()
{
    if(this.style != null && this.style == SELECTSTYLE_MULTIDUAL)
    {
        // Select all items in multidual elements. If items aren't selected,
        // they won't be posted.
        var control = this.getControl(dialog);
        for (var i = 0; i < control.options.length; i++)
        {
            control.options[i].selected = true;
        }
    }
}

function DialogField_focusNext(dialog)
{
    var dialogFieldsCount = dialog.fields.length;
    var nextField = null;
    var nextFieldControl = null;
    var fieldIndex = this.nextFieldIndex;
    var foundEditable = false;
    while((! foundEditable) && fieldIndex < dialogFieldsCount)
    {
        nextField = dialog.fields[fieldIndex];
        nextFieldAreaElem = nextField.getFieldAreaElem(dialog);
        nextFieldControl = nextField.getControl(dialog);
        //nextFieldControl = document.all.item(nextField.controlId);
        if(nextFieldControl != null && nextFieldControl.length > 0)
            nextFieldControl = nextFieldControl[0];

        if(nextField.typeName == "com.netspective.sparx.form.DialogDirector")
            return false;

        if( (nextFieldControl != null && nextFieldControl.style.display == 'none') ||
            (nextFieldAreaElem != null && nextFieldAreaElem.style.display == 'none') ||
            nextField.typeName == "com.netspective.sparx.xaf.form.field.SeparatorField" ||
            nextField.typeName == "com.netspective.sparx.xaf.form.field.StaticField" ||
            nextField.typeName == "com.netspective.sparx.xaf.form.field.DurationField" || // duration is a composite
            nextField.typeName == "com.netspective.sparx.xaf.form.DialogField" || // composites are of this type
            (nextField.flags & FLDFLAG_INVISIBLE) != 0 ||
            (nextField.flags & FLDFLAG_READONLY) != 0 ||
            (nextField.flags & FLDFLAG_INPUT_HIDDEN) != 0)
            fieldIndex++;
        else
            foundEditable = true;
    }

    if(foundEditable)
    {
        //alert("found editable: " + nextField.controlId + " -- " + fieldIndex);
        if(nextFieldControl != null)
        {
            nextFieldControl.focus();
        }
        else
        {
            alert("No control found for '"+ nextField.controlId + "' (field " + this.nextFieldIndex + ") ["+ nextField.typeName +"]")
        }
        return true;
    }

    return false;
}

/**
 * Gets the control of the table row where the dialog field belongs to for IE 4
 * This does not get the control of the dialog field(INPUT)!
 */
function DialogField_getFieldAreaElem_IE4(dialog)
{
    var fieldAreaId = FIELDROW_PREFIX + this.name;
    var fieldAreaElem = getControl_IE4(dialog, fieldAreaId);
    if(fieldAreaElem == null || (typeof fieldAreaElem == "undefined"))
    {
        fieldAreaId = GRIDFIELDROW_PREFIX + this.qualifiedName;
        fieldAreaElem = getControl_IE4(dialog, fieldAreaId);
    }
    return fieldAreaElem;
}

/**
 * Gets the control of the table row where the dialog field belongs to for Dom Browsers.
 * This does not get the control of the dialog field(INPUT)!
 */
function DialogField_getFieldAreaElem_Dom(dialog)
{
    var fieldAreaId = FIELDROW_PREFIX + this.name;
    var fieldAreaElem = getControl_Dom(dialog, fieldAreaId);
    if(fieldAreaElem == null || (typeof fieldAreaElem == "undefined"))
    {
        fieldAreaId = GRIDFIELDROW_PREFIX + this.qualifiedName;
        fieldAreaElem = getControl_Dom(dialog, fieldAreaId);
    }
    return fieldAreaElem;
}

/**
 * Gets the control of the table row which the dialog field belongsd to for Netscape 4
 * This does not get the control of the dialog field(INPUT)!
 */
function DialogField_getFieldAreaElem_NS4(dialog)
{
    var fieldAreaId = FIELDROW_PREFIX + this.name;
    var fieldAreaElem = getControl_NS4(dialog, fieldAreaId);
    if(fieldAreaElem == null || (typeof fieldAreaElem == "undefined"))
    {
        fieldAreaId = GRIDFIELDROW_PREFIX + this.qualifiedName;
        fieldAreaElem = getControl_NS4(dialog, fieldAreaId);
    }
    return fieldAreaElem;
}

function setAllCheckboxes(sourceCheckbox, otherCheckboxesPrefix)
{
    var isChecked = sourceCheckbox.checked;

    for(var f = 0; f < document.forms.length; f++)
    {
        var form = document.forms[f];
        var elements = form.elements;
        for(var i = 0; i < elements.length; i++)
        {
            control = form.elements[i];
            if(control.name.indexOf(otherCheckboxesPrefix) == 0)
                control.checked = isChecked;
        }
    }
}

//****************************************************************************
// DialogFieldConditionalDisplay class
//****************************************************************************

function DialogFieldConditionalDisplay(source, partner, expression)
{
    this.source = source;
    this.partner = partner;
    this.expression = expression;

    // the remaining are object-based methods
    this.evaluate = DialogFieldConditionalDisplay_evaluate;
}

function DialogFieldConditionalDisplay_evaluate(dialog, control)
{
    // first find the field area that we need to hide/show
    // -- if an ID with the entire field row is found (a primary field)
    //    then go ahead and use that
    // -- if no primary field row is found, find the actual control and
    //    use that to hide/show

    if(control == null)
    {
        alert("control is null in DialogFieldConditionalDisplay.evaluate(control)");
        return;
    }

    var condSource = dialog.fieldsByQualName[this.source];
    var fieldAreaElem = condSource.getFieldAreaElem(dialog);
    if(fieldAreaElem == null || (typeof fieldAreaElem == "undefined"))
    {
        fieldAreaElem = condSource.getControl(dialog);
        if(fieldAreaElem == null || (typeof fieldAreaElem == "undefined"))
        {
            alert ('Neither source element "' + fieldAreaId + '" or "'+ condSource.controlId +'" found in conditional partner.');
            return;
        }
    }

    // now that we have the fieldArea that we want to show/hide go ahead
    // and evaluate the js expression to see if the field should be shown
    // or hidden. remember, the expression is evaluted in the current context
    // which means the word "control" refers to the control that is the
    // the conditional "partner" (not the source)
    if(eval(this.expression) == true)
    {
        //fieldAreaElem.className = 'section_field_area_conditional_expanded';
        if (fieldAreaElem.style)
            fieldAreaElem.style.display = '';
        else
            fieldAreaElem.visibility = 'show';
    }
    else
    {
        //fieldAreaElem.className = 'section_field_area_conditional';
        if (fieldAreaElem.style)
            fieldAreaElem.style.display = 'none';
        else
            fieldAreaElem.visibility = 'hide';
    }
}

//****************************************************************************
// SelectField MultiDual support functions
//****************************************************************************

/*
Description:
    Moves items from one select box to another.
Input:
    strFormName = Name of the form containing the <SELECT> elements
    strFromSelect = Name of the left or "from" select list box.
    strToSelect = Name of the right or "to" select list box
    blnSort = Indicates whether list box should be sorted when an item(s) is added

Return:
    none
*/
function MoveSelectItems(strFormName, strFromSelect, strToSelect, blnSort)
{
    var dialog = eval("document.forms." + strFormName);
    var objSelectFrom = dialog.elements[strFromSelect];
    var objSelectTo = dialog.elements[strToSelect];
    var intLength = objSelectFrom.options.length;

    for (var i=0; i < intLength; i++)
    {
        if(objSelectFrom.options[i].selected && objSelectFrom.options[i].value != "")
        {
            var objNewOpt = new Option();
            objNewOpt.value = objSelectFrom.options[i].value;
            objNewOpt.text = objSelectFrom.options[i].text;
            objSelectTo.options[objSelectTo.options.length] = objNewOpt;
            objSelectFrom.options[i].value = "";
            objSelectFrom.options[i].text = "";
        }
    }

    if (blnSort) SimpleSort(objSelectTo);
    RemoveEmpties(objSelectFrom, 0);
}

/*
Description:
    Removes empty select items. This is a helper function for MoveSelectItems.
Input:
    objSelect = A <SELECT> object.
    intStart = The start position (zero-based) search. Optimizes the recursion.
Return:
    none
*/
function RemoveEmpties(objSelect, intStart)
{
    for(var i=intStart; i<objSelect.options.length; i++)
    {
        if (objSelect.options[i].value == "")
        {
            objSelect.options[i] = null;    // This removes item and reduces count
            RemoveEmpties(objSelect, i);
            break;
        }
    }
}

/*
Description:
    Sorts a select box. Uses a simple sort.
Input:
    objSelect = A <SELECT> object.
Return:
    none
*/
function SimpleSort(objSelect)
{
    var arrTemp = new Array();
    var objTemp = new Object();
    for(var i=0; i<objSelect.options.length; i++)
    {
        arrTemp[i] = objSelect.options[i];
    }

    for(var x=0; x<arrTemp.length-1; x++)
    {
        for(var y=(x+1); y<arrTemp.length; y++)
        {
            if(arrTemp[x].text > arrTemp[y].text)
            {
                objTemp = arrTemp[x].text;
                arrTemp[x].text = arrTemp[y].text;
                arrTemp[y].text = objTemp;
            }
        }
    }
}

//****************************************************************************
// Event handlers
//****************************************************************************

function controlOnClick(control, event)
{
    if(control.name == FIELDNAME_IGNORE_VALIDATION)
        setAllowValidation(false);

    field = activeDialog.fieldsById[control.name];
    if(typeof field == "undefined" || field == null || field.type == null) return;

    if (field.customHandlers.click != null)
    {
        var retval = true;
        if (field.customHandlers.clickType == 'extends')
        {
            if (field.type.click != null)
                retval = field.type.click(field, control);
        }
        if (retval)
            field.customHandlers.click(field, control);
        return retval;
    }
    else
    {
        if (field.type.click != null)
            return field.type.click(field, control);
        else
            return true;
    }
}

function controlOnKeypress(control, event)
{
    field = activeDialog.fieldsById[control.name];
    if(typeof field == "undefined" || field == null || field.type == null) return;
    if (field.customHandlers.keyPress != null)
    {
        var retval = true;
        if (field.customHandlers.keyPressType == 'extends')
        {
            if (field.type.keyPress != null)
                retval =  field.type.keyPress(field, control);
        }
        if (retval)
            retval =  field.customHandlers.keyPress(field, control);
        return retval;
    }
    else
    {
        if (field.type.keyPress != null)
            return field.type.keyPress(field, control, event);
        else
            return true;
    }
}

function controlOnFocus(control, event)
{
    field = activeDialog.fieldsById[control.name];
    if(typeof field == "undefined" || field == null || field.type == null) return;
    if (field.customHandlers.getFocus != null)
    {
        var retval = true;
        if (field.customHandlers.getFocusType == 'extends')
        {
            if (field.type.getFocus != null)
                retval =  field.type.getFocus(field, control);
        }
        if (retval)
            retval =  field.customHandlers.getFocus(field, control);
        return retval;
    }
    else
    {
        if (field.type.getFocus != null)
            return field.type.getFocus(field, control);
        else
            return true;
    }
}

function controlOnChange(control, event)
{
    field = activeDialog.fieldsById[control.name];
    if(typeof field == "undefined" || field == null) return;
    if(field.dependentConditions.length > 0)
    {
        var conditionalFields = field.dependentConditions;
        for(var i = 0; i < conditionalFields.length; i++)
            conditionalFields[i].evaluate(activeDialog, control);
    }
    if(field.type == null) return;
    if (field.customHandlers.valueChanged != null)
    {
        var retval = true;
        if (field.customHandlers.valueChangedType == 'extends')
        {
            if (field.type.valueChanged != null)
                retval = field.type.valueChanged(field, control);
        }
        if (retval)
            retval =  field.customHandlers.valueChanged(field, control);
        return retval;
    }
    else
    {
        if (field.type.valueChanged != null)
            return field.type.valueChanged(field, control);
        else
            return true;
    }
}

function controlOnBlur(control, event)
{
    field = activeDialog.fieldsById[control.name];
    if(typeof field == "undefined" || field == null || field.type == null) return;
    if (field.customHandlers.loseFocus != null)
    {
        var retval = true;
        if (field.customHandlers.loseFocusType == 'extends')
        {
            if (field.type.loseFocus != null)
                retval = field.type.loseFocus(field, control);
        }
        if (retval)
            retval =  field.customHandlers.loseFocus(field, control);
        return retval;
    }
    else
    {
        if (field.type.loseFocus != null)
            return field.type.loseFocus(field, control);
        else
            return true;
    }
}

//****************************************************************************
// Keyboard-management utility functions
//****************************************************************************

var KEYCODE_ENTER          = 13;
var NUM_KEYS_RANGE         = [48,  57];
var PERIOD_KEY_RANGE       = [46,  46];
var SLASH_KEY_RANGE        = [47,  47];
var DASH_KEY_RANGE         = [45,  45];
var UPPER_ALPHA_KEYS_RANGE = [65,  90];
var LOW_ALPHA_KEYS_RANGE   = [97, 122];
var UNDERSCORE_KEY_RANGE   = [95,  95];
var COLON_KEY_RANGE        = [58, 58];

function keypressAcceptRanges(field, control, acceptKeyRanges, event)
{
	if(! ENABLE_KEYPRESS_FILTERS)
		return true;

	// if the default document keypress handler handled the event,
	// it returns "FALSE" so we don't want to bother with the event
	if(! documentOnKeyDown())
		return true;
    // the event should have been passed in here but for some reason
    // its null, look for it in the window object (works only in IE)
    if (event == null || typeof event == "undefined")
        event = window.event;
	for (i in acceptKeyRanges)
	{
		var keyCodeValue = null;
        if (event.keyCode)
			keyCodeValue = event.keyCode;
		else
			keyCodeValue = event.which;

		var keyInfo = acceptKeyRanges[i];
		if(keyCodeValue >= keyInfo[0] && keyCodeValue <= keyInfo[1])
			return true;
	}

	// if we get to here, it means we didn't accept any of the ranges
    window.event.cancelBubble = true;
    window.event.returnValue = false;
	return false;
}

//****************************************************************************
// Field-specific validation and keypress filtering functions
//****************************************************************************

function CurrencyField_onKeyPress(field, control, event)
{
    return keypressAcceptRanges(field, control, [NUM_KEYS_RANGE, DASH_KEY_RANGE, PERIOD_KEY_RANGE], event);
}

function CurrencyField_isValid(field, control)
{
    if(field.isRequired() && control.value.length == 0)
    {
        field.alertRequired(control);
        return false;
    }
    if (control.value.length > 0)
    {
        var test = testCurrency(field, control);
        if (test == false)
        {
            field.alertMessage(control, field.text_format_err_msg);
            return false;
        }
    }
    return true;
}

function CurrencyField_valueChanged(field, control)
{
    return formatCurrency(field, control);
}

function BooleanField_onClick(field, control)
{
    if (control.type == 'checkbox' || control.type == 'radio')
    {
        if(field.dependentConditions.length > 0)
        {
            var conditionalFields = field.dependentConditions;
            for(var i = 0; i < conditionalFields.length; i++)
                conditionalFields[i].evaluate(activeDialog, control);
        }
    }
    return true;
}

function TextField_onFocus(field, control)
{
    if (field.readonly == 'yes')
        control.blur();

    return true;
}

function TextField_valueChanged(field, control)
{
    if (field.uppercase == 'yes')
    {
        control.value = control.value.toUpperCase();
    }
    return true;
}

function TextField_onKeyPress(field, control, event)
{
    if (field.identifier == 'yes')
    {
        return keypressAcceptRanges(field, control, [NUM_KEYS_RANGE, UPPER_ALPHA_KEYS_RANGE, UNDERSCORE_KEY_RANGE], event);
    }
    return true;
}

function PhoneField_valueChanged(field, control)
{
    return formatPhone(field, control);
}

function PhoneField_isValid(field, control)
{
    if(field.isRequired() && control.value.length == 0)
    {
        field.alertRequired(control);
        return false;
    }
    if (control.value.length > 0)
    {
        var test = testPhone(field, control);
        if (test == false)
        {
            field.alertMessage(control, field.text_format_err_msg);
            return false;
        }
    }
    return true;
}

function SocialSecurityField_valueChanged(field, control)
{
    return formatSSN(field, control);
}

function SocialSecurityField_isValid(field, control)
{
    if(field.isRequired() && control.value.length == 0)
    {
        field.alertRequired(control);
        return false;
    }
    if (control.value.length > 0)
    {
        var test = testSSN(field, control);
        if (test == false)
        {
            field.alertMessage(control, "Social Security Number must be in the correct format: 999-99-9999");
            return false;
        }
    }
    return true;
}

function IntegerField_onKeyPress(field, control, event)
{
	return keypressAcceptRanges(field, control, [NUM_KEYS_RANGE, DASH_KEY_RANGE], event);
}

function IntegerField_isValid(field, control)
{
	if(field.isRequired() && control.value.length == 0)
	{
		field.alertRequired(control);
		return false;
	}

	var intValue = control.value - 0;
	if(isNaN(intValue))
	{
		field.alertMessage(control, "'"+ control.value +"' is an invalid integer.");
		return false;
	}
	return true;
}

function FloatField_onKeyPress(field, control, event)
{
	return keypressAcceptRanges(field, control, [NUM_KEYS_RANGE, DASH_KEY_RANGE, PERIOD_KEY_RANGE], event);
}

function FloatField_isValid(field, control)
{
	if(field.isRequired() && control.value.length == 0)
	{
		field.alertRequired(control);
		return false;
	}

	var floatValue = control.value - 0;
	if(isNaN(floatValue))
	{
		field.alertMessage(control, "'"+ control.value +"' is an invalid decimal.");
		return false;
	}
	return true;
}

function MemoField_isValid(field, control)
{
	if(field.isRequired() && control.value.length == 0)
	{
		field.alertRequired(control);
		return false;
	}

    maxlimit = field.maxLength;
    if (control.value.length > maxlimit)
    {
        field.alertMessage(control, "Maximum number of characters allowed is " + maxlimit);
        return false;
    }
    return true;
}

function MemoField_onKeyPress(field, control, event)
{
    maxlimit = field.maxLength;
    if (control.value.length >= maxlimit)
    {
        field.alertMessage(control, "Maximum number of characters allowed is " + maxlimit);
        return false;
    }
    return true;
}

function DateField_popupCalendar()
{
	showCalendar(this.getControl(activeDialog), 0);
}

function DateField_finalizeDefn(dialog, field)
{
	field.popupCalendar = DateField_popupCalendar;
   	field.dateFmtIsKnownFormat = false;
   	field.dateItemDelim = null;
   	field.dateItemDelimKeyRange = null;
    if (field.dateDataType == DATE_DTTYPE_DATEONLY)
    {
        if (field.dateFormat == "MM/dd/yyyy" || field.dateFormat == "MM/dd/yy")
        {
            field.dateItemDelim = '/';
		   	field.dateItemDelimKeyRange = SLASH_KEY_RANGE;
            field.dateFmtIsKnownFormat = true;
        }
        else if (field.dateFormat == "MM-dd-yyyy" || field.dateFormat == "MM-dd-yy")
        {
            field.dateItemDelim = '-';
		   	field.dateItemDelimKeyRange = DASH_KEY_RANGE;
            field.dateFmtIsKnownFormat = true;
        }
    }
}

function DateField_isValid(field, control)
{
	if(field.isRequired() && control.value.length == 0)
	{
		field.alertRequired(control);
		return false;
	}

    return DateField_valueChanged(field, control);
}

function DateField_valueChanged(field, control)
{
    if (field.dateDataType == DATE_DTTYPE_DATEONLY && field.dateFmtIsKnownFormat)
    {
        var result = formatDate(field, control, field.dateItemDelim, field.dateStrictYear);
        control.value = result[1];
        return result[0];
    }
    else if (field.dateDataType == DATE_DTTYPE_TIMEONLY)
    {
        var result = formatTime(field, control);
        return result;
    }
    return true;
}

function DateField_onKeyPress(field, control, event)
{
    if (field.dateDataType == DATE_DTTYPE_DATEONLY && field.dateFmtIsKnownFormat)
    {
		return keypressAcceptRanges(field, control, [NUM_KEYS_RANGE, field.dateItemDelimKeyRange], event);
    }
    else if (field.dateDataType == DATE_DTTYPE_TIMEONLY)
    {
        return keypressAcceptRanges(field, control, [NUM_KEYS_RANGE, COLON_KEY_RANGE], event);
    }
    return true;
}

function SelectField_isValid(field, control)
{
    var style = field.style;

    if(field.isRequired())
    {
        if(style == SELECTSTYLE_RADIO)
        {
            var selectedCount = 0;
            for(var r = 0; r < control.length; r++)
            {
                if(control[r].checked)
                    selectedCount++;
            }
            if(selectedCount == 0)
            {
                field.alertRequired(control[0]);
                return false;
            }
        }
        else if(style == SELECTSTYLE_COMBO)
        {
        	if(field.isRequired() && control.options[control.selectedIndex].value.length == 0)
		{
			field.alertRequired(control);
			return false;
		}
        }
        else if(style == SELECTSTYLE_LIST || style == SELECTSTYLE_MULTILIST)
        {

            var selectedCount = 0;
            var options = control.options;
            for(var o = 0; o < options.length; o++)
            {
                if(options[o].selected)
                    selectedCount++;
            }
            if(selectedCount == 0)
            {
                field.alertRequired(control);
                return false;
            }
        }
        else if(style == SELECTSTYLE_MULTICHECK)
        {
            var selectedCount = 0;
            for(var c = 0; c < control.length; c++)
            {
                if(control[c].checked)
                    selectedCount++;
            }
            if(selectedCount == 0)
            {
                field.alertRequired(control[0]);
                return false;
            }
        }
        else if(style == SELECTSTYLE_MULTIDUAL)
        {

            var selectedCount = 0;
            var options = control.options;
            for(var o = 0; o < options.length; o++)
            {
                if(options[o].selected)
                    selectedCount++;
            }
            if(selectedCount == 0)
            {
                field.alertRequired(control);
                return false;
            }
        }
    }

    return true;
}

addFieldType("com.netspective.sparx.xaf.form.field.TextField", null, null, TextField_valueChanged, TextField_onFocus, null, null, null);
addFieldType("com.netspective.sparx.xaf.form.field.SelectField", null, SelectField_isValid, null, null, null, null, null);
addFieldType("com.netspective.sparx.xaf.form.field.BooleanField", null, null, null, null, null, null, BooleanField_onClick);
addFieldType("com.netspective.sparx.xaf.form.field.MemoField", null, MemoField_isValid, null, null, null, MemoField_onKeyPress);
addFieldType("com.netspective.sparx.xaf.form.field.DateTimeField", DateField_finalizeDefn, DateField_isValid, DateField_valueChanged, null, null, DateField_onKeyPress, null);
addFieldType("com.netspective.sparx.xaf.form.field.IntegerField", null, IntegerField_isValid, null, null, null, IntegerField_onKeyPress);
addFieldType("com.netspective.sparx.xaf.form.field.FloatField", null, FloatField_isValid, null, null, null, FloatField_onKeyPress);
addFieldType("com.netspective.sparx.xaf.form.field.SocialSecurityField", null, SocialSecurityField_isValid, SocialSecurityField_valueChanged, null, null, null, null);
addFieldType("com.netspective.sparx.xaf.form.field.PhoneField", null, PhoneField_isValid, PhoneField_valueChanged, null, null, null, null);
addFieldType("com.netspective.sparx.xaf.form.field.CurrencyField", null, CurrencyField_isValid, CurrencyField_valueChanged, null, null, null, null);

//****************************************************************************
// Date Formatting
//****************************************************************************

var VALID_NUMBERS =  ["0","1","2","3","4","5","6","7","8","9"];

// returns a string of exactly count characters left padding with zeros
function padZeros(number, count)
{
    var padding = "0";
    for (var i=1; i < count; i++)
        padding += "0";
    if (typeof(number) == 'number')
        number = number.toString();
    if (number.length < count)
        number = (padding.substring(0, (count - number.length))) + number;
    if (number.length > count)
        number = number.substring((number.length - count));
    return number;
}

function testCurrency(field, control)
{
    if (control.value == '')
        return true;
    var pattern = field.text_format_pattern;
    return pattern.test(control.value) ;
}

function formatCurrency(field, control)
{
    var test = testCurrency(field, control);
    if (test == false)
    {
        field.alertMessage(control, this.field.text_format_err_msg);
        return false;
    }
    else
    {
        if (control.value != '')
        {
            value = control.value;
            var pattern = field.text_format_pattern;
            if (pattern.exec(value))
            {
                match = pattern.exec(value)
                if (field.negative_pos == "after")
                {
                    if (match[1] == "")
                        match[1] = field.currency_symbol;
                    if (typeof match[3] == "undefined")
                        match[3] = ".00";
                    control.value = match[1] + match[2] + match[3];
                }
                else if (field.negative_pos == "before")
                {
                    if (match[2] == "")
                        match[2] = field.currency_symbol;
                    if (typeof match[4] == "undefined")
                        match[4] = ".00";
                    control.value = match[1] + match[2] + match[3] + match[4];
                }
            }
        }
    }
    return true;
}

function testPhone(field, control)
{
    if (control.value == '')
        return true;
    var phonePattern = field.text_format_pattern;
    return phonePattern.test(control.value) ;
}

function formatPhone(field, control)
{
    var test = testPhone(field, control);
    if (test == false)
    {
        field.alertMessage(control, field.text_format_err_msg);
        return false;
    }
    else
    {
        if (control.value != '')
        {
            var phoneStr = control.value;
            if (field.phone_format_type == 'dash')
            {
                phoneStr = phoneStr.replace(field.text_format_pattern, "$1-$2-$3$4");
            }
            else
            {
                phoneStr = phoneStr.replace(field.text_format_pattern, "($1) $2-$3$4");
            }
            control.value = phoneStr;
        }
    }
    return true;
}

function testSSN(field, control)
{
    if (control.value == '')
        return true;
    var ssnPattern = field.text_format_pattern ;
    return ssnPattern.test(control.value);
}

function formatSSN(field, control)
{
    var test = testSSN(field, control);
    if (test == false)
    {
        field.alertMessage(control, "Social Security Number must be in the correct format: 999-99-9999");
        return false;
    }
    if (control.value != '')
    {
        var ssn = control.value;
        ssn = ssn.replace(field.text_format_pattern, "$1-$2-$3");
        control.value = ssn;
    }
    return true;
}


function testTime(field, control)
{
    var inTime = control.value;
    if (inTime == '')
        return true;
    var hr = null;
    var min = null;
    if (inTime.length == 5 && inTime.indexOf(":") == 2)
    {
        hr = inTime.substring(0, 2);
        min = inTime.substring(3);
        if (hr > 23 || min > 59)
        {
            field.alertMessage(control, "Time field must have a valid value");
            return false;
        }
        return true;
    }
    else if (inTime.length == 4 && inTime.indexOf(":") == 1)
    {
        hr = inTime.substring(0, 1);
        min = inTime.substring(2);
        if (hr > 23 || min > 59)
        {
            field.alertMessage(control, "Time field must have a valid value");
            return false;
        }
        return true;
    }
    field.alertMessage(control, "Time field must have the correct format: " + field.dateFormat);
    return false;
}

function formatTime(field, control)
{
    var inTime = control.value;
    newTime = inTime;
    if (field.timeStrict == false && inTime.indexOf(":") == -1)
    {
        if (inTime.length == 4)
        {
            newTime = inTime.substring(0, 2) + ":"  + inTime.substring(2);
        }
        else if (inTime.length == 3)
        {
            newTime = inTime.substring(0, 1) + ":" + inTime.substring(1);
        }
        control.value = newTime;
    }
    return testTime(field, control);
}

function formatDate(field, control, delim, strictYear)
{
    if (delim == null)
        delim = "/";

    var inDate = control.value;
    var today = new Date();
    var currentDate = today.getDate();
    var currentMonth = today.getMonth() + 1;
    var currentYear = today.getYear();
    var fmtMessage = "Date must be in correct format: 'D', 'M" + delim + "D', 'M" + delim + "D" + delim + "Y', or 'M" + delim + "D" + delim + "YYYY'";

    inDate = inDate.toLowerCase();
    var a = splitNotInArray(inDate, VALID_NUMBERS);
    for (i in a)
    {
        a[i] = '' + a[i];
    }
    if (a.length == 0)
    {
        if (inDate.length > 0)
            field.alertMessage(control, fmtMessage);
        return [true, inDate];
    }
    if (a.length == 1)
    {
        if ((a[0].length == 6) || (a[0].length == 8))
        {
            a[2] = a[0].substring(4);
            a[1] = a[0].substring(2,4);
            a[0] = a[0].substring(0,2);
        }
        else
        {
            if (a[0] == 0)
            {
                a[0] = currentMonth;
                a[1] = currentDate;
            }
            else
            {
                a[1] = a[0];
                a[0] = currentMonth;
            }
        }
    }
    if (a.length == 2)
    {
        if (a[0] <= (currentMonth - 3))
            a[2] = currentYear + 1;
        else
            a[2] = currentYear;
    }

    if (strictYear != true)
    {
        if (a[2] < 100 && a[2] > 10)
            a[2] = "19" + a[2];
        if (a[2] < 1000)
            a[2] = "20" + a[2];
    }
    if ( (a[0] < 1) || (a[0] > 12) )
    {
        field.alertMessage(control, "Month value must be between 1 and 12");
        return [false, inDate];
    }
    if ( (a[1] < 1) || (a[1] > 31) )
    {
        field.alertMessage(control, "Day value must be between 1 and 31");
        return [false, inDate];
    }
    if ( (a[2] < 1800) || (a[2] > 2999) )
    {
        field.alertMessage(control, "Year must be between 1800 and 2999");
        return [false, inDate];
    }
    return [true, padZeros(a[0],2) + delim + padZeros(a[1],2) + delim + a[2]];
}

// Split "string" into multiple tokens at "char"
function splitOnChar(strString, strDelimiter)
{
    var a = new Array();
    var field = 0;
    for (var i = 0; i < strString.length; i++)
    {
        if ( strString.charAt(i) != strDelimiter )
        {
            if (a[field] == null)
                a[field] = strString.charAt(i);
            else
                a[field] += strString.charAt(i);
        }
        else
        {
            if (a[field] != null)
                field++;
        }
    }
    return a;
}

// Split "strString" into multiple tokens at inverse of "array"
function splitNotInArray(strString, arrArray)
{
    var a = new Array();
    var field = 0;
    var matched;
    for (var i = 0; i < strString.length; i++)
    {
        matched = 0;
        for (k in arrArray)
        {
            if (strString.charAt(i) == arrArray[k])
            {
                if (a[field] == null || typeof a[field] == "undefined")
                    a[field] = strString.charAt(i);
                else
                    a[field] += strString.charAt(i);
                matched = 1;
                break;
            }
        }
        if ( matched == 0 && a[field] != null )
            field++;
    }
    return a;
}

//****************************************************************************
// Event handlers
//****************************************************************************

function documentOnKeyDown()
{
    if(TRANSLATE_ENTER_KEY_TO_TAB_KEY && window.event.keyCode == KEYCODE_ENTER)
    {
        var control = window.event.srcElement;
        var field = activeDialog.fieldsById[control.name];
        if(field == null)
        {
            alert("Control '"+ control.srcElement.name + "' was not found in activeDialog.fieldsById");
            window.event.returnValue = false;
            return false;
        }

        if(field.focusNext(activeDialog))
        {
            window.event.cancelBubble = true;
            window.event.returnValue = false;
            return false;
        }
    }

    return true;
}

document.onkeydown = documentOnKeyDown;

dialogLibraryLoaded = true;

