//           *** don't edit this file ***

// This class was generated (Tue May 30 08:25:22 GMT+01:00 2000)
// by the EnumGenerator tool based on the definitions in:
//
//    C:/usr/mhr/src/com/t_tank/j2h/myEnums.txt

package com.netspective.sparx.util.java2html;

import java.util.Iterator;

/**
 wrap modes
*/

class WrapMode
{
	public static final int lowerb = -1;
  /**
     no wrapping
  */
	public static final int none = 0;
  /**
     exactly at wrap column (vi-like)
  */
	public static final int sharp = 1;
  /**
     wrap at word closest to wrap column
  */
	public static final int word = 2;
	public static final int upperb = 3;

	private int evalue = lowerb + 1;

	public WrapMode() {}

	public WrapMode(WrapMode value)
	{
		evalue = value.evalue;
	}

	public WrapMode(int value)
	{
		Eiffel.Require((value > lowerb) && (value < upperb), "illegal assignment value '" + value + "' for enum 'WrapMode'");

		evalue = value;
	}

	public int value() { return(evalue); }

	public static String value(int value)
	{
		Eiffel.Require((value > lowerb) && (value < upperb), "illegal value '" + value + "' for enum 'WrapMode'");

		return(evnames[value]);
	}

	public static Iterator iterator()
	{
		return(new EIterator());
	}

	public void set(WrapMode value)
	{
		evalue = value.evalue;
	}

	public void set(int value)
	{
		Eiffel.Require((value > lowerb) && (value < upperb), "illegal assignment value '" + value + "' for enum 'WrapMode'");

		evalue = value;
	}

	private static final String[] evnames =
	{
		"none", 
		"sharp", 
		"word"
	};

	public String toString() { return("WrapMode." + evnames[evalue]); }

	private static final class EIterator implements Iterator
	{
		WrapMode ev;
		int currentValue;

		EIterator()
		{
			ev = new WrapMode();
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
				throw(new java.util.NoSuchElementException("No more WrapMode values!"));

			return(ev);
		}
		public void remove()
		{
			throw(new UnsupportedOperationException("remove() operation not supported for enumeration WrapMode!"));

		}
	}
}
