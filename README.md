# CyanCruise

金蝶云苍穹 Cosmic 二开工程。

## Development Environment

- JDK: 17
- Gradle: use the checked-in wrapper, `gradlew.bat`
- Cosmic home: configured by `systemProp.cosmic_home` in `gradle.properties`

On Windows, make sure `JAVA_HOME` points to a JDK 17 installation before building:

```powershell
$env:JAVA_HOME = 'D:\Program Files\BellSoft\LibericaJDK-17'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat clean build
```
