//           *** don't edit this file ***

// This class was generated (Tue May 30 08:25:22 GMT+01:00 2000)
// by the EnumGenerator tool based on the definitions in:
//
//    C:/usr/mhr/src/com/t_tank/j2h/myEnums.txt

package com.netspective.sparx.util.java2html;

import java.util.Iterator;

/**
 token classes
*/

class TokenClass
{
	public static final int lowerb = -1;
	public static final int plain = 0;
	public static final int keyword = 1;
	public static final int apiClass = 2;
	public static final int constant = 3;
	public static final int comment = 4;
	public static final int upperb = 5;

	private int evalue = lowerb + 1;

	public TokenClass() {}

	public TokenClass(TokenClass value)
	{
		evalue = value.evalue;
	}

	public TokenClass(int value)
	{
		Eiffel.Require((value > lowerb) && (value < upperb), "illegal assignment value '" + value + "' for enum 'TokenClass'");

		evalue = value;
	}

	public int value() { return(evalue); }

	public static String value(int value)
	{
		Eiffel.Require((value > lowerb) && (value < upperb), "illegal value '" + value + "' for enum 'TokenClass'");

		return(evnames[value]);
	}

	public static Iterator iterator()
	{
		return(new EIterator());
	}

	public void set(TokenClass value)
	{
		evalue = value.evalue;
	}

	public void set(int value)
	{
		Eiffel.Require((value > lowerb) && (value < upperb), "illegal assignment value '" + value + "' for enum 'TokenClass'");

		evalue = value;
	}

	private static final String[] evnames =
	{
		"plain", 
		"keyword", 
		"apiClass", 
		"constant", 
		"comment"
	};

	public String toString() { return("TokenClass." + evnames[evalue]); }

	private static final class EIterator implements Iterator
	{
		TokenClass ev;
		int currentValue;

		EIterator()
		{
			ev = new TokenClass();
			currentValue = ev.value();
		}
		public boolean hasNext()
		{
			if ((currentValue > ev.lowerb) && (currentValue < ev.upperb))
				return(true);
			else
				return(false);
		}
		public Object next()
		{
			if ((currentValue > ev.lowerb) && (currentValue < ev.upperb))
				ev.evalue = currentValue++;
			else
				throw(new java.util.NoSuchElementException("No more TokenClass values!"));

			return(ev);
		}
		public void remove()
		{
			throw(new UnsupportedOperationException("remove() operation not supported for enumeration TokenClass!"));

		}
	}
}
