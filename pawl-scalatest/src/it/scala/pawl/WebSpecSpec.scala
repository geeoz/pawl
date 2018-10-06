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

/** Web spec verifications.
  */
class WebSpecSpec extends WebSpec with PropsBundle {
  val cookiesPageUrl =
    getClass getResource "/cookies-example/demo.html" toExternalForm

  "WebSpec" should "fill data into input field" in {
    Guest open "http://127.0.0.1:8080/cookies-example/demo.html"
    When enter "Johny" into "user-name"
    And click "add-cookies"
    Then see johny in "user"
    And see "Cookies demo"
  }
}
