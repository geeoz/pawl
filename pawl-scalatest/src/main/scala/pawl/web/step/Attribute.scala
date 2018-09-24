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

import org.openqa.selenium.WebDriver
import org.scalatest.Matchers
import org.scalatest.concurrent.Eventually
import org.scalatest.exceptions.NotAllowedException
import pawl.WebStep
import pawl.web.context.StyleContext
import pawl.web.{Locators, WebPatience}

/** Review attribute in web element.
  * @param attributeName   of attribute to verify
  * @param driver web driver to use
  */
final class Attribute(attributeName: String)
                     (implicit driver: WebDriver)
  extends WebStep[StyleContext]
    with Eventually with WebPatience with Matchers with Locators {
  private val context = new StyleContext()

  override def executeWithScreenshot(): Unit = {
    val selector = context.identity
    val by = context.locator.by
    val textValue = context.styleValue
    eventually {
      selector match {
        case Some(s) =>
          val element = driver.findElement(by.by(s))
          element.toString
          withClue(s"For element $element attribute $attributeName ") {
            textValue match {
              case Some(t) if name == "tag" => element.getTagName should be(t)
              case Some(t) => element getAttribute attributeName should be(t)
              case None if name == "tag" => throw new NotAllowedException(
                s"For element $element please define tag name.", 0)
              case None => element getAttribute attributeName should not be empty
            }
          }
        case None => throw new NotAllowedException(
          s"For attribute $attributeName please define selector of web element.", 0)
      }
    }
  }

  override def clarification(): StyleContext = context
}
