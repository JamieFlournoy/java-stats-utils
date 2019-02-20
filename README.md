# Pervasive Code's Java Statistics Utilities

This library includes classes for building and formatting histograms, and  for estimating task completion.

### Two libraries

This repository contains two Gradle subprojects, each of which generates a library published as a separate Maven artifact:

`com.pervasivecode:stats-utils:1.0`

and

`com.pervasivecode:stats-utils-measure-jsr363:1.0`.

The `stats-utils-measure-jsr363` is an extension of, and therefore depends on, the `stats-utils` library.

The `stats-utils-measure-jsr363` library is separate because it provides additional classes compatible with the [JSR 363 Units of Measurement API](https://jcp.org/en/jsr/detail?id=363), which provides for a type-safe Java representation of quantities with units (such as "a mass of 8 kilograms", as opposed to a plain `int` value of 8). Because of this, `stats-utils-measure-jsr363` has additional dependencies on several JSR-363-related libraries. If you don't want to use those classes, then you don't need to depend on the `stats-utils-measure-jsr363` library from your project, and can just depend on the `stats-utils` library.

## Overview of included classes

Javadocs are available on `javadoc.io`:

For stats-utils:
[![Javadocs](https://www.javadoc.io/badge/com.pervasivecode/stats-utils.svg)](https://www.javadoc.io/doc/com.pervasivecode/stats-utils)

For stats-utils-measure-jsr363:
[![Javadocs](https://www.javadoc.io/badge/com.pervasivecode/stats-utils-measure-jsr363.svg)](https://www.javadoc.io/doc/com.pervasivecode/stats-utils-measure-jsr363)

See the separate [OVERVIEW.md](OVERVIEW.md) file for a description of what interfaces and classes are included. (Overview content is taken from class Javadoc comments, so there's no need to read both.)

## Including it in your project

### Stats-utils:

Use groupId `com.pervasivecode`, name `stats-utils`, version `1.0` in your build tool of choice.

### Stats-utils-measure-jsr363:

Use groupId `com.pervasivecode`, name `stats-utils-measure-jsr363`, version `1.0` in your build tool of choice.


### Gradle Example

If you are using Gradle 4.x, put this in your build.properties file:

```
// in your build.gradle's repositories {} block:
    mavenCentral();

// in your build.gradle's dependencies {} block:
    implementation 'com.pervasivecode:stats-utils:1.0'

    // and optionally
    implementation 'com.pervasivecode:stats-utils-measure-jsr363:1.0'

// or, if you prefer the separated group/name/version syntax:
    implementation group: 'com.pervasivecode', name: 'stats-utils', version: '1.0'

    // and optionally
    implementation group: 'com.pervasivecode', name: 'stats-utils-measure-jsr363', version: '1.0'
```




## How to use it in your code

See the [Example Code][] section in [OVERVIEW.md](OVERVIEW.md) for details.



## How to use it in your code

See the [Example Code][] section in [OVERVIEW.md](OVERVIEW.md) for details.

## Contributing

See [DEVELOPERS.md](DEVELOPERS.md) and [GRADLE_INTRO.md](GRADLE_INTRO.md) if you want to build and hack on the code yourself.


## Copyright and License

Copyright © 2018 Jamie Flournoy.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.


[example code]: OVERVIEW.md#example-code
