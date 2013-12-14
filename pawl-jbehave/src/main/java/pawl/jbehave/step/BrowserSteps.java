/*
 * Copyright 2013 Geeoz Software
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

package pawl.jbehave.step;

import com.google.common.collect.Maps;
import org.hamcrest.Matchers;
import org.jbehave.core.annotations.Alias;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import pawl.jbehave.Pages;
import pawl.util.Resources;

import java.net.URL;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * <code>BrowserSteps</code> a simple POJO, which will contain the Java methods
 * that are mapped to the textual steps. The methods need to annotated with
 * one of the JBehave annotations and the annotated value should contain
 * a regex pattern that matches the textual step.
 *
 * @author Alex Voloshyn
 * @author Mike Dolinin
 * @author Serge Voloshyn
 * @version 1.12 11/6/2013
 */
public final class BrowserSteps extends Matchers {
    /**
     * Default logger.
     */
    private static final Logger LOG =
            Logger.getLogger(BrowserSteps.class.getName());
    /**
     * Web pages collection factory.
     */
    private final transient Pages browser;
    /**
     * Specifies the URL of the page.
     */
    private transient String url;

    /**
     * Specifies map to store test session data.
     */
    private transient Map<String, String> testSessionStore;

    /**
     * Create steps object that contains pages collection.
     *
     * @param pages factory of the page handlers
     */
    public BrowserSteps(final Pages pages) {
        super();
        this.browser = pages;
        testSessionStore = Maps.newConcurrentMap();
    }

    /**
     * Setup URL for next user actions.
     *
     * @param key for resources to get corresponding value
     */
    @Given("an '$key' link")
    public void setupLink(final String key) {
        url = Resources.base().string(key);
    }

    /**
     * Action for open web link.
     */
    @When("I open the link")
    public void openUrl() {
        browser.base().get(url);
    }

    /**
     * Action for refresh web page.
     */
    @When("I refresh the page")
    @Alias("refresh the page")
    public void refreshPage() {
        browser.base().navigate().refresh();
    }

    /**
     * Action for open new web link by adding to opened link context path.
     *
     * @param contextPath path for add
     */
    @When("open context path '$contextPath'")
    public void openContextPath(final String contextPath) {
        String currentUrl = browser.base().getCurrentUrl();
        browser.base().get(currentUrl + contextPath);
    }

    /**
     * Action to wait some time and then wait for all ajax responses.
     *
     * @param sec seconds to wait
     */
    @When("I wait '$sec' seconds")
    @Alias("wait '$sec' seconds")
    public void wait(final String sec) {
        final int seconds = (int) (Double.parseDouble(sec) * 1000);
        if (seconds > 0) {
            try {
                Thread.sleep(seconds);
            } catch (InterruptedException e) {
                LOG.log(Level.FINE, e.getMessage(), e.getCause());
            }
        }

        Object callResult = browser.base().executeScript(
                "return activeAjaxRequests();");
        final long sleepTime = 100L;
        while (callResult != null
                && Long.parseLong(String.valueOf(callResult)) > 0) {
            try {
                Thread.sleep(sleepTime);
            } catch (final InterruptedException e) {
                LOG.log(Level.FINE, e.getMessage(), e.getCause());
            }
            callResult = browser.base().executeScript(
                    "return activeAjaxRequests();");
        }
    }

    /**
     * Action for click web element.
     *
     * @param identity element identity for search
     */
    @When("I click '$identity'")
    @Alias("click '$identity'")
    public void click(final String identity) {
        getVisibleElement(identity).click();
    }

    /**
     * Action for click the web element with XY offsets.
     *
     * @param identity element identity for search
     * @param xOffset  X-offset for click
     * @param yOffset  Y-offset for click
     */
    @When("I click '$identity' with '$xOffset' and '$yOffset' offsets")
    @Alias("click '$identity' with '$xOffset' and '$yOffset' offsets")
    public void clickWithOffset(final String identity,
                                final String xOffset,
                                final String yOffset) {
        final WebElement webElement = getVisibleElement(identity);
        final Actions builder = new Actions(browser.base());
        builder.moveToElement(webElement,
                Integer.parseInt(xOffset), Integer.parseInt(yOffset))
                .click().perform();
    }

