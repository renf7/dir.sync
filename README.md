# dir.sync

WHAT IS IT ?

Files sharing tool and directory synchronizer using Apache Kafka. It synchronizes choosen directories for one user or multiple users. Evey user has dedicated queue in kafka. Thanks this new user will have never waited for files transfer. It works with files with every size, thanks file fregmentation to 0.9 Mb. 


HOW TO USE IT:

1. install OpenJDK 11 (recomended) or Oracle JDK version 11. Add environment variable to JAVA_HOME to JDK 11 java directory. Add environment variable to PATH to ${JAVA_HOME}/bin. Remove previous java from PATH if exists.
2. download and unpack newest maven for your OS. Add to unpacked apache-maven-xxx/bin folder to environment variable PATH
3. download and install newest kafka if needed also proper version scala
4. run kafka zookeper as bellow:
bin/zookeeper-server-start.sh /home/rafal/windows/Programowanie/praca_dodatkowa/dir_sync/dir.sync/dir.sync.server/src/main/resources/zooeper.properties 
5. Setup proper disks in /home/rafal/windows/Programowanie/praca_dodatkowa/dir_sync/dir.sync/dir.sync.server/src/main/resources/kafka-server.properties. Property name is log.dirs
6. run kafka as bellow:
bin/kafka-server-start.sh /home/rafal/windows/Programowanie/praca_dodatkowa/dir_sync/dir.sync/dir.sync.server/src/main/resources/kafka-server.properties > kafka-server.log
7. go to location with unpacked project and execute:
 mvn clean install
8. Start server with command as bellow:
java -jar dir.sync.server/target/dir.sync.server-1.0.jar
9. Start client with command as bellow:
java -jar dir.sync.client/target/dir.sync.client-1.0.jar maciek /home/rafal/tmp/input
