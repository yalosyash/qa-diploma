package pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class PayPage {

    private SelenideElement heading = $(By.xpath("//h3[contains(text(), 'карт')]"));
    private ElementsCollection inputs = $$(".input__inner");

    private SelenideElement inputNumber = inputs.get(0);
    private SelenideElement inputMouth = inputs.get(0);
    private SelenideElement inputYear = inputs.get(0);
    private SelenideElement inputOwner = inputs.get(0);
    private SelenideElement inputCvc = inputs.get(0);

    private SelenideElement submit = $("form button");

    public PayPage(String text) {
        heading.shouldBe(visible);
        inputs.get(0).$(".input__top").shouldBe(visible).shouldHave(text("Номер карты"));
        submit.shouldBe(visible).shouldHave(text("Продолжить"));
    }
}