    /**
     * Action for click web element.
     *
     * @param className        element className for search
     * @param value            element text
     * @param parentIdentifier parent element identifier
     */
    @When("I choose '$className' with '$value' in '$parentIdentifier'")
    @Alias("choose '$className' with '$value' in '$parentIdentifier'")
    public void choose(final String className, final String value,
                       final String parentIdentifier) {
        final WebElement parent = getVisibleElement(parentIdentifier);
        final List<WebElement> elements
                = parent.findElements(By.className(className));
        assertThat("Page elements should exists: class name '" + className
                + "'", elements.size(),
                is(not(equalTo(0))));

        for (final WebElement element : elements) {
            if (element.getText().equals(value)) {
                element.click();
                return;
            }
        }
        fail("Cannot find element with class name '" + className + "' and "
                + "value '" + value + "'.");
    }

    /**
     * Fill input with local file absolute path.
     *
     * @param identity         input element id
     * @param fileRelativePath relative file path
     */
    @When("I fill '$identity' with '$fileRelativePath' file")
    @Alias("fill '$identity' with '$fileRelativePath' file")
    public void fillFile(final String identity, final String fileRelativePath) {
        final URL resource = Thread.currentThread().getContextClassLoader()
                .getResource(fileRelativePath);
        assert resource != null;
//        fill(identity, resource.getFile());
        final WebElement element = browser.base().findElement(By.id(identity));

        if (element.isDisplayed()) {
            element.clear();
            element.sendKeys(resource.getFile());
            return;
        }

        final String putValue = MessageFormat.format(
                "document.getElementById(\"{0}\")"
                        + ".setAttribute(\"value\", \"{1}\");"
                        + "if (\"createEvent\" in document) '{'"
                        + "var evt = document.createEvent(\"HTMLEvents\");"
                        + "evt.initEvent(\"change\", false, true);"
                        + "document.getElementById(\"{0}\").dispatchEvent(evt);"
                        + "'}' else '{'document.getElementById(\"{0}\"')'"
                        + ".fireEvent(\"onchange\");'}'",
                identity, resource.getFile());
        System.err.println("Put new value script: " + putValue);
        browser.base().executeScript(putValue);
    }

    /**
     * Retrieve an web element that should be visible on current page.
     *
     * @param identity an identity of the element
     * @return a web element that was found
     */
    private WebElement getVisibleElement(final String identity) {
        return browser.base().findElement(getVisibleSelector(identity));
    }

    /**
     * Retrieve web elements which may be visible on current page.
     *
     * @param identity an identity of the element
     * @return web elements which were found
     */
    private List<WebElement> getVisibleElements(final String identity) {
        return browser.base().findElements(getVisibleSelector(identity));
    }

    /**
     * Retrieve web element selector by different "By" attributes.
     *
     * @param identity an identity of the element
     * @return a selector that has visible state
     */
    private By getVisibleSelector(final String identity) {
        final By[] selectors = {
                By.id(identity),
                By.xpath(Resources.base().identityXpath(identity)),
                By.name(identity),
                By.className(identity),
                By.cssSelector(identity),
                By.xpath(identity)};
        for (By selector : selectors) {
            if (isElementDisplayed(selector)) {
                return selector;
            }
        }
        return selectors[0];
    }

    /**
     * Check selector for the displayed state.
     *
     * @param selector selector that should be check for displayed state
     * @return true if selector is displayed
     */
    private boolean isElementDisplayed(final By selector) {
        try {
            return browser.base().findElement(selector).isDisplayed();
        } catch (Exception e) {
            LOG.log(Level.FINE, e.getMessage(), e.getCause());
        }
        return false;
    }

    /**
     * Action for click on link.
     *
     * @param href attribute for search link on page
     */
    @When("I click on link '$href'")
    public void clickOnLinkWithAttribute(final String href) {
        browser.base().findElement(
                By.xpath(".//a[@href='" + href + "']")).click();
    }

    /**
     * Action for filling text to input element.
     *
     * @param identity element identity for search
     * @param text     text that should be filled by step
     */
    @When("I fill '$identity' with '$value'")
    @Alias("fill '$identity' with '$value'")
    public void fill(final String identity, final String text) {
        final WebElement element = getVisibleElement(identity);
        element.clear();
        element.sendKeys(getTextFromStorageIfExist(text));
    }

    /**
     * Action for selection option in the select element.
     *
     * @param identity element identity for search
     * @param value    value that should be selected
     */
    @When("I select '$identity' with '$value'")
    @Alias("select '$identity' with '$value'")
    public void select(final String identity, final String value) {
        final WebElement element = getVisibleElement(identity);
        final Select select = new Select(element);
        select.selectByVisibleText(value);
    }

