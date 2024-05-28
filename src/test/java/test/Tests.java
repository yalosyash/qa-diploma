package test;

import dataHelper.DataHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pages.MainPage;

import static com.codeborne.selenide.Selenide.open;

class Tests {

    @BeforeEach
    void setup() {
        open("http://localhost:8080");
    }

    @Test
    void shouldOpenSite() throws InterruptedException {

        var mainPage = new MainPage();
        var payPage = mainPage.clickToPay();
        var cardInfo = DataHelper.getCardInfo(true);
        payPage.enterNumber(cardInfo);
        payPage.enterMouth(cardInfo);
        payPage.enterYear(cardInfo);
        payPage.enterOwner(cardInfo);
        payPage.enterCvc(cardInfo);
        payPage.clickSubmit();
    }
}