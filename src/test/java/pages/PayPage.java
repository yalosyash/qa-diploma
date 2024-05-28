package pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import dataHelper.CardInfo;

import org.openqa.selenium.By;

import java.time.Duration;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class PayPage {

    private SelenideElement heading = $(By.xpath("//h3[contains(text(), 'карт')]"));
    private ElementsCollection inputs = $$(".input__inner");
    private SelenideElement notification = $(".notification");

    private SelenideElement inputNumber = inputs.get(0);
    private SelenideElement inputMouth = inputs.get(1);
    private SelenideElement inputYear = inputs.get(2);
    private SelenideElement inputOwner = inputs.get(3);
    private SelenideElement inputCvc = inputs.get(4);

    private SelenideElement submit = $("form button");

    public PayPage(String text) {
        heading.shouldBe(visible);
        inputs.shouldHave(size(5));
        inputs.get(0).$(".input__top").shouldBe(visible).shouldHave(text("Номер карты"));
        submit.shouldBe(visible).shouldHave(text("Продолжить"));
    }

    public void enterNumber(CardInfo cardInfo) {
        inputNumber.$(".input__control").setValue(cardInfo.getNumber());
    }

    public void enterMouth(CardInfo cardInfo) {
        inputMouth.$(".input__control").setValue(cardInfo.getMouth());
    }

    public void enterYear(CardInfo cardInfo) {
        inputYear.$(".input__control").setValue(cardInfo.getYear());
    }

    public void enterOwner(CardInfo cardInfo) {
        inputOwner.$(".input__control").setValue(cardInfo.getOwner());
    }

    public void enterCvc(CardInfo cardInfo) {
        inputCvc.$(".input__control").setValue(cardInfo.getCvc());
    }

    public void clickSubmit() throws InterruptedException {
        submit.click();
        notification.$(".notification__title").shouldBe(visible, Duration.ofSeconds(10)).shouldHave(text("Успешно"));
        notification.$(".notification__content").shouldBe(visible, Duration.ofSeconds(10)).shouldHave(text("Операция одобрена Банком."));
    }
}