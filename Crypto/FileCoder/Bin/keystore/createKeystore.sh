#!/bin/bash

openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -days 365
cat key.pem cert.pem > keycert.pem
openssl pkcs12 -export -in keycert.pem -out keystore.p12 -name name123 -noiter -nomaciter
