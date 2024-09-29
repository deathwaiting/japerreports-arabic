package io.github.deathwaiting.jasperreports.arabic;

import net.sf.jasperreports.functions.annotations.Function;
import net.sf.jasperreports.functions.annotations.FunctionParameter;
import net.sf.jasperreports.functions.annotations.FunctionParameters;

/**
 * Utilities for handling hindi numerals (١, ٢, ٣ , ...), that are used in arabic language.
 * */
public class HindiNumeralsUtils {

    private HindiNumeralsUtils() {}

    /**
     * Converts are Arabic numerals (1,2,3 ..) in the string to Hindi numerals (١, ٢, ٣ , ...)
     * @param str the string to convert
     * @return new string after replacing the arabic numerals
     * */
    @Function("TO_HINDU_NUMERALS")
    @FunctionParameters({
            @FunctionParameter("string"),
    })
    public static String toHindiNumerals(String str) {
        String r = str.replace("0", "٠");
        r = r.replace("1", "١");
        r = r.replace("2", "٢");
        r = r.replace("3", "٣");
        r = r.replace("4", "٤");
        r = r.replace("5", "٥");
        r = r.replace("6", "٦");
        r = r.replace("7", "٧");
        r = r.replace("8", "٨");
        r = r.replace("9", "٩");
        return r;
    }
}
