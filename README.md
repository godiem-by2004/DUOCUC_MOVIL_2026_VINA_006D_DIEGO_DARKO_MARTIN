# NutricionAgricola - Registro y Estimación Nutricional Agrícola

Prototipo de aplicación móvil Android para la digitalización y captura de datos agronómicos en terreno para la Consultora Gestión y Proyectos Limitada. La solución opera bajo arquitectura MVVM, con persistencia local sin conexión (offline) e integración de recursos nativos (Cámara y GPS).

## Integrantes
- Diego Arancibia
- Darko Araya 
- Martin Bozzo

## Funcionalidades Principales
- **Registro de Terreno:** Formulario reactivo para la captura de predio, tipo de cultivo (paltos, cítricos, tomates), etapa fenológica, pH del suelo y conductividad eléctrica (CE).
- **Validaciones Desacopladas:** Validación en tiempo real desde el ViewModel con retroalimentación visual e íconos de advertencia.
- **Recursos Nativos:**
    - **Cámara:** Captura fotográfica de sintomatología y deficiencias foliares mediante FileProvider.
    - **GPS (Ubicación):** Georreferenciación de la muestra en terreno usando FusedLocationProviderClient.
- **Almacenamiento Local (Offline):** Persistencia en base de datos SQLite accesible sin conexión a internet.
- **UI & Animaciones:** Interfaz en Jetpack Compose con animaciones funcionales de visibilidad (`AnimatedVisibility`) y expansión de tarjetas (`animateContentSize`).

## Planificación del Proyecto
- **Tablero de Trello:** [Pega aquí el enlace público de su tablero de Trello]

## Requisitos de Ejecución
- Android Studio Ladybug / Koala o superior
- JDK 17 o superior
- Min SDK: 26 (Android 8.0) | Target SDK: 35
- Emulador o dispositivo físico con servicios de localización y cámara activos.