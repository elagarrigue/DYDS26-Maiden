# Plan etapa 1

## Objetivo
Modificar la estructura del proyecto para que corresponda a la arquitectura propuesta (siempre respetando los principios SOLID y clean code).
Separar el proyecto en capas claras para que cada una tenga una sola responsabilidad y dependencias en una sola dirección.

## Estructura objetivo

- Presentation
  - Lógica de interfaz.
  - Solo depende de domain.
  - Raíz para App.kt, Navigation.kt y MoviesViewModel.kt.
  - Paquetes por pantalla: home y detail.
  - Paquete utils para componentes reutilizables.
- Domain
  - Entidades en domain/entity.
  - Casos de uso en domain/usecase.
  - Interfaces de repositorio en domain/repository.
  - Regla de negocio: ordenar películas por voto y mapear el resultado a QualifiedMovie dentro del caso de uso.
- Data
  - Fuente de datos local o externa.
  - DTOs en data/external.
  - Implementación del repositorio definido en domain.
  - Mapeos de DTO a entidad dentro de data.
- DI
  - Ensamblado de dependencias.
  - Creación de cliente HTTP, repositorios, casos de uso y ViewModels.
  - Único punto con implementaciones concretas de las capas.

## Etapas

### Etapa 1 - Dominio y contratos
Commit mínimo: 1 commit.

- [ ] Crear el esqueleto de carpetas con `domain`, `data`, `presentation` y `di`.
- [ ] Trasladar `Movie.kt` a `domain/entity/`.
- [ ] Crear `MoviesRepository.kt` en `domain/repository/`.
- [ ] Crear `GetPopularMoviesUseCase.kt` y `GetMovieDetailsUseCase.kt` en `domain/usecase/`.
- [ ] Trasladar `RemoteMovie` y `RemoteResult` a `data/external/`.
- [ ] Mover la ordenación por voto y la transformación a `QualifiedMovie` dentro del caso de uso.

### Etapa 2 - Data e inyección
Commit mínimo: 1 commit.

- [ ] Crear `MoviesRepositoryImpl.kt` en `data/`.
- [ ] Inyectar la fuente externa en `MoviesRepositoryImpl`.
- [ ] Hacer el mapeo de DTO a entidad dentro de `data`.
- [ ] Mover `MoviesDependencyInjector.kt` a `di/`.
- [ ] Actualizar `MoviesViewModel.kt` para consumir casos de uso y dejar de ordenar o mapear.

### Etapa 3 - Presentation y cierre
Commit mínimo: 1 commit.

- [ ] Mover `HomeScreen.kt` a `presentation/home/`.
- [ ] Mover `DetailScreen.kt` a `presentation/detail/`.
- [ ] Mover `CommonComposables.kt` a `presentation/utils/`.
- [ ] Mover `App.kt`, `Navigation.kt` y `MoviesViewModel.kt` a la raíz de `presentation/`.
- [ ] Arreglar todos los imports rotos.
- [ ] Verificar que `main.kt` quede aislado en la raíz principal.

## Validación por etapa

- Etapa 1: `domain` queda aislado y `data/external` solo contiene DTOs.
- Etapa 2: `MoviesRepositoryImpl` y `DI` dejan de depender de lógica de negocio en el ViewModel.
- Etapa 3: `presentation` no importa `data`, home recibe la lista ordenada y clasificada desde `domain` y `DI` concentra las implementaciones concretas.

## Reglas de diseño

- Nombres significativos.
- Funciones pequeñas.
- Una responsabilidad por clase o archivo.
- Presentation sin acceso directo a la red.
- Data sin lógica de interfaz.
- Domain sin dependencias de UI ni de infraestructura.
- Evitar duplicar lógica de mapeo o clasificación.

## Validación final

- El compilador no tira errores.
- `presentation` no tiene imports de `data`.
- La pantalla home recibe la lista ya ordenada y clasificada desde `domain`.
- `DI` es el único lugar donde conviven implementaciones concretas de todas las capas.