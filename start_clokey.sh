#!/bin/bash
lein ring server &
cd /electron-quick-start && npm start
