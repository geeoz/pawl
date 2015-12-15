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

import pawl.tagobjects.IntegrationTest
import pawl.tags.HighPerformanceTest

/** Unit test example. */
@HighPerformanceTest
class SetSpec extends UnitSpec {

  "A Set" - {
    "when empty" - {
      "should have size 0" in {
        assert(Set.empty.size === 0)
      }

      "should produce NoSuchElementException when head is invoked" taggedAs IntegrationTest in {
        intercept[NoSuchElementException] {
          Set.empty.head
        }
      }
    }
  }
}
