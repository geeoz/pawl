/*
 * Copyright 2014 Geeoz Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pawl.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import pawl.webdriver.LocalizedWebDriverProvider;

import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Resources contain objects from different areas.
 *
 * @author Alex Voloshyn
 * @author Mike Dolinin
 * @version 1.1 2/27/14
 */

public class BrowserLocalizationIT {
    public static final String USER_LANGUAGE = "user.language";
    public static final String USER_COUNTRY = "user.country";
    private URL l10nPage;
    private LocalizedWebDriverProvider driverProvider;

    @Before
    public void openStaticPage(){
        l10nPage = BrowserLocalizationIT.class.getResource("/l10n-example/demo.html");
        driverProvider = new LocalizedWebDriverProvider();
    }

    @Test
    public void changeLocaleInBrowser() throws InterruptedException {
        System.setProperty(USER_LANGUAGE, "en");
        System.setProperty(USER_COUNTRY, "US");
        driverProvider.initialize();
        WebDriver webDriver = driverProvider.get();
        webDriver.get(l10nPage.toExternalForm());
        WebElement info = webDriver.findElement(By.id("info"));
        assertThat(info.getText(),
                is(equalTo("You are viewing an American English localization of this page.")));
    }

    @After
    public void stopBrowser(){
        driverProvider.get().quit();
    }
}
