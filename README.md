# OxConfig

A modular and flexible utility for automatically setting up a JSON based config
file and tuning over NetworkTables.

Developed by [nab138](https://github.com/nab138). This version is intended for
use by FIRST teams using java WPILib (not endorsed by WPI).

Inteded for use with
[Shrinkwrap](https://github.com/nab138/shrinkwrap/) for
tuning and editng values live. OxConfig can run without it, but it is not
recommended.

Check out our [slideshow](https://docs.google.com/presentation/d/1QuVK_aaOHk0eIedKAgRVHNj8FFaLIJ0PjUWnntIh-NE/edit?usp=sharing)
for an intro to OxConfig.

## Getting Started

To install, view the installation guide below.

Check out the [wiki](https://github.com/FRCTeam3044/OxConfig/wiki) for help on
getting started. [Javadocs](https://frcteam3044.github.io/OxConfig/) are available online or in code as well.

## Installation

You will need to add the Github packages maven repository to the repositories
section in `build.gradle`

```gradle
maven {
    url = uri("https://maven.pkg.github.com/FRCTeam3044/OxConfig")
    credentials {
        username = "3044-Packages-Bot"
        password = "\u0067\u0068\u0070\u005f\u0038\u0055\u0068\u0037\u0061\u004f\u0062\u0049\u004a\u0041\u005a\u0045\u0059\u0073\u0041\u0055\u0033\u0063\u0041\u0037\u004f\u0065\u0070\u0037\u0053\u0074\u0073\u0058\u0058\u0059\u0031\u004e\u006e\u0056\u0030\u004a"
    }
}
```

After adding the repo, add the following to dependencies in your build.grade:
`implementation 'me.nabdev.oxconfig:oxconfig-wpi:1.3.0-SNAPSHOT'`

## Links

- [JSON-java](https://github.com/stleary/JSON-java)
- [allwpilib](https://github.com/wpilibsuite/allwpilib)
- [REV Lib](https://docs.revrobotics.com/sparkmax/software-resources/spark-max-api-information)
- [CTRE Lib](https://pro.docs.ctr-electronics.com/en/latest/)

Questions, suggestions, bug reports, or just want to chat? **Join the
[3044 Packages Discord](https://discord.gg/ypRWZGnW66) for updates, support,
discussion, and more!**
