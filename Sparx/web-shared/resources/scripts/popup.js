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
 * $Id: popup.js,v 1.6 2003-04-23 21:06:22 aye.thu Exp $
 */

//****************************************************************************
// DialogFieldPopupWindowClass class
//****************************************************************************

var windowClasses = new Array();
var adjacentAreaSuffix = "_adjacent";

function DialogFieldPopupWindowClass(windowName, features)
{
	this.windowName = windowName;
	this.features = features;
}

windowClasses["default"] = new DialogFieldPopupWindowClass("DefaultPopupWindow", "width=520,height=350,scrollbars,resizable");
windowClasses["enum"] = new DialogFieldPopupWindowClass("EnumPopupWindow", "width=400,height=250,scrollbars,resizable");

//****************************************************************************
// PopulateControlInfo class
//****************************************************************************

function PopulateControlInfo(sourceForm, sourceField, fieldName)
{
	this.dialog = activeDialog;
	this.additive = false;
	this.adjacentArea = null;
	if(fieldName.charAt(0) == '+')
	{
		this.additive = true;
		fieldName = fieldName.substring(1);
	}

	this.fieldName = fieldName;
	this.field = this.dialog.fieldsByQualName[fieldName];
	if(this.field != null)
		this.control = this.field.getControl(this.dialog);
	else if(fieldName.indexOf(adjacentAreaSuffix) > 0)
	{
	    // this is the adjacent field so get the actual field's name
	    fieldName = fieldName.substring(0, fieldName.length - adjacentAreaSuffix.length);
	    this.fieldName = fieldName;
    	this.field = this.dialog.fieldsByQualName[fieldName];
    	if(this.field != null)
    	{
    	    // the  actual field exist so get the adjacent area control
    		this.adjacentArea = this.field.getAdjacentArea(this.dialog);
	    	if(this.adjacentArea == null)
		    	alert("In DialogFieldPopup for " + sourceForm + "." + sourceField + ", fill field '" + fieldName + "' could not be found [000].");
		}
		else
    	    alert("In DialogFieldPopup for " + sourceForm + "." + sourceField + ", fill field '" + fieldName + "' could not be found [001].");
	}
	else
    	alert("In DialogFieldPopup for " + sourceForm + "." + sourceField + ", fill field '" + fieldName + "' could not be found [002].");

	// the remaining are object-based methods
	this.populateValue = PopulateControlInfo_populateValue;
}

function PopulateControlInfo_populateValue(value)
{
	if(this.additive)
	{
		if(this.field != null)
		{
			this.control.value += "," + value;
			if((this.field.typeName == "com.netspective.sparx.xaf.form.field.StaticField") || (this.field.flags & FLDFLAG_READONLY != 0))
			{                
                element = this.field.getControlByQualifiedName(this.dialog);
				//element = document.all.item(this.field.qualifiedName);
				if(element == null)
					alert("Field " + this.field.qualifiedName + " could not filled (item not found).");
				else
					element.innerHTML += "," + value;
			}
		}
		else
			this.control.innerHTML += "," + value;
	}
	else
	{
	    if(this.adjacentArea != null)
	    {
			this.adjacentArea.innerHTML = value;
	    }
		else if(this.field != null)
		{
		    if (this.control.options)
		    {
		        // this is a select field
		        var options = this.control.options;
                for(var o = 0; o < options.length; o++)
                {
                    if(options[o].text == value)
                    {
                        options[o].selected = true;
                        return;
                    }
                }
		    }
		    else
			    this.control.value = value;
			if((this.field.typeName == "com.netspective.sparx.xaf.form.field.StaticField") || (this.field.flags & FLDFLAG_READONLY != 0))
            {                                   
                element = this.field.getControlByQualifiedName(this.dialog);                
                if (element != null)
                    element.innerHTML = value;
            }
		}
	}
}

//****************************************************************************
// DialogFieldPopup class
//****************************************************************************

