package com.netspective.sparx.util.set.test;

import com.netspective.sparx.util.set.IntSpan;

public class Grep
{
    boolean ok;

    String[] sets = {"-", "(-)", "(-0", "0-)", "1", "5", "1-5", "3-7", "1-3,8,10-23", };

    static class T implements IntSpan.Testable
    {
        public boolean test(int n)
        {
            return true;
        }

        public String toString()
        {
            return "true";
        }
    }

    static class F implements IntSpan.Testable
    {
        public boolean test(int n)
        {
            return false;
        }

        public String toString()
        {
            return "false";
        }
    }

    static class Eq1 implements IntSpan.Testable
    {
        public boolean test(int n)
        {
            return n == 1;
        }

        public String toString()
        {
            return "n==1";
        }
    }

    static class Lt5 implements IntSpan.Testable
    {
        public boolean test(int n)
        {
            return n < 5;
        }

        public String toString()
        {
            return "n<5";
        }
    }

    static class Odd implements IntSpan.Testable
    {
        public boolean test(int n)
        {
            return (n & 1) == 1;
        }

        public String toString()
        {
            return "n&1";
        }
    }

    IntSpan.Testable[] predicates = {new T(), new F(), new Eq1(), new Lt5(), new Odd()};

    String[][] expected =
            {
                {"", "", "", "", ""},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {"1", "", "1", "1", "1"},
                {"5", "", "", "", "5"},
                {"1-5", "", "1", "1-4", "1,3,5"},
                {"3-7", "", "", "3-4", "3,5,7"},
                {"1-3,8,10-23", "", "1", "1-3", "1,3,11,13,15,17,19,21,23"}
            };

    public Grep()
    {
        ok = true;
    }

    public void run()
    {
        for (int s = 0; s < sets.length; s++)
        {
            IntSpan set = new IntSpan(sets[s]);

            for (int p = 0; p < predicates.length; p++)
            {
                IntSpan.Testable test = predicates[p];
                IntSpan act = set.grep(test);
                String st = expected[s][p];
                IntSpan exp = st == null ? null : new IntSpan(st);

                if (act == null ^ exp == null ||
                        act != null && exp != null && !IntSpan.equal(act, exp))
                {
                    Object[] args = {test, set, act};
                    String problem = java.text.MessageFormat.format("grep {0} {1} -> {2}", args);
                    System.out.println(problem);

                    ok = false;
                }
            }
        }

        Object[] args = {ok ? "" : "not "};
        String msg = java.text.MessageFormat.format("Grep {0}ok", args);
        System.out.println(msg);
    }
}
