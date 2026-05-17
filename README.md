# CyanCruise

金蝶云苍穹 Cosmic 二开工程。

## Development Environment

- JDK: 1.8
- Gradle: use the checked-in wrapper, `gradlew.bat`
- Cosmic home: configured by `systemProp.cosmic_home` in `gradle.properties`

On Windows, make sure `JAVA_HOME` points to a JDK 8 installation before building:

```powershell
$env:JAVA_HOME = 'F:\kingdee\ENV\jdk'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat clean build
```
