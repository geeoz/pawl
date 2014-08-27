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

package pawl.jbehave.matchers;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.icegreen.greenmail.util.GreenMail;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.FluentWait;
import pawl.util.Resources;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Alex Voloshyn
 * @author Mike Dolinin
 * @version 1.1 8/28/14
 */
public class IsReceivedEmailWithSubject extends TypeSafeMatcher<GreenMail> {
    /**
     * Default logger.
     */
    private static final Logger LOG =
            Logger.getLogger(IsReceivedEmailWithSubject.class.getName());
    /**
     * Subject to match.
     */
    private String subject;
    /**
     * Mail server.
     */
    private GreenMail greenMail;
    /**
     * Array of current received messages.
     */
    private MimeMessage[] messages;

    /**
     * Create matcher that poll new message until find subject.
     *
     * @param subjectToMatch string
     */
    public IsReceivedEmailWithSubject(final String subjectToMatch) {
        this.subject = subjectToMatch;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("received message with subject - " + subject);
    }

    @Override
    protected void describeMismatchSafely(
            final GreenMail item, final Description mismatchDescription) {
        List<String> subjectsList = Lists.transform(
                Arrays.asList(messages), toSubjects);
        mismatchDescription.appendText("was ");
        mismatchDescription.appendValueList("", ", ", "", subjectsList);
    }

    /**
     * Function to get subjects from MIME messages.
     */
    private static Function<MimeMessage, String> toSubjects =
            new Function<MimeMessage, String>() {
                @Override
                public String apply(final MimeMessage input) {
                    if (input == null) {
                        return "";
                    }
                    try {
                        return input.getSubject();
                    } catch (MessagingException e) {
                        return "";
                    }
                }
            };


    @Override
    protected boolean matchesSafely(final GreenMail item) {
        greenMail = item;
        try {
            waitUntilReceivedMessageWithSubject();
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Factory that create new received email with subject matcher.
     *
     * @param <T>     mail server type
     * @param subject string
     * @return new matcher
     */
    @Factory
    public static <T> Matcher<GreenMail> receivedEmailWithSubject(
            final String subject) {
        return new IsReceivedEmailWithSubject(subject);
    }

    /**
     * Wait until received message with subject.
     */
    private void waitUntilReceivedMessageWithSubject() {
        final FluentWait<GreenMail> wait = new FluentWait<>(greenMail)
                .withTimeout(Resources.base().explicitWait(), TimeUnit.SECONDS)
                .pollingEvery(Resources.base().pollingInterval(),
                        TimeUnit.MILLISECONDS);
        wait.until(new Predicate<GreenMail>() {
            @Override
            public boolean apply(final GreenMail input) {
                messages = greenMail.getReceivedMessages();
                if (messages.length > 0) {
                    try {
                        return messages[messages.length - 1]
                                .getSubject().equals(subject);
                    } catch (MessagingException e) {
                        LOG.log(Level.FINE, e.getMessage(), e.getCause());
                    }
                }
                return false;
            }
        });
    }
}
