package dev.galal.jasperreports.arabic;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;

import static dev.galal.jasperreports.arabic.HindiNumeralsUtils.toHindiNumerals;
import static java.time.ZoneOffset.UTC;
import static java.util.TimeZone.getTimeZone;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TestFormatFactory {
    @Test
    void testHindiNumeralsFormatFactory() {
        var formatFactory = new HindiNumeralsFormatFactory();
        var testDate = Date.from(LocalDate.of(2023, 12, 12).atStartOfDay().toInstant(UTC));
        assertEquals("١٢٣", formatFactory.createNumberFormat("", Locale.getDefault()).format(123));
        assertEquals("٢٠٢٣", formatFactory.createDateFormat("yyyy", Locale.getDefault(), getTimeZone(UTC)).format(testDate));
    }

    @Test
    void testHindiNumeralsUtils() {
        assertEquals("bla ١٢٣", toHindiNumerals("bla 123"));
    }
}
