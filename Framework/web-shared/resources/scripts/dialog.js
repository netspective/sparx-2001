
var FIELDROW_PREFIX = "_dfr.";
var GRIDFIELDROW_PREFIX = "_dgfr.";
var GRIDHEADROW_PREFIX = "_dghr.";
var ALLOW_CLIENT_VALIDATION = false;

//****************************************************************************
// FieldType class
//****************************************************************************

function FieldType(name, onValidate, onChange, onFocus, onBlur)
{
	this.type = name;
	this.isValid = onValidate;
	this.getFocus = onFocus;
	this.valueChanged = onChange;
	this.loseFocus = onBlur;
}

var FIELD_TYPES = new Array();

function addFieldType(name, onValidate, onChange, onFocus, onBlur)
{
	FIELD_TYPES[name] = new FieldType(name, onValidate, onChange, onFocus, onBlur);
}

//****************************************************************************
// Dialog class
//****************************************************************************

function Dialog(name)
{
	this.name = name;
	this.fields = new Array();				// straight list (simple array)
	this.fieldsById = new Array();			// hash -- value is field
	this.fieldsByQualName = new Array();	// hash -- value is field
	
	// the remaining are object-based methods
	this.registerField = Dialog_registerField;
	this.finalizeContents = Dialog_finalizeContents;
	this.isValid = Dialog_isValid;
}

function Dialog_registerField(field)
{
	this.fields[this.fields.length] = field;
	this.fieldsById[field.controlId] = field;
	this.fieldsByQualName[field.qualifiedName] = field;
}

function Dialog_finalizeContents()
{
	var dialogFields = this.fields;
	for(var i = 0; i < dialogFields.length; i++)
		dialogFields[i].finalizeContents();
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
	
	if(! ALLOW_CLIENT_VALIDATION)
		return true;
		
	for(var i = 0; i < dialogFields.length; i++)
	{
		if(! dialogFields[i].isValid())
			return false;
	}
	return true;
}

var activeDialog = null;

function setActiveDialog(dialog)
{
	activeDialog = dialog;
}

//****************************************************************************
// DialogField class
//****************************************************************************

// These constants MUST be kept identical to what is in com.xaf.form.DialogField

var FLDFLAG_REQUIRED           = 1;
var FLDFLAG_INVISIBLE          = FLDFLAG_REQUIRED * 2;
var FLDFLAG_READONLY           = FLDFLAG_INVISIBLE * 2;
var FLDFLAG_PERSIST            = FLDFLAG_READONLY * 2;
var FLDFLAG_CREATEADJACENTAREA = FLDFLAG_PERSIST * 2;
var FLDFLAG_SHOWCAPTIONASCHILD = FLDFLAG_CREATEADJACENTAREA * 2;

// These constants MUST be kept identical to what is in com.xaf.form.field.SelectField

var SELECTSTYLE_RADIO      = 0;
var SELECTSTYLE_COMBO      = 1;
var SELECTSTYLE_LIST       = 2;
var SELECTSTYLE_MULTICHECK = 3;
var SELECTSTYLE_MULTILIST  = 4;
var SELECTSTYLE_MULTIDUAL  = 5;

function DialogField(type, id, name, qualifiedName, caption, flags)
{
	this.typeName = type;
	this.type = FIELD_TYPES[type];
	this.controlId = id;
	this.name = name;
	this.qualifiedName = qualifiedName;
	this.caption = caption;
	this.flags = flags;
	this.dependentConditions = new Array();
	this.style = null;
	this.requiresPreSubmit = false;
	
	// the remaining are object-based methods
	this.getControl = DialogField_getControl;
	this.evaluateConditionals = DialogField_evaluateConditionals;
	this.finalizeContents = DialogField_finalizeContents;
	this.isValid = DialogField_isValid;
	this.doPreSubmit = DialogField_doPreSubmit;
}

function DialogField_getControl()
{
	return document.all.item(this.controlId);
}

function DialogField_finalizeContents()
{

	if(this.style != null && this.style == SELECTSTYLE_MULTIDUAL)
		this.requiresPreSubmit = true;
		
	if(this.dependentConditions.length > 0)
		this.evaluateConditionals();

	//alert(this.qualifiedName + " " + this.style + " " + this.requiresPreSubmit + " " + this.dependentConditions.length);
}

function DialogField_evaluateConditionals()
{
	var control = document.all.item(this.controlId);
	if(control == null)
	{
		alert("Unable to find control '"+this.controlId+"' in DialogField.evaluateConditionals()");
		return;
	}
	
	var conditionalFields = this.dependentConditions;
	for(var i = 0; i < conditionalFields.length; i++)
		conditionalFields[i].evaluate(control);
}

function DialogField_isValid()
{
	// perform default validation first
	var control = document.all.item(this.controlId);
	if(this.flags & FLDFLAG_REQUIRED)
	{
		if(control.value.length == 0)
		{
			alert(this.caption + " is required.");
			control.focus();
			return false;
		}
	}

	// now see if there are any type-specific validations to perform
	var fieldType = this.type;
	if(fieldType != null && fieldType.isValid != null)
		return fieldType.isValid(control);

	return true;
}

function DialogField_doPreSubmit()
{
	if(this.style != null && this.style == SELECTSTYLE_MULTIDUAL)
	{
		// Select all items in multidual elements. If items aren't selected, 
		// they won't be posted.
		var control = document.all.item(this.controlId);
		for (var i = 0; i < control.options.length; i++)
		{
			control.options[i].selected = true;
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

function DialogFieldConditionalDisplay_evaluate(control)
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
	
	var fieldAreaId = FIELDROW_PREFIX + this.source;
	var fieldAreaElem = document.all.item(fieldAreaId);
	if(fieldAreaElem == null)
	{
		fieldAreaId = GRIDFIELDROW_PREFIX + this.source;
		fieldAreaElem = document.all.item(fieldAreaId);
	
		if(fieldAreaElem == null)
		{
			var condSource = activeDialog.fieldsByQualName[this.source];
			fieldAreaElem = document.all.item(condSource.controlId);

			if(fieldAreaElem == null)
			{
				alert ('Neither source element "' + fieldAreaId + '" or "'+ condSource.controlId +'" found in conditional partner.');
				return;
			}
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
		fieldAreaElem.style.display = '';
	}
	else
	{
		//fieldAreaElem.className = 'section_field_area_conditional';
		fieldAreaElem.style.display = 'none';
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
			objSelect.options[i] = null;	// This removes item and reduces count
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

function controlOnFocus(control)
{
	control.style.backgroundColor = "lightyellow";
}

function controlOnChange(control)
{
	field = activeDialog.fieldsById[control.name];
	if(field == null)
	{
		alert("Control '"+ control.name + "' was not found in activeDialog.fieldsById");
		return;
	}
	if(field.dependentConditions.length > 0)	
	{
		var conditionalFields = field.dependentConditions;
		for(var i = 0; i < conditionalFields.length; i++)
			conditionalFields[i].evaluate(control);
	}
}

function controlOnBlur(control)
{
	control.style.backgroundColor = "";
}


dialogLibraryLoaded = true;