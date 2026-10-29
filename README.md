# 🌱 AgroGestión Terreno — Registro y Estimación Nutricional Agrícola Offline

Aplicación móvil Android nativa desarrollada en **Kotlin** y **Jetpack Compose** bajo arquitectura **MVVM** para la asignatura **DSY1105 · Desarrollo de Aplicaciones Móviles** (Duoc UC — Sede Viña del Mar, Sección 006D).

---

## 👥 Integrantes del Equipo

* **Diego** — Arquitectura MVVM, Persistencia Local (Room SQLite / DataStore) y Repositorio Agronómico.
* **Darko** — Interfaz Visual Material Design 3 (Pantallas de Predios y Fertirriego), Navegación y Animaciones.
* **Martín** — Formulario Validado Desacoplado, Integración de Recursos Nativos (Cámara Foliar y Sensor GNSS/GPS) e Historial.

---

## 🎯 Contexto del Caso: Consultora Gestión y Proyectos Limitada

Los asesores agrícolas y productores de la Región de Valparaíso (Quillota, La Cruz, Hijuelas) requieren registrar en terreno parámetros fisicoquímicos de suelo y agua (pH, conductividad eléctrica, macronutrientes N-P-K-Ca-Mg), capturar evidencia fotográfica foliar y georreferenciar cada cuartel de cultivo en zonas rurales sin cobertura de internet.

**AgroGestión Terreno** resuelve esta problemática permitiendo:
1. **Operación 100% Offline** mediante almacenamiento persistente en **Room (SQLite)** y preferencias de operador y sesión en **Jetpack DataStore**.
2. **Motor Estequiométrico Agro-Calc Offline** que calcula el balance nutricional (Teórico vs. Real) y la prescripción de fertirriego según cultivo (`Palto Hass`, `Cítricos`, `Tomate`) y etapa fenológica (`Floración`, `Engorde de Fruto`, `Maduración`).
3. **Acceso Seguro a 2 Recursos Nativos del Dispositivo**:
   * **Sensor GPS (`LocationManagerCompat.getCurrentLocation` + permisos de ubicación en tiempo de ejecución)**: lee la ubicación real del dispositivo (una lectura, máx. 15 s) y su precisión en metros. Si no hay señal o falta el permiso, informa el error y se conserva la coordenada de *referencia del predio*, identificada como tal en pantalla y en el historial.
   * **Cámara Nativa (`ActivityResultContracts.TakePicture` + `FileProvider` + permiso `CAMERA`)**: la foto se guarda a tamaño completo en el almacenamiento interno de la app (`filesDir/fotos`), en Room se guarda la ruta y se muestra con Coil (esquinas redondeadas, `ContentScale.Crop`) en el formulario y en el historial. La foto se puede repetir o quitar (se borra el archivo).

---

## 🔐 Acceso (login de demostración)

La app abre en una pantalla de login con validación por campo (formato de correo, largo mínimo de contraseña, íconos de error y mensajes). Hay **un único usuario genérico**:

| Campo | Valor |
| :--- | :--- |
| Correo | `asesor@agrogestion.cl` |
| Contraseña | `Agro2026` |

* Las credenciales son **ficticias**, solo para demostración académica (Ley 21.719 / 19.628: no hay datos personales reales). Están definidas en `repository/AuthRepository.kt`.
* La sesión se guarda en **DataStore**: si se cierra la app, al volver a abrirla entra directo a *Predios*.
* **Cerrar sesión**: ícono de perfil (arriba a la derecha) → *Cerrar sesión*.

---

## 🧱 Arquitectura del Proyecto (MVVM + Repository)

