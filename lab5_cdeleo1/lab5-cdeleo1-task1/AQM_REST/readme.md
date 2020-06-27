REST API
-----
Base URI: http://localhost:8080/aqm/rest
 
* GET 

``/sensors``

return all sensor types, e.g. ["Dylos","Sensordrone"]

``/sensors/{sensorType}``

return all deviceId in {sensorType} ,e.g. /sensors/Dylos, return ["aqm1","aqm2","aqm3","aqm0"]


``/data``

``/data?num=10&start=2014032707265&end=20140328072344``

return each type's lastest 10 data in GeoJSON (now Dylos and Sensordrone)

``/data/{sensorType}``

``/data/Sensordrone?num=10&start=2014032707265&end=20140328072344``

return latest data by {sensorType} in GeoJSON

``/data/{sensorType}/{deviceId}``

``/data/Dylos/aqm2?num=10&start=2014032707265&end=20140328072344``

return latest data by {deviceId} in GeoJSON

e.g. http://localhost:8080/aqm/rest/data/Dylos?start=20140327072654&end=20140328072344
http://localhost:8080/aqm/rest/data/Dylos?start=20140327072654&end=20140328072344&num=3

