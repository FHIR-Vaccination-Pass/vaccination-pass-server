#!/usr/bin/env bash

MKCERT_CAROOT=$(mkcert -CAROOT)

cp "$MKCERT_CAROOT/rootCA.pem" .
cp ./cacerts ./rootCA.p12
keytool -importcert -storetype PKCS12 -keystore rootCA.p12 -storepass changeit -alias rootCA -file rootCA.pem -noprompt

pushd ./config > /dev/null
mkcert -pkcs12 -p12-file quarkus-keystore.p12 localhost 127.0.0.1 ::1
popd > /dev/null

cat > .env <<EOF
QUARKUS_HTTP_SSL_CERTIFICATE_KEY_STORE_FILE=$PWD/config/quarkus-keystore.p12
QUARKUS_HTTP_SSL_CERTIFICATE_KEY_STORE_PASSWORD=changeit
QUARKUS_HTTP_SSL_CERTIFICATE_TRUST_STORE_FILE=$PWD/rootCA.p12
QUARKUS_HTTP_SSL_CERTIFICATE_TRUST_STORE_PASSWORD=changeit
EOF

echo "Development certificates successfully generated! Please add the follwing to VM Options in IntelliJ:"
echo
echo "-Djavax.net.ssl.trustStore=$PWD/rootCA.p12"
echo
