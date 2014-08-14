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

package pawl.jbehave.step;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pawl.jbehave.Pages;
import pawl.webdriver.LocalizedWebDriverProvider;

import java.net.URL;

/**
 * Steps in browser verification.
 *
 * @author Mike Dolinin
 * @version 1.1 7/30/14
 */
public class BrowserStepsIT {

    private URL cookiesPageUrl;
    private LocalizedWebDriverProvider driverProvider;
    private Pages pages;

    @Before
    public void openStaticPage(){
        cookiesPageUrl = BrowserStepsIT.class.getResource("/cookies-example/demo.html");
        System.setProperty("cookies_test_page", cookiesPageUrl.toExternalForm());
        driverProvider = new LocalizedWebDriverProvider();
        driverProvider.initialize();
        System.setProperty("empty-title", "");
        pages = new Pages(driverProvider);
    }

    @Test
    public void shouldDeleteUserSessionCookieAndRefreshThePage() {
        BrowserSteps browserSteps = new BrowserSteps(pages);
        browserSteps.setupLink("cookies_test_page");
        browserSteps.openUrl();
        browserSteps.verifyElementText("session-status", "Your session is expired!");
        browserSteps.click("start-new-session");
        browserSteps.verifyElementText("session-status", "Your session is xZ9TPyIvNgxP");
        browserSteps.expireUserSession();
        browserSteps.verifyElementText("session-status", "Your session is expired!");
    }

    @After
    public void stopDriver() throws Exception {
        driverProvider.end();
    }
}
