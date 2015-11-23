Scenario: A scenario that works with parameters

Given I have an implemented JBehave scenario with one param
And the scenario with two uno and dos parameters also works
When user open 'http:\\www.google.com'
And user save google,yandex,bing for use
Given the scenario with two uno and dos parameters also works
And the scenario with two uno and dos parameters also works
Then user should see title with: 'Google'

Scenario: A scenario that has own $parameter inside
Given I have an implemented JBehave scenario with one param