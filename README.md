# RecipeBook

Android-приложение для поиска и сохранения рецептов на основе Spoonacular API.

## Стек

**Язык:** Kotlin 2.0.0  
**Архитектура:** Clean Architecture (Presentation → Domain → Data)

| Слой | Технологии                                                      |
|------|-----------------------------------------------------------------|
| UI | Fragments, ViewBinding, Material 3, Navigation Component, Coil  |
| State | ViewModel, Coroutines, Flow , Coroutine                         |
| DI | Hilt/Dagger                                                     |
| Network | Retrofit, OkHttp, Gson                                          |
| Local DB | Room                                                            |
| Build | Gradle KTS, Kapt, ProGuard                                      |

## Ключевые решения

- **Offline-first** — Room кэширует данные, приложение работает без сети
- **WihList** — Room сохраняет лайкнутые данные, приложение не удаляет их без вашего разрешения
- **Reactive UI** — Room Flow автоматически обновляет экран при изменении БД
- **ListAdapter + DiffUtils** — Позволяют плавно вставлять новые элементы и делать скроллинг
- **Пагинация** — подгрузка по 20 рецептов с удалением старого кэша (лайкнутые сохраняются)
- **Debug/Release** — в debug-сборке работает FakeRecipeApi без реальных запросов

<div style="display: flex; justify-content: center; gap: 10px;">
  <img src="https://github.com/user-attachments/assets/ca00b9e8-6058-4936-b3db-5e1b7bbed614" width="40%" />
  <img src="https://github.com/user-attachments/assets/95d0db6b-f29d-4082-8968-1a0675e2f883" width="40%" />
</div>
