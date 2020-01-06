# Ingenieria-Web

## build.gradle
Usar implementation en lugar de compile.
./gradlew dependencies permite ver si todo se ha importado correctamente.

Heroku
Usuario: b58e41dd1bceb2
Contraseña: 0b0b036af6c696b
Host: eu-cdbr-west-02.cleardb.net

## HTTP header information extraction
Recurso externo utilizado: [Maven](https://mvnrepository.com/artifact/com.github.ua-parser/uap-java/1.4.3), [Github](https://github.com/ua-parser/uap-java)

### DONE
Ficheros creados: HTTPInfo.java, HTTPInfoTest.java
Ficheros modificados: UrlShortenerController.java, UrlShortenerControllerTest.java, ClickService.java, ClickBuilder.java

### ESTADO
Funcionalidad completada, se han realizado test unitarios y de integración

## Check if URL is reachable
Recurso externo utilizado: [OkHttp](https://square.github.io/okhttp/)

### DONE
Ficheros creados: URLReachableService.java, URLReachableCallback.java
Ficheros modificados: UrlShortenerController.java, ShortUrlRepositoryImpl.java, ShortUrlRepository.java,
ShortURL.java, ShortUrlBuilder.java, ShortUrlService.java, ShortURLFixture.java, ShortURLRepositoryTests.java, UrlShortenerControllerTests.java

### ESTADO
Los tests de ShortURLRepositoryTests.java fallan, faltan crear tests de integración
en UrlShortenerControllerTests.java

## Limit number of redirections

### DONE
Ficheros creados: LimitRedirectionService.java
Ficheros modificados: UrlShortenerController.java, ClickService.java, ClickRepository.java,
ClickRepositoryImpl.java, ClickRepositoryTests.java, UrlShortenerTests.java

### ESTADO
Test para comprobar que devuelve una lista de clicks dado un limite falla y falta test de integración en UrlShortenerTests.java


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

# Working with CSV Files

# Work Done
    1. We're going to use OpenCSV to open, read and write CSV files, so the first
    thing we should do is to add OpenCSV to build.gradle:
    compile "com.opencsv:opencsv:4.0"
    2. Once we have add it, we can create the CSVController, in this case we will
    create it in src/main/java/urlshortener/utils/CSVController.java, in this file we
    are going to implement the basic operations with whom we are going to manipulate
    CSV files, by now, only will be a reading function that returns all the lines of
    the CSV path we pass  as a parameter into a List of Strings.
    3. Create a test witch opens a CSV file witch is in a specific path and tries to
    read it and assert that the data readed is correct.

    4. Implement CSV Requests.
# TODO