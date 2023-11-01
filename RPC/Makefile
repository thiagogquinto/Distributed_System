setup:
	pip install grpcio grpcio-tools protobuf bson pymongo

generate_python:
	cd pythoncode && python3 -m grpc_tools.protoc -I../ --python_out=. --grpc_python_out=. ../movie.proto

maven_build:
	cd javacode && mvn clean && mvn compile

server_run:
	python3 pythoncode/server.py

client_run:
	cd javacode && mvn exec:java -D"exec.mainClass"="Client"


