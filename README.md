# SITA_TEST_TASK

## Overview
`SITA_TEST_TASK` is a Spring Boot application that processes files in a specified directory.
The application polls a folder for new files every 5 seconds.
If a new file is found, it sums all the numbers in the file and creates a new file containing the resulting value in the output directory.
When the input file is successfully processed it is moved to the processed directory.
If an error occurs while processing the input file then the input file is moved to error directory.

## Dependencies
- Java 17
- Maven
- Tomcat server (embedded)
- Mockito and JUnit5 (Unit Testing)
- Lombok
- Spring Integration Framework (Integration File Flow)

## Components
- File Integration Config : Handles Integration Flow and polls a folder(input directory) for new text files every 5 seconds
- User Request Handler : The service process the input files. Either writes the output to output file in the output directory and move input file to processed files directory or if an error occurs while computing then moves the input file to error files directory.

## Building the Project
1. Clone the repository:
   ```bash
   git clone https://github.com/simranaggarwal1999/SITA_TEST_TASK.git
   cd SITA_TEST_TASK
2. To build the project, navigate to the project root directory and run:
   ```bash
   mvn clean install
3. To generate war file, run:
   ```bash
   mvn package
