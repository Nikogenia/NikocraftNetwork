FROM ubuntu:latest

RUN apt update

RUN apt install htop -y
RUN apt install curl -y

RUN apt install openjdk-17-jdk-headless -y

WORKDIR /proxy

ENTRYPOINT [ "./start.sh" ]
