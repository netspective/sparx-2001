package com.netspective.sparx.util.java2html;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

/**
<p>
<center>
<table border=1 cellspacing=0 cellpadding=10><tr align="center"><td><i>
&copy; 2000, Think Tank Ltd, Douglas, Isle Of Man.
All rights reserved.<br />
See <a href="../../overview-summary.html#overview_description">overview</a>
for full copyright notice, license and disclaimer.<br />
For more information please contact
<a href="mailto:info@t-tank.com?subject=JEAssert">info@t-tank.com</a>
</i></td></tr></table>
</center>
</p>

This class offers a subset of the design by contract (dbc) features
introduced in the <em>Eiffel</em> programming language:
<ul>
	<li>Preconditions</li>
	<li>Assertions</li>
	<li>Postconditions</li>
</ul>
Some of the features that require the support of an Eiffel
compiler (like the <em>old</em> clause in postconditions)
are not supported. For more information about dbc in general see the
<a href="http://www.eiffel.com/doc/manuals/technology/contract">
design by contract introduction</a> on <kbd>www.eiffel.com</kbd>
*/

public class Eiffel
{
	private static ByteArrayOutputStream gstOs = new ByteArrayOutputStream();
	private static PrintStream gstPs = new PrintStream(gstOs);

	private static String getStackTrace()   // gets a stack trace
	{
		Throwable t = new Throwable();
		t.printStackTrace(gstPs);

		String stack = gstOs.toString();

		stack = stack.substring(stack.lastIndexOf("Eiffel"));
		stack = stack.substring(stack.indexOf('\n'));

		return (stack);
	}

	/**
		Use this construct to formulate assertions that should
		hold true at the point at which the assertion is inserted.
		In case that the condition parameter evaluates to <em>false</em>
		an exception of type <code>Eiffel.EAssertion</code> will be thrown.<br />
		In the following example code line 15 will trigger the assertion violation
		below since it's invoking the <code>push()</code> operation on a
		zero-dimensioned stack object.

		<blockquote>
		<pre> <font color="navy">
			*Assertion violation : "Stack capacity exceeded!"
			*        at IntegerStack.push(IntegerStack.java:42)
			*        at IntegerStack.main(IntegerStack.java:15) </font>
		</pre>
		</blockquote>

		The exception is thrown on <a href="#line42">line 42</a>

		<pre>
   *  1 <font color="#b22222">import</font> com.netspective.sparx.util.java2html.Eiffel;
   *  2 
   *  3 <font color="#b22222">public</font> <font color="#b22222">class</font> IntegerStack
   *  4 {
   *  5   <font color="#b22222">private</font> <font color="#b22222">int</font>[] theStack;
   *  6   <font color="#b22222">private</font> <font color="#b22222">int</font> numOfElems;
   *  7   <font color="#b22222">private</font> <font color="#b22222">int</font> capacity;
   *  8 
   *  9   <font color="#b22222">public</font> <font color="#b22222">static</font> <font color="#b22222">void</font> main(<font color="#000080">String</font>[] args)
   * 10   {
   * 11     <font color="#b22222">try</font>
   * 12     {
   * 13       IntegerStack is = <font color="#b22222">new</font> IntegerStack(0);
   * 14       <font color="#00A0DD">// provoke assertion violation</font>
   * 15       is.push(42);
   * 16     }
   * 17     <font color="#b22222">catch</font> (Eiffel.Error ee)
   * 18     {
   * 19       ee.printStackTrace();
   * 20     }
   * 21   }
   * 22   <font color="#b22222">public</font> IntegerStack(<font color="#b22222">int</font> size)
   * 23   {
   * 24     <font color="#b22222">if</font> (size &gt; 0)
   * 25     {
   * 26       capacity = size;
   * 27       theStack = <font color="#b22222">new</font> <font color="#b22222">int</font>[capacity];
   * 28     }
   * 29   }
   * 30   <font color="#b22222">public</font> <font color="#b22222">boolean</font> isEmpty()
   * 31   {
   * 32     <font color="#b22222">return</font>(numOfElems == 0);
   * 33   }
   * 34   <font color="#b22222">public</font> <font color="#b22222">int</font> pop()
   * 35   {
   * 36     Eiffel.Require(!isEmpty(), <font color="#008b00">&quot;Can't invoke pop() on an empty stack!&quot;</font>);
   * 37 
   * 38     <font color="#b22222">return</font>(theStack[--numOfElems]);
   * 39   }
   * 40   <font color="#b22222">public</font> <font color="#b22222">void</font> push(<font color="#b22222">int</font> newElement)
   * 41   {
   * <a name="line42">42</a>     Eiffel.Assert(numOfElems &lt; capacity, <font color="#008b00">&quot;Stack capacity exceeded!&quot;</font>);
   * 43 
   * 44     theStack[numOfElems++] = newElement;
   * 45   }
   * 46 }
		</pre>

		@param condition a boolean expression specifying the assertion
		@param message message string for the exception that is thrown if the assertion is violated
	*/
	public static void Assert(boolean condition, String message) throws EAssertion	{
		if (!condition) {
			throw(new EAssertion(message));
		}
	}

