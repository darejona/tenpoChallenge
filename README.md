# tenpoChallenge
## This project is to resolve Tenpo's challenge

In order to run this project you just need to install docker on your computer and download the docker-compose.yml file.

Then execute in your local command line: **docker-compose up**

There are different environment variables you can set to control the microservices behavior:

msvc-sum: <p>
> -PORT: 8001  (microservice port) <p>
-DB_HOST: postgres14:5432  (hostname and database port) <p>
-DB_DATABASE: msvc_sum  (database name to be created) <p>
-DB_USERNAME: postgres  (postgres database user) <p>
-DB_PASSWORD: postgres  (postgres database password) <p>
-PERCENTAGE_URL: http://msvc-percentage:8002 (percentage microservice internal url) <p>
-REQUEST_PER_MINUTE: 3 (amount of request accepted per minute by the /sum endpoint) <p>
-PERCENTAGE_API_MAX_ATTEMPTS: 3  (number of retries to communicate with the percentages microservice) <p>
-PERCENTAGE_API_POLLING_FREQUENCY: 1800000 (frequency with which new percentages are queried in milliseconds. Ej. 1800000 = 30 minutos) <p>
<p>
      
msvc-percentage: <p>
> -PORT: 8002   (microservice port) <p>
-PERCENTAGE_RANDOM_GENERATION_FREQUENCY: 1800000  (frequency at which new random percentages are generated in milliseconds) <p>
<p>

### In general we have 3 endpoints: <p>

> GET to url_base_msvc_percentage/percentage -->  generates a random value between 0 and 20, with the configured frequency <p>
GET to url_base_ msvc-sum/sum?value1=5&value2=5  --> adds the indicated values (value1 and value2) plus the last percentage obtained <p>
GET to url_base_ msvc-sum/callHistory?page=0&size=50&sort=id   --> returns the history of calls made to the msvc of percentages, paginated (page is the page number starting from 0, size is the number of records for each page, and sort is the attribute by which the records are sorted, normally it is ' id') <p>

