package test;

import com.codeborne.selenide.logevents.SelenideLogger;
import dataHelper.CardInfo;
import dataHelper.DataHelper;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import pages.MainPage;
import pages.PayPage;

import static com.codeborne.selenide.Selenide.open;
import static dataHelper.DataHelper.*;
import static dataHelper.SqlHelper.*;

public class CreditPaymentTourTests {

    private final String success = "Успешно";
    private final String error = "Ошибка";
    private final String wrongFormat = "Неверный формат";
    private final String invalidExpirationDate = "Неверно указан срок действия карты";
    private final String cardExpired = "Истёк срок действия карты";
    private final String requiredField = "Поле обязательно для заполнения";
    private final int countCardNumber = 16;
    private final int countOfMonth = 12;

    private final int inputNumber = 0;
    private final int inputMouth = 1;
    private final int inputYear = 2;
    private final int inputOwner = 3;
    private final int inputCvc = 4;

    private final String approved = "APPROVED";

    @BeforeAll
    static void setupAllureReports() {
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());
    }

    @BeforeEach
    void setup() {
        open(System.getProperty("sut.url"));
    }

    @AfterEach
    public void tearDown() {
        cleanDB();
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("AllureSelenide");
    }

    // [Функциональные тесты] ------------------------------------------------------------------------------------------

    @Test
    @DisplayName("Кредитная карта. Успешная оплата с подтвержденной карты (со значением “APPROVED”)")
    void successfulPayFromApprovedDebitCard() {
        CardInfo cardInfo = DataHelper.getCardInfo(true);

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();
        payPage.getNoticeText(success);
    }

    @Test
    @DisplayName("Кредитная карта. Результат выполнения операции в базе данных, при оплате с подтвержденной карты (со значением “APPROVED”)")
    void successfulPayFromApprovedDebitCardInDB() throws InterruptedException {
        CardInfo cardInfo = DataHelper.getCardInfo(true);

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();
        Thread.sleep(10000);
        String paymentStatusDB = getStatusFromCreditEntity();

        Assertions.assertEquals(approved, paymentStatusDB);
    }

    @Test
    @DisplayName("Кредитная карта. Результат выполнения операции в базе данных, при оплате с отклоненной карты (со значением “DECLINED”)")
    void failedPayFromApprovedDebitCardInDB() throws InterruptedException {
        CardInfo cardInfo = DataHelper.getCardInfo(false);

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();
        Thread.sleep(10000);
        String paymentStatusDB = getStatusFromCreditEntity();

        Assertions.assertEquals(null, paymentStatusDB);
    }

    @Test
    @DisplayName("Кредитная карта. Неудачная оплата с отклоненной карты (со значением “DECLINED”)")
    void failedPayFromApprovedDebitCard() {
        CardInfo cardInfo = DataHelper.getCardInfo(false);

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();
        payPage.getNoticeText(error);

        //Баг - отображается уведомление об успехе операции
    }

    @Test
    @DisplayName("Кредитная карта. Неудачная оплата картой, которой нет в базе")
    void failedPayFromNonexistenceDebitCard() {
        CardInfo cardInfo = new CardInfo(generateNumber(16), generateMouth(), generateYear(), generateOwner(), getCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();
        payPage.getNoticeText(error);

        //Баг - отображается оба уведомления об успехе и неудаче операции
    }

    @Test
    @DisplayName("Кредитная карта. Неудачная отправка пустой формы для проверки валидации полей")
    void failedSendEmptyFormDebitCard() {

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.clickSubmit();

        payPage.getInputsSub(5);
        payPage.getNoticeInputNumber(wrongFormat);
        payPage.getNoticeInputMouth(wrongFormat);
        payPage.getNoticeInputYear(wrongFormat);
        payPage.getNoticeInputOwner(requiredField);
        payPage.getNoticeInputCvc(wrongFormat);
    }

    @Test
    @DisplayName("Кредитная карта. Удачная оплата после отправки пустой формы (Проверка скрытия подсказок валидации полей)")
    void successfulSendAfterEmptyFormDebitCard() {
        CardInfo cardInfo = DataHelper.getCardInfo(true);

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.clickSubmit();

        payPage.getInputsSub(5);

        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();
        payPage.getNoticeText(success);
        payPage.getInputsSub(0);

        // Баг - подсказки валидации не скрываются после ввода валидных данных
    }

    // [Валидация полей] -----------------------------------------------------------------------------------------------
    // [Валидация поля "Номер карты"] ----------------------------------------------------------------------------------

    @Test
    @DisplayName("Кредитная карта. Валидация поля Номер карты. Ввод 15 цифр")
    void validationNumberCardField15Digits() {
        CardInfo cardInfo = new CardInfo(generateNumber(countCardNumber - 1), generateMouth(), generateYear(), generateOwner(), getCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();

        payPage.getInputsSub(5);
        payPage.getNoticeInputNumber(wrongFormat);
    }

    @Test
    @DisplayName("Кредитная карта. Валидация поля Номер карты. Ввод 17 цифр")
    void validationNumberCardField17Digits() {
        CardInfo cardInfo = new CardInfo(generateNumber(countCardNumber + 1), generateMouth(), generateYear(), generateOwner(), getCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();

        payPage.getInputsSub(0);
        Assertions.assertEquals(countCardNumber, payPage.getInputValue(inputNumber).length() - 3);
    }

    @Test
    @DisplayName("Кредитная карта. Валидация поля Номер карты. Ввод букв")
    void validationNumberCardFieldLetters() {
        CardInfo cardInfo = new CardInfo(generateOwner(), generateMouth(), generateYear(), generateOwner(), getCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();

        payPage.getInputsSub(1);
        Assertions.assertEquals("", payPage.getInputValue(inputNumber));
        payPage.getNoticeInputNumber(wrongFormat);
    }

    @Test
    @DisplayName("Кредитная карта. Валидация поля Номер карты. Ввод спецсимволов")
    void validationNumberCardFieldSymbols() {
        CardInfo cardInfo = new CardInfo(getSymbolStr(), generateMouth(), generateYear(), generateOwner(), getCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();

        payPage.getInputsSub(1);
        Assertions.assertEquals("", payPage.getInputValue(inputNumber));
        payPage.getNoticeInputNumber(wrongFormat);
    }

    @Test
    @DisplayName("Кредитная карта. Валидация поля Номер карты. Пустое поле при заполненных остальных полях")
    void validationNumberCardFieldEmpty() {
        CardInfo cardInfo = new CardInfo("", generateMouth(), generateYear(), generateOwner(), getCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();

        payPage.getInputsSub(1);
        Assertions.assertEquals("", payPage.getInputValue(inputNumber));
        payPage.getNoticeInputNumber(requiredField);
        // Баг - отображается подсказка неверного формата
    }

    // [Валидация поля "Месяц"] ----------------------------------------------------------------------------------------

    @Test
    @DisplayName("Кредитная карта. Валидация поля Месяц. Ввод значения 00")
    void validationMouthField00() {
        CardInfo cardInfo = new CardInfo(getApprovedCardNumber(), "00", generateYear(), generateOwner(), getCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();

        payPage.getInputsSub(1);
        payPage.getNoticeInputMouth(invalidExpirationDate);
        // Баг - нет валидации поля на ввод 00
    }

    @Test
    @DisplayName("Кредитная карта. Валидация поля Месяц. Ввод значения 13")
    void validationMouthField13() {
        CardInfo cardInfo = new CardInfo(getApprovedCardNumber(), String.valueOf(countOfMonth + 1), generateYear(), generateOwner(), getCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();

        payPage.getInputsSub(1);
        payPage.getNoticeInputMouth(invalidExpirationDate);
    }

    @Test
    @DisplayName("Кредитная карта. Валидация поля Месяц. Ввод значения с одной цифрой")
    void validationMouthField1Digit() {
        CardInfo cardInfo = new CardInfo(getApprovedCardNumber(), generateNumber(1), generateYear(), generateOwner(), getCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();

        payPage.getInputsSub(1);
        payPage.getNoticeInputMouth(wrongFormat);
    }

    @Test
    @DisplayName("Кредитная карта. Валидация поля Месяц. Ввод букв")
    void validationMouthCardFieldLetters() {
        CardInfo cardInfo = new CardInfo(getApprovedCardNumber(), generateOwner(), generateYear(), generateOwner(), getCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();

        payPage.getInputsSub(1);
        Assertions.assertEquals("", payPage.getInputValue(inputMouth));
        payPage.getNoticeInputMouth(wrongFormat);
    }

    @Test
    @DisplayName("Кредитная карта. Валидация поля Месяц. Ввод спецсимволов")
    void validationMouthCardFieldSymbols() {
        CardInfo cardInfo = new CardInfo(getApprovedCardNumber(), getSymbolStr(), generateYear(), generateOwner(), getCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();

        payPage.getInputsSub(1);
        Assertions.assertEquals("", payPage.getInputValue(inputMouth));
        payPage.getNoticeInputMouth(wrongFormat);
    }

    @Test
    @DisplayName("Кредитная карта. Валидация поля Месяц. Пустое поле при заполненных остальных полях")
    void validationMouthCardFieldEmpty() {
        CardInfo cardInfo = new CardInfo(getApprovedCardNumber(), "", generateYear(), generateOwner(), getCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();

        payPage.getInputsSub(1);
        Assertions.assertEquals("", payPage.getInputValue(inputMouth));
        payPage.getNoticeInputMouth(requiredField);
        // Баг - отображается подсказка неверного формата
    }

    // [Валидация поля "Год"] ------------------------------------------------------------------------------------------

    @Test
    @DisplayName("Кредитная карта. Валидация поля Год. Ввод значения предыдущего года")
    void validationYearFieldLastYear() {
        CardInfo cardInfo = new CardInfo(getApprovedCardNumber(), generateMouth(), generateYear(-1), generateOwner(), getCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();

        payPage.getInputsSub(1);
        payPage.getNoticeInputYear(cardExpired);
    }

    @Test
    @DisplayName("Кредитная карта. Валидация поля Год. Ввод года плюс 5 лет")
    void validationYearFieldPlus5Year() {
        CardInfo cardInfo = new CardInfo(getApprovedCardNumber(), generateMouth(), generateYear(+5), generateOwner(), getCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();

        payPage.getNoticeText(success);

        payPage.getInputsSub(0);
    }

    @Test
    @DisplayName("Кредитная карта. Валидация поля Год. Ввод года плюс 6 лет")
    void validationYearFieldPlus6Year() {
        CardInfo cardInfo = new CardInfo(getApprovedCardNumber(), generateMouth(), generateYear(+6), generateOwner(), getCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();

        payPage.getInputsSub(1);
        payPage.getNoticeInputYear(invalidExpirationDate);
    }

    @Test
    @DisplayName("Кредитная карта. Валидация поля Год. Формат указания года 4-мя цифрами")
    void validationYearField4Digits() {
        CardInfo cardInfo = new CardInfo(getApprovedCardNumber(), generateMouth(), ("20" + generateYear()), generateOwner(), getCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();

        payPage.getInputsSub(1);
        payPage.getNoticeInputYear(cardExpired);
        Assertions.assertEquals("20", payPage.getInputValue(inputYear));
    }

    @Test
    @DisplayName("Кредитная карта. Валидация поля Год. Ввод букв")
    void validationYearCardFieldLetters() {
        CardInfo cardInfo = new CardInfo(getApprovedCardNumber(), generateMouth(), generateOwner(), generateOwner(), getCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();

        payPage.getInputsSub(1);
        Assertions.assertEquals("", payPage.getInputValue(inputYear));
        payPage.getNoticeInputYear(wrongFormat);
    }

    @Test
    @DisplayName("Кредитная карта. Валидация поля Год. Ввод спецсимволов")
    void validationYearCardFieldSymbols() {
        CardInfo cardInfo = new CardInfo(getApprovedCardNumber(), generateMouth(), getSymbolStr(), generateOwner(), getCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();

        payPage.getInputsSub(1);
        Assertions.assertEquals("", payPage.getInputValue(inputYear));
        payPage.getNoticeInputYear(wrongFormat);
    }

    @Test
    @DisplayName("Кредитная карта. Валидация поля Год. Пустое поле при заполненных остальных полях")
    void validationYearCardFieldEmpty() {
        CardInfo cardInfo = new CardInfo(getApprovedCardNumber(), generateMouth(), "", generateOwner(), getCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();

        payPage.getInputsSub(1);
        Assertions.assertEquals("", payPage.getInputValue(inputYear));
        payPage.getNoticeInputYear(requiredField);
        // Баг - отображается подсказка неверного формата
    }

    // [Валидация поля "Владелец"] -------------------------------------------------------------------------------------

    @Test
    @DisplayName("Кредитная карта. Валидация поля Владелец. Ввод цифр")
    void validationOwnerCardFieldDigits() {
        CardInfo cardInfo = new CardInfo(getApprovedCardNumber(), generateMouth(), generateYear(), generateNumber(5) + " " + generateNumber(5), getCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();

        payPage.getInputsSub(1);
        payPage.getNoticeInputOwner(wrongFormat);
        // Баг - нет валидации поля на ввод цифр
    }

    @Test
    @DisplayName("Кредитная карта. Валидация поля Владелец. Ввод кириллицы")
    void validationOwnerCardFieldCyrillic() {
        CardInfo cardInfo = new CardInfo(getApprovedCardNumber(), generateMouth(), generateYear(), generateOwnerInCyrillic(), getCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();

        payPage.getInputsSub(1);
        payPage.getNoticeInputOwner(wrongFormat);
        // Баг - нет валидации поля на ввод кириллицы
    }

    @Test
    @DisplayName("Кредитная карта. Валидация поля Владелец. Пробел в начале")
    void validationOwnerCardFieldSpaceInAtFirst() {
        CardInfo cardInfo = new CardInfo(getApprovedCardNumber(), generateMouth(), generateYear(), " " + removeSpace(generateOwner()), getCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();
        payPage.getNoticeText(success);

        payPage.getNoticeInputOwner(wrongFormat);
        // Баг - нет валидации поля на ввод пробела в начале
    }

    @Test
    @DisplayName("Кредитная карта. Валидация поля Владелец. Пробел в конце")
    void validationOwnerCardFieldSpaceInAtTheEnd() {
        CardInfo cardInfo = new CardInfo(getApprovedCardNumber(), generateMouth(), generateYear(), removeSpace(generateOwner()) + " ", getCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();
        payPage.getNoticeText(success);

        payPage.getNoticeInputOwner(wrongFormat);
        // Баг - нет валидации поля на ввод пробела в конце
    }

    @Test
    @DisplayName("Кредитная карта. Валидация поля Владелец. Без пробела")
    void validationOwnerCardFieldSpaceLess() {
        CardInfo cardInfo = new CardInfo(getApprovedCardNumber(), generateMouth(), generateYear(), removeSpace(generateOwner()), getCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();
        payPage.getNoticeText(success);

        payPage.getInputsSub(1);
        payPage.getNoticeInputOwner(wrongFormat);
        // Баг - нет валидации поля на ввод без пробела
    }

    @Test
    @DisplayName("Кредитная карта. Валидация поля Владелец. Ввод спецсимволов")
    void validationOwnerCardFieldSymbols() {
        CardInfo cardInfo = new CardInfo(getApprovedCardNumber(), generateMouth(), generateYear(), getSymbolStr(), getCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();

        payPage.getInputsSub(1);
        payPage.getNoticeInputOwner(wrongFormat);
        // Баг - нет валидации поля на ввод спец. символов
    }

    @Test
    @DisplayName("Кредитная карта. Валидация поля Владелец. Пустое поле")
    void validationOwnerCardFieldEmpty() {
        CardInfo cardInfo = new CardInfo(getApprovedCardNumber(), generateMouth(), generateYear(), "", getCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();

        payPage.getInputsSub(1);
        payPage.getNoticeInputOwner(requiredField);
    }

    // [Валидация поля "CVC/CVV"] --------------------------------------------------------------------------------------

    @Test
    @DisplayName("Кредитная карта. Валидация поля CVC/CVV. Ввод 2-х цифр")
    void validationCvcCardField2Digits() {
        CardInfo cardInfo = new CardInfo(getApprovedCardNumber(), generateMouth(), generateYear(), generateOwner(), generateNumber(2));

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();

        payPage.getInputsSub(1);
        payPage.getNoticeInputCvc(wrongFormat);
    }

    @Test
    @DisplayName("Кредитная карта. Валидация поля CVC/CVV. Ввод 4-х цифр")
    void validationCvcCardField4Digits() {
        CardInfo cardInfo = new CardInfo(getApprovedCardNumber(), generateMouth(), generateYear(), generateOwner(), generateNumber(4));

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();

        payPage.getInputsSub(1);
        Assertions.assertEquals(3, payPage.getInputValue(inputCvc).length());
    }

    @Test
    @DisplayName("Кредитная карта. Валидация поля CVC/CVV. Ввод букв")
    void validationCvcCardFieldLetters() {
        CardInfo cardInfo = new CardInfo(getApprovedCardNumber(), generateMouth(), generateYear(), generateOwner(), generateFirstName());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();

        payPage.getInputsSub(1);
        payPage.getNoticeInputCvc(requiredField);
        Assertions.assertEquals("", payPage.getInputValue(inputCvc));
        // Баг - отображается две подсказки валидации поля, должна быть одна
    }

    @Test
    @DisplayName("Кредитная карта. Валидация поля CVC/CVV. Ввод спец. символов")
    void validationCvcCardFieldSymbols() {
        CardInfo cardInfo = new CardInfo(getApprovedCardNumber(), generateMouth(), generateYear(), generateOwner(), getSymbolStr());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();

        payPage.getInputsSub(1);
        payPage.getNoticeInputCvc(requiredField);
        Assertions.assertEquals("", payPage.getInputValue(inputCvc));
        // Баг - отображается две подсказки валидации поля, должна быть одна
    }

    @Test
    @DisplayName("Кредитная карта. Валидация поля CVC/CVV. Пустое поле")
    void validationCvcCardFieldEmpty() {
        CardInfo cardInfo = new CardInfo(getApprovedCardNumber(), generateMouth(), generateYear(), generateOwner(), "");

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();

        payPage.getInputsSub(1);
        payPage.getNoticeInputCvc(requiredField);
        // Баг - отображается две подсказки валидации поля, должна быть одна
    }
}