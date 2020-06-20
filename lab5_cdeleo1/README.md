# SER422: Lab 5
## Air Quality Monitoring (AQM) System
### Original Program by Dr. Kevin Gary, Refactored by Cristi DeLeo

## Basic Requirements
1. Replace the web API that accepts HTTP POSTs with a message-based Producer that **pushes messages to a Topic**.
2. Provide two Consumers that **(1) writes data to a relational database**, and **(2) publishes air quality alerts to registered subscribers based on region**.

## Executing Original Program

1. Modify /AQM_Common/properties/**dao.properties** file as shown below:
> # MYSQL
> daoClassName=edu.asupoly.heal.aqm.dmp.AQMDAOJDBCImpl
> # embedded derby jdbc properties
> jdbc.driver=com.mysql.jdbc.Driver
> # this one has to be changed to point to a local directory on your system
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

