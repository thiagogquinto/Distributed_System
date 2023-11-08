setup: 
	npm install csv csv-parser amqplib
	pip install pika questionary

maven_build:
	mvn clean && mvn compile 

run_server:
	mvn exec:java -D"exec.mainClass"="Collector"
	node classifier.js

run_client:
	python3 client.py