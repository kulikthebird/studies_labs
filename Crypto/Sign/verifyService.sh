#!/bin/bash

openssl dgst -md5 -verify <(openssl x509 -in cacertificate.pem -pubkey -noout) -signature grade.sign grade.txt