	/**
		Use this construct to specify the preconditions
		a method expects to be met by its caller upon invocation.
		In case that the condition parameter evaluates to <em>false</em>
		an exception of type <code>Eiffel.EPrecondition</code> will be thrown.<br />
		The invocation of <code>pop()</code> on line 14 in the following
		code example will trigger the following exception:

		<blockquote>
		<pre> <font color="navy">
			*Precondition violation : "Can't invoke pop() on an empty stack!"
			*        at IntegerStack.pop(IntegerStack.java:31)
			*        at IntegerStack.main(IntegerStack.java:14) </font>
		</pre>
		</blockquote>

		The exception will be thrown in <a href="#line31">line 31</a> since <code>pop()</code>
		is invoked on an empty stack.

		<pre>
    *  1 <font color="#b22222">import</font> com.netspective.sparx.util.java2html.Eiffel;
    *  2 
    *  3 <font color="#b22222">public</font> <font color="#b22222">class</font> IntegerStack
    *  4 {
    *  5   <font color="#b22222">private</font> <font color="#b22222">int</font>[] theStack;
    *  6   <font color="#b22222">private</font> <font color="#b22222">int</font> numOfElems;
    *  7 
    *  8   <font color="#b22222">public</font> <font color="#b22222">static</font> <font color="#b22222">void</font> main(<font color="#000080">String</font>[] args)
    *  9   {
    * 10     <font color="#b22222">try</font>
    * 11     {
    * 12       IntegerStack is = <font color="#b22222">new</font> IntegerStack(10);
    * 13       <font color="#00A0DD">// provoke precondition violation</font>
    * 14       is.pop();
    * 15     }
    * 16     <font color="#b22222">catch</font> (Eiffel.Error ee)
    * 17     {
    * 18       ee.printStackTrace();
    * 19     }
    * 20   }
    * 21   <font color="#b22222">public</font> IntegerStack(<font color="#b22222">int</font> size)
    * 22   {
    * 23     theStack = <font color="#b22222">new</font> <font color="#b22222">int</font>[size];
    * 24   }
    * 25   <font color="#b22222">public</font> <font color="#b22222">boolean</font> isEmpty()
    * 26   {
    * 27     <font color="#b22222">return</font>(numOfElems == 0);
    * 28   }
    * 29   <font color="#b22222">public</font> <font color="#b22222">int</font> pop()
    * 30   {
    * <a name="line31">31</a>     Eiffel.Require(!isEmpty(), <font color="#008b00">&quot;Can't invoke pop() on an empty stack!&quot;</font>);
    * 32 
    * 33     <font color="#b22222">return</font>(theStack[--numOfElems]);
    * 34   }
    * 35 }
		</pre>

		@param condition a boolean expression specifying the precondition
		@param message message string for the exception that is thrown if the precondition is violated
	*/
	public static void Require(boolean condition, String message) throws EPrecondition
	{
		if (!condition) {
			throw(new EPrecondition(message));
		}
	}

