package test;

import com.codeborne.selenide.SelenideElement;
import dataHelper.CardInfo;
import dataHelper.DataHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.MainPage;
import pages.PayPage;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Selenide.open;
import static dataHelper.DataHelper.*;

public class PayTourTests {

    private final String success = "Успешно";
    private final String error = "Ошибка";
    private final String wrongFormat = "Неверный формат";
    private final String invalidExpirationDate = "Неверно указан срок действия карты";
    private final int countCardNumber = 16;
    private final int countOfMonth = 12;

    private final int inputNumber = 0;
    private final int inputMouth = 1;
    private final int inputYear = 2;
    private final int inputOwner = 3;
    private final int inputCvc = 4;

    @BeforeEach
    void setup() {
        open("http://localhost:8080");
    }

    // [Функциональные тесты для каждого вида оплаты] ------------------------------------------------------------------
    // [Оплата по дебетовой карте] -------------------------------------------------------------------------------------
    @Test
    @DisplayName("Дебетовая карта. Успешная оплата с подтвержденной карты (со значением “APPROVED”)")
    void successfulPayFromApprovedDebitCard() {
        CardInfo cardInfo = DataHelper.getCardInfo(true);

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPay();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();
        String notice = payPage.getNoticeText();

        Assertions.assertTrue(notice.contains(success));
    }

    @Test
    @DisplayName("Дебетовая карта. Неудачная оплата с отклоненной карты (со значением “DECLINED”)")
    void failedPayFromApprovedDebitCard() {
        CardInfo cardInfo = DataHelper.getCardInfo(false);

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPay();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();
        String notice = payPage.getNoticeText();

        Assertions.assertTrue(notice.contains(error));
        //Баг - отображается уведомление об успехе операции
    }

