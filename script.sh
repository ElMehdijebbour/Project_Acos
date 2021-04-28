#!/bin/bash
cd /mnt/hgfs/shared/

echo 'step 1 : generating public key from private key:'
openssl pkey -in rsaprivatekey.pem -pubout -out rsa-public.pem
echo 'step 2 :md5 has:'
openssl dgst -md5 -out hashfile.txt data.txt 
echo 'step 2 : signature genereation and verification'
openssl rsautl -sign -in hashfile.txt -inkey rsaprivatekey.pem -out sig.bin 
