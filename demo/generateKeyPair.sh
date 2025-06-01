#!/usr/bin/env bash

openssl genrsa -out private_org_key.pem 4096
openssl rsa -pubout -in private_org_key.pem -out public_key.pem

# convert private key to pkcs8 format in order to import it from Java
openssl pkcs8 -topk8 -in private_org_key.pem -inform pem -out private_key.pem -outform pem -nocrypt