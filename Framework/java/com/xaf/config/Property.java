package com.xaf.config;

/**
 * Title:        The eXtensible Application Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author
 * @version 1.0
 */

import org.w3c.dom.*;
import com.xaf.value.*;

public interface Property
{
	public final static long PROPFLAG_IS_FINAL              = 1;
	public final static long PROPFLAG_IS_DYNAMIC            = PROPFLAG_IS_FINAL * 2;
	public final static long PROPFLAG_HAS_REPLACEMENTS      = PROPFLAG_IS_DYNAMIC * 2;
	public final static long PROPFLAG_FINALIZE_ON_FIRST_GET = PROPFLAG_HAS_REPLACEMENTS * 2;

	public String getName();
	public String getExpression();
	public String getValue(ValueContext vc);
	public String getDescription();

	public boolean hasReplacements();
	public void setFinalValue(String value);

	public long getFlags();
	public boolean flagIsSet(long flag);
	public void setFlag(long flag);
	public void clearFlag(long flag);

	public void importFromXml(Element elem);
}