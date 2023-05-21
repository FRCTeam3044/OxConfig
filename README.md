# OxConfig
A modular and flexible utility for automatically setting up a YAML based config file and tuning over NetworkTables.

_Developed by [nab138](https://github.com/nab138), this version is intended for use by FIRST teams using WPILib (not endorsed by WPI). A standard java version that doesn't depend on WPIlib will be coming soon._

**This module is in very early beta and breaking changes will be made frequently. It is not considered stable yet so user beware.**

Inteded for use with [AdvantageScope-3044](https://github.com/FRCTeam3044/AdvantageScope-3044/) for tuning and editng values live. OxConfig can run without it, but it is not recommended.

Questions, suggestions, bug reports, or just want to chat? Come join us on our [Discord!](https://discord.gg/aBMPrADRCm)

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
If you don't have a token, follow the guide [here](https://docs.github.com/en/enterprise-server@3.4/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token) (On step 3 and 4, you will need to select "Tokens (classic)". You will need to grant the token the `read:packges` scope before it can be used. (After release, we will have a bot of some sort to remove this process from installation)

After adding the repo, add the following to dependencies in your build.grade:
`implementation 'me.nabdev.oxconfig:oxconfig-wpi:0.0.8'`

## Getting Started
Check out the [wiki](https://github.com/FRCTeam3044/OxConfig/wiki) for help on getting started.

## Links
- [eo-yaml](https://github.com/decorators-squad/eo-yaml)
- [JSON-java](https://github.com/stleary/JSON-java)
- [allwpilib](https://github.com/wpilibsuite/allwpilib)
- [REV Lib](https://docs.revrobotics.com/sparkmax/software-resources/spark-max-api-information)
- [CTRE Lib v5](https://v5.docs.ctr-electronics.com/en/stable/)
