default:
	mvn clean compile assembly:single

run:
	gradle run

runOld:
	java -jar target/jweb-1.0-SNAPSHOT-jar-with-dependencies.jar

jar:
	gradle jar

runjar: jar
	java -jar build/libs/jweb.jar

clean:
	gradle clean
