FROM ubuntu:latest

RUN apt update
RUN apt install openjdk-17-jdk-headless -y

WORKDIR /server

ENTRYPOINT [ "./start.sh" ]
