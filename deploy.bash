#!/bin/bash

git pull
sudo lein with-profile uberjar run # screw it, we tried to do the right thing :'(
