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

package pawl.web

import org.scalatest.concurrent.{PatienceConfiguration, AbstractPatienceConfiguration}
import org.scalatest.time.{Seconds, Span}

/** Stackable modification trait for PatienceConfiguration that provides default timeout and interval
  * values appropriate for integration testing using web browser.
  */
trait WebPatience extends AbstractPatienceConfiguration { this: PatienceConfiguration =>

  private val defaultPatienceConfig: PatienceConfig =
    PatienceConfig(
      timeout = scaled(Span(10, Seconds)),
      interval = scaled(Span(1, Seconds))
    )

  /**
    * Implicit <code>PatienceConfig</code> value providing default configuration values suitable
    * for integration testing using web browser.
    */
  implicit abstract override val patienceConfig: PatienceConfig = defaultPatienceConfig
}
