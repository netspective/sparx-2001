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
 * $Id: SegmentRef.java,v 1.1 2002-02-27 00:49:19 snshah Exp $
 */

package com.netspective.junxion.edi.format.igml;

import com.netspective.junxion.edi.format.igml.util.TextContainer;
import com.netspective.junxion.edi.format.igml.util.TextList;
import com.netspective.junxion.edi.format.igml.attributes.Requirement;
import com.netspective.junxion.edi.format.igml.attributes.Usage;

public class SegmentRef implements TextContainer
{
    /**
     * The id or name of the segment.
     */
    private String id;

    /**
     * The position of the segment within the transaction set or message.
     */
    private String pos;

    /**
     * Requirement information for this segment.
     */
    private Requirement req;

    /**
     * Usage information for this segment.
     */
    private Usage usage;

    /**
     * The minimum usage or minimum repeat count of the segment.
     */
    private int minUse = 0;

    /**
     * The maximum usage or maximum repeat count of the segment. A value of "N/A" means unlimited ("N/A" applies to ASC
     * X12 only)
     */
    private int maxUse = 1;

    private TextList texts = new TextList();

    public SegmentRef()
    {
    }
    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getPos()
    {
        return pos;
    }

    public void setPos(String position)
    {
        this.pos = position;
    }

    public Requirement getReq()
    {
        return req;
    }

    public void setReq(Requirement requirement)
    {
        this.req = requirement;
    }

    public Usage getUsage()
    {
        return usage;
    }

    public void setUsage(Usage usage)
    {
        this.usage = usage;
    }

    public int getMinUse()
    {
        return minUse;
    }

    public void setMinUse(int minUse)
    {
        this.minUse = minUse;
    }

    public int getMaxUse()
    {
        return maxUse;
    }

    /**
     * In some cases, this number can be "N/A" so we have to check that.
     */
    public void setMaxUse(String maxUse)
    {
        if(maxUse.equals("N/A"))
            this.maxUse = Integer.MAX_VALUE;
        else
            this.maxUse = Integer.parseInt(maxUse);
    }

    public void addText(Text text)
    {
        texts.add(text);
    }

    public TextList getTexts()
    {
        return texts;
    }

    public void setTexts(TextList texts)
    {
        this.texts = texts;
    }

}