```text
app/src/main/java/com/example/agrogestion/
├── model/
│   └── PredioCatalogo.kt          # Modelos de dominio, catálogo de predios y tablas estequiométricas
├── data/
│   └── local/
│       ├── RegistroEntity.kt      # Entidad SQLite (@Entity registros_terreno); fecha como epoch Long
│       ├── RegistroDao.kt         # Consultas reactivas Flow<List<RegistroEntity>> (@Dao)
│       ├── AppDatabase.kt         # Singleton de RoomDatabase
│       └── UserDataStore.kt       # DataStore: asesor, predio activo y sesión del login
├── repository/
│   ├── AgronomicoRepository.kt    # Única fuente de verdad de muestras y cálculo de recomendaciones
│   └── AuthRepository.kt          # Login con usuario hardcodeado + sesión persistida
├── viewmodel/
│   ├── FormularioValidator.kt     # Validación del formulario de muestra, sin dependencias de Android
│   ├── LoginValidator.kt          # Validación de correo y contraseña, sin dependencias de Android
│   ├── AgroUiState.kt             # Estados inmutables (formulario, dashboard, filtro, cálculos de fertirriego)
│   ├── AgroViewModel.kt           # Estado de las pantallas internas con StateFlow y corrutinas
│   └── LoginViewModel.kt          # LoginUiState + StateFlow del login
├── ui/
│   ├── components/
│   │   ├── AgroTopBar.kt          # Barra superior: estado offline, sincronizar y menú de perfil
│   │   └── BannerSincronizacion.kt# Aviso animado del progreso de sincronización (4 pantallas)
│   ├── navigation/
│   │   └── AppNavigation.kt       # NavHost (Login + 4 destinos) y NavigationBar inferior
│   ├── screens/
│   │   ├── LoginScreen.kt         # Acceso con validación, loader y error de credenciales
│   │   ├── PrediosScreen.kt       # Dashboard: predio seleccionado, pH/CE de la última muestra, fenología
│   │   ├── NuevoRegistroScreen.kt # Recursos nativos (GPS + Cámara) y formulario con validaciones
│   │   ├── FertirriegoScreen.kt   # Prescripción calculada y barras animadas de balance N-P-K-Ca-Mg
│   │   └── HistorialScreen.kt     # Trazabilidad de muestras: filtro, edición, eliminación y sync
│   └── theme/
│       ├── Color.kt               # Sistema de diseño Field Management (#1E5128, #52B788, #2D6A4F)
│       ├── Theme.kt               # Configuración Material Design 3 (solo tema claro)
│       └── Type.kt                # Jerarquía tipográfica
├── util/
│   ├── FormatoCoordenada.kt       # "32.8824° S, 71.2489° W" (hemisferio sin signo negativo)
│   ├── FormatoFecha.kt            # Hoy / Ayer / dd/MM/yyyy a partir del epoch guardado en Room
│   └── FotoFoliarStorage.kt       # Archivo de foto + Uri de FileProvider para la cámara
└── MainActivity.kt                # Punto de entrada: crea repositorios y ViewModels
```

---

## ✅ Cumplimiento de Indicadores de Evaluación (Evaluación Parcial 3 — Defensa)

