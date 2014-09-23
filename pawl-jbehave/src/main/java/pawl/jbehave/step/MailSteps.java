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

package pawl.jbehave.step;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import org.hamcrest.Matchers;
import org.jbehave.core.annotations.Alias;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import pawl.util.Resources;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static org.hamcrest.MatcherAssert.assertThat;
import static pawl.jbehave.matchers.IsReceivedEmailWithParameters
        .receivedEmailWithParameters;

/**
 * <code>MailSteps</code> a simple POJO, which will contain the Java methods
 * that are mapped to the textual steps. The methods need to annotated with
 * one of the JBehave annotations and the annotated value should contain
 * a regex pattern that matches the textual step.
 *
 * @author Alex Voloshyn
 * @version 1.0 2/27/14
 */
public final class MailSteps extends Matchers {
    /**
     * Key name to store found email body in context.
     */
    public static final String FOUND_EMAIL_BODY = "found email body";
    /**
     * An instance of the green mail server.
     */
    private transient GreenMail greenMail = null;

    /**
     * Start Email Test Server.
     */
    @Given("an email test server")
    public void startEmailTestServer() {
        if (greenMail == null) {
            greenMail = new GreenMail(); //uses test ports by default
            greenMail.start();
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
        String toValue = Resources.base().string(recipient, recipient);
        String subjectValue = Resources.base().string(subject, subject);
        assertThat(greenMail, is(receivedEmailWithParameters(toValue,
                subjectValue)));
        MimeMessage message = findMessageWithParameters(toValue, subjectValue);
        final String body = GreenMailUtil.getBody(message);
        Resources.context().put(FOUND_EMAIL_BODY, body);
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
    private MimeMessage findMessageWithParameters(final String recipient,
                                                  final String subject) {
        StringBuilder recipientsAndSubjects = new StringBuilder();
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        for (MimeMessage mimeMessage : receivedMessages) {
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
                continue;
            }
        }
        throw new AssertionError("Could not find message with parameters: "
                + "recipient - " + recipient + ", subject - " + subject
                + "\nin mailbox with : "
                + recipientsAndSubjects.toString());
    }

    /**
     * Retrieve an instance of the green mail server.
     *
     * @return an instance of the green mail server
     */
    protected GreenMail getGreenMail() {
        return greenMail;
    }
}
