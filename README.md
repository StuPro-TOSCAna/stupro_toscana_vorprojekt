# StuPro TOSCAna preliminary design study
[![Build Status](https://travis-ci.org/nfode/stupro_toscana_vorprojekt.svg?branch=master)](https://travis-ci.org/nfode/stupro_toscana_vorprojekt) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/46bbcc976f084a19a01300393554adf0)](https://www.codacy.com/app/nfode/stupro_toscana_vorprojekt?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=nfode/stupro_toscana_vorprojekt&amp;utm_campaign=Badge_Grade)
[![codecov](https://codecov.io/gh/nfode/stupro_toscana_vorprojekt/branch/master/graph/badge.svg)](https://codecov.io/gh/nfode/stupro_toscana_vorprojekt)


Open repository for the preliminary design study for TOSCAna.

This is the repository for the preliminary design study for the study project TOSCAna. The project consists of the following two subprojects:

## TOSCA2vSphere
TOSCA2vSphere is a command line tool developed in the preliminary design study for the study project TOSCAna. The purpose of this tool is to deploy a TOSCAlite model as it is definend in the corresponding archive.

## TOSCAlite
TOSCAlite is a very lightweight version of the OASIS TOSCA standard. It can be used to model a deployment based on Linux systems (mostly Ubuntu 16.04 LTS). You can read more about TOSCAlite in its documentation [here](doc/toscalite/).

## Project structure
- [`doc/`](doc/) - Documentation
- [`utils/`](utils/) - Scripts that help building and running the app
- [`phpapp/`](phpapp/) - Example php app
- [`example_models/`](example_models/) - Example TOSCAlite conform models
- [`src/main/java/`](src/main/java/) - Java source code
- [`src/main/resources/`](src/main/resources/) - Resources needed for the program, f.ex. logger properties
- [`src/test/java/`](src/test/java/) - Java tests
- [`src/test/resources/`](src/test/resources/) - Resources needed to test

## Table of Contents
* [StuPro TOSCAna preliminary design study](#stupro-toscana-preliminary-design-study)
    * [TOSCA2vSphere](#tosca2vsphere)
    * [TOSCAlite](#toscalite)
    * [Project structure](#project-structure)
    * [Obtaining TOSCA2vSphere](#obtaining-tosca2vsphere)
      * [Prebuild package](#prebuild-package)
      * [Build from source](#build-from-source)
    * [Usage](#usage)
    * [Tools](#tools)
      * [Util scripts](#util-scripts)
      * [IntelliJ](#intellij)


## Obtaining TOSCA2vSphere

### Prebuild package

Download a prebuild package.

**TODO** *add link to latest release prebuild package*

### Build from source
Steps to build **TOSCA2vSphere** from sources:
1. Clone the project.

    `git clone https://github.com/nfode/stupro_toscana_vorprojekt.git`
2. Enter the project root directory.

    `cd stupro_toscana_vorprojekt`
3. Build the project with:

    `
    mvn package
    `
4. You can find the generated jar file in the target folder.

## Usage
How to use **TOSCA2vSphere**:

1. Start **TOSCA2vSphere** with:

    `
    java -jar de.toscana.transformator-1.0-SNAPSHOT.jar model.zip
    `

    Replace *model.zip* with the file you want to open.

    **IMPORTANT**: The topology described in the model.xml of the .zip archive has to match the requirements described in the TOSCAlite specification.

2. If parsing the file was successful you can now use the CLI:
    1. You can create everything needed to run your model if you enter:

        `
        create
        `
    2. if the model was created you can start it with:

        `
        start
        `
    3. and finally there is the possibility to stop it with:

        `
        stop
        `

    It is mandatory that `create` has to be called before `start` and `start` has to be called before `stop`. If not, there will be an error.

## Tools

Tools that are used in this project.

- IDE: [IntelliJ](https://www.jetbrains.com/idea/)
- Project management: [ZenHub](https://www.zenhub.com/)
- CI: [TravisCI](https://travis-ci.org/nfode/stupro_toscana_vorprojekt)
- Code analysis: [Codacy](https://www.codacy.com/app/nfode/stupro_toscana_vorprojekt/dashboard)
- Code coverage: [Codecov](https://codecov.io/gh/nfode/stupro_toscana_vorprojekt), [Get browser extension](https://github.com/codecov/browser-extension)

### Util scripts

In the folder [`util`](util/) you can find util scripts for operating systems with **bash**. 
- `utils/build_with_test` - builds with tests
- `utils/build_without_test` - builds without tests for test purposes
- `utils/run_with_example_phpapp` - starts the CLI with the example php app


### IntelliJ

In the folder [`doc/config/IntelliJ`](doc/config/IntelliJ/) you can find a class template named `Class.java`. This template adds automatically everything you need for logging to the header when you create a new class.
If you want to use this file, copy it to the folder `.idea/fileTemplates/internal/Class.java`.  
