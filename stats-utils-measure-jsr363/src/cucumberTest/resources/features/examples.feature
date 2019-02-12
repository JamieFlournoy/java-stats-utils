Feature: Usage Examples

  Scenario: Power Plant Capacity Histogram Example
    Given I am running the Power Plant Capacity Histogram Example
    When I run the program
    Then I should see the output
    """
    As a histogram:
    <= 1 kW                                                                   0%
    <= 10 kW                                                                  0%
    <= 100 kW                                                                 0%
    <= 1 MW   ****                                                          2.4%
    <= 10 MW  ************************************************************ 37.8%
    <= 100 MW ********************************************************     35.4%
    <= 1 GW   ******************************                               19.1%
    <= 10 GW  ********                                                      5.3%
    >  10 GW                                                                  0%


    """
