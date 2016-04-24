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

package pawl

import java.io.File

import org.openqa.selenium.WebDriver
import org.scalatest.selenium.WebBrowser

/** PAWL web step that takes screenshot on failure.
  * @tparam T clarification type that used before execution
  */
abstract class WebStep[T](implicit val webDriver: WebDriver) extends Step[T] with WebBrowser {

  override def execute(): Unit = {
    setCaptureDir(new File(System.getProperty("java.io.tmpdir")) + File.separator + "pawl-screenshots")
    withScreenshot {
      executeWithScreenshot()
    }
  }

  def executeWithScreenshot(): Unit
}
