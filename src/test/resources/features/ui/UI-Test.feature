@ui
Feature: SauceDemo checkout calculations
    Validates that cart subtotal, tax, and final total
    are calculated correctly during checkout.

  Background:
    Given I am on the login page
    And I login via UI as "standard_user" with password "secret_sauce"
    And I should be on the inventory page

  @regression
  Scenario Outline: Add items shows correct cart badge count
    When I add these items to the cart "<items>"
    Then the cart badge count should be <count>

    Examples:
      | items                                                                                     | count |
      | Sauce Labs Backpack\nSauce Labs Fleece Jacket\nSauce Labs Bolt T-Shirt\nSauce Labs Onesie | 4     |
      | Sauce Labs Backpack\nSauce Labs Onesie                                                    | 2     |

  @regression
  Scenario Outline: Remove item reduces cart badge count
    When I add these items to the cart "<items>"
    And the cart badge count should be <before>
    When I open the cart
    And I remove item "<removeItem>"
    Then the cart badge count should be <after>

    Examples:
      | items                                                                                     | before | removeItem               | after |
      | Sauce Labs Backpack\nSauce Labs Fleece Jacket\nSauce Labs Bolt T-Shirt\nSauce Labs Onesie | 4      | Sauce Labs Fleece Jacket | 3     |

  @regression
  Scenario Outline: Checkout totals and tax are correct
    When I add these items to the cart "<items>"
    And I open the cart
    And I proceed to checkout
    And I enter checkout info first "<first>" last "<last>" zip "<zip>"
    And I continue to the overview page
    Then the item total should equal the sum of item prices
    And the tax rate should be 8 percent

    Examples:
      | items                                                                                     | first     | last     | zip     |
      | Sauce Labs Backpack\nSauce Labs Fleece Jacket\nSauce Labs Bolt T-Shirt\nSauce Labs Onesie | FirstName | LastName | EC1A 9JU |