	/**
		Use this construct to specify the postconditions
		a method will guarantee upon successful termination.
		In the following code example a bug was introduced intentionally
		in order to trigger the postcondition violation exception
		below.

		<blockquote>
		<pre> <font color="navy">
			*Postcondition violation : "wrong value for element counter!"
			*        at IntegerStack.push(IntegerStack.java:52)
			*        at IntegerStack.main(IntegerStack.java:14) </font>
		</pre>
		</blockquote>

		<a href="#line50">Line 50</a> in the code below is buggy since
		the increment operator
		is missing after <code>numOfElems</code>. This will trigger a
		postcondition violation exception.

		<pre>
    *  1 <font color="#b22222">import</font> com.netspective.sparx.util.java2html.Eiffel;
    *  2 
    *  3 <font color="#b22222">public</font> <font color="#b22222">class</font> IntegerStack
    *  4 {
    *  5   <font color="#b22222">private</font> <font color="#b22222">int</font>[] theStack;
    *  6   <font color="#b22222">private</font> <font color="#b22222">int</font> numOfElems;
    *  7   <font color="#b22222">private</font> <font color="#b22222">int</font> capacity;
    *  8 
    *  9   <font color="#b22222">public</font> <font color="#b22222">static</font> <font color="#b22222">void</font> main(<font color="#000080">String</font>[] args)
    * 10   {
    * 11     <font color="#b22222">try</font>
    * 12     {
    * 13       IntegerStack is = <font color="#b22222">new</font> IntegerStack(10);
    * 14       is.push(42);
    * 15     }
    * 16     <font color="#b22222">catch</font> (Eiffel.Error ee)
    * 17     {
    * 18       ee.printStackTrace();
    * 19     }
    * 20   }
    * 21   <font color="#b22222">public</font> IntegerStack(<font color="#b22222">int</font> size)
    * 22   {
    * 23     <font color="#b22222">if</font> (size &gt; 0)
    * 24     {
    * 25       capacity = size;
    * 26       theStack = <font color="#b22222">new</font> <font color="#b22222">int</font>[capacity];
    * 27     }
    * 28   }
    * 29   <font color="#b22222">public</font> <font color="#b22222">boolean</font> isEmpty()
    * 30   {
    * 31     <font color="#b22222">return</font>(numOfElems == 0);
    * 32   }
    * 33   <font color="#b22222">public</font> <font color="#b22222">int</font> pop()
    * 34   {
    * 35     Eiffel.Require(!isEmpty(), <font color="#008b00">&quot;Can't invoke pop() on an empty stack!&quot;</font>);
    * 36 
    * 37     <font color="#b22222">return</font>(theStack[--numOfElems]);
    * 38   }
    * 39   <font color="#b22222">public</font> <font color="#b22222">void</font> push(<font color="#b22222">int</font> newElement)
    * 40   {
    * 41     Eiffel.Assert(numOfElems &lt; capacity, <font color="#008b00">&quot;Stack capacity exceeded!&quot;</font>);
    * 42 
    * 43     <font color="#b22222">int</font> oldValueFor_numOfElems = numOfElems;
    * 44 
    * 45     <font color="#00A0DD">// a bug was introduced intentionally in line 50, it should</font>
    * 46     <font color="#00A0DD">// actually read</font>
    * 47     <font color="#00A0DD">//</font>
    * 48     <font color="#00A0DD">// theStack[numOfElems++] = newElement;</font>
    * 49 
    * <a name="line50">50</a>     theStack[numOfElems] = newElement;
    * 51 
    * 52     Eiffel.Ensure(numOfElems == (oldValueFor_numOfElems + 1), <font color="#008b00">&quot;wrong value for element counter!&quot;</font>);
    * 53   }
    * 54 }
		</pre>

		@param condition a boolean expression specifying the postcondition
		@param message message string for the exception that is thrown if the postcondition is violated
	*/
	public static void Ensure(boolean condition, String message) throws EPostcondition
	{
		if (!condition) {
			throw(new EPostcondition(message));
		}
	}

	/**
		Use this method to throw an error exception when an unrecoverable error
		occurs
	*/
	public static void Error(String message) throws EError
	{
		throw(new EError(message));
	}

	private Eiffel() {}

	/**
		Base class for all <code>Eiffel</code> error classes. Can
		be used to catch exceptions thrown by any <code>Eiffel</code>
		dbc construct as in the following example:
		<blockquote>
		<pre>
			* 1 import com.netspective.sparx.util.java2html.Eiffel;
			* 2
			* 3 public class XYZ
			* 4 {
			* 6   public static void main(String[] args)
			* 7   {
			* 8     try
			* 9     {
			*10       // whatever..
			*11     }
			*12     catch (Eiffel.Error ee)
			*13     {
			*14       System.err.println(ee.fullMessage());
			*15     }
			*16   }
			*17 }
		</pre>
		</blockquote>
	*/
	public static class Error extends java.lang.Error
	{
		String stack;

		public Error(String constructName, String message)
		{
			super(constructName + " : \"" + message + "\"");
			stack = Eiffel.getStackTrace();
		}
		/**
			returns the message that was supplied for this error plus
			the stack trace.
		*/
		public String fullMessage()
		{
			return(getMessage() + stack);
		}
		/**
			Reimplementation of the same method in "java.lang.Throwable"
			with a more useful stack trace (stopping at the point where
			the assertion was specified).
		*/
		public void printStackTrace()
		{
			System.err.println(fullMessage());
		}
		/**
			Reimplementation of the same method in "java.lang.Throwable"
			with a more useful stack trace (stopping at the point where
			the assertion was specified).
		*/
		public void printStackTrace(PrintStream s)
		{
			s.print(fullMessage());
		}
		/**
			Reimplementation of the same method in "java.lang.Throwable"
			with a more useful stack trace (stopping at the point where
			the assertion was specified).
		*/
		public void printStackTrace(PrintWriter s)
		{
			s.print(fullMessage());
		}
	}

	/**
		Exception thrown in case of a precondition violation
		@see com.netspective.sparx.util.java2html.Eiffel#Require
	*/
	public static class EPrecondition extends Error {
		public EPrecondition(String message)
		{
			super("Precondition violation", message);
		}
	}

	/**
		Exception thrown in case of a postcondition violation
		@see com.netspective.sparx.util.java2html.Eiffel#Ensure
	*/
	public static class EPostcondition extends Error {
		public EPostcondition(String message)
		{
			super("Postcondition violation", message);
		}
	}

	/**
		Exception thrown in case of an assertion violation
		@see com.netspective.sparx.util.java2html.Eiffel#Assert
	*/
	public static class EAssertion extends Error {
		public EAssertion(String message)
		{
			super("Assertion violation", message);
		}
	}

	/**
		Exception thrown upon invocation of <code>Eiffel.Error()</code>
		@see com.netspective.sparx.util.java2html.Eiffel#Error
	*/
	public static class EError extends Error {
		public EError(String message)
		{
			super("Error", message);
		}
	}
}
