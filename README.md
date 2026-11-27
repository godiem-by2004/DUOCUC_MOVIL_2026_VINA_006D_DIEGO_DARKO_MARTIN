# AgroGestion — Sistema Móvil de Monitoreo Nutricional y Fertirriego

## Integrantes
Diego, Darko y Martín (Sección DSY1105)

## Funcionalidades
Inicio de sesión validado, gestión de predios georreferenciados, toma de muestras foliares con GPS/Cámara, persistencia local offline con Room/DataStore, cálculo de balance NPK, sincronización REST con Spring Boot y consulta meteorológica en tiempo real.

## Endpoints Utilizados
- **Microservicio `ms-registros` (`http://localhost:8081`):** 
  - `GET /api/registros`
  - `GET /api/registros/{id}`
  - `POST /api/registros`
  - `POST /api/registros/sync`
  - `PUT /api/registros/{id}`
  - `DELETE /api/registros/{id}`
- **Microservicio `ms-catalogo` (`http://localhost:8082`):** 
  - `GET /api/catalogo/predios`
  - `GET /api/catalogo/requerimientos?cultivo={c}&etapa={e}`
- **API Externa Open-Meteo (`https://api.open-meteo.com`):** 
  - `GET /v1/forecast?latitude={lat}&longitude={lon}&current=temperature_2m,relative_humidity_2m,wind_speed_10m,precipitation`

## Pasos para Ejecutar
1. Levantar `ms-registros` (puerto `8081`) y `ms-catalogo` (puerto `8082`).
2. Abrir la app en Android Studio.
3. Ejecutar en emulador (`10.0.2.2`).
4. Correr `./gradlew testDebugUnitTest` para validar las pruebas unitarias.

## APK Firmado y Llave `.jks`
- **Llave:** `app/agrogestion-release.jks`
- **APK:** `app/release/app-release.apk`

*(Inserta aquí la captura de pantalla del APK firmado generado en Android Studio)*
