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

/** Actions in browser verification.
  */
class BrowserActionsSpec extends WebSpec {

  val longLoadedPageUrl =
    getClass getResource "/long-loaded-example/demo.html" toExternalForm

  "Steps" should "wait for elements to be present" in {
    Guest open longLoadedPageUrl
    Then see "Long loaded page..." in title
    Then see "Page is ready" in title
    // verify wait element to click
    When click "load-button" by id
    And click "#new-loaded-button" by css
    Then see "new loaded button" in "new-loaded-button"
    // verify wait element to fill
    When click "//*[@id='load-input']" by xpath
    And enter "this is text" in "new-loaded-input"
    Then see "this is text" in "new-loaded-input"
    // verify wait text
    When click "load-text"
    Then see "new loaded text" in "new-loaded-div"
    // verify change text
    When click "load-new-text"
    Then see "text was changed" in "new-loaded-div"
    // verify wait for new element attached in the DOM
    When click "reload-button"
    And click "new-reloaded-button"
    Then see "new reloaded button" in "new-reloaded-button"
    // Negative actions
    And could not see "hidden-button"
    And could not see "no-button"
    And could not click "disabled-button" by id
    And could not click "hidden-button" by id
    And could not click "no-button" by id
  }

}
