default:
	mvn clean compile assembly:single

run:
	gradle run

runOld:
	java -jar target/jweb-1.0-SNAPSHOT-jar-with-dependencies.jar

jar:
	gradle jar

runjar: jar
	mv build/libs/jeedup.jar .
	java -jar jeedup.jar

clean:
	gradle clean
	rm -f jeedup.jar
