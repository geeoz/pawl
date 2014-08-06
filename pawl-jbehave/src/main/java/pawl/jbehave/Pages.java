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

package pawl.jbehave;

import org.jbehave.web.selenium.WebDriverPage;
import org.jbehave.web.selenium.WebDriverProvider;
import pawl.util.Resources;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Pages object is typically a factory class that allows users to easily
 * get hold of the objects for each web page, and can be injected into the
 * JBehave Steps classes. The advantage of having a pages' factory class is
 * that even as the number of page objects grow the dependencies at the Steps
 * class level still remains the same. Typically, the implementation of a step
 * requires interaction with more than one page.
 *
 * @author Alexander Voloshyn
 * @version 1.0 12/13/2012
 */
public final class Pages {
    /**
     * Default logger.
     */
    private static final Logger LOG = Logger.getLogger(Pages.class.getName());
    /**
     * WebDriver Provider.
     */
    private final transient WebDriverProvider provider;
    /**
     * Simple web page with common methods.
     */
    private transient BasePage page;

    /**
     * Default constructor.
     *
     * @param driverProvider is a web browser driver provider
     */
    public Pages(final WebDriverProvider driverProvider) {
        provider = driverProvider;
    }

    /**
     * Provide capability to run tests in slower mode. For example: can be used
     * for demo.
     */
    private void beforeAction() {
        final String wait = Resources.base().waitBeforeStep();
        if (wait != null) {
            try {
                final int seconds = (int) (Double.parseDouble(wait) * 1000);
                if (seconds > 0) {
                    Thread.sleep(seconds);
                }
            } catch (InterruptedException e) {
                LOG.log(Level.FINE, e.getMessage(), e.getCause());
            }
        }
    }

    /**
     * Web page handler for user actions.
     *
     * @return common web page
     */
    public WebDriverPage base() {
        beforeAction();
        if (page == null) {
            page = new BasePage(provider);
        }
        return page;
    }

    /**
     * Base web page object that contains common methods.
     */
    static final class BasePage extends WebDriverPage {
        /**
         * Web BasePage default constructor.
         *
         * @param driverProvider provide browser driver
         */
        public BasePage(final WebDriverProvider driverProvider) {
            super(driverProvider);
        }

        @Override
        public void get(final String url) {
            super.get(url);
            final String wait = Resources.base().waitPage();
            final int seconds = (int) (Double.parseDouble(wait) * 1000);
            if (seconds > 0) {
                try {
                    Thread.sleep(seconds);
                } catch (InterruptedException e) {
                    LOG.log(Level.FINE, e.getMessage(), e.getCause());
                }
            }
        }
    }
}
