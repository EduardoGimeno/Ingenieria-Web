# Ingenieria-Web

## build.gralde
Usar implementation en lugar de compile.
Añadido implementation 'com.github.ua-parser:uap-java:1.4.3'
./gradlew dependencies permite ver si todo se ha importado correctamente.

## HTTP header information extraction
Utilizado [Maven](https://mvnrepository.com/artifact/com.github.ua-parser/uap-java/1.4.3), [Github](https://github.com/ua-parser/uap-java)

Desde redirectTo de UrlShortenerController se utiliza getOS y getNav de /utils/HTTPInfo.java para obtener un string con el sistema operativo y el navegador. Una vez obtenidos en saveClick se introducen como argumentos
para ser guardados en la base de datos. Si no se pueden obtener se devuelve INTERNAL_SERVER_ERROR en la respuesta.

En ClickBuilder.java se han añadido operaciones para añadir valor a los campos browser y platform de la clase
click. En ClickService.java se utilizan para añadir los valores necesarios a la clase Click y guardar la instancia en la base de datos.

Añadidos tests en test/utils/HTTÌnfoTets.java que comprueban que se extraen bien ambos datos dada una
cabecera USER-AGENT creada. Se prueba con diferentes sistemas operativos y navegadores.

### TODO
¿Tests de integración?