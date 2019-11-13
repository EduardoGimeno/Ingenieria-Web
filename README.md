# Ingenieria-Web  
Para trabajar he utilizado IntelliJ que detecta la configuración de gradle.  
He probado el código mediante *gradle check* y creando las clases correspondientes en la carpeta test.

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

## Lector/escritor de CSV
De momento se ha comenzado con la idea de utilizar [OpenCSV](http://opencsv.sourceforge.net/).

**Cambios realizados para Lector/escritor de CSV:**
 * Añadido a build.gradle: compile group: 'com.opencsv', name: 'opencsv', version: '4.1'
 * Creada clase en en src/main/java/urlshortener/utils/CSVOperations

### TODOLIST:
 * Funciones principales
 * Banco de pruebas.
