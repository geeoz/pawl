/*
 * Copyright 2014 Geeoz Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package pawl.util;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.ExpectedCondition;

import java.util.Set;

/**
 * Provide different expected conditions for webdriver.
 *
 * @author Mike Dolinin
 * @version 1.0 8/19/14
 */
public final class WebExpectedConditions {

    /**
     * For non-instantiability.
     */
    private WebExpectedConditions() {
        super();
    }

    /**
     * Get expected conditions for webdriver.
     *
     * @return a expected conditions for webdriver
     */
    public static WebExpectedConditions get() {
        return new WebExpectedConditions();
    }

    /**
     * Retrieve an any other window than input.
     *
     * @param windows windows that should be excluded
     * @return expected condition result
     */
    public ExpectedCondition<String> anyWindowOtherThan(
            final Set<String> windows) {
        return new NewWindowExpectedCondition(windows);
    }

    /**
     * Continuously check active counter.
     * When counter become 0 return true.
     *
     * @param counterStateFunction js function
     * @return expected condition result
     */
    public ExpectedCondition<Boolean> complete(
            final String counterStateFunction) {
        return new CounterExpectedCondition(counterStateFunction);
    }

    /**
     * Get expected conditions for new window.
     */
    private static class NewWindowExpectedCondition
            implements ExpectedCondition<String> {

        /**
         * Handle set of all windows ids.
         */
        private final Set<String> allWindows;

        /**
         * Create an object expected conditions for new window.
         *
         * @param windows set of all windows ids.
         */
        public NewWindowExpectedCondition(final Set<String> windows) {
            this.allWindows = windows;
        }

        /**
         * Check condition.
         *
         * @param driver for browser
         * @return check state
         */
        public String apply(final WebDriver driver) {
            if (driver == null) {
                throw new WebDriverException();
            }
            final Set<String> all = driver.getWindowHandles();
            all.removeAll(allWindows);
            if (all.size() > 0) {
                return all.iterator().next();
            }
            return null;
        }
    }

    /**
     * Get expected conditions for counter.
     */
    private static class CounterExpectedCondition
            implements ExpectedCondition<Boolean> {

        /**
         * Handle js function for counter check.
         */
        private final String counterJsFunction;

        /**
         * Create an object expected conditions for counter.
         *
         * @param counterStateFunction js function.
         */
        public CounterExpectedCondition(final String counterStateFunction) {
            this.counterJsFunction = counterStateFunction;
        }

        /**
         * Check condition.
         *
         * @param driver for browser
         * @return check state
         */
        public Boolean apply(final WebDriver driver) {
            if (driver == null) {
                throw new WebDriverException();
            }
            long counter;
            try {

                counter = (Long) ((JavascriptExecutor) driver)
                        .executeScript("return " + counterJsFunction + ";",
                                new Object[]{});
            } catch (WebDriverException e) {
                counter = 0;
            }
            return counter == 0;
        }
    }
}
