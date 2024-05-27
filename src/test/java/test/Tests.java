package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pages.MainPage;

import static com.codeborne.selenide.Selenide.*;

class Tests {

    @BeforeEach
    void setup() {
        open("http://localhost:8080");
    }

    @Test
    void shouldOpenSite() {

        var mainPage = new MainPage();
        mainPage.clickToPay();
        mainPage.clickToPayInCredit();
    }
}