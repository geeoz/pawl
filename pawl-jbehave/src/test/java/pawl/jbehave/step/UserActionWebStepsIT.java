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
 * @version 1.0 5/2/15
 */
public class UserActionWebStepsIT {
    private LocalizedWebDriverProvider driverProvider;
    private Pages pages;

    @Before
    public void openStaticPage(){
        URL longLoadedPageUrl = UserActionWebStepsIT.class.getResource("/long-loaded-example/demo.html");
        System.setProperty("long_loaded_test_page", longLoadedPageUrl.toExternalForm());
        driverProvider = new LocalizedWebDriverProvider();
        driverProvider.initialize();
        pages = new Pages(driverProvider);
    }

    @Test
    public void shouldWaitForElementsToBePresent() {
        BrowserSteps browserSteps = new BrowserSteps(pages);
        browserSteps.setupLink("long_loaded_test_page");
        browserSteps.openUrl();
        browserSteps.verifyTitle("Long loaded page...");
        browserSteps.verifyTitle("Page is ready");
        //verify wait element to click
        browserSteps.click("load-button");
        browserSteps.click("new-loaded-button");
        browserSteps.verifyElementText("new-loaded-button", "new loaded button");
        //verify wait element to fill
        browserSteps.click("load-input");
        browserSteps.fill("new-loaded-input", "this is text");
        browserSteps.verifyElementText("new-loaded-input", "this is text");
        //verify wait text
        browserSteps.click("load-text");
        browserSteps.verifyElementText("new-loaded-div", "new loaded text");
        //verify change text
        browserSteps.click("load-new-text");
        browserSteps.verifyElementText("new-loaded-div", "text was changed");
        //verify wait for new element attached in the DOM
        browserSteps.click("reload-button");
        browserSteps.click("new-reloaded-button");
        browserSteps.verifyElementText("new-reloaded-button", "new reloaded button");
    }

    @After
    public void stopDriver() throws Exception {
        driverProvider.end();
    }
}
