/*
 * Copyright 2015 Geeoz Software
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

package pawl.jbehave;

import org.jbehave.web.selenium.WebDriverPage;
import org.jbehave.web.selenium.WebDriverProvider;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pawl.util.Resources;

/**
 * Web page object that execute all methods with waiting and polling.
 *
 * @author Mike Dolinin
 * @version 1.0 5/2/15
 */
public class BasePage extends WebDriverPage {
    /**
     * Web BasePage default constructor.
     *
     * @param driverProvider provide browser driver
     */
    public BasePage(final WebDriverProvider driverProvider) {
        super(driverProvider);
    }

    /**
     * Find element on page and wait until it will be visible.
     *
     * @param identity of element
     * @return visible element
     */
    public LoadingElement find(final String identity) {
        By by = parseBy(identity);
        WebElement element = getWait().until(
                ExpectedConditions.visibilityOfElementLocated(by));
        return new LoadingElement(
                element, getWait(), by);
    }

    /**
     * Creates new wait with timeout from properties.
     *
     * @return webdriver wait
     */
    public WebDriverWait getWait() {
        return new WebDriverWait(this, Resources.base().explicitWait());
    }

    /**
     * Covert user string to By objects.
     *
     * @param identity an identity of the element
     * @return By selector
     */
    public By parseBy(final String identity) {
        By selector = new By.ById(identity);
        if (identity.startsWith("/")) {
            selector = new By.ByXPath(identity);
        } else if (identity.startsWith("#") || identity.startsWith(".")) {
            selector = new By.ByCssSelector(identity);
        }
        return selector;
    }
}
