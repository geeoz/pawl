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

import java.awt.Toolkit

import org.openqa.selenium.{Point, WebDriver}
import pawl._
import pawl.web._

/** Open web page step.
  * @param url URL to open
  * @param driver web driver to use
  */
final class Open(url: String)
                (implicit driver: WebDriver) extends Step[Unit] {
  override def execute(): Unit = {
    driver get url
    maximize()
  }

  override def clarification(): Unit = {}

  protected def maximize(): Unit = {
    Config.getString(Browser) match {
      case Chrome => maximizeChromeBrowser(driver.manage.window)
      case _ => driver.manage.window.maximize()
    }
  }

  protected def maximizeChromeBrowser(window: WebDriver.Window): Unit = {
    val toolkit: Toolkit = Toolkit.getDefaultToolkit
    val screenResolution: org.openqa.selenium.Dimension =
      new org.openqa.selenium.Dimension(
        toolkit.getScreenSize.getWidth.toInt, toolkit.getScreenSize.getHeight.toInt)
    window.setSize(screenResolution)
    window.setPosition(new Point(0, 0))
  }
}
