#!/bin/bash

openssl genrsa -out cakeySec.pem 2048
openssl req -new -sha256 -key cakeySec.pem -out cacsrSec.csr
openssl req -x509 -sha256 -days 365 -key cakeySec.pem -in cacsrSec.csr -out cacertSec.pem