package dataHelper;

import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;

public class DataHelper {

    private static final String approvedCardNumber = "4444 4444 4444 4441";
    private static final String declinedCardNumber = "4444 4444 4444 4442";
    private static final Faker fakerRU = new Faker(new Locale("ru"));
    private static final Faker fakerEN = new Faker(new Locale("en"));
    private static final String symbolStr = " *?/\\|<>,.()[]{};:'\"!@#$%^&";
    private static final int validShift = 3;

    private DataHelper() {
    }

    public static String getSymbolStr() {
        return symbolStr;
    }

    public static String generateNumber(int count) {
        return fakerEN.numerify("#".repeat(count));
    }

    public static String generateMouth() {
        int shift = new Random().nextInt(12) + 1;
        return LocalDate.now().plusMonths(shift).format(DateTimeFormatter.ofPattern("MM"));
    }

    public static String generateYear(int shift) {
        return LocalDate.now().plusYears(shift).format(DateTimeFormatter.ofPattern("yy"));
    }

    public static String generateYear() {
        return LocalDate.now().plusYears(validShift).format(DateTimeFormatter.ofPattern("yy"));
    }

    public static String generateOwner() {
        return fakerEN.name().firstName() + " " + fakerEN.name().lastName();
    }

    private static String generateOwnerInCyrillic() {
        return fakerRU.name().firstName() + " " + fakerRU.name().lastName();
    }

    public static String generateCvc() {
        return String.valueOf(fakerEN.number().numberBetween(100, 999));
    }

    public static CardInfo getCardInfo(boolean status) {
        if (status) {
            return new CardInfo(approvedCardNumber, generateMouth(), generateYear(validShift), generateOwner(), generateCvc());
        }
        return new CardInfo(declinedCardNumber, generateMouth(), generateYear(validShift), generateOwner(), generateCvc());
    }

    public static String getApprovedCardNumber() {
        return approvedCardNumber;
    }

    public static String getDeclinedCardNumber() {
        return approvedCardNumber;
    }
}