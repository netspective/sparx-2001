package com.xaf.form.taglib;

import javax.servlet.jsp.tagext.*;

public class DialogTagTEI extends TagExtraInfo
{
	public VariableInfo[] getVariableInfo(TagData data)
	{
		VariableInfo[] result =
		{
			new VariableInfo("dialog", "com.xaf.form.Dialog", true, VariableInfo.NESTED),
			new VariableInfo("dialogContext", "com.xaf.form.DialogContext", true, VariableInfo.NESTED)
		};
		return result;
	}

	public boolean isValid(TagData data)
	{
		return true;
	}
}