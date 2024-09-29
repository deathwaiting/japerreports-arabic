# jasperreports-arabic

A set of utilities for creating Arabic reports using [jasperreports](https://github.com/TIBCOSoftware/jasperreports).
## Usage 

Add to you maven project 

```xml
<dependency>
    <groupId>io.github.deathwaiting</groupId>
    <artifactId>jaserreports-arabic</artifactId>
    <version>1.0</version>
</dependency>
```

## Utilities

- `HindiNumeralsFormatFactory` :  
A `FormatFactory` implementation that makes Date and numeral fields use Hindi numeral - also called [Indo-Arabic numerals](https://en.wikipedia.org/wiki/Eastern_Arabic_numerals) -  instead of [Arabic numerals](https://en.wikipedia.org/wiki/Arabic_numerals) used by English language.
- `HindiNumeralsUtils` :  
Utils for converting Arabic numerals to hindi numerals in strings.
- `MonetaryValue` :
A class for representing monetary values, that can describe them as a sentence in Arabic or English. A common requirement for invoices in Arabic speaking countries.  
ex :
```java
var valueAsSentence = MonetaryValue.inArabic("10781234.45", EGP);
```
will produce this sentence in Arabic
```
فقط عشرة ملايين و سبعمائة و واحد و ثمانون ألفاً و مئتان و أربعة و ثلاثون جنيهاً و خمسة و أربعون قرشاً لا غير.
```
