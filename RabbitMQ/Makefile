setup: 
	npm install csv csv-parser amqplib
	pip install pika questionary

maven_build:
	mvn clean && mvn compile 

class_run:
	mvn exec:java -D"exec.mainClass"="Classifier"
