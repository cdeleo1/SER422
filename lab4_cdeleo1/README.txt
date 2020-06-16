From each subdirectory (lab4-cdeleo1-app, lab4-cdeleo1-calc, lab4-cdeleo1-map), run:
	- ant deploy
	- sudo docker build -tag ./
From lab4-cdeleo1-app, run:
	- sudo docker run -p 8001:8080 <image>
	- sudo docker cp gradeapp.war <container>:/usr/local/tomcat/webapps
From lab4-cdeleo1-calc, run:
	- sudo docker run -p 8002:8080 <image>
	- sudo docker cp calc.war <container>:/usr/local/tomcat/webapps
From lab4-cdeleo1-map, run:
	- sudo docker run -p 8003:8080 <image>
	- sudo docker cp map.war <container>:/usr/local/tomcat/webapps

From Web Browser, navigate to:
	- localhost:8001/gradeapp
	- localhost:8002/calc
	- localhost:8003/map

From each subdirectory (lab4-cdeleo1-app, lab4-cdeleo1-calc, lab4-cdeleo1-map), change:
	- build.properties file to appropriate tomcat.dir path
