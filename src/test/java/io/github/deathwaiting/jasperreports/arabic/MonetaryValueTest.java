package io.github.deathwaiting.jasperreports.arabic;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static io.github.deathwaiting.jasperreports.arabic.MonetaryValue.Currency.EGP;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MonetaryValueTest {
    @Test
    void testToArabicConversion() {
        var expectedSentenceAr = "فقط عشرة ملايين و سبعمائة و واحد و ثمانون ألفاً و مئتان و أربعة و ثلاثون جنيهاً و خمسة و أربعون قرشاً لا غير.";
        var expectedSentenceEn = "Ten Million Seven Hundred Eighty One Thousand Two Hundred Thirty Four Egyptian Pounds and Forty Five Piasters only.";
        assertEquals(expectedSentenceAr, MonetaryValue.inArabic(new BigDecimal("10781234.45"), EGP));
        assertEquals(expectedSentenceEn, MonetaryValue.inEnglish(new BigDecimal("10781234.45"), EGP));
    }
}
