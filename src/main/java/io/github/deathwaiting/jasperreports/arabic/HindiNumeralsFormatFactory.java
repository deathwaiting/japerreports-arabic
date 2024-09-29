package io.github.deathwaiting.jasperreports.arabic;

import net.sf.jasperreports.engine.util.DefaultFormatFactory;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A JasperReports FormatFactory that makes reports use hindi numerals for Dates and numeric fields.
 * */
public class HindiNumeralsFormatFactory extends DefaultFormatFactory {
    @Override
    public NumberFormat createNumberFormat(String pattern, Locale locale) {
        String numPattern = pattern;
        if (pattern == null || pattern.isBlank()) {
            numPattern = "#,###";
        }

        var numberFormat = super.createNumberFormat(numPattern, locale);
        var decimalFormat = (DecimalFormat)numberFormat;
        var symbols = decimalFormat.getDecimalFormatSymbols();
        symbols.setZeroDigit('٠');
        decimalFormat.setDecimalFormatSymbols(symbols);

        return numberFormat;
    }

    @Override
    public DateFormat createDateFormat(String pattern, Locale locale, TimeZone tz) {
        var dateFormat = super.createDateFormat(pattern, locale, tz);
        var numberFormat = dateFormat.getNumberFormat();
        var decimalFormat = (DecimalFormat)numberFormat;
        var symbols = decimalFormat.getDecimalFormatSymbols();
        symbols.setZeroDigit('٠');
        decimalFormat.setDecimalFormatSymbols(symbols);

        return dateFormat;
    }
}
