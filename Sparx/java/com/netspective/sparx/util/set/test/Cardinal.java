package com.netspective.sparx.util.set.test;

import com.netspective.sparx.util.set.IntSpan;

public class Cardinal
{
    boolean ok;

    static Test[] tests =
            {
                //		         C  E  F  N  P  I  U  min              max
                new Test("  -   ", 0, 1, 1, 0, 0, 0, 0, null, null),
                new Test(" (-)  ", -1, 0, 0, 1, 1, 1, 1, null, null),
                new Test(" (-0  ", -1, 0, 0, 1, 0, 1, 0, null, new Integer(0)),
                new Test(" 0-)  ", -1, 0, 0, 0, 1, 1, 0, new Integer(0), null),
                new Test("  1   ", 1, 0, 1, 0, 0, 0, 0, new Integer(1), new Integer(1)),
                new Test("  5   ", 1, 0, 1, 0, 0, 0, 0, new Integer(5), new Integer(5)),
                new Test(" 1,3,5", 3, 0, 1, 0, 0, 0, 0, new Integer(1), new Integer(5)),
                new Test(" 1,3-5", 4, 0, 1, 0, 0, 0, 0, new Integer(1), new Integer(5)),
                new Test("-1-5  ", 7, 0, 1, 0, 0, 0, 0, new Integer(-1), new Integer(5)),
            };

    public Cardinal()
    {
        ok = true;
    }

    public void run()
    {

        for (int i = 0; i < tests.length; i++)
        {
            Test t = tests[i];

            // System.out.println(t.runList);
            IntSpan set = new IntSpan(t.runList);
            metric(t, "cardinality", t.cardinality, set.cardinality());
            predicate(t, "empty", t.empty, set.empty());
            predicate(t, "finite", t.finite, set.finite());
            predicate(t, "negInf", t.negInf, set.negInfite());
            predicate(t, "posInf", t.posInf, set.posInfite());
            predicate(t, "infinite", t.infinite, set.infinite());
            predicate(t, "universal", t.universal, set.universal());
            metric(t, "min", t.min, set.min());
            metric(t, "max", t.max, set.max());
        }

        Object[] args = {ok ? "" : "not "};
        String msg = java.text.MessageFormat.format("Cardinal {0}ok", args);
        System.out.println(msg);
    }

    void metric(Test t, String name, int expected, int actual)
    {
        if (expected != actual)
        {
            Object[] args = {t.runList, name, new Integer(expected),
                             new Integer(actual)};
            String format = "{0} {1}: {2} -> {3}";
            String msg = java.text.MessageFormat.format(format, args);
            System.out.println(msg);
            ok = false;
        }
    }

    void metric(Test t, String name, Integer expected, Integer actual)
    {
        if (expected == null ^ actual == null ||
                expected != null && actual != null &&
                expected.intValue() != actual.intValue())
        {
            Object[] args = {t.runList, name, expected, actual};
            String format = "{0} {1}: {2} -> {3}";
            String msg = java.text.MessageFormat.format(format, args);
            System.out.println(msg);
            ok = false;
        }
    }

    void predicate(Test t, String name, boolean expected, boolean actual)
    {
        if (expected ^ actual)
        {
            Object[] args = {t.runList, name, new Boolean(expected),
                             new Boolean(actual)};
            String format = "{0} {1}: {2} -> {3}";
            String msg = java.text.MessageFormat.format(format, args);
            System.out.println(msg);
            ok = false;
        }
    }

    static class Test
    {
        String runList;
        int cardinality;
        boolean empty;
        boolean finite;
        boolean negInf;
        boolean posInf;
        boolean infinite;
        boolean universal;
        Integer min;
        Integer max;

        Test(String runList,
             int cardinality,
             int empty,
             int finite,
             int negInf,
             int posInf,
             int infinite,
             int universal,
             Integer min,
             Integer max)
        {
            this.runList = runList;
            this.cardinality = cardinality;
            this.empty = empty > 0 ? true : false;
            this.finite = finite > 0 ? true : false;
            this.negInf = negInf > 0 ? true : false;
            this.posInf = posInf > 0 ? true : false;
            this.infinite = infinite > 0 ? true : false;
            this.universal = universal > 0 ? true : false;
            this.min = min;
            this.max = max;
        }
    }
}
