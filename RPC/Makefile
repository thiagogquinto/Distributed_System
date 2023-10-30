setup:
	pip install grpcio grpcio-tools protobuf bson pymongo

generate_python:
	cd pythoncode && python3 -m grpc_tools.protoc -I../ --python_out=. --grpc_python_out=. ../movie.proto

maven_install:
	cd javacode && sudo mvn install && mvn clean install

server_run:
	python3 pythoncode/server.py

