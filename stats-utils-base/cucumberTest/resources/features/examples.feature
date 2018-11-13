Feature: Usage Examples

  Scenario: Word Count Histogram Example
    Given I am running the Word Count Histogram Example
    When I run the program
    Then I should see the output
    """
    Characters per word:
    1 chars:	23
    2 chars:	85
    3 chars:	117
    4 chars:	124
    5 chars:	83
    6 chars:	55
    7 chars:	40
    8 chars:	16
    9 chars:	11
    10 chars:	5
    11 chars:	3
    12 chars:	2
    13 chars:	1
    Total number of words: 565

	As a histogram:
    <= 1 ******                                                         4%
    <= 2 *********************                                         15%
    <= 4 ************************************************************  43%
    <= 8 ************************************************              34%
    >  8 *****                                                          4%


    """
