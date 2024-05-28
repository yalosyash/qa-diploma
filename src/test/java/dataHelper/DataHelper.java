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
    private static final int shift = 3;

    private DataHelper() {
    }

    private static String generateNumber() {
        return fakerEN.business().creditCardNumber();
    }

    private static String generateMouth() {
        int shift = new Random().nextInt(12) + 1;
        return LocalDate.now().plusMonths(shift).format(DateTimeFormatter.ofPattern("MM"));
    }

    private static String generateYear() {
        return LocalDate.now().plusYears(shift).format(DateTimeFormatter.ofPattern("yy"));
    }

    private static String generateOwner() {
        return fakerEN.name().firstName() + " " + fakerEN.name().lastName();
    }

    private static String generateOwnerInCyrillic() {
        return fakerRU.name().firstName() + " " + fakerRU.name().lastName();
    }

    private static String generateCvc() {
        return String.valueOf(fakerEN.number().numberBetween(100, 999));
    }

    public static CardInfo getCardInfo(boolean status) {
        if (status) {
            return new CardInfo(approvedCardNumber, generateMouth(), generateYear(), generateOwner(), generateCvc());
        }
        return new CardInfo(declinedCardNumber, generateMouth(), generateYear(), generateOwner(), generateCvc());
    }
}