package com.plugincraft.geocaching.util;

public class Time {
    public static String ago(long ms) {
        long d = System.currentTimeMillis() - ms;
        long s = d/1000, m = s/60, h = m/60, days = h/24;
        if (days>0) return days+"d";
        if (h>0) return h+"h";
        if (m>0) return m+"m";
        return s+"s";
    }
}