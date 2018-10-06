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

import java.net.URL

import com.typesafe.config.ConfigException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.remote.{DesiredCapabilities, RemoteWebDriver}
import org.scalatest._
import org.scalatest.words.NotWord
import pawl.web._
import pawl.web.context.{ResizeContext, StyleContext, WebContext}
import pawl.web.step._

import scala.collection.JavaConverters._

/** <code>WebSpec</code> a simple trait for PAWL WEB DSL.
  */
trait WebSpec extends BaseSpec with Locators with Matchers {
  this: Suite =>

  lazy val Guest = this
  lazy val When = this
  lazy val Then = this
  lazy val And = this

  /** WebDriver to user based on settings. */
  lazy final implicit val driver = initDriver

  protected def initDriver: WebDriver = Config.getString(Browser) match {
    case Firefox if Config.hasPath(Remote) => new RemoteWebDriver(new URL(Config.getString(Remote)), DesiredCapabilities.firefox())
    case Chrome if Config.hasPath(Remote) =>
      val capabilities = DesiredCapabilities.chrome()
      capabilities.setCapability("applicationContainers", Array("web"))
      new RemoteWebDriver(new URL(Config.getString(Remote)), capabilities)
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

    /** Resize browser window.
      * @param param to change
      */
    def resize(param: String): ResizeContext = {
      add(new Resize(param))
    }

    /** Enters specified text.
      * @param text text for entering
      * @return clarification object
      */
    def enter(text: String): WebContext = {
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
    def see(text: String): WebContext = {
      add(new See(text))
    }

    /** Review specified style.
      * @param name of style for reviewing
      * @return clarification object
      */
    def style(name: String): StyleContext = {
      add(new Style(name))
    }

    /** Review specified attribute.
      * @param name of attribute for reviewing
      * @return clarification object
      */
    def attribute(name: String): StyleContext = {
      add(new Attribute(name))
    }

    /** Switch to negative actions.
      * @param notWord negative flag
      * @return negative web statements
      */
    def could(notWord: NotWord): NegativeWebStatement = {
      new NegativeWebStatement(spec)
    }
  }

  class NegativeWebStatement(spec: WebSpec) extends Statement(spec) {

    /** Verify could not see element
      * @param selector specification of the element to verify
      * @return clarification object
      */
    def see(selector: String): Locator = {
      add(new NotSee(selector))
    }

    /** Verify could not click on web element.
      * @param selector specification of the element to click
      * @return clarification object
      */
    def click(selector: String): Locator = {
      add(new NotClick(selector))
    }

  }

  /** Quits this driver, closing every associated window. */
  override protected def afterEach(): Unit = {
    try super.afterEach()
    finally driver.quit()
  }

}
