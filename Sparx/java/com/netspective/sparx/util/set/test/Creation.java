package com.netspective.sparx.util.set.test;

import com.netspective.sparx.util.set.IntSpan;

public class Creation
{
    boolean ok;

    static Test[] tests =
            {
                new Test("", "-", new int[]{}),
                new Test("     ", "-", new int[]{}),
                new Test(" ( - )  ", "(-)", null),
                new Test("-2 -     -1  ", "-2--1", new int[]{-2, -1}),
                new Test("-", "-", new int[]{}),
                new Test("0", "0", new int[]{0}),
                new Test("1", "1", new int[]{1}),
                new Test("1-1", "1", new int[]{1}),
                new Test("-1", "-1", new int[]{-1}),
                new Test("1-2", "1-2", new int[]{1, 2}),
                new Test("-2--1", "-2--1", new int[]{-2, -1}),
                new Test("-2-1", "-2-1", new int[]{-2, -1, 0, 1}),
                new Test("1,2-4", "1-4", new int[]{1, 2, 3, 4}),
                new Test("1-3,4,5-7", "1-7", new int[]{1, 2, 3, 4, 5, 6, 7}),
                new Test("1-3,4", "1-4", new int[]{1, 2, 3, 4}),
                new Test("1,2,3,4,5,6,7", "1-7", new int[]{1, 2, 3, 4, 5, 6, 7}),
                new Test("1,2-)", "1-)", null),
                new Test("(-0,1-)", "(-)", null),
                new Test("(-)", "(-)", null),
                new Test("1-)", "1-)", null),
                new Test("(-1", "(-1", null),
                new Test("-3,-1-)", "-3,-1-)", null),
                new Test("(-1,3", "(-1,3", null)
            };

    public Creation()
    {
        ok = true;
    }

    public void run()
    {

        for (int i = 0; i < tests.length; i++)
        {
            Test t = tests[i];

            // System.out.println(t.input);
            IntSpan set = new IntSpan(t.input);
            String actual = set.runList();
            String expected = t.runList;
            if (!expected.equals(actual))
                report(expected, actual);

            IntSpan set1 = (IntSpan) (set.clone());
            actual = set1.runList();
            if (!expected.equals(actual))
                report(expected, actual);

            int[] eActual = set.elements();
            int[] eExpected = t.elements;

            if (eActual == null ^ eExpected == null ||
                    eActual != null && eExpected != null &&
                    !java.util.Arrays.equals(eActual, eExpected))
                report(eExpected, eActual);
        }

        Object[] args = {ok ? "" : "not "};
        String msg = java.text.MessageFormat.format("Creation {0}ok", args);
        System.out.println(msg);
    }

    void report(String expected, String actual)
    {
        Object[] args = {expected, actual};
        String problem = java.text.MessageFormat.format("{0} -> {1}", args);
        System.out.println(problem);
        ok = false;
    }

    void report(int[] expected, int[] actual)
    {
        dump(expected);
        System.out.print("-> ");
        dump(actual);
        System.out.println("");
        ok = false;
    }

    void dump(int[] a)
    {
        if (a == null)
            System.out.print("null");
        else
            for (int i = 0; i < a.length; i++)
            {
                System.out.print(a[i]);
                System.out.print(" ");
            }
    }

    static class Test
    {
        String input;
        String runList;
        int[] elements;

        Test(String input, String runList, int[] elements)
        {
            this.input = input;
            this.runList = runList;
            this.elements = elements;
        }
    }
}
