# ArcGIS Runtime API for Java Samples

![Gradle build](https://github.com/Esri/arcgis-runtime-samples-java/workflows/Java%20CI%20with%20Gradle/badge.svg)

This repo contains a set of sample projects demonstrating how to accomplish various mapping and GIS tasks with the ArcGIS Runtime API for Java.

Browse the category directories to explore the samples. Each sample is an individual [Gradle](https://docs.gradle.org/current/userguide/userguide.html) project that can be run standalone. The Gradle buildscripts have tasks for running the application, building a jar, and distributing the app as a zip.

Installing Gradle is not necessary since each sample includes the Gradle wrapper.

Accessing Esri location services, including basemaps, routing, and geocoding, requires authentication using either an ArcGIS identity or an API Key:
 1. ArcGIS identity: An ArcGIS named user account that is a member of an organization in ArcGIS Online or ArcGIS Enterprise.
 2. API key: A permanent key that gives your application access to Esri location services. Visit your [ArcGIS Developers Dashboard](https://developers.arcgis.com/dashboard) to create a new API key or access an existing API key.

Note: *in the following instructions for setting the API key, if a `gradle.properties` file does not already exist in the `/.gradle` folder within your home directory, a Gradle task in the samples build.gradle file will generate one for you.*

## Instructions

### IntelliJ IDEA

1. Open IntelliJ IDEA and select _File > Open..._.
2. Choose one of the sample project directories (not the category folder) and click _OK_.
3. Select _File > Project Structure..._ and ensure that the Project SDK and language level are set to use Java 11.
4. Store your API key in the `gradle.properties` file located in the `/.gradle` folder within your home directory. The API key will be set as a Java system property when the sample is run.
   ```
   apiKey = yourApiKey
   ```
5. Open the Gradle view with _View > Tool Windows > Gradle_.
6. In the Gradle view, double-click the `run` task under _Tasks > application_ to run the app.

### Eclipse

1. Open Eclipse and select _File > Import_.
2. In the import wizard, choose _Gradle > Existing Gradle Project_, then click _Next_.
3. Choose one of the sample project directories (not the category folder) as the project root directory.
4. Click _Finish_ to complete the import.
5. Store your API key in the `gradle.properties` file located in the `/.gradle` folder within your home directory. The API key will be set as a Java system property when the sample is run.
   ```
   apiKey = yourApiKey
   ```
6. Open the Gradle Tasks view with _Window > Show View > Other... > Gradle > Gradle Tasks_.
7. In the Gradle Tasks view, double-click the `run` task under _{project_name} > application_ to run the app.

### Terminal

1. `cd` into one of the sample project directories (not the category folder).
2. Run `gradle wrapper` to create the Gradle Wrapper
3. Store your API key in the `gradle.properties` file located in the `/.gradle` folder within your home directory. The API key will be set as a Java system property when the sample is run.
4. Run `./gradlew run` on Linux/Mac or `gradlew.bat run` on Windows to run the app.

### Java 11
Java 11 users may find exceptions when running the project if their library path is still set for Oracle JDK 1.8 (see the [OpenJavaFX docs](https://openjfx.io/openjfx-docs/) for more information). A workaround for this is to add the following argument in the `run` task of the Gradle buildscript:
```
systemProperty "java.library.path", "C:\tmp"
```

### Offline sample data
Some samples require offline data. A `samples-data` directory will automatically download to the project root when the Gradle project is configured/imported.

## Requirements

See the ArcGIS Runtime API's [system requirements](https://developers.arcgis.com/java/reference/system-requirements/).

## Resources

* [ArcGIS Runtime API for Java](https://developers.arcgis.com/java/)  
* [Toolkit](https://github.com/Esri/arcgis-runtime-toolkit-java)
* [ArcGIS Blog](https://blogs.esri.com/esri/arcgis/)  
* [Esri Twitter](https://twitter.com/esri)  

## Issues

Find a bug or want to request a new feature?  Please let us know by submitting an issue.

## Contributing

Esri welcomes contributions from anyone and everyone. Please see our [guidelines for contributing](https://github.com/esri/contributing).

New to Git? Check out our [Working with Git](https://github.com/Esri/arcgis-runtime-samples-java/blob/master/WorkingWithGit.md) guide.

## Licensing

Copyright 2021 Esri

Licensed under the Apache License, Version 2.0 (the "License"); you may not 
use this file except in compliance with the License. You may obtain a copy 
of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
License for the specific language governing permissions and limitations 
under the License.

A copy of the license is available in the repository's license.txt file.
