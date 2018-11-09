Feature: Usage Examples

  Scenario: Word Count Histogram Example
    Given I am running the Word Count Histogram Example
    When I run the program
    Then I should see the output
    """
    Letters per word:
    <= 1 ******                                                         4%
    <= 2 *********************                                         15%
    <= 4 ************************************************************  43%
    <= 8 ************************************************              34%
    >  8 *****                                                          4%


    """
