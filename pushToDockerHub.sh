#!/bin/sh
if [[ "$(docker images -q megar1ck/wires-backend:latest 2> /dev/null)" != "" ]]; then
  docker image rm megar1ck/wires-backend
fi
docker build -t megar1ck/wires-backend .
docker image push megar1ck/wires-backend
docker image rm megar1ck/wires-backend
