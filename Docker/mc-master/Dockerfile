FROM ubuntu:latest

RUN apt update
RUN apt install openjdk-17-jdk-headless -y

WORKDIR /master

ENTRYPOINT [ "./start.sh" ]
