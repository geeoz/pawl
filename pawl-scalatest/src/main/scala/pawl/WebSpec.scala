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

package pawl

import com.typesafe.config.ConfigException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.scalatest._
import pawl.web._
import pawl.web.step.{Click, Enter, Open, See}

/** <code>WebSpec</code> a simple trait for PAWL WEB DSL.
  */
trait WebSpec extends BaseSpec with BeforeAndAfter with Locators {
  this: Suite =>

  lazy val Guest = this
  lazy val When = this
  lazy val Then = this
  lazy val And = this

  /** WebDriver to user based on settings. */
  final implicit val driver = initDriver

  protected def initDriver: WebDriver = Config.getString(Browser) match {
    case Firefox => new FirefoxDriver()
    case Chrome => new ChromeDriver()
    case _ =>
      throw new ConfigException.BadValue(Browser, Config.getString(Browser))
  }

  /** Implicit class for PAWL WEB DSL.
    * @param spec specification that runs
    */
  implicit class WebStatement(spec: WebSpec) extends Statement(spec) {
    /** Open url in browser.
      * @param url open
      */
    def open(url: String): Unit = {
      add(new Open(url))
    }

    /** Enters specified text.
      * @param text text for entering
      * @return clarification object
      */
    def enter(text: String): Context = {
      add(new Enter(text))
    }

    /** Click on web element.
      * @param selector specification of the element to click
      * @return clarification object
      */
    def click(selector: String): Locator = {
      add(new Click(selector))
    }

    /** Review specified text.
      * @param text text for reviewing
      * @return clarification object
      */
    def see(text: String): Context = {
      add(new See(text))
    }
  }

  /** Quits this driver, closing every associated window. */
  after {
    driver.quit()
  }
}
