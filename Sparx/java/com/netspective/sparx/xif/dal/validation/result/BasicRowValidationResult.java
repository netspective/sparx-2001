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
 * $Id: BasicRowValidationResult.java,v 1.2 2002-12-30 15:49:57 shahid.shah Exp $
 */
package com.netspective.sparx.xif.dal.validation.result;

import java.util.ArrayList;
import java.util.List;

public class BasicRowValidationResult implements RowValidationResult
{
    public static int ALL_TESTS = 1;
    public static int FAILED_TESTS = ALL_TESTS << 1;
    public static int PASSED_TESTS = FAILED_TESTS << 1;

    /**
     * The results List stores the DataValidationResult object for each column in the corresponding index location.
     * Therefore, we need no Map to help us get the DataValidationResult for any particular column
     */
    protected List results = new ArrayList();

    public BasicRowValidationResult()
    {
    }

    public BasicRowValidationResult(List results)
    {
        this.results = results;
    }

    public void addDataValidationResult(BasicDataValidationResult bdvResult)
    {
        results.add(bdvResult);
    }

    public void addDataValidationResult(int index, BasicDataValidationResult bdvResult)
    {
        results.add(index, bdvResult);
    }

    public List getResults(int type)
    {
        if (FAILED_TESTS == type)
            return getFailedResults();

        if (PASSED_TESTS == type)
            return getPassedResults();

        return getAllResults();
    }

    public List getFailedResults()
    {
        List failedTests = new ArrayList();

        for (int i = 0; i < results.size(); i++)
        {
            BasicDataValidationResult bdvResult = (BasicDataValidationResult) results.get(i);

            if (false == bdvResult.isValid())
                failedTests.add(bdvResult);
        }

        return failedTests;
    }

    public List getPassedResults()
    {
        List passedTests = new ArrayList();

        for (int i = 0; i < results.size(); i++)
        {
            BasicDataValidationResult bdvResult = (BasicDataValidationResult) results.get(i);

            if (true == bdvResult.isValid())
                passedTests.add(bdvResult);
        }

        return passedTests;
    }

    public List getAllResults()
    {
        return results;
    }

    public int getPassPercentage()
    {
        int passed = 0;
        int total = results.size();

        for (int i = 0; i < results.size(); i++)
        {
            BasicDataValidationResult bdvResult = (BasicDataValidationResult) results.get(i);

            if (true == bdvResult.isValid())
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
        String output = "[brvr: (" + getPassPercentage() + ") ";

        for (int i = 0; i < results.size(); i++)
        {
            output += results.get(i).toString();
        }

        output += "]";

        return output;
    }
}
