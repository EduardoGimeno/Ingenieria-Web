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

## Google Safe Browsing
Hay dos opciones disponibles para comprobar si una URL es segura con Google Safe Browsing, [Lookup API](https://developers.google.com/safe-browsing/v4/lookup-api) y [Update API](https://developers.google.com/safe-browsing/v4/update-api). De momento se utiliza la primera porque es más sencilla.

**Cambios realizados para Google Safe Browsing:**

 * Añadido a build.gradle: compile 'com.google.apis:google-api-services-safebrowsing:v4-rev123-1.25.0'
 * Añadido [código básico](https://stackoverflow.com/questions/46599053/google-safe-browsing-v4-api-java) en src/main/java/urlshortener/utils/SafeBrowsing
 * Añadido controller simple para pruebas interactivas (127.0.0.1:8080/safecheck/{url})

**Información relevante:**

 * [Formato](https://developers.google.com/safe-browsing/v4/lookup-api) de petición y respuesta de comprobación de seguridad.
 * [Formato](https://developers.google.com/safe-browsing/v4/lists) de petición y respuesta de lista amenazas.
 * Obtener [api_key](https://console.cloud.google.com/apis/credentials)
 * Activar el [API de Google Safe Browsing](https://console.cloud.google.com/apis/api/safebrowsing.googleapis.com/)

### TODOLIST:
 * Estudiar diferencias entre [Lookup API](https://developers.google.com/safe-browsing/v4/lookup-api) y [Update API](https://developers.google.com/safe-browsing/v4/update-api), ¿cuál deberíamos utilizar?
 * Banco de pruebas (empezado).
 * ¿Usar api_key común cifrada como en la práctica 2?
 * Revisar los tipos de [amenazas](https://developers.google.com/safe-browsing/v4/reference/rest/v4/ThreatType), [plataformas](https://developers.google.com/safe-browsing/v4/reference/rest/v4/PlatformType) y [entradas](https://developers.google.com/safe-browsing/v4/reference/rest/v4/ThreatEntryType) que nos interesa analizar.
 * Ajustar comportamiento del método *checkURLs*, ¿devolver lista de URLs seguras|inseguras?
 * Si el controller se queda añadir casos error, salida correcta,...