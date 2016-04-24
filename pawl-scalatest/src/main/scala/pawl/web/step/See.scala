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

package pawl.web.step

import org.openqa.selenium.WebDriver
import org.scalatest.Matchers
import org.scalatest.concurrent.Eventually
import pawl.WebStep
import pawl.web.{Context, Locators, WebPatience}

/** Review text in web element.
  * @param text to verify
  * @param driver web driver to use
  */
final class See(text: String)
               (implicit driver: WebDriver)
  extends WebStep[Context]
  with Eventually with WebPatience with Matchers with Locators {
  private val context = new Context()

  override def executeWithScreenshot(): Unit = {
    val selector = context.identity
    val by = context.locator.by
    eventually {
      selector match {
        case Some(s) if title.equals(s) => driver.getTitle should be(text)
        case Some(s) =>
          val element = driver.findElement(by.by(s))
          element.getTagName match {
            case "input" | "textarea" =>
              element getAttribute "value" should be(text)
            case _ => element.getText should be(text)
          }
        case None => driver.getPageSource should include(text)
      }
    }
  }

  override def clarification(): Context = context
}
