docker-compose down
call mvn clean package -DskipTests
docker ps
docker image rm alexbakker/carlease
docker build -t alexbakker/carlease .
docker-compose up