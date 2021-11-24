#!/bin/bash

iopenssl genrsa -out cakey.pem 360
openssl req -new -md5 -key cakey.pem -out cacsr.csr
openssl req -x509 -md5 -days 365 -key cakey.pem -in cacsr.csr -out cacertificate.pem