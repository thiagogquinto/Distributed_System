setup: 
	npm install csv csv-parser amqplib
	pip install pika questionary

maven_build:
	mvn clean && mvn compile 

class_run:
	node collector.js
	mvn exec:java -D"exec.mainClass"="Classifier"
