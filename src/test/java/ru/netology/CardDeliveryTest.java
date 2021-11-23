package ru.netology;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;


public class CardDeliveryTest {
    @BeforeAll
    static void setUpAll() {
//            System.setProperty("webdriver.chrome.driver", "./driver/chromedriver.exe");
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setUp() {
        Configuration.browser = "chrome";
        open("http://localhost:9999");
    }
        public LocalDate getDateOfMeetingInLocalDate(int days) {
        return LocalDate.now().plusDays(days);
    }
        public String getDateOfMeetingInString(int days, String formatePattern) {
        return LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern(formatePattern));
    }

    public String getMonthOfMeetingInRussian(String monthStrInEnglish) {
        String[] engMonths = {
                "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE",
                "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"};
        String[] ruMonths = {
                "Январь ", "Февраль ", "Март ", "Апрель ", "Май ", "Июнь ",
                "Июль ", "Август ", "Сентябрь ", "Октябрь ", "Ноябрь ", "Декабрь "};
        for (
                int m = 0;
                m < engMonths.length; m++) {
            if (monthStrInEnglish.contains(engMonths[m])) {
                monthStrInEnglish = monthStrInEnglish.replace(engMonths[m], ruMonths[m]);
                break;
            }
        }
        String monthOfMeetingInRussian = monthStrInEnglish;
        return monthOfMeetingInRussian;
    }

    @Test
    public void shouldOrderCardDelivery() {
        $("[data-test-id=city] .input__control").setValue("Москва");
        String formattedDateOfMeeting = getDateOfMeetingInString(3, "dd.MM.yyyy");
        $("[data-test-id=date] .input__control").sendKeys(Keys.CONTROL + "A");
        $("[data-test-id=date] .input__control").sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] .input__control").setValue(formattedDateOfMeeting);
        $("[data-test-id=name] .input__control").setValue("Манаков Иван");
        $("[data-test-id=phone] .input__control").setValue("+76438808726");
        $("div form fieldset label").click();
        $(Selectors.byText("Забронировать")).click();
        $(Selectors.withText("Успешно")).shouldBe(visible, Duration.ofSeconds(15));
        $("div.notification__content").shouldHave(exactText("Встреча успешно забронирована на " + formattedDateOfMeeting));
    }

    @Test
    public void shouldOrderCardDeliveryWithCityChoiceAndManualChoiceOfDay() {

        $("[data-test-id=city] .input__control").setValue("Са");
        $(".popup_theme_alfa-on-white.input__popup").shouldBe(visible);
        $(Selectors.byText("Санкт-Петербург")).click();
        String formattedDateOfMeeting = getDateOfMeetingInString(3, "dd.MM.yyyy");
        Integer dayOfMeeting = getDateOfMeetingInLocalDate(3).getDayOfMonth();
        String dayOfMeetingStr = String.valueOf(dayOfMeeting);
        Month monthOfMeeting = getDateOfMeetingInLocalDate(3).getMonth();
        String monthStr = monthOfMeeting.toString();
        String monthStrInRussian = getMonthOfMeetingInRussian(monthStr);
        Integer year = getDateOfMeetingInLocalDate(3).getYear();
        String yearStr = String.valueOf(year);
        $("[data-test-id=date]").click();
        $(".calendar__name").shouldBe(visible).shouldHave(exactText(monthStrInRussian + yearStr));
        Month currentMonth = LocalDate.now().getMonth();
        if (currentMonth == monthOfMeeting) {
            $$(".calendar__day").findBy(text(dayOfMeetingStr)).click();
        } else {
            $(".calendar__arrow_direction_right [data-step=1]").click();
            $$(".calendar__day").findBy(text(dayOfMeetingStr)).click();
        }
        $("[data-test-id=name] .input__control").setValue("Иванов Иван");
        $("[data-test-id=phone] .input__control").setValue("+79245678630");
        $("div form fieldset label").click();
        $(Selectors.byText("Забронировать")).click();
        $(Selectors.withText("Успешно")).shouldBe(visible, Duration.ofSeconds(15));
        $("div.notification__content").shouldHave(exactText("Встреча успешно забронирована на " + formattedDateOfMeeting));
    }
}