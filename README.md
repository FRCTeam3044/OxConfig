# OxConfig
A modular and flexible utility for automatically setting up a YAML based config file and tuning over NetworkTables.

_Developed by [nab138](https://github.com/nab138), this version is intended for use by FIRST teams using WPILib (not endorsed by WPI). A standard java version is also available (will be coming soon)._

**This module is in very early beta and breaking changes will be made frequently. It is not considered stable yet so user beware.**

Check out our [roadmap](https://github.com/FRCTeam3044/OxConfig/blob/main/ROADMAP.md)!

## Installation
You will need to add the Github packages maven repository to the repositories section in `build.gradle`
```gradle
maven {
    url = uri("https://maven.pkg.github.com/FRCTeam3044/OxConfig")
    credentials {
        username = "username"
        password = "token"
    }
}
```
If you don't have a token, [contact nab138](mailto:nab@nabdev.me). This package will hopefully be put on maven central after beta testing is over.

After adding the repo, add the following to dependencies in your build.grade:
`implementation 'me.nabdev.oxconfig:oxconfig-wpi:0.0.3'`

## Getting Started
Check out the [wiki](https://github.com/FRCTeam3044/OxConfig/wiki) for help on getting started.

## Links
- [eo-yaml](https://github.com/decorators-squad/eo-yaml)
- [JSON-java](https://github.com/stleary/JSON-java)
- [allwpilib](https://github.com/wpilibsuite/allwpilib)
- [REV Lib](https://docs.revrobotics.com/sparkmax/software-resources/spark-max-api-information)
- [CTRE Lib v5](https://v5.docs.ctr-electronics.com/en/stable/)
