# Ingenieria-Web

## build.gralde
Usar implementation en lugar de compile.
Añadido implementation 'com.github.ua-parser:uap-java:1.4.3'
./gradlew dependencies permite ver si todo se ha importado correctamente.

## HTTP header information extraction
Utilizado [Maven](https://mvnrepository.com/artifact/com.github.ua-parser/uap-java/1.4.3), [Github](https://github.com/ua-parser/uap-java)

Desde UrlShortenerController se utiliza getInfo de urlshortener/utils/HTTPInfo.java para obtener un string con el formato
OS: sistema_operativo NAV: navegador. Este string se envía al cliente en caso de no haber tenido ningún
problema, en caso de ocurrir una excepción se envía INTERNAL_SERVER_ERROR.

Añadidos tests en test/utils/HTTÌnfoTets.java que comprueban que se extraen bien ambos datos dada una
cabecera USER-AGENT creada. Se prueba con diferentes sistemas operativos y navegadores.

### TODO
UrlShortenerController.java
Añadir a la respuesta enviada al cliente el string con la información.

UrlShortenerTests.java
Añadir test para realizar la comprobación global de toda la petición.