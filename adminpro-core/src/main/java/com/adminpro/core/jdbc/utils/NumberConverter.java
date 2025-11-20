package com.adminpro.core.jdbc.utils;

public final class NumberConverter {
    public static final String SUP_SCRIPT = "P";
    public static final String SUB_SCRIPT = "B";
    private static final String[] LESS_THAN_TWENTY = {"zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen"};
    private static final String[] TENS = {"twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"};
    private static final String[] MORE_THAN_HUNDRED = {"hundred", "thousand", "million", "billion"};

    public static String getOrdinal(int i) {
        return getOrdinal(i, null);
    }

    public static String getOrdinal(int i, String script) {
        String result = "";
        String[] sufixes = new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                result = "th";
                break;
            default:
                result = sufixes[i % 10];
        }
        if (SUP_SCRIPT.equals(script)) {
            result = "<span class=\"sup\">" + result + "</span>";
        } else if (SUB_SCRIPT.equals(script)) {
            result = "<span class=\"sub\">" + result + "</span>";
        }
        return i + result;
    }

    // covert number less than 100
    private static String convertLessThanHundred(int num) throws Exception {
        if (num < 20) {
            return LESS_THAN_TWENTY[num];
        }

        int tensIdx = num / 10 - 2;
        if (num % 10 != 0) {
            return TENS[tensIdx] + "-" + LESS_THAN_TWENTY[num % 10];
        } else {
            return TENS[tensIdx];
        }
    }

    private static String convertLessThanHundred(int num, boolean capitalize) throws Exception {
        if (num < 20) {
            if (capitalize) {
                return (new StringBuilder()).append(Character.toUpperCase(LESS_THAN_TWENTY[num].charAt(0))).append(LESS_THAN_TWENTY[num].substring(1)).toString();
            } else {
                return LESS_THAN_TWENTY[num];
            }
        }

        int tensIdx = num / 10 - 2;
        if (num % 10 != 0) {
            if (capitalize) {
                return (new StringBuilder()).append(Character.toUpperCase(TENS[tensIdx].charAt(0))).append(TENS[tensIdx].substring(1)).toString() + "-" + (new StringBuilder()).append(Character.toUpperCase(LESS_THAN_TWENTY[num % 10].charAt(0))).append(LESS_THAN_TWENTY[num % 10].substring(1)).toString();
            } else {
                return TENS[tensIdx] + "-" + LESS_THAN_TWENTY[num % 10];
            }
        } else {
            if (capitalize) {
                return (new StringBuilder()).append(Character.toUpperCase(TENS[tensIdx].charAt(0))).append(TENS[tensIdx].substring(1)).toString();
            } else {
                return TENS[tensIdx];
            }
        }
    }

    // covert number less than 1000
    private static String convertLessThanThousand(int num) throws Exception {
        String word = "";
        int rem = num / 100;
        int mod = num % 100;
        if (rem > 0) {
            word = LESS_THAN_TWENTY[rem] + " " + MORE_THAN_HUNDRED[0];
            if (mod > 0) {
                word = word + " ";
            }
        }
        if (mod > 0) {
            if (StringUtil.isEmpty(word)) {
                word = word + convertLessThanHundred(mod);
            } else {
                word = word + "and " + convertLessThanHundred(mod);
            }
        }
        return word;
    }

    private static String convertLessThanThousand(int num, boolean capitalize) throws Exception {
        String word = "";
        int rem = num / 100;
        int mod = num % 100;
        if (rem > 0) {
            if (capitalize) {
                word = (new StringBuilder()).append(Character.toUpperCase(LESS_THAN_TWENTY[rem].charAt(0))).append(LESS_THAN_TWENTY[rem].substring(1)).toString() + " " + (new StringBuilder()).append(Character.toUpperCase(MORE_THAN_HUNDRED[0].charAt(0))).append(MORE_THAN_HUNDRED[0].substring(1)).toString();
            } else {
                word = LESS_THAN_TWENTY[rem] + " " + MORE_THAN_HUNDRED[0];
            }
            if (mod > 0) {
                word = word + " ";
            }
        }
        if (mod > 0) {
            if (StringUtil.isEmpty(word)) {
                if (capitalize) {
                    word = word + convertLessThanHundred(mod, capitalize);
                } else {
                    word = word + convertLessThanHundred(mod);
                }
            } else {
                if (capitalize) {
                    word = word + "and " + convertLessThanHundred(mod, capitalize);
                } else {
                    word = word + "and " + convertLessThanHundred(mod);
                }
            }
        }
        return word;
    }

    public static String convertToEnglish(int num) throws Exception {
        if (num < 100) {
            return convertLessThanHundred(num);
        }
        if (num < 1000) {
            return convertLessThanThousand(num);
        }
        for (int i = 0; i < MORE_THAN_HUNDRED.length; i++) {
            int dIdx = i - 1;
            int dVal = (int) Math.pow(1000, i);
            if (dVal > num) {
                int mod = (int) Math.pow(1000, dIdx);
                int l = num / mod;
                int r = num - (l * mod);
                String ret = convertLessThanThousand(l) + " " + MORE_THAN_HUNDRED[dIdx];
                if (r > 0) {
                    ret = ret + " and " + convertToEnglish(r);
                }
                return ret;
            }
        }
        throw new Exception("NubmerUtil: number " + num + " is out of range.");
    }

    public static String convertToEnglish(int num, boolean capitalize) throws Exception {
        if (num < 100) {
            if (capitalize) {
                return convertLessThanHundred(num, capitalize);
            } else {
                return convertLessThanHundred(num);
            }
        }
        if (num < 1000) {
            if (capitalize) {
                return convertLessThanThousand(num, capitalize);
            } else {
                return convertLessThanThousand(num);
            }
        }
        for (int i = 0; i < MORE_THAN_HUNDRED.length; i++) {
            int dIdx = i - 1;
            int dVal = (int) Math.pow(1000, i);
            if (dVal > num) {
                int mod = (int) Math.pow(1000, dIdx);
                int l = num / mod;
                int r = num - (l * mod);
                String ret = "";
                if (capitalize) {
                    ret = convertLessThanThousand(l, capitalize) + " " + (new StringBuilder()).append(Character.toUpperCase(MORE_THAN_HUNDRED[dIdx].charAt(0))).append(MORE_THAN_HUNDRED[dIdx].substring(1)).toString();
                } else {
                    ret = convertLessThanThousand(l) + " " + MORE_THAN_HUNDRED[dIdx];
                }
                if (r > 0) {
                    if (capitalize) {
                        ret = ret + " and " + convertToEnglish(r, capitalize);
                    } else {
                        ret = ret + " and " + convertToEnglish(r);
                    }
                }
                return ret;
            }
        }
        throw new Exception("NubmerUtil: number " + num + " is out of range.");
    }

    public static String convertToEnglishForMoney(double num) throws Exception {
        if (!NumberUtil.isPositive(num)) {
            return "Zero dollars";
        }
        String numString = Formatter.formatNumber(num);
        String numWithoutComma = numString.replace(",", "");
        String integerPartStr = "";
        String decimalPartStr = "";
        String resultStr = "";
        if (numWithoutComma.indexOf(".") > 0) {
            String integerPart = numWithoutComma.substring(0, numWithoutComma.indexOf("."));
            String decimalPart = numWithoutComma.substring(numWithoutComma.indexOf(".") + 1);
            if (!StringUtil.isEmpty(integerPart) && Integer.parseInt(integerPart) > 0) {
                System.out.println(integerPart);
                integerPartStr = convertToEnglish(Integer.parseInt(integerPart));
            }
            if (!StringUtil.isEmpty(decimalPart) && Integer.parseInt(decimalPart) > 0) {
                System.out.println(decimalPart);
                decimalPartStr = convertToEnglish(Integer.parseInt(decimalPart));
            }
            if (!StringUtil.isEmpty(integerPartStr) && !StringUtil.isEmpty(decimalPartStr)) {
                resultStr = integerPartStr + " dollars and " + decimalPartStr + " cents";
            } else if (!StringUtil.isEmpty(integerPartStr) && StringUtil.isEmpty(decimalPartStr)) {
                resultStr = integerPartStr + " dollars";
            } else if (StringUtil.isEmpty(integerPartStr) && !StringUtil.isEmpty(decimalPartStr)) {
                resultStr = decimalPartStr + " cents";
            }
        } else {
            resultStr = convertToEnglish(Integer.parseInt(numWithoutComma)) + " dollars";
        }
        return resultStr;
    }

    public static int getInt(String str) {
        return Integer.parseInt(str);
    }

    public static int getInt(String str, int defaultVal) {
        try {
            return getInt(str);
        } catch (Exception ex) {
            return defaultVal;
        }
    }

    private NumberConverter() {
    }
}
