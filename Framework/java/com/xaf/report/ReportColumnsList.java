package com.xaf.report;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import java.util.*;

public class ReportColumnsList extends ArrayList
{
	public ReportColumn getColumn(int i)
	{
		return (ReportColumn) this.get(i);
	}
}