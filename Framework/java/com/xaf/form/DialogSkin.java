package com.xaf.form;

import java.io.*;
import java.util.*;
import com.xaf.form.field.*;

public interface DialogSkin
{
	public String getHtml(DialogContext dc);
	public String getCompositeControlsHtml(DialogContext dc, DialogField field);
	public String getGridControlsHtml(DialogContext dc, GridField gridField);
	public String getSeparatorHtml(DialogContext dc, SeparatorField field);
	public String getDefaultControlAttrs();
	public String getControlAreaFontAttrs();
}
