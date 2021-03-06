
/* this file is generated by com.netspective.sparx.xaf.form.Dialog.getSubclassedDialogContextCode(), do not modify (you can extend it, though) */

package dialog.context.Test;

import com.netspective.sparx.xaf.form.*;

public class DialogTest06Context extends DialogContext
{
	public boolean isSelFieldListValueSet() { return hasValue("sel_field_list"); }
	public boolean isSelFieldListFlagSet(long flag) { return flagIsSet("sel_field_list", flag); }
	public void setSelFieldListFlag(long flag) { setFlag("sel_field_list", flag); }
	public void clearSelFieldListFlag(long flag) { clearFlag("sel_field_list", flag); }
	public String getSelFieldListRequestParam() { return request.getParameter("_dc.sel_field_list"); }
	public DialogField getSelFieldListField() { return getField("sel_field_list"); }
	public DialogContext.DialogFieldState getSelFieldListFieldState() { return getFieldState("sel_field_list"); }
	public void addSelFieldListErrorMsg(String msg) { addErrorMessage("sel_field_list", msg); }
	public String getSelFieldList() { return getValue("sel_field_list"); }
	public String getSelFieldList(String defaultValue) { return getValue("sel_field_list", defaultValue); }
	public int getSelFieldListInt() { String s = getValue("sel_field_list"); return s == null ? -1 : Integer.parseInt(s); }
	public int getSelFieldListInt(int defaultValue) { String s = getValue("sel_field_list"); return s == null ? defaultValue : Integer.parseInt(s); }
	public void setSelFieldList(String value) { setValue("sel_field_list", value); }
	public void setSelFieldList(int value) { setValue("sel_field_list", Integer.toString(value)); }

	public boolean isStaticFieldValueSet() { return hasValue("static_field"); }
	public boolean isStaticFieldFlagSet(long flag) { return flagIsSet("static_field", flag); }
	public void setStaticFieldFlag(long flag) { setFlag("static_field", flag); }
	public void clearStaticFieldFlag(long flag) { clearFlag("static_field", flag); }
	public String getStaticFieldRequestParam() { return request.getParameter("_dc.static_field"); }
	public DialogField getStaticFieldField() { return getField("static_field"); }
	public DialogContext.DialogFieldState getStaticFieldFieldState() { return getFieldState("static_field"); }
	public void addStaticFieldErrorMsg(String msg) { addErrorMessage("static_field", msg); }
	public String getStaticField() { return getValue("static_field"); }
	public String getStaticField(String defaultValue) { return getValue("static_field", defaultValue); }
	public String getStaticFieldOrBlank() { return getValue("static_field", ""); }
	public void setStaticField(String value) { setValue("static_field", value); }

	public boolean isCheckboxFieldValueSet() { return hasValue("checkbox_field"); }
	public boolean isCheckboxFieldFlagSet(long flag) { return flagIsSet("checkbox_field", flag); }
	public void setCheckboxFieldFlag(long flag) { setFlag("checkbox_field", flag); }
	public void clearCheckboxFieldFlag(long flag) { clearFlag("checkbox_field", flag); }
	public String getCheckboxFieldRequestParam() { return request.getParameter("_dc.checkbox_field"); }
	public DialogField getCheckboxFieldField() { return getField("checkbox_field"); }
	public DialogContext.DialogFieldState getCheckboxFieldFieldState() { return getFieldState("checkbox_field"); }
	public void addCheckboxFieldErrorMsg(String msg) { addErrorMessage("checkbox_field", msg); }
	public boolean getCheckboxField() { Boolean o = (Boolean) getValueAsObject("checkbox_field"); return o == null ? false : o.booleanValue(); }
	public boolean getCheckboxField(boolean defaultValue) { Boolean o = (Boolean) getValueAsObject("checkbox_field"); return o == null ? defaultValue : o.booleanValue(); }
	public void setCheckboxField(boolean value) { setValue("checkbox_field", value == true ? "1" : "0"); }

	public boolean isStaticField2ValueSet() { return hasValue("static_field2"); }
	public boolean isStaticField2FlagSet(long flag) { return flagIsSet("static_field2", flag); }
	public void setStaticField2Flag(long flag) { setFlag("static_field2", flag); }
	public void clearStaticField2Flag(long flag) { clearFlag("static_field2", flag); }
	public String getStaticField2RequestParam() { return request.getParameter("_dc.static_field2"); }
	public DialogField getStaticField2Field() { return getField("static_field2"); }
	public DialogContext.DialogFieldState getStaticField2FieldState() { return getFieldState("static_field2"); }
	public void addStaticField2ErrorMsg(String msg) { addErrorMessage("static_field2", msg); }
	public String getStaticField2() { return getValue("static_field2"); }
	public String getStaticField2(String defaultValue) { return getValue("static_field2", defaultValue); }
	public String getStaticField2OrBlank() { return getValue("static_field2", ""); }
	public void setStaticField2(String value) { setValue("static_field2", value); }

}
