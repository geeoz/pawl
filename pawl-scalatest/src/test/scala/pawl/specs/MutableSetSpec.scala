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

package pawl.specs

import pawl.tagobjects.UnitTest
import pawl.tags.SmokeTest

import scala.collection.mutable

/** Integration test example. */
@SmokeTest
class MutableSetSpec extends IntegrationSpec {

  "A mutable Set" - {
    "should allow an element to be added" in {
      val set = mutable.Set.empty[String]
      set += "clarity"
      assert(set.size === 1)
      assert(set.contains("clarity"))
      info("That's all folks!")
    }

    "should produce NoSuchElementException when head is invoked" taggedAs UnitTest in {
      intercept[NoSuchElementException] {
        mutable.Set.empty[String].head
      }
    }
  }
}
