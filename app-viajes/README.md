# ViajeIA 🧳🤖

App Android (Kotlin + Jetpack Compose) de **viajes con IA**, todo en uno:

- **Planificar** → la IA (Gemini) genera un itinerario día a día según destino, días, presupuesto e intereses.
- **Guía IA** → chat conversacional ("Globo") para preguntar sobre rutas, comida, transporte, frases útiles, etc.
- **Viajes / Diario / Gastos** → guarda cada viaje, escribe entradas de diario y controla los gastos (almacenamiento local con Room).

Reutiliza el enfoque de **VitaLife**: la IA funciona con tu **clave propia de Gemini (BYOK)** o apuntando a tu **servidor proxy** (URL base configurable, clave vacía).

## Abrir en Android Studio

1. Abre **Android Studio** (Koala o superior).
2. `File → Open` y selecciona la carpeta `app-viajes`.
3. Android Studio descargará Gradle y generará el *wrapper* automáticamente. Si no, ejecuta `gradle wrapper` o usa el botón **Sync Project with Gradle Files**.
4. Espera a que termine el *Gradle Sync* (descarga dependencias).
5. Pulsa **Run ▶** en un emulador o dispositivo (mínimo Android 8.0 / API 26).

## Configurar la IA

En la app, pestaña **Ajustes**:

- **BYOK**: pega tu clave de Gemini (de [Google AI Studio](https://aistudio.google.com/app/apikey)) y deja la URL base por defecto.
- **Proxy**: deja la clave vacía y pon la URL de tu servidor proxy en *URL base* (igual que en VitaLife).
- **Modelo**: por defecto `gemini-2.0-flash` (puedes cambiarlo).

## Estructura

```
app/src/main/java/com/serranen/viajeia/
├── data/
│   ├── local/        # Room: entidades, DAOs, base de datos
│   ├── remote/       # GeminiService (API REST)
│   ├── repository/   # TripRepository, AiRepository
│   └── settings/     # SettingsRepository (DataStore: clave/proxy/modelo)
├── di/               # AppContainer (inyección de dependencias manual)
└── ui/
    ├── home/         # Lista de viajes
    ├── planner/      # Planificador con IA
    ├── chat/         # Guía conversacional
    ├── diary/        # Detalle de viaje: itinerario + diario + gastos
    ├── settings/     # Configuración de la IA
    ├── navigation/   # NavHost + barra inferior
    └── theme/        # Material 3
```

## Arquitectura

MVVM con `ViewModel` + `StateFlow` + Compose. Sin librerías de DI externas
(contenedor manual en `AppContainer`) para mantenerlo simple.

## Próximos pasos sugeridos

- Login con Google (Firebase Auth) y copia en Drive, como en VitaLife.
- Mapa en las entradas de diario (coordenadas + Google Maps).
- Adjuntar fotos al diario (Coil ya está incluido).
- Exportar itinerario a PDF / compartir.
- AdMob + Play Billing para la versión Pro.
