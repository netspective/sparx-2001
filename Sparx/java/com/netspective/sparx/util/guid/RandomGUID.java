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
 * @author Erich K. Oliphant
 */
package com.netspective.sparx.util.guid;
import java.net.*;
import java.util.*;
import java.security.*;

/*
    Random UUID Generator based on code from:
    <a href://http://www.javaexchange.com/aboutRandomGUID.html >JavaExchange</a>

 */
public class RandomGUID extends Object {

    public String valueBeforeMD5 = "";
    public String valueAfterMD5 = "";
    private static Random myRand;
    private static SecureRandom mySecureRand;

    /*
     * Static block to take care of one time secureRandom seed.
     * It takes a few seconds to initialize SecureRandom.  You might
     * want to consider removing this static block or replacing
     * it with a "time since first loaded" seed to reduce this time.
     * This block will run only once per JVM instance.
     */

    static {
	mySecureRand = new SecureRandom();
	long secureInitializer = mySecureRand.nextLong();
	myRand = new Random(secureInitializer);
    }


    /*
     * Default constructor.  With no specification of security option,
     * this constructor defaults to lower security, high performance.
     */
    public RandomGUID() {
	getRandomGUID(false);
    }

    /*
     * Constructor with security option.  Setting secure true
     * enables each random number generated to be cryptographically
     * strong.  Secure false defaults to the standard Random function seeded
     * with a single cryptographically strong random number.
     */
    public RandomGUID(boolean secure) {
	getRandomGUID(secure);
    }

    /*
     * Method to generate the random GUID
     */
    private void getRandomGUID(boolean secure) {
	MessageDigest md5=null;
	StringBuffer sbValueBeforeMD5 = new StringBuffer();

	try {
	    md5 = MessageDigest.getInstance("MD5");
	} catch (NoSuchAlgorithmException e) {
	    System.out.println("Error: " + e);
	}

	try {
	    InetAddress id = InetAddress.getLocalHost();
	    long time = System.currentTimeMillis();
	    long rand = 0;

	    if(secure) {
		rand = mySecureRand.nextLong();
	    } else {
		rand = myRand.nextLong();
	    }

	    // This StringBuffer can be a long as you need; the MD5
	    // hash will always return 128 bits.  You can change
	    // the seed to include anything you want here.
	    // You could even stream a file through the MD5 making
	    // the odds of guessing it at least as great as that
	    // of guessing the contents of the file!
	    sbValueBeforeMD5.append(id.toString());
	    sbValueBeforeMD5.append(":");
	    sbValueBeforeMD5.append(Long.toString(time));
	    sbValueBeforeMD5.append(":");
	    sbValueBeforeMD5.append(Long.toString(rand));

	    valueBeforeMD5 = sbValueBeforeMD5.toString();
	    md5.update(valueBeforeMD5.getBytes());

	    byte [] array =  md5.digest();
	    StringBuffer sb = new StringBuffer();
	    for(int j=0; j<array.length;++j) {
		int b = array[j]&0xFF;
		if(b<0x10) sb.append('0');
		sb.append(Integer.toHexString(b));
	    }

	    valueAfterMD5 = sb.toString();

	} catch (UnknownHostException e) {
	    System.out.println("Error:" + e);
	}
    }


    /*
     * Convert to the standard format for GUID
     * (Useful for SQL Server UniqueIdentifiers, etc.)
     * Example: C2FEEEAC-CFCD-11D1-8B05-00600806D9B6
     */
    public String toString() {
	String raw = valueAfterMD5.toUpperCase();
	StringBuffer sb = new StringBuffer();
	sb.append(raw.substring(0,8));
	sb.append("-");
	sb.append(raw.substring(8,12));
	sb.append("-");
	sb.append(raw.substring(12,16));
	sb.append("-");
	sb.append(raw.substring(16,20));
	sb.append("-");
	sb.append(raw.substring(20));

	return sb.toString();
    }
    
    /*
     * Demonstraton and self test of class
     */
    public static void main(String args[]) {
	RandomGUID myGUID = new RandomGUID();
	System.out.println("Seeding String=" + myGUID.valueBeforeMD5);
	System.out.println("rawGUID=" + myGUID.valueAfterMD5);
	System.out.println("RandomGUID=" + myGUID.toString());
    }
}