var activeDialogPopup = null;

function DialogFieldPopup(sourceForm, sourceField, actionURL, windowClass, closeAfterSelect, allowMultiSelect)
{
	this.srcFormName = sourceForm;
	this.srcFieldName = sourceField;
	this.actionURL = actionURL;
	this.closeAfterSelect = closeAfterSelect;
	this.allowMultiSelect = allowMultiSelect;
	this.windowClass = windowClasses[windowClass];
	this.popupWindow = null;
	this.controlsInfo = new Array();
	this.extractInfo = new Array();
    this.dialog = activeDialog;
    
	// every arg after allowMultiSelect is a field name that should be "filled"
	// by the popup
	
	var startFillArg = 6;
	var argsLen = arguments.length;
    
	var controls = this.controlsInfo;
	/*
	for(var i = startFillArg; i < argsLen; i++)
	{
		var fieldName = arguments[i];
		var realIndex = i - startFillArg;
		controls[realIndex] = new PopulateControlInfo(sourceForm, sourceField, fieldName);
	}
	*/
	// expecting the seventh argument to be an array object with the fill field names
	var fillArray = arguments[startFillArg];
    if (fillArray != null && fillArray.length != null)
    {
        for (var i=0; i < fillArray.length; i++)
        {
            var fieldName = fillArray[i];
            controls[i] = new PopulateControlInfo(sourceForm, sourceField, fieldName);
        }
    }
	if (argsLen == 8)
	{
	    this.appendActionUrl = DialogFieldPopup_appendActionUrl;
        // expecting the last variable as an array object containing field names from which to extract values and
	    // append to the popup URL
	    var extractArray = arguments[startFillArg+1];
	    //this.appendActionUrl(extractArray);
    }

	// the remaining are object-based methods
	this.populateControl = DialogFieldPopup_populateControl;
	this.populateControls = DialogFieldPopup_populateControls;
	this.doPopup = DialogFieldPopup_doPopup;
	
	this.doPopup();
}

/*
function DialogFieldPopup_appendActionUrl(valueArray)
{
    // test to make sure it is an array
    if (valueArray != null && valueArray.length != null)
    {
        for (var i=0; i < valueArray.length; i++)
        {
            fieldName = valueArray[i];
            field= activeDialog.fieldsByQualName[fieldName];
            if(field != null)
            {
		        control = field.getControl(this.dialog);
		        // check if the Url already contains a '?'
		        this.actionUrl = this.actionUrl + control.value;

		    }
        }
    }
}
*/
function DialogFieldPopup_populateControl(value)
{    
	this.controlsInfo[0].populateValue(value);
	
	if(this.closeAfterSelect)
		this.popupWindow.close();
}

function DialogFieldPopup_populateControls()
{
	// any number of arguments may be passed in, with each one being filled appropriately
	var controls = this.controlsInfo;    
    for(var i = 0; i < arguments.length; i++)
    {
        controls[i].populateValue(arguments[i]);
	}	
	if(this.closeAfterSelect)
		this.popupWindow.close();
}

function DialogFieldPopup_populateControlsWithValues(values)
{
	// any number of values may be passed in, with each one being filled appropriately

	var controls = this.controlsInfo;
    for(var i = 0; i < values.length; i++)
    {
        controls[i].populateValue(values[i]);
	}
	if(this.closeAfterSelect)
		this.popupWindow.close();
}

function DialogFieldPopup_doPopup()
{
	activeDialogPopup = this;
	this.popupWindow = open(this.actionURL, this.windowClass.windowName, this.windowClass.features);
	this.popupWindow.focus();    
}

function chooseItem()
{
	var popup = opener.activeDialogPopup;
    popup.populateControlsWithValues(arguments);
}

function createArray()
{
    var newArray =  new Array(arguments.length);
    for(var i = 0; i < arguments.length; i++)
	{
		newArray[i] = arguments[i];
	}
	return newArray;
}