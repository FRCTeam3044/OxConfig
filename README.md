# OxConfig

A modular and flexible utility for automatically setting up a JSON based config
file and tuning over NetworkTables.

Developed by [nab138](https://github.com/nab138). This version is intended for
use by FIRST teams using java WPILib (not endorsed by WPI).

Inteded for use with
[AdvantageScope-3044](https://github.com/FRCTeam3044/AdvantageScope-3044/) for
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
            username = "Mechanical-Advantage-Bot"
            password = "\u0067\u0068\u0070\u005f\u006e\u0056\u0051\u006a\u0055\u004f\u004c\u0061\u0079\u0066\u006e\u0078\u006e\u0037\u0051\u0049\u0054\u0042\u0032\u004c\u004a\u006d\u0055\u0070\u0073\u0031\u006d\u0037\u004c\u005a\u0030\u0076\u0062\u0070\u0063\u0051"
    }
}
```

After adding the repo, add the following to dependencies in your build.grade:
`implementation 'me.nabdev.oxconfig:oxconfig-wpi:1.2.1-SNAPSHOT'`

## Links

- [JSON-java](https://github.com/stleary/JSON-java)
- [allwpilib](https://github.com/wpilibsuite/allwpilib)
- [REV Lib](https://docs.revrobotics.com/sparkmax/software-resources/spark-max-api-information)
- [CTRE Lib](https://pro.docs.ctr-electronics.com/en/latest/)

Questions, suggestions, bug reports, or just want to chat? **Join the
[3044 Packages Discord](https://discord.gg/ypRWZGnW66) for updates, support,
discussion, and more!**
