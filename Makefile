default: 
	gradle archive

run:
	gradle run

jar:
	gradle jar

runjar: jar
	mv build/libs/jeedup.jar .
	java -jar jeedup.jar

dist:
	gradle archive

clean:
	gradle clean
	rm -f jeedup.jar
