Feature: Usage Examples

  Scenario: Power Plant Capacity Histogram Example
    Given I am running the Power Plant Capacity Histogram Example
    When I run the program
    Then I should see the output
    """
    As a histogram:
    <= 1 kW                                                                  0%
    <= 10 kW                                                                 0%
    <= 100 kW                                                                0%
    <= 1 MW   ****                                                           2%
    <= 10 MW  ************************************************************  38%
    <= 100 MW ********************************************************      35%
    <= 1 GW   ******************************                                19%
    <= 10 GW  ********                                                       5%
    >  10 GW                                                                 0%


    """
