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

package pawl.util;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Resources contain objects from different areas.
 *
 * @author Alex Voloshyn
 * @version 1.3 1/23/15
 */
public class ResourcesTest {
    @Test
    public void shouldRetrieveValueFromDefaultProperties() {
        final Resources resources = Resources.get("base-default");
        assertThat("Should retrieve value from default properties.",
                resources.explicitWait(), is(equalTo(60)));
    }

    @Test
    public void shouldRetrieveValueFromBaseProperties() {
        System.setProperty("user.language", "ru");
        System.setProperty("user.country", "RU");
        final Resources resources = Resources.base();
        assertThat(resources.string("@login.text"), is(equalTo("Логин")));
    }
}
