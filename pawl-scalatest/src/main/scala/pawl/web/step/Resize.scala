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
import org.scalatest.exceptions.NotAllowedException
import pawl.WebStep
import pawl.web._
import pawl.web.context.ResizeContext

/** Change browser size.
  * @param param to change
  * @param driver web driver to use
  */
final class Resize(param: String)
                  (implicit driver: WebDriver) extends WebStep[ResizeContext] {

  private val context = new ResizeContext()

  override def executeWithScreenshot(): Unit = {
    val value = context.paramValue
    val current = driver.manage.window.getSize
    value match {
      case Some(p) =>
        param match {
          case Width => driver.manage.window.setSize(new org.openqa.selenium.Dimension(p, current.height))
          case Height => driver.manage.window.setSize(new org.openqa.selenium.Dimension(current.width, current.height))
          case _ => throw new NotAllowedException(
            s"$param is not valid for resize. Please use $Width or $Height options.", 0)
        }
      case None => throw new NotAllowedException(
        s"For resize $param please define value to change to.", 0)
    }
  }

  override def clarification(): ResizeContext = context

}
