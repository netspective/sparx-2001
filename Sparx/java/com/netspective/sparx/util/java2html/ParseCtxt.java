//           *** don't edit this file ***

// This class was generated (Tue May 30 08:25:22 GMT+01:00 2000)
// by the EnumGenerator tool based on the definitions in:
//
//    C:/usr/mhr/src/com/t_tank/j2h/myEnums.txt

package com.netspective.sparx.util.java2html;

import java.util.Iterator;

/**
 parse context enumeration
*/

class ParseCtxt
{
	public static final int lowerb = -1;
	public static final int none = 0;
	public static final int mlComment = 1;
	public static final int slComment = 2;
	public static final int string = 3;
	public static final int charLiteral = 4;
	public static final int upperb = 5;

	private int evalue = lowerb + 1;

	public ParseCtxt() {}

	public ParseCtxt(ParseCtxt value)
	{
		evalue = value.evalue;
	}

	public ParseCtxt(int value)
	{
		Eiffel.Require((value > lowerb) && (value < upperb), "illegal assignment value '" + value + "' for enum 'ParseCtxt'");

		evalue = value;
	}

	public int value() { return(evalue); }

	public static String value(int value)
	{
		Eiffel.Require((value > lowerb) && (value < upperb), "illegal value '" + value + "' for enum 'ParseCtxt'");

		return(evnames[value]);
	}

	public static Iterator iterator()
	{
		return(new EIterator());
	}

	public void set(ParseCtxt value)
	{
		evalue = value.evalue;
	}

	public void set(int value)
	{
		Eiffel.Require((value > lowerb) && (value < upperb), "illegal assignment value '" + value + "' for enum 'ParseCtxt'");

		evalue = value;
	}

	private static final String[] evnames =
	{
		"none", 
		"mlComment", 
		"slComment", 
		"string", 
		"charLiteral"
	};

	public String toString() { return("ParseCtxt." + evnames[evalue]); }

	private static final class EIterator implements Iterator
	{
		ParseCtxt ev;
		int currentValue;

		EIterator()
		{
			ev = new ParseCtxt();
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
				throw(new java.util.NoSuchElementException("No more ParseCtxt values!"));

			return(ev);
		}
		public void remove()
		{
			throw(new UnsupportedOperationException("remove() operation not supported for enumeration ParseCtxt!"));

		}
	}
}
