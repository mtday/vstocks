#!/bin/sh

cd "$(dirname $0)"
rm -f *.jks *.p12 *.crt *.pem *.pub

# Create a self signed key pair root CA certificate.
keytool -genkeypair -v \
  -alias localhost \
  -dname "CN=localhost, OU=vstocks, C=US" \
  -keystore keystore.p12 \
  -storetype PKCS12 \
  -storepass changeit \
  -keypass changeit \
  -keyalg RSA \
  -keysize 4096 \
  -ext BasicConstraints:"critical=ca:true" \
  -validity 9999

# Export the public certificate so that it can be used in trust stores.
keytool -export -v \
  -alias localhost \
  -file localhost.crt \
  -keystore keystore.p12 \
  -storetype PKCS12 \
  -storepass changeit \
  -keypass changeit \
  -rfc

# Import the public certificate into a trust store.
keytool -importcert \
  -file localhost.crt \
  -keystore truststore.p12 \
  -storetype PKCS12 \
  -storepass changeit \
  -alias localhost \
  -noprompt

