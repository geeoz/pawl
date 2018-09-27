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

import org.scalatest._

import scala.collection.mutable.ArrayBuffer

/** <code>BaseSpec</code> a simple trait for PAWL DSL.
  */
trait BaseSpec extends FlatSpec with BeforeAndAfterEach with Bundle {
  this: Suite =>
  /** Array of the steps for execution */
  private lazy val scenario = ArrayBuffer[Step[_]]()

  /** Base statement class for DSL.
    * @param spec specification that runs
    */
  abstract class Statement(spec: BaseSpec) {
    /** Adds new step and run previous steps that where waiting for
      * execution.
      * @param s new step
      * @tparam T type of the step clarification object
      * @return step clarification object
      */
    protected def add[T](s: Step[T]): T = {
      if (scenario.nonEmpty) {
        scenario foreach (_.execute())
        scenario clear()
      }
      scenario += s
      s.clarification()
    }
  }

  /** Execute last step if previous not failed.
    */
  override def withFixture(test: NoArgTest): Outcome = {
    super.withFixture(test) match {
      case Succeeded =>
        scenario foreach (_.execute())
        Succeeded
      case any: Any => any
    }
  }
}