    @Test
    @DisplayName("Дебетовая карта. Неудачная оплата картой, которой нет в базе")
    void failedPayFromNonexistenceDebitCard() {
        CardInfo cardInfo = new CardInfo(generateNumber(16), generateMouth(), generateYear(), generateOwner(), generateCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPay();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();
        String notice = payPage.getNoticeText();

        Assertions.assertTrue(notice.contains(error));
        //Баг - отображается оба уведомления об успехе и неудаче операции
    }

    @Test
    @DisplayName("Дебетовая карта. Неудачная отправка пустой формы для проверки валидации полей")
    void failedSendEmptyFormDebitCard() {

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPay();
        payPage.clickSubmit();

        payPage.getInputsSub().shouldHave(size(5));
    }

    // [Функциональные тесты для каждого вида оплаты] ------------------------------------------------------------------
    // [Оплата по кредитной карте] -------------------------------------------------------------------------------------

    @Test
    @DisplayName("Кредитная карта. Успешная оплата с подтвержденной карты (со значением “APPROVED”)")
    void successfulPayFromApprovedCreditCard() {
        CardInfo cardInfo = DataHelper.getCardInfo(true);

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();
        String notice = payPage.getNoticeText();

        Assertions.assertTrue(notice.contains(success));
    }

    @Test
    @DisplayName("Кредитная карта. Неудачная оплата с отклоненной карты (со значением “DECLINED”)")
    void failedPayFromApprovedCreditCard() {
        CardInfo cardInfo = DataHelper.getCardInfo(false);

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();
        String notice = payPage.getNoticeText();

        Assertions.assertTrue(notice.contains(error));
        //Баг - отображается уведомление об успехе операции
    }

    @Test
    @DisplayName("Кредитная карта. Неудачная оплата картой, которой нет в базе")
    void failedPayFromNonexistenceCreditCard() {
        CardInfo cardInfo = new CardInfo(generateNumber(16), generateMouth(), generateYear(), generateOwner(), generateCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();
        String notice = payPage.getNoticeText();

        Assertions.assertTrue(notice.contains(error));
        //Баг - отображается оба уведомления об успехе и неудаче операции
    }

    @Test
    @DisplayName("Кредитная карта. Неудачная отправка пустой формы для проверки валидации полей")
    void failedSendEmptyFormCreditCard() {

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayInCredit();
        payPage.clickSubmit();

        payPage.getInputsSub().shouldHave(size(5));
    }

    // [Валидация полей для каждого вида оплаты] -----------------------------------------------------------------------
    // [Оплата по дебетовой карте] -------------------------------------------------------------------------------------
    // [Валидация поля "Номер карты"] ----------------------------------------------------------------------------------

    @Test
    @DisplayName("Дебетовая карта. Валидация поля Номер карты. Ввод 15 цифр")
    void validationNumberCardField15Digits() {
        CardInfo cardInfo = new CardInfo(generateNumber(countCardNumber - 1), generateMouth(), generateYear(), generateOwner(), generateCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPay();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();

        Assertions.assertEquals(wrongFormat, payPage.getNoticeInputNumber());
    }

    @Test
    @DisplayName("Дебетовая карта. Валидация поля Номер карты. Ввод 17 цифр")
    void validationNumberCardField17Digits() {
        CardInfo cardInfo = new CardInfo(generateNumber(countCardNumber + 1), generateMouth(), generateYear(), generateOwner(), generateCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPay();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();

        Assertions.assertEquals(countCardNumber, payPage.getInputValue(inputNumber).length() - 3);
    }

    @Test
    @DisplayName("Дебетовая карта. Валидация поля Номер карты. Ввод букв")
    void validationNumberCardFieldLetters() {
        CardInfo cardInfo = new CardInfo(generateOwner(), generateMouth(), generateYear(), generateOwner(), generateCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPay();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();

        Assertions.assertEquals("", payPage.getInputValue(inputNumber));
        Assertions.assertEquals(wrongFormat, payPage.getNoticeInputNumber());
    }

    @Test
    @DisplayName("Дебетовая карта. Валидация поля Номер карты. Ввод спецсимволов")
    void validationNumberCardFieldSymbols() {
        CardInfo cardInfo = new CardInfo(getSymbolStr(), generateMouth(), generateYear(), generateOwner(), generateCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPay();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();

        Assertions.assertEquals("", payPage.getInputValue(inputNumber));
        Assertions.assertEquals(wrongFormat, payPage.getNoticeInputNumber());
    }

    @Test
    @DisplayName("Дебетовая карта. Валидация поля Номер карты. Пустое поле при заполненных остальных полях")
    void validationNumberCardFieldEmpty() {
        CardInfo cardInfo = new CardInfo("", generateMouth(), generateYear(), generateOwner(), generateCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPay();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();

        Assertions.assertEquals("", payPage.getInputValue(inputNumber));
        Assertions.assertEquals(wrongFormat, payPage.getNoticeInputNumber());
    }

    // [Валидация поля "Месяц"] ----------------------------------------------------------------------------------------

    @Test
    @DisplayName("Дебетовая карта. Валидация поля Месяц. Ввод значения 00")
    void validationMouthField00() {
        CardInfo cardInfo = new CardInfo(getApprovedCardNumber(), "00", generateYear(), generateOwner(), generateCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPay();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();

        Assertions.assertEquals(invalidExpirationDate, payPage.getNoticeInputMouth());
        // Баг - нет валидации поля на ввод 00
    }

    @Test
    @DisplayName("Дебетовая карта. Валидация поля Месяц. Ввод значения 13")
    void validationMouthField13() {
        CardInfo cardInfo = new CardInfo(getApprovedCardNumber(), String.valueOf(countOfMonth + 1), generateYear(), generateOwner(), generateCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPay();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();

        Assertions.assertEquals(invalidExpirationDate, payPage.getNoticeInputMouth());
    }

    @Test
    @DisplayName("Дебетовая карта. Валидация поля Месяц. Ввод значения с одной цифрой")
    void validationMouthField1Digit() {
        CardInfo cardInfo = new CardInfo(getApprovedCardNumber(), generateNumber(1), generateYear(), generateOwner(), generateCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPay();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();

        Assertions.assertEquals(wrongFormat, payPage.getNoticeInputMouth());
    }

    @Test
    @DisplayName("Дебетовая карта. Валидация поля Месяц. Ввод букв")
    void validationMouthCardFieldLetters() {
        CardInfo cardInfo = new CardInfo(getApprovedCardNumber(), generateOwner(), generateYear(), generateOwner(), generateCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPay();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();

        Assertions.assertEquals("", payPage.getInputValue(inputMouth));
        Assertions.assertEquals(wrongFormat, payPage.getNoticeInputMouth());
    }

    @Test
    @DisplayName("Дебетовая карта. Валидация поля Месяц. Ввод спецсимволов")
    void validationMouthCardFieldSymbols() {
        CardInfo cardInfo = new CardInfo(getApprovedCardNumber(), getSymbolStr(), generateYear(), generateOwner(), generateCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPay();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();

        Assertions.assertEquals("", payPage.getInputValue(inputMouth));
        Assertions.assertEquals(wrongFormat, payPage.getNoticeInputMouth());
    }

    @Test
    @DisplayName("Дебетовая карта. Валидация поля Месяц. Пустое поле при заполненных остальных полях")
    void validationMouthCardFieldEmpty() {
        CardInfo cardInfo = new CardInfo(getApprovedCardNumber(), "", generateYear(), generateOwner(), generateCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPay();
        payPage.enterCardData(cardInfo);
        payPage.clickSubmit();

        Assertions.assertEquals("", payPage.getInputValue(inputMouth));
        Assertions.assertEquals(wrongFormat, payPage.getNoticeInputMouth());
    }
}