//****************************************************************************
// DialogFieldPopupWindowClass class
//****************************************************************************

var windowClasses = new Array();

function DialogFieldPopupWindowClass(windowName, features)
{
	this.windowName = windowName;
	this.features = features;
}

windowClasses["default"] = new DialogFieldPopupWindowClass("DefaultPopupWindow", "width=520,height=350,scrollbars,resizable");

//****************************************************************************
// PopulateControlInfo class
//****************************************************************************

function PopulateControlInfo(sourceForm, sourceField, fieldName)
{
	this.dialog = activeDialog;
	this.additive = false;
	if(fieldName.charAt(0) == '+')
	{
		this.additive = true;
		fieldName = fieldName.substring(1);
	}
	
	this.fieldName = fieldName;
	this.field = this.dialog.fieldsByQualName[fieldName];
	if(this.field != null)
		this.control = this.field.getControl();
	else
	{
		this.control = document.all.item(fieldName);
		if(this.control == null)
			alert("In DialogFieldPopup for " + sourceForm + "." + sourceField + ", fill field '" + fieldName + "' could not be found.");
	}
	
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
			if((this.field.typeName == "com.xaf.form.field.StaticField") || (this.field.flags & FLDFLAG_READONLY != 0))
			{
				element = document.all.item(this.field.qualifiedName);
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
		if(this.field != null)
		{
			this.control.value = value;
			if((this.field.typeName == "com.xaf.form.field.StaticField") || (this.field.flags & FLDFLAG_READONLY != 0))
				document.all.item(this.field.qualifiedName).innerHTML = value;
		}
		else
			this.control.innerHTML = value;
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
	
	// every arg after allowMultiSelect is a field name that should be "filled"
	// by the popup
	
	var startFillArg = 6;
	var argsLen = arguments.length;
	
	var controls = this.controlsInfo;
	for(var i = startFillArg; i < argsLen; i++)
	{
		var fieldName = arguments[i];
		var realIndex = i - startFillArg;
		controls[realIndex] = new PopulateControlInfo(sourceForm, sourceField, fieldName);
	}
	
	// the remaining are object-based methods
	this.populateControl = DialogFieldPopup_populateControl;
	this.populateControls = DialogFieldPopup_populateControls;
	this.doPopup = DialogFieldPopup_doPopup;
	
	this.doPopup();
}

function DialogFieldPopup_populateControl(value)
{
	this.controlsInfo[0].populateValue(value);
	
	if(this.closeAfterSelect)
		this.popupWindow.close();
}

function DialogFieldPopup_populateControls()
{
	// any number of values may be passed in, with each one being filled appropriately
	//
	var controls = this.controlsInfo;
	for(var i = 0; i < arguments.length; i++)
		controls[i].populateValue(arguments[i]);
		
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
	// any number of values may be passed in, with each one being filled appropriately
	//
	var popup = opener.activeDialogPopup;
	var controls = popup.controlsInfo;
	for(var i = 0; i < arguments.length; i++)
		controls[i].populateValue(arguments[i]);
		
	if(popup.closeAfterSelect)
		popup.popupWindow.close();
}