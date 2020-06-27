# SER422: Lab 5
## Air Quality Monitoring (AQM) System
 **Original Program by Dr. Kevin Gary, Refactored by Cristi DeLeo**

## Basic Requirements
1. Replace the web API that accepts HTTP POSTs with a message-based Producer that **pushes messages to a Topic**.
2. Provide two Consumers that **(1) writes data to a relational database**, and **(2) publishes air quality alerts to registered subscribers based on region**.

## Update tomcat.home Variable in build.properties File
1. In **AQM_Apps/properties/**, update the **build.properties** file to reflect local installation of tomcat-8.5.55-8080.
2. In **AQM_REST/**, update the **build.properties** file to reflect local installation of tomcat-8.5.55-8080.

## Executing Task 1
1. Open seven terminal windows/tabs
2. Run/start MySQL Database *aqmtest* in terminal #1:
`$ mysql -u aqm -p`
`$ Enter password: <aqm276>`
`$ mysql> CREATE database aqmtest;`
`$ mysql> USE aqmtest;`
`$ mysql> SOURCE /<path>/<to>/lab5_cdeleo1/lab5-cdeleo1-task1/AQM_REST/sql/aqmtestdump061620.sql;`
3. Run Tomcat in terminal #2:
`$ cd /<path>/<to>/tomcat-8.5.55-8080/bin`
`$ ./startup.sh`
4. Run Apache ActiveMQ in terminal #3:
`$ cd /<path>/<to>/apache-activemq-5.15.8/bin`
`$ sudo ./activemq start`
5. Compile AQM_Common in terminal #4:
`$ cd /path/to/lab5_cdeleo1/lab5-cdeleo1-task1/AQM_Common`
`$ ant clean`
`$ ant compile`
6. Deploy AQM_REST in terminal #5:
`$ cd /path/to/lab5_cdeleo1/lab5-cdeleo1-task1/AQM_REST`
`$ ant clean`
`$ ant deploy`
7. Deploy AQM_Apps and start SimplePumpPublisherModel in terminal #6:
`$ cd /path/to/lab5_cdeleo1/lab5-cdeleo1-task1/AQM_Apps`
`$ ant clean`
`$ ant deploy`
`$ cd ./classes`
`$ java -cp .:../lib/*:classes edu.asupoly.heal.aqm.jms.SimplePumpPublisherModel Chat1 <username> <password>`
8. Start SimplePumpSubscriberModelWithListener in terminal #7:
`$ cd /path/to/lab5_cdeleo1/lab5-cdeleo1-task1/AQM_Apps/classes`
`$ java -cp .:../lib/*:classes edu.asupoly.heal.aqm.jms.SimplePumpSubscriberModelWithListener Chat1 <clientid> <username> <password>`
9. In terminal #6, publish JSON string:
`$ [{"coData": -2, "dateTime": "Fri Jun 12 13:27:18 MST 2020", "geoLongitude": -111.6823322, "co2Data": -1, "co2DeviceID": "UNKNOWN", "geoMethod": "Network", "type": "sensordrone", "pressureData": 96464, "tempData": 25, "geoLatitude": 33.2993071, "deviceId": "SensorDroneB8:FF:FE:B9:C3:FE", "humidityData": 32}]`
10. In terminal #7, verify message was recieved (terminal should show the JSON string and other text shown below):
`Message received: [{"coData": -2, "dateTime": "Fri Jun 12 13:27:18 MST 2020", "geoLongitude":` `-111.6823322, "co2Data": -1, "co2DeviceID": "UNKNOWN", "geoMethod": "Network", "type":` `"sensordrone", "pressureData": 96464, "tempData": 25, "geoLatitude": 33.2993071, "deviceId":` `"SensorDroneB8:FF:FE:B9:C3:FE", "humidityData": 32}]`
`Jun 23, 2020 12:19:50 AM edu.asupoly.heal.aqm.dmp.AQMDAOJDBCImpl init`
`INFO: Testing DAO Connection -- OK`
`obj instanceof JSONArray: true`
`Jun 23, 2020 12:19:50 AM edu.asupoly.heal.aqm.dmp.AQMDAOJDBCImpl importReadings`
`INFO: data type = sensordrone`
`dao.importReadings(jsonString): SERVER_DYLOS_IMPORT_SUCCESS`
`returnValue value: SERVER_DYLOS_IMPORT_SUCCESS`
11. Verify *aqmtest* DB was successfully updated by opening browser and navigating to http://localhost:8080/aqm/aqmimport.
12. In terminals #6 and #7, type *exit* to close connection.
13. In terminal #3, type `$ sudo ./activemq stop`
14. In terminal #2, type `$ ./shutdown.sh`
15. In terminal #1, type `$ mysql> quit;`

## Executing Original Program/Initial Setup
1. Modify /AQM_Common/properties/**dao.properties** file as shown below:
> MYSQL
> daoClassName=edu.asupoly.heal.aqm.dmp.AQMDAOJDBCImpl
> embedded derby jdbc properties
> jdbc.driver=com.mysql.jdbc.Driver
> this one has to be changed to point to a local directory on your system
> jdbc.url=jdbc:mysql://localhost:3306/**aqmtest**
> jdbc.user=**root**
> jdbc.passwd=**password**
2. Modify /path/to/AQM_Apps/**build.properties** file as shown below:
> tomcat.home=**/path/to/tomcat**
3. Modify /path/to/AQM_REST/**build.properties** file as shown below:
> tomcat.home=**/path/to/tomcat**
4. Install and setup MySQL:
`$ sudo apt-get install mysql-server`
`$ sudo mysql_secure_installation` Set root password=*password*
`$ Remove anonymous users?` Type *N* for 'No'
`$ Disallow root login remotely?` Type *Y* for 'Yes'
`$ Remove test database and access to it?` Type *N* for 'No'
`$ Reload privilege tables now?` Type *Y* for 'Yes'
`$ sudo mysql -u root -p`
`$ mysql> CREATE database aqmtest;`
`$ mysql> USE aqmtest;`
`$ mysql> SOURCE /path/to/AQM_REST/sql/aqmtestdump061620.sql`
5. Clean and deploy AQM_Apps:
`$ cd /path/to/AQM_Apps`
`$ ant clean`
`$ ant deploy`
6. Clean and compile AQM_Common:
`$ cd /path/to/AQM_Common`
`$ ant clean`
`$ ant compile`
7. Clean and deploy AQM_REST:
`$ cd /path/to/AQM_REST`
`$ ant clean`
`$ ant deploy`
8. Deploy Tomcat:
`$ cd /path/to/Tomcat/bin`
`$ ./startup.sh`
9. Open **http://localhost:8080/aqmapps/** in web browser
10. Navigate to **http://localhost:8080/aqm/aqmimport** to verify devices were imported properly from MySQL database **aqmtest**
11. Navigate to **http://localhost:8080/aqmapps/test.html** to verify POST writes properly to MySQL database **aqmtest**

