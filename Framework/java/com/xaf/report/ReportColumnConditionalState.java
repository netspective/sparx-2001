/*
 * Created by IntelliJ IDEA.
 * User: snshah
 * Date: Nov 26, 2001
 * Time: 7:14:26 AM
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.xaf.report;

import org.w3c.dom.Element;

public interface ReportColumnConditionalState
{
    public boolean importFromXml(ReportColumn column, Element elem, int conditionalItem);
    public void makeStateChanges(ReportContext rc, ReportContext.ColumnState state);
}
