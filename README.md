# BeatTreat 🎵

**BeatTreat** es una aplicación social para amantes de la música, centrada en la experiencia de descubrir, calificar y debatir álbumes con una comunidad activa. Los usuarios pueden explorar lanzamientos, escribir reseñas, comentar las de otros y conectar con personas que comparten sus gustos musicales.

<p align="center">
  <img src="https://github.com/user-attachments/assets/a2652fce-5705-447a-b670-c2203ba523e1" width="200"/>
</p>---

## Pantallas de la aplicación

| Pantalla | Descripción |
|---|---|
| **Login / Registro** | Inicio de sesión y creación de cuenta, con opción de Sign in con Google |
| **Home** | Feed principal con álbumes por artista y banner destacado |
| **Biblioteca** | Colección personal: canciones guardadas, artistas, álbumes y playlists |
| **Descubre** | Exploración de categorías, géneros y nuevos lanzamientos |
| **Reseñas** | Feed de reseñas destacadas de la comunidad con calificación por estrellas |
| **Comentarios** | Hilo de comentarios de una reseña específica con opción de responder |
| **Escribir Reseña** | Formulario para publicar una reseña con calificación y texto |
| **Perfil** | Información del usuario, álbumes favoritos y reseñas recientes |
| **Chat** | Mensajería grupal con soporte de imágenes |

---

## Navegación

```
Login ──────► Home
Registro ───► Home
              ├── Home ────────────── Perfil
              │     └── (álbum) ────► Reseñas
              ├── Biblioteca
              ├── Descubre ─────────── Perfil
              │     └── (álbum) ────► Reseñas
              ├── Chat ◄───────────── Perfil
              └── Perfil
                    ├── (reseña) ───► Comentarios
                    └── Ver todas ──► Reseñas
                                         ├── (reseña) ► Comentarios
                                         └── Escribir Reseña
```
##  Tecnologías

- **Plataforma:** Android
- **Entorno de desarrollo:** Android Studio
- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose + Material Design 3
- **Navegación:** Navigation Compose
- **Control de versiones:** Git & GitHub

---

## Arquitectura

El proyecto aplica los siguientes principios de Jetpack Compose:

- **State Hoisting:** cada pantalla se divide en un composable stateful (maneja el estado) y uno stateless (recibe datos y emite eventos), facilitando el testing y la reutilización.
- **Scaffold único:** un solo `Scaffold` en `MainActivity` gestiona el `BottomBar` global, que se oculta automáticamente en Login y Registro.
- **Composables pequeños:** los componentes de UI están divididos en piezas reutilizables con `modifier` como parámetro en todas ellas.
- **Datos locales:** cada pantalla tiene su propio archivo de modelo en la carpeta `model/`, con datos quemados que representan entidades de UI.

### Estructura de carpetas

```
com.example.login/
├── MainActivity.kt
├── navigation/
│   └── AppNavegacion.kt
├── screens/
│   ├── LoginScreen.kt
│   ├── RegistroScreen.kt
│   ├── HomeScreen.kt
│   ├── BibliotecaScreen.kt
│   ├── DescubreScreen.kt
│   ├── ChatScreen.kt
│   ├── ResenaScreen.kt
│   ├── ComentariosScreen.kt
│   ├── EscribirResenaScreen.kt
│   └── ProfileScreen.kt
├── model/
│   ├── HomeUI.kt
│   ├── BibliotecaUI.kt
│   ├── DescubreUI.kt
│   ├── MensajeUI.kt
│   ├── PefilUI.kt
│   └── ResenaUI.kt
└── ui/theme/
    └── BeatTreatTheme.kt
```

---

## Equipo
* Alexander Aponte
* Juan Esteban Nonsoque
* Juan Manuel Solano
* Sofía Guerra
