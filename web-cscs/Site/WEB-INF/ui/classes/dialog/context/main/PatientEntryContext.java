
/* this file is generated by com.netspective.sparx.xaf.form.Dialog.getSubclassedDialogContextCode(), do not modify (you can extend it, though) */

package dialog.context.main;

import com.netspective.sparx.xaf.form.*;

public class PatientEntryContext extends DialogContext
{
	public boolean isNameFirstValueSet() { return hasValue("name_first"); }
	public boolean isNameFirstFlagSet(long flag) { return flagIsSet("name_first", flag); }
	public void setNameFirstFlag(long flag) { setFlag("name_first", flag); }
	public void clearNameFirstFlag(long flag) { clearFlag("name_first", flag); }
	public String getNameFirstRequestParam() { return request.getParameter("_dc.name_first"); }
	public DialogField getNameFirstField() { return getField("name_first"); }
	public DialogContext.DialogFieldState getNameFirstFieldState() { return getFieldState("name_first"); }
	public void addNameFirstErrorMsg(String msg) { addErrorMessage("name_first", msg); }
	public String getNameFirst() { return getValue("name_first"); }
	public String getNameFirst(String defaultValue) { return getValue("name_first", defaultValue); }
	public String getNameFirstOrBlank() { return getValue("name_first", ""); }
	public void setNameFirst(String value) { setValue("name_first", value); }

	public boolean isNameLastValueSet() { return hasValue("name_last"); }
	public boolean isNameLastFlagSet(long flag) { return flagIsSet("name_last", flag); }
	public void setNameLastFlag(long flag) { setFlag("name_last", flag); }
	public void clearNameLastFlag(long flag) { clearFlag("name_last", flag); }
	public String getNameLastRequestParam() { return request.getParameter("_dc.name_last"); }
	public DialogField getNameLastField() { return getField("name_last"); }
	public DialogContext.DialogFieldState getNameLastFieldState() { return getFieldState("name_last"); }
	public void addNameLastErrorMsg(String msg) { addErrorMessage("name_last", msg); }
	public String getNameLast() { return getValue("name_last"); }
	public String getNameLast(String defaultValue) { return getValue("name_last", defaultValue); }
	public String getNameLastOrBlank() { return getValue("name_last", ""); }
	public void setNameLast(String value) { setValue("name_last", value); }

	public boolean isSsnValueSet() { return hasValue("ssn"); }
	public boolean isSsnFlagSet(long flag) { return flagIsSet("ssn", flag); }
	public void setSsnFlag(long flag) { setFlag("ssn", flag); }
	public void clearSsnFlag(long flag) { clearFlag("ssn", flag); }
	public String getSsnRequestParam() { return request.getParameter("_dc.ssn"); }
	public DialogField getSsnField() { return getField("ssn"); }
	public DialogContext.DialogFieldState getSsnFieldState() { return getFieldState("ssn"); }
	public void addSsnErrorMsg(String msg) { addErrorMessage("ssn", msg); }
	public String getSsn() { return getValue("ssn"); }
	public String getSsn(String defaultValue) { return getValue("ssn", defaultValue); }
	public String getSsnOrBlank() { return getValue("ssn", ""); }
	public void setSsn(String value) { setValue("ssn", value); }

	public boolean isCityValueSet() { return hasValue("city"); }
	public boolean isCityFlagSet(long flag) { return flagIsSet("city", flag); }
	public void setCityFlag(long flag) { setFlag("city", flag); }
	public void clearCityFlag(long flag) { clearFlag("city", flag); }
	public String getCityRequestParam() { return request.getParameter("_dc.city"); }
	public DialogField getCityField() { return getField("city"); }
	public DialogContext.DialogFieldState getCityFieldState() { return getFieldState("city"); }
	public void addCityErrorMsg(String msg) { addErrorMessage("city", msg); }
	public String getCity() { return getValue("city"); }
	public String getCity(String defaultValue) { return getValue("city", defaultValue); }
	public String getCityOrBlank() { return getValue("city", ""); }
	public void setCity(String value) { setValue("city", value); }

	public boolean isStateValueSet() { return hasValue("state"); }
	public boolean isStateFlagSet(long flag) { return flagIsSet("state", flag); }
	public void setStateFlag(long flag) { setFlag("state", flag); }
	public void clearStateFlag(long flag) { clearFlag("state", flag); }
	public String getStateRequestParam() { return request.getParameter("_dc.state"); }
	public DialogField getStateField() { return getField("state"); }
	public DialogContext.DialogFieldState getStateFieldState() { return getFieldState("state"); }
	public void addStateErrorMsg(String msg) { addErrorMessage("state", msg); }
	public String getState() { return getValue("state"); }
	public String getState(String defaultValue) { return getValue("state", defaultValue); }
	public String getStateOrBlank() { return getValue("state", ""); }
	public void setState(String value) { setValue("state", value); }

	public boolean isZipValueSet() { return hasValue("zip"); }
	public boolean isZipFlagSet(long flag) { return flagIsSet("zip", flag); }
	public void setZipFlag(long flag) { setFlag("zip", flag); }
	public void clearZipFlag(long flag) { clearFlag("zip", flag); }
	public String getZipRequestParam() { return request.getParameter("_dc.zip"); }
	public DialogField getZipField() { return getField("zip"); }
	public DialogContext.DialogFieldState getZipFieldState() { return getFieldState("zip"); }
	public void addZipErrorMsg(String msg) { addErrorMessage("zip", msg); }
	public String getZip() { return getValue("zip"); }
	public String getZip(String defaultValue) { return getValue("zip", defaultValue); }
	public String getZipOrBlank() { return getValue("zip", ""); }
	public void setZip(String value) { setValue("zip", value); }

}