package com.netspective.sparx.util.set.test;

import com.netspective.sparx.util.set.IntSpan;

public class Map
{
    boolean ok;

    String[] sets = {"-", "(-)", "(-0", "0-)", "1", "5", "1-5", "3-7", "1-3,8,10-23", };

    static class Null implements IntSpan.Mappable
    {
        public int[] map(int n)
        {
            return new int[]{};
        }

        public String toString()
        {
            return "null";
        }
    }

    static class One implements IntSpan.Mappable
    {
        public int[] map(int n)
        {
            return new int[]{1};
        }

        public String toString()
        {
            return "1";
        }
    }

    static class I implements IntSpan.Mappable
    {
        public int[] map(int n)
        {
            return new int[]{n};
        }

        public String toString()
        {
            return "n";
        }
    }

    static class Neg implements IntSpan.Mappable
    {
        public int[] map(int n)
        {
            return new int[]{-n};
        }

        public String toString()
        {
            return "-n";
        }
    }

    static class P5 implements IntSpan.Mappable
    {
        public int[] map(int n)
        {
            return new int[]{n + 5};
        }

        public String toString()
        {
            return "n+5";
        }
    }

    static class MP implements IntSpan.Mappable
    {
        public int[] map(int n)
        {
            return new int[]{-n, n};
        }

        public String toString()
        {
            return "-n, n";
        }
    }

    static class Mod5 implements IntSpan.Mappable
    {
        public int[] map(int n)
        {
            return new int[]{n % 5};
        }

        public String toString()
        {
            return "n%5";
        }
    }

    IntSpan.Mappable[] maps = {new Null(), new One(), new I(), new Neg(), new P5(), new MP(), new Mod5()};

    String[][] expected =
            {
                {"", "", "", "", "", "", ""},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {"", "1", "1", "-1", "6", "-1,1", "1"},
                {"", "1", "5", "-5", "10", "-5,5", "0"},
                {"", "1", "1-5", "-5--1", "6-10", "-5--1,1-5", "0-4"},
                {"", "1", "3-7", "-7--3", "8-12", "-7--3,3-7", "0-4"},
                {"", "1", "1-3,8,10-23", "-23--10,-8,-3--1", "6-8,13,15-28", "-23--10,-8,-3--1,1-3,8,10-23", "0-4"},
            };

    public Map()
    {
        ok = true;
    }

    public void run()
    {
        for (int s = 0; s < sets.length; s++)
        {
            IntSpan set = new IntSpan(sets[s]);

            for (int m = 0; m < maps.length; m++)
            {
                IntSpan.Mappable map = maps[m];
                IntSpan act = set.map(map);
                String st = expected[s][m];
                IntSpan exp = st == null ? null : new IntSpan(st);

                if (act == null ^ exp == null ||
                        act != null && exp != null && !IntSpan.equal(act, exp))
                {
                    Object[] args = {map, set, act};
                    String problem = java.text.MessageFormat.format("map {0} {1} -> {2}", args);
                    System.out.println(problem);

                    ok = false;
                }
            }
        }

        Object[] args = {ok ? "" : "not "};
        String msg = java.text.MessageFormat.format("Map {0}ok", args);
        System.out.println(msg);
    }
}