    /**
     * Retrieve text from test session store.
     *
     * @param text text key that used for storing
     * @return a text from test session store
     */
    private String getTextFromStorageIfExist(final String text) {
        final String storedText = testSessionStore.get(text);
        if (storedText != null) {
            return storedText;
        }
        return text;
    }

    /**
     * Verify the current page title.
     *
     * @param title for check
     */
    @Then("I get title '$title'")
    public void verifyTitle(final String title) {
        assertThat("The page title should be as follow.",
                browser.base().getTitle(), equalTo(title));
    }

    /**
     * Search text on the current page.
     *
     * @param text for search
     */
    @Then("I get text '$text'")
    @Alias("text '$text'")
    public void verifySource(final String text) {
        assertTrue("Page source should contains the text.",
                browser.base().getPageSource().contains(text));
    }

    /**
     * Search HTML element on the current page with specified identity.
     *
     * @param identity element identity for search
     */
    @Then("I get '$identity' element")
    @Alias("'$elementId' element")
    public void verifyElement(final String identity) {
        assertThat("Page element should exists: '" + identity,
                getVisibleElement(identity),
                is(notNullValue()));
    }

    /**
     * Verify that HTML element with specified identity does not exist
     * on the current page .
     *
     * @param identity element identity for search
     */
    @Then("I get no '$identity' element")
    @Alias("no '$elementId' element")
    public void verifyElementIsNotPresent(final String identity) {
        assertThat("Page element should not exists: '" + identity,
                getVisibleElements(identity).size(), is(equalTo(0)));
    }

    /**
     * Search HTML element on the current page with specified identity and tag
     * name.
     *
     * @param identity element identity for search
     */
    @Then("I get '$identity' link")
    @Alias("'$identity' link")
    public void verifyLink(final String identity) {
        assertThat("Page element should exists: '" + identity,
                getVisibleElement(identity).getTagName(),
                is(equalTo("a")));
    }

    /**
     * Search HTML element on the current page with specified identity.
     *
     * @param identity element identity for search
     * @param text     the text for verification
     */
    @Then("I get '$identity' with '$text'")
    @Alias("'$identity' with '$text'")
    public void verifyElementText(final String identity, final String text) {
        final WebElement element = getVisibleElement(identity);
        assertThat("Page element should exists: '" + identity + "'",
                element, is(notNullValue()));
        assertThat(String.format(
                "Page element '%s' should have text: '%s'", identity, text),
                getTextFrom(element),
                is(equalTo(getTextFromStorageIfExist(text))));
    }

    /**
     * Retrieve a text from input/textarea/element.
     *
     * @param element an element for process
     * @return text value from input element
     */
    private String getTextFrom(final WebElement element) {
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

    /**
     * Store text from element on the current page with specified identity,
     * to test session map.
     *
     * @param identity element identity for search and get text
     * @param key      for store and get text from session map
     */
    @When("I remember text from '$identity' to '$key' variable")
    public void storeTextFromElement(final String identity, final String key) {
        testSessionStore.put(key, getTextFrom(getVisibleElement(identity)));
    }

    /**
     * Switch to a new window.
     */
    @When("I switch to new window")
    public void switchToNewWindow() {
        final WebDriver driver = browser.base();
        final String baseWindowHandle = driver.getWindowHandle();
        final Set<String> opened = driver.getWindowHandles();
        String newWindow;
        if (opened.size() > 1 && opened.remove(baseWindowHandle)) {
            final Iterator<String> iterator = opened.iterator();
            newWindow = iterator.next();
        } else {
            final WebDriverWait wait =
                    new WebDriverWait(driver, Resources.base().explicitWait());
            newWindow = wait.until(anyWindowOtherThan(opened));
        }
        driver.switchTo().window(newWindow);
    }

    /**
     * Retrieve an any other window than input.
     *
     * @param windows windows that should be excluded
     * @return expected condition result
     */
    public static ExpectedCondition<String> anyWindowOtherThan(
            final Set<String> windows) {
        return new ExpectedCondition<String>() {
            public String apply(final WebDriver driver) {
                if (driver == null) {
                    throw new WebDriverException();
                }
                final Set<String> all = driver.getWindowHandles();
                all.removeAll(windows);
                if (all.size() > 0) {
                    return all.iterator().next();
                }
                return null;
            }
        };
    }
}
