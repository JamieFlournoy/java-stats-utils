# Pervasive Code's Java Statistics Utilities

This library includes classes for building and formatting histograms, and  for estimating task completion.

### Two libraries

This repository contains two Gradle subprojects, each of which generates a library published as a separate Maven artifact:

`com.pervasivecode:stats-utils:0.9`

and

`com.pervasivecode:stats-utils-measure-jsr363:0.9`.

The `stats-utils-measure-jsr363` is an extension of, and therefore depends on, the `stats-utils` library.

The `stats-utils-measure-jsr363` library is separate because it provides additional classes compatible with the [JSR 363 Units of Measurement API](https://jcp.org/en/jsr/detail?id=363), which provides for a type-safe Java representation of quantities with units (such as "a mass of 8 kilograms", as opposed to a plain `int` value of 8). Because of this, `stats-utils-measure-jsr363` has additional dependencies on several JSR-363-related libraries. If you don't want to use those classes, then you don't need to depend on the `stats-utils-measure-jsr363` library from your project, and can just depend on the `stats-utils` library.

## Overview of included classes

Javadocs are available on `javadoc.io`:

For stats-utils:
[![Javadocs](https://www.javadoc.io/badge/com.pervasivecode/stats-utils.svg)](https://www.javadoc.io/doc/com.pervasivecode/stats-utils)

For stats-utils-measure-jsr363:
[![Javadocs](https://www.javadoc.io/badge/com.pervasivecode/stats-utils-measure-jsr363.svg)](https://www.javadoc.io/doc/com.pervasivecode/stats-utils-measure-jsr363)

See the separate [OVERVIEW.md](OVERVIEW.md) file for a description of what interfaces and classes are included. (Overview content is taken from class Javadoc comments, so there's no need to read both.)

## How to use it in your code

See the [Example Code](OVERVIEW.md#example-code) section in [OVERVIEW.md](OVERVIEW.md) for details.



