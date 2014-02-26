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

import com.google.common.util.concurrent.MoreExecutors;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.junit.JUnitStories;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.InstanceStepsFactory;
import org.jbehave.web.selenium.PerStoriesWebDriverSteps;
import org.jbehave.web.selenium.SeleniumConfiguration;
import org.jbehave.web.selenium.SeleniumContext;
import org.jbehave.web.selenium.WebDriverProvider;
import org.jbehave.web.selenium.WebDriverScreenshotOnFailure;
import org.jbehave.web.selenium.WebDriverSteps;
import pawl.util.Resources;
import pawl.webdriver.LocalizedWebDriverProvider;

import java.util.List;

import static java.util.Arrays.asList;
import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;
import static org.jbehave.core.reporters.Format.CONSOLE;
import static org.jbehave.core.reporters.Format.TXT;

/**
 * <code>AbstractWebStories</code> class provide capability to run all BDD
 * stories this that corresponds to file name patter from default configuration
 * properties.
 *
 * @author Alex Voloshyn
 * @author Mike Dolinin
 * @author Serge Voloshyn
 * @version 1.5 2/26/14
 * @see pawl.jbehave.step.BrowserSteps#setupLink(String)
 * @see pawl.jbehave.step.BrowserSteps#openUrl()
 * @see pawl.jbehave.step.BrowserSteps#openContextPath(String)
 * @see pawl.jbehave.step.BrowserSteps#refreshPage()
 * @see pawl.jbehave.step.BrowserSteps#wait(String)
 * @see pawl.jbehave.step.BrowserSteps#choose(String, String, String)
 * @see pawl.jbehave.step.BrowserSteps#click(String)
 * @see pawl.jbehave.step.BrowserSteps#clickWithOffset(String, String, String)
 * @see pawl.jbehave.step.BrowserSteps#clickOnLinkWithAttribute(String)
 * @see pawl.jbehave.step.BrowserSteps#fill(String, String)
 * @see pawl.jbehave.step.BrowserSteps#fillFile(String, String)
 * @see pawl.jbehave.step.BrowserSteps#select(String, String)
 * @see pawl.jbehave.step.BrowserSteps#storeTextFromElement(String, String)
 * @see pawl.jbehave.step.BrowserSteps#switchToNewWindow()
 * @see pawl.jbehave.step.BrowserSteps#verifyTitle(String)
 * @see pawl.jbehave.step.BrowserSteps#verifySource(String)
 * @see pawl.jbehave.step.BrowserSteps#verifyElement(String)
 * @see pawl.jbehave.step.BrowserSteps#verifyLink(String)
 * @see pawl.jbehave.step.BrowserSteps#verifyElementText(String, String)
 * @see pawl.jbehave.step.BrowserSteps#verifyElementIsNotPresent(String)
 */
public abstract class AbstractWebStories extends JUnitStories {
    /**
     * JBehave web driver provider.
     */
    private final transient WebDriverProvider driverProvider =
            new LocalizedWebDriverProvider();
    /**
     * JBehave web driver provider.
     */
    private final transient WebDriverSteps lifecycleSteps =
            new PerStoriesWebDriverSteps(driverProvider);
    /**
     * Web testing context.
     */
    private final transient SeleniumContext context = new SeleniumContext();

    /**
     * JBehave user story launcher.
     */
    public AbstractWebStories() {
        super();
        // If configuring lifecycle per-stories, you need to ensure that you
        // a same-thread executor
        configuredEmbedder().useExecutorService(
                MoreExecutors.sameThreadExecutor());
    }

    // Here we specify the configuration, starting from default
    // SeleniumConfiguration, and changing only what is needed
    @Override
    public final Configuration configuration() {
        return new SeleniumConfiguration()
                .useSeleniumContext(context)
                .useWebDriverProvider(driverProvider)
                        // where to find the stories
                .useStoryLoader(new LoadFromClasspath(this.getClass()))
                        // CONSOLE and TXT reporting
                .useStoryReporterBuilder(
                        new StoryReporterBuilder().withDefaultFormats()
                                .withFormats(CONSOLE, TXT));
    }

    // Here we specify the steps classes
    @Override
    public final InjectableStepsFactory stepsFactory() {
        final Configuration configuration = configuration();
        final List<Object> steps = stepsInstances();
        steps.add(lifecycleSteps);
        steps.add(new WebDriverScreenshotOnFailure(driverProvider,
                configuration.storyReporterBuilder()));
        return new InstanceStepsFactory(configuration(), steps);
    }

    @Override
    protected final List<String> storyPaths() {
        // Specify story paths as URLs
        final String file = codeLocationFromClass(this.getClass()).getFile();
        return new StoryFinder().findPaths(file,
                asList(getStoriesToRun()), asList(""));
    }

    /**
     * Path pattern for user stories.
     *
     * @return path pattern for user stories.
     */
    private String getStoriesToRun() {
        String result = storiesToRun();
        if (result == null) {
            result = Resources.base().webStoriesToRun();
        }
        return result;
    }

    /**
     * Gets current web driver provider.
     *
     * @return web driver provider
     */
    public final WebDriverProvider getDriverProvider() {
        return driverProvider;
    }

    /**
     * Gets stories for run.
     *
     * @return path patters for story paths
     */
    protected abstract String storiesToRun();

    /**
     * Gets steps for include.
     *
     * @return steps instances
     */
    protected abstract List<Object> stepsInstances();
}
