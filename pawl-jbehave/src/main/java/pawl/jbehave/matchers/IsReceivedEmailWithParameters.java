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

import javax.mail.Address;
import javax.mail.Message;
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
public class IsReceivedEmailWithParameters extends TypeSafeMatcher<GreenMail> {
    /**
     * Default logger.
     */
    private static final Logger LOG =
            Logger.getLogger(IsReceivedEmailWithParameters.class.getName());
    /**
     * Recipient email to match.
     */
    private String to;
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
     * Create matcher that poll new message until find.
     *
     * @param recipientEmailToMatch string
     * @param subjectToMatch        string
     */
    public IsReceivedEmailWithParameters(final String recipientEmailToMatch,
                                         final String subjectToMatch) {
        this.to = recipientEmailToMatch;
        this.subject = subjectToMatch;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("received message with parameters: "
                + "recipient - " + to
                + ", subject - " + subject);
    }

    @Override
    protected void describeMismatchSafely(
            final GreenMail item, final Description mismatchDescription) {
        List<String> recipientsAndSubjectsList = Lists.transform(
                Arrays.asList(messages), toRecipientsAndSubjects);
        mismatchDescription.appendText("was ");
        mismatchDescription.appendValueList("", ", ", "",
                recipientsAndSubjectsList);
    }

    /**
     * Function to get subjects from MIME messages.
     */
    private Function<MimeMessage, String> toRecipientsAndSubjects =
            new Function<MimeMessage, String>() {
                @Override
                public String apply(final MimeMessage input) {
                    return "recipient - " + getRecipients(input)
                            + ", subject - "
                            + getSubject(input);
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
     * @param recipientEmail string
     * @param subject string
     * @return new matcher
     */
    @Factory
    public static <T> Matcher<GreenMail> receivedEmailWithParameters(
            final String recipientEmail,
            final String subject) {
        return new IsReceivedEmailWithParameters(recipientEmail, subject);
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
                for (MimeMessage mimeMessage : messages) {
                    for (Address address : getRecipients(mimeMessage)) {
                        if (address.toString().equals(to)
                                && getSubject(mimeMessage).equals(subject)) {
                            return true;
                        }
                    }
                }
                return false;
            }
        });
    }

    /**
     * Get recipients type to from message.
     * @param msg message
     * @return list of recipients addresses
     */
    private List<Address> getRecipients(final MimeMessage msg) {
        try {
            return Arrays.asList(msg.getRecipients(Message.RecipientType.TO));
        } catch (MessagingException e) {
            LOG.log(Level.FINE, e.getMessage(), e.getCause());
            return Arrays.asList(new Address[]{});
        }
    }

    /**
     * Get subject from message.
     * @param msg message
     * @return message subject
     */
    private String getSubject(final MimeMessage msg) {
        try {
            return msg.getSubject();
        } catch (MessagingException e) {
            LOG.log(Level.FINE, e.getMessage(), e.getCause());
            return "";
        }
    }

}
