$MKCERT_CAROOT= mkcert -CAROOT

Copy-Item "$MKCERT_CAROOT/rootCA.pem" .
Copy-Item ./cacerts ./rootCA.p12
keytool -importcert -storetype PKCS12 -keystore rootCA.p12 -storepass changeit -alias rootCA -file rootCA.pem -noprompt

Push-Location ./config
mkcert -pkcs12 -p12-file quarkus-keystore.p12 localhost 127.0.0.1 ::1
Pop-Location

$MY_PATH = $PWD -replace '\\', '/'

Write-Output "
QUARKUS_HTTP_SSL_CERTIFICATE_KEY_STORE_FILE=$MY_PATH/config/quarkus-keystore.p12
QUARKUS_HTTP_SSL_CERTIFICATE_KEY_STORE_PASSWORD=changeit
QUARKUS_HTTP_SSL_CERTIFICATE_TRUST_STORE_FILE=$MY_PATH/rootCA.p12
QUARKUS_HTTP_SSL_CERTIFICATE_TRUST_STORE_PASSWORD=changeit
" | Out-File -Encoding ascii .env

Write-Output "
Development certificates successfully generated! Please add the follwing to VM Options in IntelliJ:

-Djavax.net.ssl.trustStore='$PWD\rootCA.p12'

"
