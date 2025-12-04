package com.adminpro.core.jdbc;

import java.util.Locale;

public final class Consts {

    public static final String YES = "Y";
    public static final String NO = "N";
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String OTHERS = "00";
    public static final String OTHERS_XX = "XX";
    public static final String OTHERS_O = "O";
    public static final String EMPTY_STR = "-";
    public static final String KIV = "K";

    // User domain
    public static final String DOMAIN_FRONTEND = "FE";
    public static final String DOMAIN_BACKEND = "BE";

    // common status
    public static final String ACTIVE = "A";
    public static final String DRAFT = "T";
    public static final String INACTIVE = "I";
    public static final String DELETED = "D";

    // Time Period
    public static final String TP_AM = "A";
    public static final String TP_PM = "P";

    // Time Type
    public static final String TT_AM = "AM";// AM
    public static final String TT_PM = "PM";// PM
    public static final String TT_FULL = "F";// Full Day

    // default char set
    public static final String DFT_CHARSET = "UTF-8";

    // default datasource
    public static final int DFT_PAGE_SIZE = 10;
    public static final int PAGE_SIZE_20 = 20;
    public static final int PAGE_SIZE_50 = 50;
    public static final int DFT_CUT_SIZE = 3000;
    public static final String DFT_DELIMITER = "|";
    public static final String DET_SPLIT = ":";

    // default language
    public static final String DFT_LANGUAGE = "EN";
    public static final Locale DFT_LOCALE = new Locale("en", "SG");
    public static final String DFT_TIME = "09:30";// AM
    public static final String DFT_MS_AM = "11:59" + " " + TT_AM;
    public static final String DFT_MS_PM = "11:59" + " " + TT_PM;

    private Consts() {
    }
}
