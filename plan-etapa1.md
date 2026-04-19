# Plan etapa 1

## Objetivo
Separar el proyecto en capas claras para que cada una tenga una sola responsabilidad y dependencias en una sola dirección.

## Estructura objetivo

- Presentation
  - Lógica de interfaz.
  - Solo depende de domain.
  - Un paquete por pantalla: home y detail.
  - Un paquete utils para componentes reutilizables.
- Domain
  - Entidades.
  - Casos de uso.
  - Interfaces de repositorio.
  - Regla de negocio: ordenar películas por voto y mapear el resultado a QualifiedMovie.
- Data
  - Fuente de datos local o externa.
  - Implementación del repositorio definido en domain.
  - DTOs y mapeos hacia domain.
- DI
  - Ensamblado de dependencias.
  - Creación de cliente HTTP, repositorios, casos de uso y ViewModels.

## Orden de migración

1. Crear la nueva estructura de paquetes o módulos.
2. Mover Movie y QualifiedMovie a domain.
3. Definir el contrato del repositorio en domain.
4. Extraer la lógica de ordenar y clasificar películas a un caso de uso en domain.
5. Mover RemoteMovie y RemoteResult a data.
6. Implementar el repositorio en data usando la fuente TMDB.
7. Dejar la conversión de DTO a entidad dentro de data.
8. Separar la presentación por pantalla.
9. Mover CommonComposables a presentation.utils.
10. Dividir MoviesViewModel en ViewModels por pantalla o por flujo de UI.
11. Centralizar el armado de objetos en DI.
12. Ajustar navegación y punto de entrada para usar la nueva composición.

## Mapeo de archivos actuales

- CommonComposables.kt -> presentation.utils
- HomeScreen.kt -> presentation.home
- DetailScreen.kt -> presentation.detail
- MoviesViewModel.kt -> presentation.home y presentation.detail, o una coordinación mínima en presentation
- Movie.kt -> domain.model
- RemoteMovie y RemoteResult -> data.remote
- MoviesDependencyInjector.kt -> di
- Navigation.kt -> presentation.navigation o presentation
- main.kt -> entrada de desktop sin lógica de negocio

## Reglas de diseño

- Nombres significativos.
- Funciones pequeñas.
- Una responsabilidad por clase o archivo.
- Presentation sin acceso directo a la red.
- Data sin lógica de interfaz.
- Domain sin dependencias de UI ni de infraestructura.
- Evitar duplicar lógica de mapeo o clasificación.

## Validación

- La app compila.
- La pantalla home muestra la lista ordenada por voteAverage.
- La clasificación de QualifiedMovie sale desde domain.
- La pantalla detail obtiene el detalle desde un caso de uso.
- Presentation depende solo de domain.
- Data implementa el contrato de domain.
- DI queda como único punto de ensamblado.