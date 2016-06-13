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

import pawl.web._

/** Web fonts verifications.
  */
class WebFontsSpec extends WebSpec with PropsBundle {
  val fontsPageUrl =
    getClass getResource "/fonts-example/demo.html" toExternalForm

  "Fonts" should "change acording to browser width" in {
    Guest open fontsPageUrl
    Then see "Heading Heading Heading" in "test-h1"
    And style "font-size" is "30px" in "test-h1"
    And style "font-size" is "60px" in "test-div"
    And attribute "id" is "test-h1" in "test-h1"
    And attribute "tag" is "h1" in "test-h1"
    When resize Width to 600
    Then style "font-size" is "10px" in "test-h1"
    And style "font-size" is "20px" in "test-div"
    When resize Width to 1200
    Then style "font-size" is "20px" in "test-h1"
    And style "font-size" is "40px" in "test-div"
  }
}
