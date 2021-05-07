#!/bin/bash
cd /mnt/hgfs/shared/acos/
echo '1-Generate signature\n2-Verify signature\n3-Generate Certificate'
read INPUT_STRING
  case $INPUT_STRING in
	1)
		echo 'step 1 : MD5 data hashing :'
		openssl dgst -md5 -out hashfile.txt data.txt
		echo 'step 2 : generating private key :'
		openssl genrsa -out rsaprivatekey.pem 512
		echo 'step 3 : generating public key from private key:'
		openssl pkey -in rsaprivatekey.pem -pubout -out rsapublickey.pem 
		echo "Step 4 : generating signature "
		openssl rsautl -sign -in hashfile.txt -inkey rsaprivatekey.pem -out signature.bin
		;;
	2)
		echo 'signature  verification'
		openssl rsautl -verify -pubin -inkey rsapublickey.pem -in signature.bin -out verifyhash.txt
		break
		;;
	3)
		echo "Generating certificate"
		openssl req -new -x509 -key authorityprivatekey.pem >cert.pem
		openssl x509  -in cert.pem -text -noout >certificate.txt
		break
		;;
	*)
		echo "Sorry, I don't understand"
		;;
  esac



