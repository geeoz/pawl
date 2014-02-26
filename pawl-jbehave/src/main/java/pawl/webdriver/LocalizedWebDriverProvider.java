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

package pawl.webdriver;

import org.jbehave.web.selenium.PropertyWebDriverProvider;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

/**
 * Provide localization support for different browsers.
 *
 * @author Mike Dolinin
 * @version 1.0 2/26/14
 */
public class LocalizedWebDriverProvider extends PropertyWebDriverProvider {
    /**
     * User language property name.
     */
    public static final String USER_LANGUAGE = "user.language";

    /**
     * Provide new Firefox driver with setup of user language.
     *
     * @return firefox driver
     */
    protected FirefoxDriver createFirefoxDriver() {
        FirefoxProfile firefoxProfile = new FirefoxProfile();
        firefoxProfile.setPreference("intl.accept_languages",
                System.getProperty(USER_LANGUAGE));
        return new FirefoxDriver(firefoxProfile);
    }
}
