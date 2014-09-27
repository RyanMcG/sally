#!/bin/bash

git pull
lein uberjar
sudo java -jar target/sally.jar

