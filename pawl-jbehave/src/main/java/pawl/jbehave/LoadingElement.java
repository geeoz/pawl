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

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Web element object that wait for elements before interact with them.
 *
 * @author Mike Dolinin
 * @version 1.0 5/2/15
 */
public class LoadingElement implements WebElement {
    /**
     * Inner web element holder.
     */
    private WebElement element;

    /**
     * Webdriver wait for polling interactions.
     */
    private final WebDriverWait wait;

    /**
     * Web element locator.
     */
    private final By by;

    /**
     * Loading element default constructor.
     *
     * @param webElement to wrap
     * @param webDriverWait for polling
     * @param elementBy for find element
     */
    public LoadingElement(final WebElement webElement,
                          final WebDriverWait webDriverWait,
                          final By elementBy) {
        this.element = webElement;
        this.wait = webDriverWait;
        this.by = elementBy;
    }

    /**
     * Do action on web element safely if element
     * is not longer attached to the DOM.
     *
     * @param action to perform
     * @param description of action
     */
    public void safe(final Consumer<WebElement> action,
                     final String description) {
        String message = String.format("Could not %s element '%s'",
                description, by);
        wait.withMessage(message)
                .until((WebDriver driver) -> {
                    try {
                        action.accept(element);
                        return true;
                    } catch (StaleElementReferenceException e) {
                        element = driver.findElement(by);
                        return false;
                    }
                });
    }

    @Override
    public void click() {
        safe(WebElement::click, "click on");
    }

    @Override
    public void submit() {
        safe(WebElement::submit, "submit");
    }

    @Override
    public void sendKeys(final CharSequence... keysToSend) {
        safe((el) -> el.sendKeys(keysToSend),
                "fill value = " + Arrays.toString(keysToSend) + " into");
    }

    @Override
    public void clear() {
        safe(WebElement::clear, "clear");
    }

    @Override
    public String getTagName() {
        return element.getTagName();
    }

    @Override
    public String getAttribute(final String name) {
        return element.getAttribute(name);
    }

    @Override
    public boolean isSelected() {
        return element.isSelected();
    }

    @Override
    public boolean isEnabled() {
        return element.isEnabled();
    }

    @Override
    public String getText() {
        final String elementTag = element.getTagName();
        final String elementText;
        switch (elementTag) {
            case "input":
            case "textarea":
                elementText = element.getAttribute("value");
                break;
            default:
                elementText = element.getText();
                break;
        }
        return elementText;
    }

    @Override
    public List<WebElement> findElements(final By locator) {
        return element.findElements(locator);
    }

    @Override
    public WebElement findElement(final By locator) {
        return element.findElement(locator);
    }

    @Override
    public boolean isDisplayed() {
        return element.isDisplayed();
    }

    @Override
    public Point getLocation() {
        return element.getLocation();
    }

    @Override
    public Dimension getSize() {
        return element.getSize();
    }

    @Override
    public String getCssValue(final String propertyName) {
        return element.getCssValue(propertyName);
    }

    /**
     * Enter value into field.
     *
     * @param value to enter
     */
    public void fillWith(final String value) {
        clear();
        sendKeys(value);
    }

    /**
     * Verify element contains text same as value
     * safely if element is not longer attached to the DOM.
     *
     * @param value text
     */
    public void shouldHaveText(final String value) {
        String message = String.format("Element '%s' should have text: '%s'",
                by, value);
        wait.withMessage(message)
                .until((WebDriver driver) -> {
                    try {
                        return getText().contains(value);
                    } catch (StaleElementReferenceException e) {
                        element = driver.findElement(by);
                        return false;
                    }
                });
    }
}
