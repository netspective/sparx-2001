package com.netspective.sparx.util.set.test;

import com.netspective.sparx.util.set.IntSpan;

public class Binary
{
    boolean ok;

    static Test tests[] =
            {
                //         A              B       U           I        X           A-B      B-A
                new Test(" -        ", "  -  ", " -      ", " -   ", " -       ", " -  ", "  -  "),
                new Test(" -        ", " (-) ", "(-)     ", " -   ", "(-)      ", " -  ", " (-) "),
                new Test("(-)       ", " (-) ", "(-)     ", "(-)  ", " -       ", " -  ", "  -  "),
                new Test("(-)       ", " (-1 ", "(-)     ", "(-1  ", "2-)      ", "2-) ", "  -  "),
                new Test("(-0       ", " 1-) ", "(-)     ", " -   ", "(-)      ", "(-0 ", " 1-) "),
                new Test("(-0       ", " 2-) ", "(-0,2-) ", " -   ", "(-0,2-)  ", "(-0 ", " 2-) "),
                new Test("(-2       ", " 0-) ", "(-)     ", "0-2  ", "(--1,3-) ", "(--1", " 3-) "),
                new Test("1         ", " 1   ", "1       ", "1    ", " -       ", " -  ", "  -  "),
                new Test("1         ", " 2   ", "1-2     ", " -   ", "1-2      ", " 1  ", "  2  "),
                new Test("3-9       ", " 1-2 ", "1-9     ", " -   ", "1-9      ", "3-9 ", " 1-2 "),
                new Test("3-9       ", " 1-5 ", "1-9     ", "3-5  ", "1-2,6-9  ", "6-9 ", " 1-2 "),
                new Test("3-9       ", " 4-8 ", "3-9     ", "4-8  ", "3,9      ", "3,9 ", "  -  "),
                new Test("3-9       ", " 5-12", "3-12    ", "5-9  ", "3-4,10-12", "3-4 ", "10-12"),
                new Test("3-9       ", "10-12", "3-12    ", " -   ", "3-12     ", "3-9 ", "10-12"),
                new Test("1-3,5,8-11", " 1-6 ", "1-6,8-11", "1-3,5", "4,6,8-11 ", "8-11", "4,6  "),
            };

    public Binary()
    {
        ok = true;
    }

    public void run()
    {
        for (int i = 0; i < tests.length; i++)
        {
            Test t = tests[i];

            binary(t.A, t.B, new Union(), t.U);
            binary(t.A, t.B, new Intersect(), t.I);
            binary(t.A, t.B, new XOR(), t.X);
            binary(t.A, t.B, new Diff(), t.AB);
            binary(t.B, t.A, new Diff(), t.BA);
        }

        Object[] args = {ok ? "" : "not "};
        String msg = java.text.MessageFormat.format("Binary {0}ok", args);
        System.out.println(msg);
    }

    void binary(String rlA, String rlB, Operable op, String expected)
    {
        IntSpan A = new IntSpan(rlA);
        IntSpan B = new IntSpan(rlB);
        String actual = op.binary(A, B).runList();

        if (!expected.trim().equals(actual))
        {
            Object[] args = {A, op, B, actual};
            String problem = java.text.MessageFormat.format("{0} {1} {2} -> {3}", args);
            System.out.println(problem);
            ok = false;
        }
    }

    static interface Operable
    {
        IntSpan binary(IntSpan a, IntSpan b);
    }

    static class Union implements Operable
    {
        public IntSpan binary(IntSpan a, IntSpan b)
        {
            return IntSpan.union(a, b);
        }

        public String toString()
        {
            return "union";
        }
    }

    static class Intersect implements Operable
    {
        public IntSpan binary(IntSpan a, IntSpan b)
        {
            return IntSpan.intersect(a, b);
        }

        public String toString()
        {
            return "intersect";
        }
    }

    static class XOR implements Operable
    {
        public IntSpan binary(IntSpan a, IntSpan b)
        {
            return IntSpan.xor(a, b);
        }

        public String toString()
        {
            return "xor";
        }
    }

    static class Diff implements Operable
    {
        public IntSpan binary(IntSpan a, IntSpan b)
        {
            return IntSpan.diff(a, b);
        }

        public String toString()
        {
            return "diff";
        }
    }

    static private class Test
    {
        String A;
        String B;
        String U;
        String I;
        String X;
        String AB;
        String BA;

        Test(String A,
             String B,
             String U,
             String I,
             String X,
             String AB,
             String BA)
        {
            this.A = A;
            this.B = B;
            this.U = U;
            this.I = I;
            this.X = X;
            this.AB = AB;
            this.BA = BA;
        }
    }
}
