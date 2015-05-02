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

import com.google.common.base.Predicate;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.jbehave.core.annotations.Alias;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.FluentWait;
import pawl.util.Resources;

import javax.mail.Address;
import javax.mail.AuthenticationFailedException;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * <code>MailSteps</code> a simple POJO, which will contain the Java methods
 * that are mapped to the textual steps. The methods need to annotated with
 * one of the JBehave annotations and the annotated value should contain
 * a regex pattern that matches the textual step.
 *
 * @author Alex Voloshyn
 * @version 1.1 5/2/15
 */
public final class MailSteps {
    /**
     * Default logger.
     */
    private static final Logger LOG =
            Logger.getLogger(MailSteps.class.getName());
    /**
     * Key name to store found email body in context.
     */
    public static final String FOUND_EMAIL_BODY = "found email body";
    /**
     * An instance of the green mail server.
     */
    private transient GreenMail greenMail = null;
    /**
     * An instance of the mail store.
     */
    private Store store;
    /**
     * An instance of the inbox mail.
     */
    private Folder inbox;
    /**
     * All recipients and subjects in inbox.
     */
    private StringBuilder recipientsAndSubjects = new StringBuilder();

    /**
     * Start Email Test Server.
     */
    @Given("an email test server")
    public void startEmailTestServer() {
        try {
            if (greenMail == null) {
                greenMail = new GreenMail(ServerSetupTest.SMTP_POP3);
                Thread.UncaughtExceptionHandler silentHandler =
                        new SilentExceptionHandler();
                greenMail.getSmtp().setUncaughtExceptionHandler(silentHandler);
                greenMail.getPop3().setUncaughtExceptionHandler(silentHandler);
                //uses test ports by default
                greenMail.start();
            }
        } catch (RuntimeException e) {
            LOG.fine("Email server is already started");
            // hope email server is running
        }
    }

    /**
     * Verify received email with parameters.
     *
     * @param recipient mail message subject
     * @param subject   last mail message subject
     */
    @Then("I get email with parameters '$recipient' and '$subject'")
    @Alias("mail with parameters '$recipient' and '$subject'")
    public void verifyLastEmailSubject(final String recipient,
                                       final String subject) {
        String recipientValue = Resources.base().string(recipient, recipient);
        String subjectValue = Resources.base().string(subject, subject);
        waitUntilReceivedMessageWithParameters(recipientValue, subjectValue);
        MimeMessage message = findMessageWithParams(recipientValue,
                subjectValue);
        final String body = GreenMailUtil.getBody(message);
        Resources.context().put(FOUND_EMAIL_BODY, body);
        closeInbox();
    }

    /**
     * Store link from element in the last email with specified identity,
     * to test session map.
     *
     * @param identity element identity for search and get text
     * @param key      for store and get text from session map
     */
    @When("I remember email link from '$identity' to '$key' variable")
    public void storeLinkFromElement(final String identity, final String key) {
        String body = Resources.context().get(FOUND_EMAIL_BODY);
        final Document html = Jsoup.parse(body);
        final Element link = html.getElementById(identity);
        Resources.context().put(key, link.attr("href"));
    }

    /**
     * Find received email with parameters.
     *
     * @param recipient mail message subject
     * @param subject   last mail message subject
     * @return message with parameters
     */
    private MimeMessage findMessageWithParams(final String recipient,
                                              final String subject) {
        recipientsAndSubjects = new StringBuilder();
        for (MimeMessage mimeMessage : getReceivedMessages()) {
            try {
                for (Address address : mimeMessage.getRecipients(
                        Message.RecipientType.TO)) {
                    String addressString = address.toString();
                    String subjectString = mimeMessage.getSubject();
                    recipientsAndSubjects.append("\nrecipient - ");
                    recipientsAndSubjects.append(addressString);
                    recipientsAndSubjects.append(", subject - ");
                    recipientsAndSubjects.append(subjectString);
                    if (addressString.equals(recipient)
                            && subjectString.equals(subject)) {
                        return mimeMessage;
                    }
                }
            } catch (MessagingException e) {
                LOG.warning(e.getMessage());
            }
        }
        return null;
    }

    /**
     * Wait until received message with parameters.
     *
     * @param recipient of email
     * @param subject   of email
     */
    private void waitUntilReceivedMessageWithParameters(final String recipient,
                                                        final String subject) {
        final FluentWait<String> wait = new FluentWait<>(recipient)
                .withTimeout(Resources.base().explicitWait(), TimeUnit.SECONDS)
                .pollingEvery(Resources.base().pollingInterval(),
                        TimeUnit.MILLISECONDS);
        try {
            wait.until(new Predicate<String>() {
                @Override
                public boolean apply(final String recipient) {
                    try {
                        openInboxAs(recipient);
                    } catch (AuthenticationFailedException e) {
                        return false;
                    }
                    return findMessageWithParams(recipient, subject) != null;
                }
            });
        } catch (TimeoutException e) {
            throw new AssertionError("Could not find message with parameters: "
                    + "recipient - " + recipient + ", subject - " + subject
                    + "\nin mailbox with : "
                    + recipientsAndSubjects.toString());
        }
    }

    /**
     * Retrieve an instance of the green mail server.
     *
     * @return an instance of the green mail server
     */
    protected GreenMail getGreenMail() {
        return greenMail;
    }

    /**
     * Open POP3 connection to Inbox.
     *
     * @param user to connect
     * @throws javax.mail.AuthenticationFailedException wrong credentials
     */
    private void openInboxAs(final String user)
            throws AuthenticationFailedException {
        Properties properties = System.getProperties();
        Session session = Session.getDefaultInstance(properties);
        URLName urlName = new URLName("pop3", "localhost",
                ServerSetupTest.POP3.getPort(), null,
                user, user);
        try {
            store = session.getStore(urlName);
            store.connect();
            inbox = store.getFolder("Inbox");
            inbox.open(Folder.READ_ONLY);
        } catch (MessagingException e) {
            if (e instanceof AuthenticationFailedException) {
                throw (AuthenticationFailedException) e;
            } else {
                LOG.warning(e.getMessage());
            }
        }
    }

    /**
     * Get all emails.
     *
     * @return all messages
     */
    private MimeMessage[] getReceivedMessages() {
        Message[] messages = new Message[0];
        try {
            messages = inbox.getMessages();
        } catch (MessagingException e) {
            LOG.warning(e.getMessage());
        }
        MimeMessage[] mimeMessages = new MimeMessage[messages.length];
        for (int i = 0; i < messages.length; i++) {
            if (messages[i] instanceof MimeMessage) {
                mimeMessages[i] = (MimeMessage) messages[i];
            }
        }
        return mimeMessages;
    }

    /**
     * Close POP3 connection to Inbox.
     */
    private void closeInbox() {
        try {
            inbox.close(true);
            store.close();
        } catch (MessagingException e) {
            LOG.warning(e.getMessage());
        }
    }

    /**
     * Handle exceptions when email servers is already started.
     */
    private static class SilentExceptionHandler
            implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(final Thread t,
                                      final Throwable e) {
            LOG.fine("Email server is already started");
        }
    }
}
