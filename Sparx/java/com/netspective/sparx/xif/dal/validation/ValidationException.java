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
 * $Id: ValidationException.java,v 1.2 2002-12-30 15:49:57 shahid.shah Exp $
 */
package com.netspective.sparx.xif.dal.validation;

import com.netspective.sparx.xif.dal.validation.result.DataValidationResult;
import com.netspective.sparx.xif.dal.validation.result.DataValidationResultInfo;
import com.netspective.sparx.xif.dal.validation.result.RowValidationResult;

import java.util.List;

public class ValidationException extends RuntimeException
{
    String plainMessage = new String();
    String htmlMessage = new String();

    public ValidationException()
    {
    }

    public ValidationException(String s)
    {
        super(s);
    }

    public ValidationException(DataValidationResult dvResult)
    {
        super();

        plainMessage = formatPlainMessage(dvResult);
        htmlMessage = formatHtmlMessage(dvResult);
    }

    public ValidationException(RowValidationResult rvResult)
    {
        super();

        plainMessage = formatPlainMessage(rvResult);
        htmlMessage = formatHtmlMessage(rvResult);
    }

    public String getPlainMessage()
    {
        return plainMessage;
    }

    public void setPlainMessage(String plainMessage)
    {
        this.plainMessage = plainMessage;
    }

    public String getHtmlMessage()
    {
        return htmlMessage;
    }

    public void setHtmlMessage(String htmlMessage)
    {
        this.htmlMessage = htmlMessage;
    }

    public String getMessage()
    {
        return plainMessage;
    }

    private String formatPlainMessage(DataValidationResult dvResult)
    {
        String message = "\n";
        message += "\tField: " + dvResult.getFieldName() + "\n";
        List failedTests = dvResult.getFailedResults();

        for (int i = 0; i < failedTests.size(); i++)
        {
            DataValidationResultInfo resultInfo = (DataValidationResultInfo) failedTests.get(i);

            if (0 == i) message += "\t\tFailed Rules: ";
            message += resultInfo.getRuleName() + ((i < failedTests.size() - 1) ? ", " : "\n");
        }

        return message;
    }

    private String formatPlainMessage(RowValidationResult rvResult)
    {
        String message = "\n";
        List failedResults = rvResult.getFailedResults();

        for (int i = 0; i < failedResults.size(); i++)
        {
            DataValidationResult validationResult = (DataValidationResult) failedResults.get(i);
            List failedTests = validationResult.getFailedResults();
            message += "\tField: " + validationResult.getFieldName() + "\n";

            for (int j = 0; j < failedTests.size(); j++)
            {
                DataValidationResultInfo resultInfo = (DataValidationResultInfo) failedTests.get(j);

                if (0 == j) message += "\t\tFailed Rules: ";
                message += resultInfo.getRuleName() + ((j < failedTests.size() - 1) ? ", " : "\n");
            }
        }
        return message;
    }

    private String formatHtmlMessage(DataValidationResult dvResult)
    {
        String message = "";
        message += "<table>";
        message += "    <tr>";
        message += "        <td width=\"30%\"><b>Field</b></td>";
        message += "        <td width=\"70%\"><b>Failed Results</b></td>";
        message += "    </tr>";

        message += "<tr><td>" + dvResult.getFieldName() + "</td><td>";
        List failedTests = dvResult.getFailedResults();

        for (int i = 0; i < failedTests.size(); i++)
        {
            DataValidationResultInfo resultInfo = (DataValidationResultInfo) failedTests.get(i);
            message += resultInfo.getRuleName() + ((i < failedTests.size() - 1) ? ", " : "\n");
        }

        message += "</td></tr>";
        message += "</table>";

        return message;
    }

    private String formatHtmlMessage(RowValidationResult rvResult)
    {
        String message = "";
        message += "<table>";
        message += "    <tr>";
        message += "        <td width=\"30%\"><b>Field</b></td>";
        message += "        <td width=\"70%\"><b>Failed Results</b></td>";
        message += "    </tr>";
        List failedResults = rvResult.getFailedResults();

        for (int i = 0; i < failedResults.size(); i++)
        {
            DataValidationResult validationResult = (DataValidationResult) failedResults.get(i);
            List failedTests = validationResult.getFailedResults();
            message += "<tr><td>" + validationResult.getFieldName() + "</td><td>";

            for (int j = 0; j < failedTests.size(); j++)
            {
                DataValidationResultInfo resultInfo = (DataValidationResultInfo) failedTests.get(j);
                message += resultInfo.getRuleName() + ((j < failedTests.size() - 1) ? ", " : "\n");
            }

            message += "</td></tr>";
        }

        message += "</table>";

        return message;
    }

}
