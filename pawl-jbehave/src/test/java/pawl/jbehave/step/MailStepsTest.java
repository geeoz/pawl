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
import org.junit.Test;
import pawl.util.Resources;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Email test server steps verification.
 *
 * @author Alex Voloshyn
 * @author Serge Voloshyn
 * @version 1.1 3/11/14
 */
public class MailStepsTest {
    /**
     * Verify that email test server was started after given step call.
     */
    @Test
    public void shouldStartEmailTestServerForGivenStep() {
        final MailSteps steps = new MailSteps();
        steps.startEmailTestServer();
        final GreenMail greenMail = steps.getGreenMail();
        try {
            GreenMailUtil.sendTextEmailTest(
                    "to@localhost.com", "from@localhost.com", "subject", "body");
            assertThat("Email body should be the same",
                    "body",
                    is(equalTo(
                            GreenMailUtil.getBody(
                                    greenMail.getReceivedMessages()[0]))));
        } finally {
            greenMail.stop();
        }
    }

    /**
     * Verify that THEN step verify last email subject.
     */
    @Test
    public void shouldCheckLastEmailSubject() {
        final MailSteps steps = new MailSteps();
        steps.startEmailTestServer();
        final GreenMail greenMail = steps.getGreenMail();
        try {
            GreenMailUtil.sendTextEmailTest(
                    "to@localhost.com", "from@localhost.com", "subject1", "body");
            GreenMailUtil.sendTextEmailTest(
                    "to@localhost.com", "from@localhost.com", "subject2", "body");
            steps.verifyLastEmailSubject("subject2");
        } finally {
            greenMail.stop();
        }
    }

    /**
     * Verify that THEN step verify last email subject
     * with polling if email is not received yet
     */
    @Test
    public void shouldCheckLastEmailSubjectWithPolling()
            throws InterruptedException {
        final MailSteps steps = new MailSteps();
        steps.startEmailTestServer();
        final GreenMail greenMail = steps.getGreenMail();
        try {
            Runnable sendEmailAfterFewSeconds = new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.currentThread().sleep(
                                Resources.base().pollingInterval() - 100);
                        GreenMailUtil.sendTextEmailTest(
                                "to@localhost.com",
                                "from@localhost.com", "subject4", "body");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            Thread t = new Thread(sendEmailAfterFewSeconds);
            t.start();
            steps.verifyLastEmailSubject("subject4");
        } finally {
            greenMail.stop();
        }
    }

    /**
     * Verify that THEN step verify last email subject.
     */
    @Test
    public void shouldCheckLastEmailSubjectRU() throws MessagingException,
            UnsupportedEncodingException {
        final MailSteps steps = new MailSteps();
        steps.startEmailTestServer();
        final GreenMail greenMail = steps.getGreenMail();
        try {
            GreenMailUtil.sendTextEmailTest(
                    "to@localhost.com", "from@localhost.com", "темма", "body");
            GreenMailUtil.sendTextEmailTest(
                    "to@localhost.com", "from@localhost.com", "тема2", "body");
            steps.verifyLastEmailSubject("тема2");
        } finally {
            greenMail.stop();
        }
    }

    /**
     * Verify that link was store in context store.
     */
    @Test
    public void shouldStoreLinkInContextStore() {
        final MailSteps steps = new MailSteps();
        steps.startEmailTestServer();
        final GreenMail greenMail = steps.getGreenMail();
        try {
            GreenMailUtil.sendTextEmailTest(
                    "to@localhost.com", "from@localhost.com", "subject",
                    "body with <a id=\"id\" href=\"http://localhost\">Link</a>");

            assertThat("Values should be empty.",
                    Resources.context().get("key"), is(nullValue()));

            steps.storeLinkFromElement("id", "key");
            assertThat("Values should be the same.",
                    Resources.context().get("key"),
                    is(equalTo("http://localhost")));

            Resources.context().put("key", "123");
            GreenMailUtil.sendTextEmailTest(
                    "to@localhost.com", "from@localhost.com", "subject",
                    "body with <a id='id' href='http://localhost'/>");
            steps.storeLinkFromElement("id", "key");
            assertThat("Values should be the same.",
                    Resources.context().get("key"),
                    is(equalTo("http://localhost")));
        } finally {
            greenMail.stop();
        }
    }
}
