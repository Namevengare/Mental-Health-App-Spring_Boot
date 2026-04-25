# Manual Tecnico - Backend Spring Boot

## 1) Ejecutar el backend en IntelliJ

1. Abrir el proyecto `Mental-Health-App-Spring_Boot`.
2. Esperar a que IntelliJ termine de sincronizar Gradle.
3. Ir a `src/main/java/com/example/MentalHealth_Backend/MentalHealthBackendApplication.java`.
4. Ejecutar con el boton verde `Run 'MentalHealthBackendApplication'`.

Tambien se puede ejecutar desde Gradle:

- Panel **Gradle** -> **Tasks** -> `bootRun`.

## 2) Configuracion de Java para evitar LinkageError

Si aparece:

`LinkageError occurred while loading main class com.example.MentalHealth_Backend.MentalHealthBackendApplication`

normalmente hay una incompatibilidad de version de Java entre lo compilado y la JVM de ejecucion.

### Recomendacion

Usar Java 17 en todo el flujo:

- `Project SDK` = 17
- `Gradle JVM` = 17
- `build.gradle` toolchain = 17

Ejemplo:

```gradle
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}
```

Luego:

1. `Reload All Gradle Projects`
2. `Build > Rebuild Project`
3. Ejecutar nuevamente la aplicacion.

## 3) Puerto del servidor

Actualmente se usa:

```yaml
server:
  port: 8081
```

Archivo: `src/main/resources/application.yaml`.

## 4) Probar endpoints principales

### 4.1 Endpoint raiz (evitar Whitelabel en `/`)

- `GET /`
- URL local: `http://localhost:8081/`

### 4.2 WebSocket de notificaciones

- Ruta: `/ws-notifications`
- URL local: `ws://localhost:8081/ws-notifications`

### 4.3 Envio manual de notificaciones

- `POST /api/notifications/send`
- Content-Type recomendado: `text/plain`

Ejemplo:

```bash
curl -X POST "http://localhost:8081/api/notifications/send" -H "Content-Type: text/plain" -d "Hola"
```

### 4.4 Noticias cientificas de salud mental (PubMed E-Utilities)

- `GET /api/news/mental-health`
- URL local: `http://localhost:8081/api/news/mental-health?limit=10`

Notas:

- Siempre aplica filtro de salud mental.
- Parametro opcional `query`, pero se combina con filtro de salud mental.
- `limit` se acota internamente entre 1 y 20.

## 5) Uso con ngrok

ngrok debe apuntar al mismo puerto donde corre Spring Boot.

Si Spring corre en `8081`:

```bash
ngrok http 8081
```

Ejemplos de consumo publico:

- REST: `https://<tu-subdominio>.ngrok-free.dev/api/news/mental-health?limit=10`
- WebSocket: `wss://<tu-subdominio>.ngrok-free.dev/ws-notifications`

## 6) Troubleshooting rapido

- **`Port 8080/8081 was already in use`**: liberar puerto o cambiar `server.port`.
- **Whitelabel 404**: revisar metodo y ruta correctos (por ejemplo `POST` vs `GET`).
- **No llegan mensajes WebSocket**: verificar que cliente use `/ws-notifications` y puerto correcto.
- **Ngrok no responde al backend**: revisar que ngrok y Spring usen el mismo puerto.
