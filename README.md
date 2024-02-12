# BoxQ
 
# Registro de Actividad Física

Esta es una aplicación de Android para el registro de actividad física que utiliza Firebase para la autenticación de usuarios y almacenamiento de datos en la nube.

## Funcionalidades

- **Autenticación de Usuarios:** Permite a los usuarios registrarse, iniciar sesión y cerrar sesión en la aplicación.
- **Registro de Actividades:** Los usuarios pueden registrar sus actividades físicas diarias, como ejercicios realizados.
- **Visualización de Registros:** Los usuarios pueden ver un registro de sus actividades anteriores.
- **Interfaz Intuitiva:** Diseño sencillo y fácil de usar para una experiencia fluida del usuario.

## Tecnologías Utilizadas

- **Firebase Authentication:** Para la autenticación de usuarios.
- **Firebase Firestore:** Para el almacenamiento de datos en la nube.
- **Android Studio:** Entorno de desarrollo integrado para la creación de la aplicación Android.
- **Java:** Lenguaje de programación utilizado para desarrollar la lógica de la aplicación.

## Requisitos Previos

- **Android Studio:** Es necesario tener instalado Android Studio para compilar y ejecutar la aplicación.
- **Cuenta de Firebase:** Se requiere una cuenta de Firebase para configurar la autenticación y la base de datos en la nube.

## Configuración

1. **Clonar el Repositorio:** Clona este repositorio en tu máquina local utilizando Git:

    ```bash
    git clone https://github.com/Nicotob7/BoxQ.git
    ```

2. **Configurar Firebase:** Crea un proyecto en Firebase Console y sigue las instrucciones para agregar tu aplicación Android. Descarga el archivo `google-services.json` y colócalo en el directorio `app/` de tu proyecto en Android Studio.

3. **Configurar Reglas de Seguridad:** En la consola de Firebase, configura las reglas de seguridad de Firestore según tus necesidades para proteger los datos de tu aplicación.

## Ejecución

1. Abre el proyecto en Android Studio.
2. Asegúrate de tener configurado un emulador o un dispositivo físico conectado para ejecutar la aplicación.
3. Haz clic en el botón de "Run" (Ejecutar) en Android Studio para compilar y ejecutar la aplicación en el emulador o dispositivo.
