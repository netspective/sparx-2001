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
 * $Id: Rows.java,v 1.2 2002-12-04 17:51:56 shahbaz.javeed Exp $
 */

package com.netspective.sparx.xif.dal;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

public interface Rows
{
    /**
     * Given a ResultSet, loop through each row in the ResultSet and create an
     * appropriate Row object that represents a ResultSet row. The assumption is
     * that this method retrieves the columns in each ResultSet row in the order
     * the column was defined in the table (by a 1-based numeric index).
     */
    public void populateDataByIndexes(ResultSet resultSet) throws SQLException;

    /**
     * Given a ResultSet, loop through each row in the ResultSet and create an
     * appropriate Row object that represents a ResultSet row. The ordering of
     * columns is not important because each Row that will be created will search
     * for its columns in the ResultSet by name.
     */
    public void populateDataByNames(ResultSet resultSet) throws SQLException;

    /**
     * Given an XML element that contains row/column data, extract each row and
     * create an appropriate Row instance. The main element must follow this DTD
     * (the element parameter to the method call is assumed to be the "data"
     * element):
     * <pre>
     *      <!ELEMENT data (row)*>
     *          <!ELEMENT row (col)*>
     *              <!ELEMENT col %DATATYPE.TEXT;>
     *                  <!ATTLIST col name CDATA #REQUIRED>
     * </pre>
     */
    public void populateDataByNames(Element element) throws ParseException, DOMException;
}
