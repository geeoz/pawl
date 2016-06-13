/*
 * Copyright 2016 Geeoz Software
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

import org.openqa.selenium.{NoSuchElementException, WebDriver}
import org.scalatest.Matchers
import org.scalatest.concurrent.Eventually
import pawl.WebStep
import pawl.web.context.WebContext
import pawl.web.{Locator, WebPatience}

/** Verify not see web element.
  * @param selector web element identity
  * @param driver web driver to use
  */
final class NotSee(selector: String)
                  (implicit driver: WebDriver)
  extends WebStep[Locator] with Eventually with WebPatience with Matchers {
  private val context = new WebContext()

  override def executeWithScreenshot(): Unit = {
    val by = context.locator.by
    eventually {
      try {
        val element = driver.findElement(by.by(selector))
        element should not be 'displayed
      } catch {
        case e: NoSuchElementException => Unit
      }
    }
  }

  override def clarification(): Locator = context.locator
}
