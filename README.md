# OxConfig
OxConfig is a modular and flexible utility for automatically setting up a YAML based config file and tuning over networktables.

## Installation
You will need to add the github packages maven repository to the repositories section in `build.gradle`
```gradle
maven {
    url = uri("https://maven.pkg.github.com/FRCTeam3044/AutoConfig")
    credentials {
        username = "username"
        password = "token"
    }
}
```
If you don't have a token, [contact nab138](mailto:nab@nabdev.me). This package will hopefully be put on maven central after beta testing is over.

After adding the repo, add the folowing to dependencies in your build.grade:
`implementation 'me.nabdev.autoconfig:autoconfig:0.0.2'`

## Quickstart
Check out the [wiki](https://github.com/FRCTeam3044/AutoConfig/wiki) for help on getting started.

## Links
- [eo-yaml](https://github.com/decorators-squad/eo-yaml)
- [JSON-java](https://github.com/stleary/JSON-java)
- [allwpilib](https://github.com/wpilibsuite/allwpilib)
- [REV Lib](https://docs.revrobotics.com/sparkmax/software-resources/spark-max-api-information)
