package com.netspective.sparx.util.set.test;

import com.netspective.sparx.util.set.IntSpan;

public class Member
{
    boolean ok;

    String[] sets = {"-", "(-)", "(-3", "3-)", "3", "3-5", "3-5,7-9"};

    int[][] isMember =
            {
                {0, 0, 0, 0, 0, 0, 0},
                {1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 0, 0, 0, 0},
                {0, 0, 1, 1, 1, 1, 1},
                {0, 0, 1, 0, 0, 0, 0},
                {0, 0, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 0, 1},
            };

    String[][] inserted =
            {
                {"1        ", "2      ", "3      ", "4      ", "5      ", "6      ", "7      "},
                {"(-)      ", "(-)    ", "(-)    ", "(-)    ", "(-)    ", "(-)    ", "(-)    "},
                {"(-3      ", "(-3    ", "(-3    ", "(-4    ", "(-3,5  ", "(-3,6  ", "(-3,7  "},
                {"1,3-)    ", "2-)    ", "3-)    ", "3-)    ", "3-)    ", "3-)    ", "3-)    "},
                {"1,3      ", "2-3    ", "3      ", "3-4    ", "3,5    ", "3,6    ", "3,7    "},
                {"1,3-5    ", "2-5    ", "3-5    ", "3-5    ", "3-5    ", "3-6    ", "3-5,7  "},
                {"1,3-5,7-9", "2-5,7-9", "3-5,7-9", "3-5,7-9", "3-5,7-9", "3-9    ", "3-5,7-9"},
            };

    String[][] removed =
            {
                {"-        ", "-      ", "-      ", "-      ", "-      ", "-      ", "-      "},
                {"(-0,2-)  ", "(-1,3-)", "(-2,4-)", "(-3,5-)", "(-4,6-)", "(-5,7-)", "(-6,8-)"},
                {"(-0,2-3  ", "(-1,3  ", "(-2    ", "(-3    ", "(-3    ", "(-3    ", "(-3    "},
                {"3-)      ", "3-)    ", "4-)    ", "3,5-)  ", "3-4,6-)", "3-5,7-)", "3-6,8-)"},
                {"3        ", "3      ", "-      ", "3      ", "3      ", "3      ", "3      "},
                {"3-5      ", "3-5    ", "4-5    ", "3,5    ", "3-4    ", "3-5    ", "3-5    "},
                {"3-5,7-9  ", "3-5,7-9", "4-5,7-9", "3,5,7-9", "3-4,7-9", "3-5,7-9", "3-5,8-9"},
            };


    public Member()
    {
        ok = true;
    }

    public void run()
    {
        for (int i = 0; i < sets.length; i++)
        {
            for (int j = 0; j < 7; j++)
            {
                int n = j + 1;
                member(sets[i], n, isMember[i][j]);
                delta(sets[i], n, new Insert(), inserted[i][j]);
                delta(sets[i], n, new Remove(), removed[i][j]);
            }
        }

        Object[] args = {ok ? "" : "not "};
        String msg = java.text.MessageFormat.format("Member {0}ok", args);
        System.out.println(msg);
    }

    void member(String runList, int n, int isMember)
    {
        IntSpan s = new IntSpan(runList);

        if (s.member(n) ^ isMember == 1)
        {
            Object[] args = {runList, new Integer(n)};
            String problem = java.text.MessageFormat.format("{1} element {0}", args);
            System.out.println(problem);

            ok = false;
        }
    }

    void delta(String runList, int n, Operable op, String expected)
    {
        IntSpan s = new IntSpan(runList);
        op.delta(s, n);
        String actual = s.runList();

        if (!expected.trim().equals(actual))
        {
            Object[] args = {op, s, new Integer(n), actual};
            String problem = java.text.MessageFormat.format("{0} {1} {2} -> {3}", args);
            System.out.println(problem);
            ok = false;
        }
    }

    static interface Operable
    {
        void delta(IntSpan s, int n);
    }

    static class Insert implements Operable
    {
        public void delta(IntSpan s, int n)
        {
            s.insert(n);
        }

        public String toString()
        {
            return "insert";
        }
    }

    static class Remove implements Operable
    {
        public void delta(IntSpan s, int n)
        {
            s.remove(n);
        }

        public String toString()
        {
            return "remove";
        }
    }
}
