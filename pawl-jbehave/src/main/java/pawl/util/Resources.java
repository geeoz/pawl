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

import com.google.common.collect.Maps;

import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Resources contain objects from different areas. When your program needs
 * a resource, a <code>String</code> for example, your program can load it
 * from the system properties or specified resource bundle.
 * <p/>
 * When your program needs an object, it loads the <code>Resources</code>
 * class using the {@link #get(String) get(String)}
 * method:
 * <blockquote>
 * <pre>
 * Resources resources = Resources.get("resources");
 * </pre>
 * </blockquote>
 * or you can load default resources using the
 * {@link #base() base} method:
 * <blockquote>
 * <pre>
 * Resources resources = Resources.base();
 * </pre>
 * </blockquote>
 *
 * @author Alex Voloshyn
 * @author Mike Dolinin
 * @version 1.6 2/28/14
 * @see ResourceBundle
 */
public final class Resources {
    /**
     * Logger for exceptions or info.
     */
    private static final Logger LOG =
            Logger.getLogger(Resources.class.getName());
    /**
     * Default base name for resource bundle.
     */
    private static final String DEFAULT_BASE_NAME = "base";
    /**
     * System base name for resource bundle.
     */
    private static final String SYSTEM_BASE_NAME = "base-default";
    /**
     * Specifies map to store test session data.
     */
    private static final ThreadLocal<Map<String, String>> CONTEXT =
            new ThreadLocal<>();

    /**
     * Resource bundle.
     */
    private transient ResourceBundle bundle;

    /**
     * For non-instantiability.
     */
    private Resources() {
        super();
    }

    /**
     * Create a object using the specified base name.
     *
     * @param baseName the base name of the resource package
     */
    private Resources(final String baseName) {
        this();
        Locale locale = new Locale(
                System.getProperty("user.language"),
                System.getProperty("user.country"));
        bundle = ResourceBundle.getBundle(baseName, locale);
    }

    /**
     * Gets a resources using the specified base name.
     *
     * @param baseName the base name of the resources, a fully qualified class
     *                 name
     * @return a resources for the given base name
     */
    public static Resources get(final String baseName) {
        return new Resources(baseName);
    }

    /**
     * Get resources for the 'base' base name.
     *
     * @return a resources for the 'base' base name
     */
    public static Resources base() {
        return get(DEFAULT_BASE_NAME);
    }

    /**
     * Get context map.
     *
     * @return a context map
     */
    public static Map<String, String> context() {
        if (CONTEXT.get() == null) {
            CONTEXT.set(Maps.<String, String>newConcurrentMap());
        }
        return CONTEXT.get();
    }

    /**
     * Get resources for the 'base-default' base name.
     *
     * @return a resources for the 'base-default' base name
     */
    private static Resources system() {
        return get(SYSTEM_BASE_NAME);
    }

    /**
     * Retrieves XPath for element identification.
     *
     * @param identity the value of the element identity
     * @return XPath for element identification
     */
    public String identityXpath(final String identity) {
        return String.format(string("xpath.identity"), identity);
    }

    /**
     * Return value for wait after page open key.
     *
     * @return wait time
     */
    public String waitPage() {
        return string("wait.after.page.open");
    }

    /**
     * Return value for wait before step key.
     *
     * @return wait time
     */
    public String waitBeforeStep() {
        return string("wait.before.step.seconds");
    }

    /**
     * Return name of user session cookie.
     *
     * @return name
     */
    public String userSessionCookieName() {
        return string("user.session.cookie.name");
    }

    /**
     * Return value for explicit wait.
     *
     * @return wait time
     */
    public int explicitWait() {
        return Integer.parseInt(string("explicit.wait.seconds"));
    }

    /**
     * Return value for polling interval.
     *
     * @return polling interval time
     */
    public int pollingInterval() {
        return Integer.parseInt(string("polling.interval.milliseconds"));
    }

    /**
     * Return web stories for run.
     *
     * @return path patters for story paths
     */
    public String webStoriesToRun() {
        return string("user.web.stories.to.run", "**/*.user.web.story");
    }

    /**
     * Gets a string for the given key from system properties or resource
     * bundle. And return default value from input in case when property with
     * such key wasn't find.
     *
     * @param key          the key for the desired string
     * @param defaultValue default value for cases when property with such key
     *                     wasn't find
     * @return the string for the given key or default value from input
     */
    public String string(final String key, final String defaultValue) {
        String result = string(key);
        if (result == null) {
            result = defaultValue;
        }
        return result;
    }

    /**
     * Gets a string for the given key from system properties or resource
     * bundle.
     *
     * @param key the key for the desired string
     * @return the string for the given key or null if resource is missing
     */
    public String string(final String key) {
        return string(key, false);
    }

    /**
     * Gets a string for the given key from system properties or resource
     * bundle. In case when bundle is not system and property doesn't exists
     * function try to find it in system bundle.
     *
     * @param key      the key for the desired string
     * @param isSystem the flag for identification of the system bundle
     * @return the string for the given key or null if resource is missing
     */
    private String string(final String key, final boolean isSystem) {
        final String searchKey = key.replaceAll(" ", "_");
        String result = System.getProperty(searchKey);

        if (result == null) {
            try {
                result = bundle.getString(searchKey);
            } catch (MissingResourceException e) {
                if (!isSystem) {
                    LOG.log(Level.FINE, e.getMessage(), e.getCause());
                }
            }
        }

        if (result == null && !isSystem) {
            result = system().string(key, true);
        }

        return result;
    }
}
