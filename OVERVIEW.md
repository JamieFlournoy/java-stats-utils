# Code Overview

This overview lists all of the public interfaces and implementation classes from both `com.pervasivecode:stats-utils:0.9` and `com.pervasivecode:stats-utils-measure-jsr363:0.9`, but the ones that are only available in `stats-utils-measure-jsr363` are marked as such on an item by item basis.

### Javadoc links:

If you prefer Javadocs, they are available on `javadoc.io`:

For stats-utils:
[![Javadocs](https://www.javadoc.io/badge/com.pervasivecode/stats-utils.svg)](https://www.javadoc.io/doc/com.pervasivecode/stats-utils)

For stats-utils-measure-jsr363:
[![Javadocs](https://www.javadoc.io/badge/com.pervasivecode/stats-utils-measure-jsr363.svg)](https://www.javadoc.io/doc/com.pervasivecode/stats-utils-measure-jsr363)

## Interfaces

### In package com.pervasivecode.utils.stats:

#### [DurationEstimator](stats-utils-base/src/main/java/com/pervasivecode/utils/stats/DurationEstimator.java)

This object can estimate the rate at which a repeatedly set progress value is currently changing, and can estimate how long it will take for that value to reach a specified target value.

### In package com.pervasivecode.utils.stats.histogram:

#### [BucketingSystem](stats-utils-base/src/main/java/com/pervasivecode/utils/stats/histogram/BucketingSystem.java)

Common methods for things that put values into a fixed number of adjacent intervals.

#### [BucketSelector](stats-utils-base/src/main/java/com/pervasivecode/utils/stats/histogram/BucketSelector.java)

This object determines which histogram bucket a particular value belongs in.

#### [Histogram](stats-utils-base/src/main/java/com/pervasivecode/utils/stats/histogram/Histogram.java)

This is a data structure that holds frequency counts of values for use in a histogram.


#### [MutableHistogram](stats-utils-base/src/main/java/com/pervasivecode/utils/stats/histogram/MutableHistogram.java)
This is a mutable version of the `Histogram` interface, adding a method for counting additional values.

## Implementation Classes

### In package com.pervasivecode.utils.stats:

#### [HorizontalBarGraph](stats-utils-base/src/main/java/com/pervasivecode/utils/stats/HorizontalBarGraph.java)

A textual representation of a set of values, in the form of a bar graph growing rightward from an axis on left.

#### [SimpleDurationEstimator](stats-utils-base/src/main/java/com/pervasivecode/utils/stats/SimpleDurationEstimator.java)
This is a trivial `DurationEstimator` that just uses the total amount processed divided by the total time elapsed to estimate the rate (that is, it's blind to any short-term fluctuations in rate that may occur, and only examines the entire process).

### In package com.pervasivecode.utils.stats.histogram:

#### [BucketSelectors](stats-utils-base/src/main/java/com/pervasivecode/utils/stats/histogram/BucketSelectors.java)	
`BucketSelector` factory methods for basic bucketing strategies.

#### [ConcurrentHistogram](stats-utils-base/src/main/java/com/pervasivecode/utils/stats/histogram/ConcurrentHistogram.java)	
A thread-safe `Histogram` based on the [java.util.concurrent.atomic.AtomicLongArray](https://docs.oracle.com/javase/10/docs/api/java/util/concurrent/atomic/AtomicLongArray.html?is-external=true) class.

#### [FunctionBasedBucketSelector](stats-utils-base/src/main/java/com/pervasivecode/utils/stats/histogram/FunctionBasedBucketSelector.java)
A BucketSelector based on a pair of Functions, one of which converts values into bucket indices, the other of which converts bucket indices into upper-bound values.

#### [HistogramBucketCountFormatters](stats-utils-base/src/main/java/com/pervasivecode/utils/stats/histogram/HistogramBucketCountFormatters.java)

Factory methods for use with HistogramFormat.Builder.setBucketCountFormatter(BiFunction).


#### [HistogramFormat](stats-utils-base/src/main/java/com/pervasivecode/utils/stats/histogram/HistogramFormat.java)
Configuration for a `HistogramFormatter`.

#### [HistogramFormatter](stats-utils-base/src/main/java/com/pervasivecode/utils/stats/histogram/HistogramFormatter.java)
Format `Histogram` contents for a text display.


#### [Histograms](stats-utils-base/src/main/java/com/pervasivecode/utils/stats/histogram/Histograms.java)
Utility methods for working with `Histogram`s.

#### [ImmutableHistogram](stats-utils-base/src/main/java/com/pervasivecode/utils/stats/histogram/ImmutableHistogram.java)
An immutable representation of a `Histogram`.

#### [IrregularSetBucketSelector](stats-utils-base/src/main/java/com/pervasivecode/utils/stats/histogram/IrregularSetBucketSelector.java)
This `BucketSelector` counts values in buckets that have an irregular set of bucket upper bound values, such as {1, 5, 7}, that are most easily expressed explicitly, rather than by a formula that generates a series of upper bound values.

### In package com.pervasivecode.utils.stats.histogram.measure:

#### [ImmutableQuantityHistogram](stats-utils-measure-jsr363/src/main/java/com/pervasivecode/utils/stats/histogram/measure/ImmutableQuantityHistogram.java) _(only in stats-utils-measure-jsr363)_

This is an adapter to present a histogram of plain numeric types as a histogram whose type is a `Quantity`.

#### [QuantityBucketSelectors](stats-utils-measure-jsr363/src/main/java/com/pervasivecode/utils/stats/histogram/measure/QuantityBucketSelectors.java) _(only in stats-utils-measure-jsr363)_

`BucketSelector` factory methods for basic bucketing strategies, working with values that are instances of `Quantity<T>`.


## Example Code

### [WordCountHistogramExample](stats-utils-base/src/examples/java/com/pervasivecode/utils/stats/histogram/example/WordCountHistogramExample.java)

Demonstration of how to create, populate, and format the contents of a `Histogram` counting word lengths for each of the words in a text document.

### [PowerPlantCapacityHistogramExample](stats-utils-measure-jsr363/src/examples/java/com/pervasivecode/utils/stats/histogram/measure/example/PowerPlantCapacityHistogramExample.java)

Demonstration of how to create, populate, and format the contents of a `Histogram` counting `Quantity<Power>` values representing the electricity generation capacities of over 28,000 known power plants worldwide.

