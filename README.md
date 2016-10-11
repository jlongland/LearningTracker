# Learning Tracker

## Introduction
TODO

## Working architecture

The working architecture of the Learning Tracker has three components as shown in the figure below.
1. Local component - computing the information to be displayed on the widget based on the data extracted from the trace logs of learners (Java8).
2. Server backend - hosting a Tomcat servlet that generates the Learning Tracker script for each learner when requests are made from the edX course pages (Java8).
3. edX component - integrating the Learning Tracker on edX course pages (JavaScript).

![Technical architecture](images/LT_working_architecture.svg)

## 1. Local component - metric calculation
TODO

## 2. Server backend - data storing and script generation
The server backend serves two purposes:
a. storing online for easier access the data that is to be displayed on the widget in a MySQL database
b. serving HTTPS requests from the edX pages 



The code for generating the metric values and the script are customizable for every run of the experiment. The current implementation calculates 15 metrics. The metrics to be displayed on the Learning Tracker are selected in the `initialize` method of the `MetricComputation` class.

The current version is customized for running experiments on the PreCalc MOOC of TU Delft on edX. All the files needed to calculate the metrics for PreCalc course are uploaded in PreCalcLT.zip.

## Step 0: Calculating thresholds - values of the average graduates.
Thresholds are calculated using the method `computeThreshold` in the class `MetricComputation`. The results will be placed in the folder `thresholds` and used in the subsequent steps.

## Step 1: Calculate values for upload for a new week.
1. The .zip file contains the jar with the metric generation application and the structure of the files as required by the application. The files paths in the code are for a run on a Windows machine.
2. Create a new folder "weekX". 
3. Inside folder "weekX", create another folder "data".
4. Input files have the extension .csv and should be placed inside the "data" folder.
5. Run "java -jar PreCalcLT.jar precalc X precalc/". (precalc X and precalc/ are the three parameters for the .jar. First one is the name of the course, X is the number of the week and the last one is the path to the folder that contains the "weekX" folders)
7. The console shows information about how much data was read (learners, quizzes, videos... etc).
8. Results are placed in the folder "precalc/weekX/metrics/". Data ready for upload is in the file "PRECALC_week4_for_database.csv"
9. For uploading on the mySQL database, the first row (header of the table) should be delete.

## Step 2: Upload data to the database on the server.
The file PRECALC_week4_for_database.csv is uploaded on idxmooc.ewi.tudelft.nl server and add it to the learning_tracker database.

## Step 3: Integration on edX.
The code in the folder `edX integration` is inserted into edX as JavaScript script in a Raw HTML component on the MOOC pages.
The Learning Tracker script is loaded as an external script from the server through HTTPS requests. The HTTPS request parameters are the anonymous ID of the learners and the week for which data is requested. 

##Step 4: Server backend
The server backend is implemented as a Tomcat servlet that receives HTTPS requests and responds with the generated Learning Tracker script as a string. The data is stored in a mySQL database and updated weekly.


