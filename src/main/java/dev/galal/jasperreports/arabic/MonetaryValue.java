package dev.galal.jasperreports.arabic;

import java.math.BigDecimal;
import java.util.Objects;

import static java.util.Optional.ofNullable;


/**
 * Provides utils for generating an arabic sentence describing a monetary value. This is a common requirement in arabic invoices.
 * This was based on a code provided by another library long time ago, unfortunately I don't recall the source for this code.
 * */
public class MonetaryValue {
    private static final String DEFAULT_EN_PREFIX = "";
    private static final String DEFAULT_EN_SUFFIX = "only.";
    private static final String DEFAULT_AR_PREFIX = "فقط";
    private static final String DEFAULT_AR_SUFFIX = "لا غير.";
    private static final String[] englishOnes = new String[]{"Zero", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
    private static final String[] englishTens = new String[]{"Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};
    private static final String[] englishGroup = new String[]{"Hundred", "Thousand", "Million", "Billion", "Trillion", "Quadrillion", "Quintillion", "Sextillian", "Septillion", "Octillion", "Nonillion", "Decillion", "Undecillion", "Duodecillion", "Tredecillion", "Quattuordecillion", "Quindecillion", "Sexdecillion", "Septendecillion", "Octodecillion", "Novemdecillion", "Vigintillion", "Unvigintillion", "Duovigintillion", "10^72", "10^75", "10^78", "10^81", "10^84", "10^87", "Vigintinonillion", "10^93", "10^96", "Duotrigintillion", "Trestrigintillion"};
    private static final String[] arabicOnes = new String[]{"", "واحد", "اثنان", "ثلاثة", "أربعة", "خمسة", "ستة", "سبعة", "ثمانية", "تسعة", "عشرة", "أحد عشر", "اثنا عشر", "ثلاثة عشر", "أربعة عشر", "خمسة عشر", "ستة عشر", "سبعة عشر", "ثمانية عشر", "تسعة عشر"};
    private static final String[] arabicFeminineOnes = new String[]{"", "إحدى", "اثنتان", "ثلاث", "أربع", "خمس", "ست", "سبع", "ثمان", "تسع", "عشر", "إحدى عشرة", "اثنتا عشرة", "ثلاث عشرة", "أربع عشرة", "خمس عشرة", "ست عشرة", "سبع عشرة", "ثماني عشرة", "تسع عشرة"};
    private static final String[] arabicTens = new String[]{"عشرون", "ثلاثون", "أربعون", "خمسون", "ستون", "سبعون", "ثمانون", "تسعون"};
    private static final String[] arabicHundreds = new String[]{"", "مائة", "مئتان", "ثلاثمائة", "أربعمائة", "خمسمائة", "ستمائة", "سبعمائة", "ثمانمائة", "تسعمائة"};
    private static final String[] arabicAppendedTwos = new String[]{"مئتا", "ألفا", "مليونا", "مليارا", "تريليونا", "كوادريليونا", "كوينتليونا", "سكستيليونا"};
    private static final String[] arabicTwos = new String[]{"مئتان", "ألفان", "مليونان", "ملياران", "تريليونان", "كوادريليونان", "كوينتليونان", "سكستيليونان"};
    private static final String[] arabicGroup = new String[]{"مائة", "ألف", "مليون", "مليار", "تريليون", "كوادريليون", "كوينتليون", "سكستيليون"};
    private static final String[] arabicAppendedGroup = new String[]{"", "ألفاً", "مليوناً", "ملياراً", "تريليوناً", "كوادريليوناً", "كوينتليوناً", "سكستيليوناً"};
    private static final String[] arabicPluralGroups = new String[]{"", "آلاف", "ملايين", "مليارات", "تريليونات", "كوادريليونات", "كوينتليونات", "سكستيليونات"};

    private final BigDecimal number;
    private final Currency currency;
    private final CurrencyInfo currencyInfo;
    private final String englishPrefixText;
    private final String englishSuffixText;
    private final String arabicPrefixText;
    private final String arabicSuffixText;
    private final long integerValue;
    private final int decimalValue;

    /**
     * Return a string in arabic describing the monetary value.</br>
     * ex: مئتان و أربعة و ثلاثون جنيهاً و خمسة و أربعون قرشاً لا غير
     * @param value monetary value
     * @param currency supported currencies are AED, SYP, SAR, TND, XAU, JOD, BHD, EGP
     * */
    public static String inArabic(BigDecimal value, Currency currency) {
        return new MonetaryValue(value, currency).asArabicSentence();
    }

    /**
     * Return a string in english describing the monetary value.</br>
     * ex:  Two Hundred Thirty Four Egyptian Pounds and Forty Five Piasters only.
     * @param value monetary value
     * @param currency supported currencies are AED, SYP, SAR, TND, XAU, JOD, BHD, EGP
     * */
    public static String inEnglish(BigDecimal value, Currency currency) {
        return new MonetaryValue(value, currency).asEnglishSentence();
    }

    /**
     * Return a new monetary value instance.</br>
     * @param value monetary value
     * @param currency supported currencies are AED, SYP, SAR, TND, XAU, JOD, BHD, EGP
     * */
    public static MonetaryValue of(BigDecimal value, Currency currency) {
        return new MonetaryValue(value, currency);
    }

    private MonetaryValue(BigDecimal number, Currency currency) {
        this(number, currency, DEFAULT_EN_PREFIX, DEFAULT_EN_SUFFIX, DEFAULT_AR_PREFIX, DEFAULT_AR_SUFFIX);
    }

    private MonetaryValue(BigDecimal number, Currency currency, String englishPrefixText, String englishSuffixText, String arabicPrefixText, String arabicSuffixText) {
        this.number = number;
        this.currency = currency;
        this.currencyInfo = new CurrencyInfo(currency);
        this.englishPrefixText = ofNullable(englishPrefixText).orElse(DEFAULT_EN_PREFIX);
        this.englishSuffixText = ofNullable(englishSuffixText).orElse(DEFAULT_EN_SUFFIX);
        this.arabicPrefixText = ofNullable(arabicPrefixText).orElse(DEFAULT_AR_PREFIX);
        this.arabicSuffixText = ofNullable(arabicSuffixText).orElse(DEFAULT_AR_SUFFIX);
        String[] splits = number.toString().split("\\.");
        integerValue = Long.parseLong(splits[0]);
        if (splits.length > 1) {
            decimalValue = Integer.parseInt(getDecimalValue(splits[1]));
        } else {
            decimalValue = 0;
        }
    }

    private String getDecimalValue(String decimalPart) {
        StringBuilder result = new StringBuilder();
        int decimalPartLength;
        if (currencyInfo.getPartPrecision() != decimalPart.length()) {
            decimalPartLength = decimalPart.length();

            int dec;
            for(dec = 0; dec < currencyInfo.partPrecision - decimalPartLength; ++dec) {
                decimalPart = decimalPart + "0";
            }

            dec = Math.min(decimalPart.length(), currencyInfo.getPartPrecision());
            result = new StringBuilder(decimalPart.substring(0, dec));
        } else {
            result = new StringBuilder(decimalPart);
        }

        for(decimalPartLength = result.length(); decimalPartLength < currencyInfo.getPartPrecision(); ++decimalPartLength) {
            result.append("0");
        }

        return result.toString();
    }

    private static String processGroup(int groupNumber) {
        int tens = groupNumber % 100;
        int hundreds = groupNumber / 100;
        String retVal = "";
        if (hundreds > 0) {
            retVal = String.format("%s %s", englishOnes[hundreds], englishGroup[0]);
        }

        if (tens > 0) {
            if (tens < 20) {
                retVal = retVal + (!Objects.equals(retVal, "") ? " " : "") + englishOnes[tens];
            } else {
                int ones = tens % 10;
                tens = tens / 10 - 2;
                retVal = retVal + (!Objects.equals(retVal, "") ? " " : "") + englishTens[tens];
                if (ones > 0) {
                    retVal = retVal + (!retVal.isEmpty() ? " " : "") + englishOnes[ones];
                }
            }
        }

        return retVal;
    }

    /**
     * Return a string in english describing the monetary value.</br>
     * ex:  Two Hundred Thirty Four Egyptian Pounds and Forty Five Piasters only.
     * */
    public String asEnglishSentence() {
        BigDecimal tempNumber = number;
        if (tempNumber.compareTo(new BigDecimal(0)) == 0) {
            return "Zero";
        } else {
            String decimalString = processGroup(decimalValue);
            String retVal = "";
            int group = 0;
            if (tempNumber.compareTo(new BigDecimal(0)) < 1) {
                retVal = englishOnes[0];
            } else {
                for(; tempNumber.compareTo(new BigDecimal(0)) > 0; ++group) {
                    int numberToProcess = tempNumber.remainder(new BigDecimal(1000)).intValue();
                    tempNumber = tempNumber.divideToIntegralValue(new BigDecimal(1000));
                    String groupDescription = processGroup(numberToProcess);
                    if (groupDescription != "") {
                        if (group > 0) {
                            retVal = String.format("%s %s", englishGroup[group], retVal);
                        }

                        retVal = String.format("%s %s", groupDescription, retVal);
                    }
                }
            }

            String formattedNumber = "";
            formattedNumber = formattedNumber + (englishPrefixText != "" ? String.format("%s ", englishPrefixText) : "");
            formattedNumber = formattedNumber + (retVal != "" ? retVal : "");
            formattedNumber = formattedNumber + (retVal != "" ? (integerValue == 1L ? currencyInfo.englishCurrencyName : currencyInfo.englishPluralCurrencyName) : "");
            formattedNumber = formattedNumber + (decimalString != "" ? " and " : "");
            formattedNumber = formattedNumber + (decimalString != "" ? decimalString : "");
            formattedNumber = formattedNumber + (decimalString != "" ? " " + (decimalValue == 1 ? currencyInfo.englishCurrencyPartName : currencyInfo.englishPluralCurrencyPartName) : "");
            formattedNumber = formattedNumber + (englishSuffixText != "" ? String.format(" %s", englishSuffixText) : "");
            return formattedNumber;
        }
    }

    private String getDigitFeminineStatus(int digit, int groupLevel) {
        if (groupLevel == -1) {
            return currencyInfo.isCurrencyPartNameFeminine ? arabicFeminineOnes[digit] : arabicOnes[digit];
        } else if (groupLevel == 0) {
            return currencyInfo.isCurrencyNameFeminine ? arabicFeminineOnes[digit] : arabicOnes[digit];
        } else {
            return arabicOnes[digit];
        }
    }

    private String processArabicGroup(int groupNumber, int groupLevel, BigDecimal remainingNumber) {
        int tens = groupNumber % 100;
        int hundreds = groupNumber / 100;
        String retVal = "";
        if (hundreds > 0) {
            if (tens == 0 && hundreds == 2) {
                retVal = String.format("%s", arabicAppendedTwos[0]);
            } else {
                retVal = String.format("%s", arabicHundreds[hundreds]);
            }
        }

        if (tens > 0) {
            if (tens < 20) {
                if (tens == 2 && hundreds == 0 && groupLevel > 0) {
                    if (integerValue != 2000L && integerValue != 2000000L && integerValue != 2000000000L && integerValue != 2000000000000L && integerValue != 2000000000000000L && integerValue != 2000000000000000000L) {
                        retVal = String.format("%s", arabicTwos[groupLevel]);
                    } else {
                        retVal = String.format("%s", arabicAppendedTwos[groupLevel]);
                    }
                } else {
                    if (retVal != "") {
                        retVal = retVal + " و ";
                    }

                    if (tens == 1 && groupLevel > 0 && hundreds == 0) {
                        retVal = retVal + " ";
                    } else if ((tens == 1 || tens == 2) && (groupLevel == 0 || groupLevel == -1) && hundreds == 0 && remainingNumber.compareTo(new BigDecimal(0)) == 0) {
                        retVal = retVal + "";
                    } else {
                        retVal = retVal + getDigitFeminineStatus(tens, groupLevel);
                    }
                }
            } else {
                int ones = tens % 10;
                tens = tens / 10 - 2;
                if (ones > 0) {
                    if (retVal != "") {
                        retVal = retVal + " و ";
                    }

                    retVal = retVal + getDigitFeminineStatus(ones, groupLevel);
                }

                if (retVal != "") {
                    retVal = retVal + " و ";
                }

                retVal = retVal + arabicTens[tens];
            }
        }

        return retVal;
    }

    /**
     * Return a string in arabic describing the monetary value.</br>
     * ex: مئتان و أربعة و ثلاثون جنيهاً و خمسة و أربعون قرشاً لا غير
     * */
    public String asArabicSentence() {
        BigDecimal tempNumber = number;
        if (tempNumber.compareTo(new BigDecimal(0)) == 0) {
            return "صفر";
        } else {
            String decimalString = processArabicGroup(decimalValue, -1, new BigDecimal(0));
            String retVal = "";

            for(Byte group = 0; tempNumber.compareTo(new BigDecimal(0)) > 0; group = (byte)(group + 1)) {
                int numberToProcess = tempNumber.remainder(new BigDecimal(1000)).intValue();
                tempNumber = tempNumber.divideToIntegralValue(new BigDecimal(1000));
                String groupDescription = processArabicGroup(numberToProcess, group, new BigDecimal(Math.floor(tempNumber.doubleValue())));
                if (groupDescription != "") {
                    if (group > 0) {
                        if (retVal != "") {
                            retVal = String.format("%s %s", "و", retVal);
                        }

                        if (numberToProcess != 2) {
                            if (numberToProcess % 100 != 1) {
                                if (numberToProcess >= 3 && numberToProcess <= 10) {
                                    retVal = String.format("%s %s", arabicPluralGroups[group], retVal);
                                } else if (retVal != "") {
                                    retVal = String.format("%s %s", arabicAppendedGroup[group], retVal);
                                } else {
                                    retVal = String.format("%s %s", arabicGroup[group], retVal);
                                }
                            } else {
                                retVal = String.format("%s %s", arabicGroup[group], retVal);
                            }
                        }
                    }

                    retVal = String.format("%s %s", groupDescription, retVal);
                }
            }

            String formattedNumber = "";
            formattedNumber = formattedNumber + (arabicPrefixText != "" ? String.format("%s ", arabicPrefixText) : "");
            formattedNumber = formattedNumber + (retVal != "" ? retVal : "");
            int remaining100;
            if (integerValue != 0L) {
                remaining100 = (int)(integerValue % 100L);
                if (remaining100 == 0) {
                    formattedNumber = formattedNumber + currencyInfo.arabic1CurrencyName;
                } else if (remaining100 == 1) {
                    formattedNumber = formattedNumber + currencyInfo.arabic1CurrencyName;
                } else if (remaining100 == 2) {
                    if (integerValue == 2L) {
                        formattedNumber = formattedNumber + currencyInfo.arabic2CurrencyName;
                    } else {
                        formattedNumber = formattedNumber + currencyInfo.arabic1CurrencyName;
                    }
                } else if (remaining100 >= 3 && remaining100 <= 10) {
                    formattedNumber = formattedNumber + currencyInfo.arabic310CurrencyName;
                } else if (remaining100 >= 11 && remaining100 <= 99) {
                    formattedNumber = formattedNumber + currencyInfo.arabic1199CurrencyName;
                }
            }

            formattedNumber = formattedNumber + (decimalValue != 0 ? " و " : "");
            formattedNumber = formattedNumber + (decimalValue != 0 ? decimalString : "");
            if (decimalValue != 0) {
                formattedNumber = formattedNumber + " ";
                remaining100 = decimalValue % 100;
                if (remaining100 == 0) {
                    formattedNumber = formattedNumber + currencyInfo.arabic1CurrencyPartName;
                } else if (remaining100 == 1) {
                    formattedNumber = formattedNumber + currencyInfo.arabic1CurrencyPartName;
                } else if (remaining100 == 2) {
                    formattedNumber = formattedNumber + currencyInfo.arabic2CurrencyPartName;
                } else if (remaining100 >= 3 && remaining100 <= 10) {
                    formattedNumber = formattedNumber + currencyInfo.arabic310CurrencyPartName;
                } else if (remaining100 >= 11 && remaining100 <= 99) {
                    formattedNumber = formattedNumber + currencyInfo.arabic1199CurrencyPartName;
                }
            }

            formattedNumber = formattedNumber + (arabicSuffixText != "" ? String.format(" %s", arabicSuffixText) : "");
            return formattedNumber;
        }
    }

    static class CurrencyInfo {
        Currency currencyID;
        String currencyCode;
        boolean isCurrencyNameFeminine;
        String englishCurrencyName;
        String englishPluralCurrencyName;
        String englishCurrencyPartName;
        String englishPluralCurrencyPartName;
        String arabic1CurrencyName;
        String arabic2CurrencyName;
        String arabic310CurrencyName;
        String arabic1199CurrencyName;
        String arabic1CurrencyPartName;
        String arabic2CurrencyPartName;
        String arabic310CurrencyPartName;
        String arabic1199CurrencyPartName;
        int partPrecision;
        boolean isCurrencyPartNameFeminine;

        public Currency getCurrencyID() {
            return this.currencyID;
        }

        public void setCurrencyID(Currency currencyID) {
            this.currencyID = currencyID;
        }

        public String getCurrencyCode() {
            return this.currencyCode;
        }

        public void setCurrencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
        }

        public boolean isCurrencyNameFeminine() {
            return this.isCurrencyNameFeminine;
        }

        public void setCurrencyNameFeminine(boolean isCurrencyNameFeminine) {
            this.isCurrencyNameFeminine = isCurrencyNameFeminine;
        }

        public String getEnglishCurrencyName() {
            return this.englishCurrencyName;
        }

        public void setEnglishCurrencyName(String englishCurrencyName) {
            this.englishCurrencyName = englishCurrencyName;
        }

        public String getEnglishPluralCurrencyName() {
            return this.englishPluralCurrencyName;
        }

        public void setEnglishPluralCurrencyName(String englishPluralCurrencyName) {
            this.englishPluralCurrencyName = englishPluralCurrencyName;
        }

        public String getEnglishCurrencyPartName() {
            return this.englishCurrencyPartName;
        }

        public void setEnglishCurrencyPartName(String englishCurrencyPartName) {
            this.englishCurrencyPartName = englishCurrencyPartName;
        }

        public String getEnglishPluralCurrencyPartName() {
            return this.englishPluralCurrencyPartName;
        }

        public void setEnglishPluralCurrencyPartName(String englishPluralCurrencyPartName) {
            this.englishPluralCurrencyPartName = englishPluralCurrencyPartName;
        }

        public String getArabic1CurrencyName() {
            return this.arabic1CurrencyName;
        }

        public void setArabic1CurrencyName(String arabic1CurrencyName) {
            this.arabic1CurrencyName = arabic1CurrencyName;
        }

        public String getArabic2CurrencyName() {
            return this.arabic2CurrencyName;
        }

        public void setArabic2CurrencyName(String arabic2CurrencyName) {
            this.arabic2CurrencyName = arabic2CurrencyName;
        }

        public String getArabic310CurrencyName() {
            return this.arabic310CurrencyName;
        }

        public void setArabic310CurrencyName(String arabic310CurrencyName) {
            this.arabic310CurrencyName = arabic310CurrencyName;
        }

        public String getArabic1199CurrencyName() {
            return this.arabic1199CurrencyName;
        }

        public void setArabic1199CurrencyName(String arabic1199CurrencyName) {
            this.arabic1199CurrencyName = arabic1199CurrencyName;
        }

        public String getArabic1CurrencyPartName() {
            return this.arabic1CurrencyPartName;
        }

        public void setArabic1CurrencyPartName(String arabic1CurrencyPartName) {
            this.arabic1CurrencyPartName = arabic1CurrencyPartName;
        }

        public String getArabic2CurrencyPartName() {
            return this.arabic2CurrencyPartName;
        }

        public void setArabic2CurrencyPartName(String arabic2CurrencyPartName) {
            this.arabic2CurrencyPartName = arabic2CurrencyPartName;
        }

        public String getArabic310CurrencyPartName() {
            return this.arabic310CurrencyPartName;
        }

        public void setArabic310CurrencyPartName(String arabic310CurrencyPartName) {
            this.arabic310CurrencyPartName = arabic310CurrencyPartName;
        }

        public String getArabic1199CurrencyPartName() {
            return this.arabic1199CurrencyPartName;
        }

        public void setArabic1199CurrencyPartName(String arabic1199CurrencyPartName) {
            this.arabic1199CurrencyPartName = arabic1199CurrencyPartName;
        }

        public int getPartPrecision() {
            return this.partPrecision;
        }

        public void setPartPrecision(int partPrecision) {
            this.partPrecision = partPrecision;
        }

        public boolean isCurrencyPartNameFeminine() {
            return this.isCurrencyPartNameFeminine;
        }

        public void setCurrencyPartNameFeminine(boolean isCurrencyPartNameFeminine) {
            this.isCurrencyPartNameFeminine = isCurrencyPartNameFeminine;
        }

        public CurrencyInfo(Currency currency) {
            switch (currency) {
                case AED:
                    this.currencyID = currency;
                    this.currencyCode = currency.toString();
                    this.isCurrencyNameFeminine = false;
                    this.englishCurrencyName = "UAE Dirham";
                    this.englishPluralCurrencyName = "UAE Dirhams";
                    this.englishCurrencyPartName = "Fils";
                    this.englishPluralCurrencyPartName = "Fils";
                    this.arabic1CurrencyName = "درهم إماراتي";
                    this.arabic2CurrencyName = "درهمان إماراتيان";
                    this.arabic310CurrencyName = "دراهم إماراتية";
                    this.arabic1199CurrencyName = "درهماً إماراتياً";
                    this.arabic1CurrencyPartName = "فلس";
                    this.arabic2CurrencyPartName = "فلسان";
                    this.arabic310CurrencyPartName = "فلوس";
                    this.arabic1199CurrencyPartName = "فلساً";
                    this.partPrecision = 2;
                    this.isCurrencyPartNameFeminine = false;
                    break;
                case JOD:
                    this.currencyID = currency;
                    this.currencyCode = currency.toString();
                    this.isCurrencyNameFeminine = false;
                    this.englishCurrencyName = "Jordanian Dinar";
                    this.englishPluralCurrencyName = "Jordanian Dinars";
                    this.englishCurrencyPartName = "Fils";
                    this.englishPluralCurrencyPartName = "Fils";
                    this.arabic1CurrencyName = "دينار أردني";
                    this.arabic2CurrencyName = "ديناران أردنيان";
                    this.arabic310CurrencyName = "دنانير أردنية";
                    this.arabic1199CurrencyName = "ديناراً أردنياً";
                    this.arabic1CurrencyPartName = "فلس";
                    this.arabic2CurrencyPartName = "فلسان";
                    this.arabic310CurrencyPartName = "فلوس";
                    this.arabic1199CurrencyPartName = "فلساً";
                    this.partPrecision = 3;
                    this.isCurrencyPartNameFeminine = false;
                    break;
                case BHD:
                    this.currencyID = currency;
                    this.currencyCode = currency.toString();
                    this.isCurrencyNameFeminine = false;
                    this.englishCurrencyName = "Bahraini Dinar";
                    this.englishPluralCurrencyName = "Bahraini Dinars";
                    this.englishCurrencyPartName = "Fils";
                    this.englishPluralCurrencyPartName = "Fils";
                    this.arabic1CurrencyName = "دينار بحريني";
                    this.arabic2CurrencyName = "ديناران بحرينيان";
                    this.arabic310CurrencyName = "دنانير بحرينية";
                    this.arabic1199CurrencyName = "ديناراً بحرينياً";
                    this.arabic1CurrencyPartName = "فلس";
                    this.arabic2CurrencyPartName = "فلسان";
                    this.arabic310CurrencyPartName = "فلوس";
                    this.arabic1199CurrencyPartName = "فلساً";
                    this.partPrecision = 3;
                    this.isCurrencyPartNameFeminine = false;
                    break;
                case SAR:
                    this.currencyID = currency;
                    this.currencyCode = currency.toString();
                    this.isCurrencyNameFeminine = false;
                    this.englishCurrencyName = "Saudi Riyal";
                    this.englishPluralCurrencyName = "Saudi Riyals";
                    this.englishCurrencyPartName = "Halala";
                    this.englishPluralCurrencyPartName = "Halalas";
                    this.arabic1CurrencyName = "ريال سعودي";
                    this.arabic2CurrencyName = "ريالان سعوديان";
                    this.arabic310CurrencyName = "ريالات سعودية";
                    this.arabic1199CurrencyName = "ريالاً سعودياً";
                    this.arabic1CurrencyPartName = "هللة";
                    this.arabic2CurrencyPartName = "هللتان";
                    this.arabic310CurrencyPartName = "هللات";
                    this.arabic1199CurrencyPartName = "هللة";
                    this.partPrecision = 2;
                    this.isCurrencyPartNameFeminine = true;
                    break;
                case SYP:
                    this.currencyID = currency;
                    this.currencyCode = currency.toString();
                    this.isCurrencyNameFeminine = true;
                    this.englishCurrencyName = "Syrian Pound";
                    this.englishPluralCurrencyName = "Syrian Pounds";
                    this.englishCurrencyPartName = "Piaster";
                    this.englishPluralCurrencyPartName = "Piasteres";
                    this.arabic1CurrencyName = "ليرة سورية";
                    this.arabic2CurrencyName = "ليرتان سوريتان";
                    this.arabic310CurrencyName = "ليرات سورية";
                    this.arabic1199CurrencyName = "ليرة سورية";
                    this.arabic1CurrencyPartName = "قرش";
                    this.arabic2CurrencyPartName = "قرشان";
                    this.arabic310CurrencyPartName = "قروش";
                    this.arabic1199CurrencyPartName = "قرشاً";
                    this.partPrecision = 2;
                    this.isCurrencyPartNameFeminine = false;
                    break;
                case TND:
                    this.currencyID = currency;
                    this.currencyCode = currency.toString();
                    this.isCurrencyNameFeminine = false;
                    this.englishCurrencyName = "Tunisian Dinar";
                    this.englishPluralCurrencyName = "Tunisian Dinars";
                    this.englishCurrencyPartName = "milim";
                    this.englishPluralCurrencyPartName = "millimes";
                    this.arabic1CurrencyName = "درهم إماراتي";
                    this.arabic2CurrencyName = "درهمان إماراتيان";
                    this.arabic310CurrencyName = "دراهم إماراتية";
                    this.arabic1199CurrencyName = "درهماً إماراتياً";
                    this.arabic1CurrencyPartName = "فلس";
                    this.arabic2CurrencyPartName = "فلسان";
                    this.arabic310CurrencyPartName = "فلوس";
                    this.arabic1199CurrencyPartName = "فلساً";
                    this.partPrecision = 3;
                    this.isCurrencyPartNameFeminine = false;
                    break;
                case XAU:
                    this.currencyID = currency;
                    this.currencyCode = currency.toString();
                    this.isCurrencyNameFeminine = false;
                    this.englishCurrencyName = "Gram";
                    this.englishPluralCurrencyName = "Grams";
                    this.englishCurrencyPartName = "Milligram";
                    this.englishPluralCurrencyPartName = "Milligrams";
                    this.arabic1CurrencyName = "جرام";
                    this.arabic2CurrencyName = "جرامان";
                    this.arabic310CurrencyName = "جرامات";
                    this.arabic1199CurrencyName = "جراماً";
                    this.arabic1CurrencyPartName = "ملجرام";
                    this.arabic2CurrencyPartName = "ملجرامان";
                    this.arabic310CurrencyPartName = "ملجرامات";
                    this.arabic1199CurrencyPartName = "ملجراماً";
                    this.partPrecision = 2;
                    this.isCurrencyPartNameFeminine = false;
                    break;
                case EGP:
                    this.currencyID = currency;
                    this.currencyCode = currency.toString();
                    this.isCurrencyNameFeminine = false;
                    this.englishCurrencyName = "Egyptian Pound";
                    this.englishPluralCurrencyName = "Egyptian Pounds";
                    this.englishCurrencyPartName = "Piaster";
                    this.englishPluralCurrencyPartName = "Piasters";
                    this.arabic1CurrencyName = "جنيه";
                    this.arabic2CurrencyName = "جنيهان";
                    this.arabic310CurrencyName = "جنيهات";
                    this.arabic1199CurrencyName = "جنيهاً";
                    this.arabic1CurrencyPartName = "قرش";
                    this.arabic2CurrencyPartName = "قرشان";
                    this.arabic310CurrencyPartName = "قروش";
                    this.arabic1199CurrencyPartName = "قرشاً";
                    this.partPrecision = 2;
                    this.isCurrencyPartNameFeminine = false;
                    break;
                default:
                    this.currencyID = currency;
                    this.currencyCode = currency.toString();
                    this.isCurrencyNameFeminine = false;
                    this.englishCurrencyName = "Jordanian Dinar";
                    this.englishPluralCurrencyName = "Jordanian Dinars";
                    this.englishCurrencyPartName = "Fils";
                    this.englishPluralCurrencyPartName = "Fils";
                    this.arabic1CurrencyName = "دينار أردني";
                    this.arabic2CurrencyName = "ديناران أردنيان";
                    this.arabic310CurrencyName = "دنانير أردنية";
                    this.arabic1199CurrencyName = "دينارا أردنيا";
                    this.arabic1CurrencyPartName = "فلس";
                    this.arabic2CurrencyPartName = "فلسان";
                    this.arabic310CurrencyPartName = "فلس";
                    this.arabic1199CurrencyPartName = "فلسا";
                    this.partPrecision = 3;
                    this.isCurrencyPartNameFeminine = false;
            }

        }
    }

    public enum Currency {
        AED,
        SYP,
        SAR,
        TND,
        XAU,
        JOD,
        BHD,
        EGP;

        private Currency() {
        }
    }
}

