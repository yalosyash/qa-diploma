package test;

import com.codeborne.selenide.logevents.SelenideLogger;
import dataHelper.CardInfo;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;

import static dataHelper.ApiHelper.*;
import static dataHelper.DataHelper.*;
import static dataHelper.SqlHelper.*;

public class ApiPaymentTourTests {
    private final String approved = "APPROVED";

    @BeforeAll
    static void setupAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
        cleanDB();
    }

    @AfterEach
    public void tearDown() {
        cleanDB();
    }

    @AfterAll
    public static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @Test
    @DisplayName("Успешная оплата с дебетовой карты (со значением “APPROVED”)")
    public void payByCardStatusApproved() {
        CardInfo approvedCard = getCardInfo(true);

        String paymentStatusResponse = debitCard(approvedCard);
        String paymentStatusDB = getStatusFromPaymentEntity();

        Assertions.assertEquals(approved, paymentStatusResponse);
        Assertions.assertEquals(approved, paymentStatusDB);
    }
}