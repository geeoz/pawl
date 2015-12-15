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
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import org.openqa.selenium.remote.DesiredCapabilities
import org.scalatest.Suite
import pawl.web._

import scala.collection.JavaConverters._

/** <code>MobileWebSpec</code> a trait for web test in Mobile Emulation mode. */
trait MobileWebSpec extends WebSpec {
  this: Suite =>

  /** WebDriver to user based on settings. */
  override def initDriver: WebDriver = Config.getString(Browser) match {
    case Chrome =>
      val mobileConf = Config.getConfig(Mobile)
      val deviceMetrics = Map(Width -> mobileConf.getInt(Width),
        Height -> mobileConf.getInt(Height),
        PixelRatio -> mobileConf.getDouble(PixelRatio))
      val mobileEmulation = Map("deviceMetrics" -> deviceMetrics.asJava,
        UserAgent -> mobileConf.getString(UserAgent))
      val chromeOptions = Map("mobileEmulation" -> mobileEmulation.asJava)
      val capabilities = DesiredCapabilities.chrome()
      capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions.asJava)
      new ChromeDriver(capabilities)
    case _ =>
      throw new ConfigException.BadValue(Browser, Config.getString(Browser))
  }

}
