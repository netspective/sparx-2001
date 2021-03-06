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
 * @author Shahbaz Javeed
 */

/**
 * $Id: BasicDataValidationResult.java,v 1.2 2002-12-30 15:49:57 shahid.shah Exp $
 */
package com.netspective.sparx.xif.dal.validation.result;

import java.util.ArrayList;
import java.util.List;

/**
 * The BasicDataValidationResult class forms the basis of all other validation result classes.  The main functionality
 * that it provides is:
 *
 * <ul>
 * <li> Store a list of all the tests that are performed on a data type </li>
 * <li> Store a corresponding list of (pass/fail) status for each test on a particular datum </li>
 * <li> Store a corresponding list of (success/failure) messages for each test on a particular datum </li>
 * </ul>
 */
public class BasicDataValidationResult implements DataValidationResult
{
    public static int ALL_TESTS = 1;
    public static int FAILED_TESTS = ALL_TESTS << 1;
    public static int PASSED_TESTS = FAILED_TESTS << 1;

    protected List results = new ArrayList();
    protected String fieldName = "";

    public BasicDataValidationResult(String fieldName)
    {
        this.fieldName = fieldName;
        this.results = new ArrayList();
    }

    public BasicDataValidationResult(String fieldName, List results)
    {
        this.fieldName = fieldName;
        this.results = results;
    }

    public void addResultInfo(String ruleName, boolean status, String message)
    {
        DataValidationResultInfo ri = new DataValidationResultInfo(ruleName, status, message);
        results.add(ri);
    }

    public void addResultInfo(DataValidationResultInfo ri)
    {
        results.add(ri);
    }

    public void addResultInfo(int index, String ruleName, boolean status, String message)
    {
        DataValidationResultInfo ri = new DataValidationResultInfo(ruleName, status, message);
        results.add(index, ri);
    }

    public void addResultInfo(int index, DataValidationResultInfo ri)
    {
        results.add(index, ri);
    }

    public DataValidationResultInfo getResultInfo(int index)
    {
        return (DataValidationResultInfo) results.get(index);
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public void setFieldName(String fieldName)
    {
        this.fieldName = fieldName;
    }

    public List getResults(int type)
    {
        if (FAILED_TESTS == type)
            return getFailedResults();

        if (PASSED_TESTS == type)
            return getPassedResults();

        return getAllResults();
    }

    public List getAllResults()
    {
        return results;
    }

    public List getFailedResults()
    {
        List failedTests = new ArrayList();

        for (int i = 0; i < results.size(); i++)
        {
            DataValidationResultInfo ri = (DataValidationResultInfo) results.get(i);

            if (false == ri.isStatus())
                failedTests.add(ri);
        }

        return failedTests;
    }

    public List getPassedResults()
    {
        List passedTests = new ArrayList();

        for (int i = 0; i < results.size(); i++)
        {
            DataValidationResultInfo ri = (DataValidationResultInfo) results.get(i);

            if (true == ri.isStatus())
                passedTests.add(ri);
        }

        return passedTests;
    }

    public int getPassPercentage()
    {
        int passed = 0;
        int total = results.size();

        for (int i = 0; i < results.size(); i++)
        {
            DataValidationResultInfo ri = (DataValidationResultInfo) results.get(i);

            if (true == ri.isStatus())
                passed++;
        }

        if (passed == total)
            return 100;

        if (0 == passed)
            return 0;

        return (int) (((float) passed / (float) total) * 100.0);
    }

    public boolean isValid()
    {
        boolean status = true;

        if (100 > getPassPercentage())
        {
            status = false;
        }

        return status;
    }

    public String toString()
    {
        String output = "[bdvr: Name: " + fieldName + " (" + getPassPercentage() + ") ";

        for (int i = 0; i < results.size(); i++)
        {
            output += (results.get(i)).toString();
        }

        output += "]";

        return output;
    }
}
