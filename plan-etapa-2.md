# Plan Etapa 2 - Issues (3 stages)

## Criterios globales (aplican a todas las issues)
- Fakes manuales por archivo (autocontenidos, sin librerias de mocking).
- Nombres de tests en espanol (backticks).
- Asserts de kotlin.test (assertEquals, assertTrue, assertNull).
- JUnit4 para ejecucion (@Test) con kotlin-test-junit.
- Usar runTest para tests suspend (repository y use cases).
- En ViewModels: usar StandardTestDispatcher, Dispatchers.setMain/resetMain con MainDispatcherRule, y runTest con el dispatcher de la regla.
- En ViewModels: usar advanceUntilIdle() y capturar secuencia con uiState.take(3).toList().
- No validar guardas de re-entrada (no existen en el codigo).
