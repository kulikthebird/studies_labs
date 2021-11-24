#!/bin/bash

openssl dgst -md5 -sign cakey.pem -out grade.sign grade.txt