| Indicador | Dónde se evidencia en el proyecto |
| :--- | :--- |
| **IE 2.1.1 (20%)** Lógica de validación de formularios | `FormularioNutricionalValidator` y `LoginValidator` (reglas puras, límites como constantes), estados `FormularioUiState` / `LoginUiState` (error visible solo si el campo fue tocado o se intentó guardar), `CampoValidadoAgro` y `CampoLogin` (`isError`, ícono `ErrorOutline`/`CheckCircle`, `supportingText`). Tests en `FormularioValidatorTest` y `LoginValidatorTest`. |
| **IE 2.1.2 (7%)** Diseño visual, navegación y usabilidad | Material Design 3 con paleta propia (`ui/theme`), `NavigationBar` de 4 destinos + Login sin barra, `FlowRow` para chips que no caben, selectores `ExposedDropdownMenuBox`, teclado con "Siguiente" entre campos. |
| **IE 2.2.1 (22%)** Estructura modular | Capas `model` / `data` / `repository` / `viewmodel` / `ui`, componentes reutilizables (`AgroTopBar`, `BannerSincronizacion`, `SelectorAgro`), un ViewModel por flujo (`AgroViewModel`, `LoginViewModel`). |
| **IE 2.2.2 (7%)** Estructuras de control, estado y respuesta visual | `StateFlow` + `collectAsState`; propiedades calculadas (`registrosFiltrados`, `requerimientoActual`, `balancesNutricionales`); `when` para estados de GPS; animaciones `animateFloatAsState`, `animateColorAsState`, `AnimatedVisibility`, transiciones `fadeIn/fadeOut` del `NavHost` y loaders en *Ingresar* y *Guardar Muestra*. |
| **IE 2.3.1 (18%)** Almacenamiento local en vivo | Room: `RegistroDao` (insertar, recuperar con `Flow`, actualizar observaciones, eliminar, marcar sincronizados). DataStore: `UserDataStore` (asesor, predio activo, sesión). |
| **IE 2.3.2 (7%)** Arquitectura y herramientas colaborativas | MVVM + Repository (árbol de arriba), historial de commits en GitHub y tablero Trello (ver *Gestión del proyecto*). |
| **IE 2.4.1 (7%)** Integración de recursos nativos y permisos | `ACCESS_FINE/COARSE_LOCATION` y `CAMERA` pedidos en tiempo de ejecución con `rememberLauncherForActivityResult`; `FileProvider` (`res/xml/file_paths.xml`) para no exponer rutas `file://`; mensajes cuando el permiso se niega o el GPS está apagado. |
| **IE 2.4.2 (12%)** Resultado del recurso nativo en la interfaz | Foto mostrada con Coil (`ContentScale.Crop`, esquinas redondeadas) en el formulario y en el Historial; coordenadas y precisión del GPS sobre el mapa del cuartel. |

---

## 🗂️ Gestión del proyecto

* **GitHub**: control de versiones con commits técnicos por integrante.
* **Trello**: tablero Kanban con la distribución de tareas por integrante. *Enlace pendiente de agregar por el equipo.*

---

## 🚀 Pasos para Ejecutar el Proyecto en Android Studio

1. Clonar el repositorio o descomprimir la carpeta del proyecto.
2. Abrir **Android Studio** y seleccionar **Open** → elegir la carpeta raíz del proyecto.
3. Esperar a que finalice **Gradle Sync** automáticamente (en la barra inferior).
4. En el administrador de dispositivos (**Device Manager**), iniciar un emulador Android (API 28 o superior, recomendado Pixel 7 / API 34+) o conectar un smartphone físico por USB con Depuración USB activa.
5. Presionar el botón **Run 'app' (▶️)** en la barra superior.
6. La primera pantalla es el **Login**: ingresar con `asesor@agrogestion.cl` / `Agro2026`.

> Desde la terminal, Gradle necesita un JDK. Si no hay `java` en el PATH, usar el que trae Android Studio:
> `JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"`.

---

## 🧪 Pruebas unitarias

`app/src/test/` contiene pruebas JVM (sin emulador) de los validadores (formulario y login), del estado de las pantallas (formulario, dashboard, filtro, fertirriego, login) y del formato de fechas y coordenadas.
Ejecutar: **Android Studio → clic derecho en `app/src/test` → Run Tests** o `./gradlew testDebugUnitTest`.

## ⚠️ Alcance actual y pendientes para la EFT

* **Sincronización simulada**: el botón de sync solo marca los registros como sincronizados localmente. Falta la API REST (Spring Boot) y el cliente Retrofit exigidos por el caso, además de la sincronización automática al detectar red.
* **Base de datos**: usa `fallbackToDestructiveMigration()` mientras el esquema cambia; antes de usar datos reales hay que escribir las migraciones de Room.
* **APK release firmado**: pendiente (el caso lo exige como entregable).
* Los datos precargados del historial son un dataset ficticio de demostración (Ley 21.719 / 19.628: no hay datos personales reales).
