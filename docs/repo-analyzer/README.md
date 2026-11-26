# Анализатор репозитория

Утилита на Kotlin: принимает путь к каталогу, считает файлы и строки по расширениям (`.kt`, `.kts`, `.md`) и показывает три самых длинных файла. Пропускаются каталоги `.git`, `.gradle`, `build` и символические ссылки.

## Ссылки

- [Монолит](https://github.com/katashik777-lgtm/kfd-sidyakin/commit/d014bca635fe402612f089bf671e97f250e504e5) — оба отчёта в одном модуле `:app`, тесты проходят.
- [Модули](https://github.com/katashik777-lgtm/kfd-sidyakin/commit/a045d534ed677a4735bceb37967ecc9747ad6083) — разделение на `:core` и `:cli`, те же тесты проходят.
- [Успешный прогон CI](https://github.com/katashik777-lgtm/kfd-sidyakin/actions/runs/45731871052)

## Структура

- `:core` — обход файлов, подсчёт строк, сортировка (`scan`, `extensionReport`, `longestFiles`). Тесты расчётов — `core/src/test`, запускаются как `./gradlew :core:test` без CLI.
- `:cli` — `main`, аргументы, форматирование вывода, код завершения; зависит от `:core` (`implementation(project(":core"))`). Тесты — `cli/src/test`.

`:cli` зависит от `:core`, а не наоборот: ядро не знает о консоли и переиспользуемо, а точка входа лишь вызывает его.
Сохранность результата при переносе проверена теми же тестами (пример из четырёх файлов, пустой каталог, пропуск служебных каталогов, ошибка пути) и сравнением вывода на примере.

## Запуск

```bash
./gradlew :cli:run --args="/tmp/sample" --console=plain -q
```

Вывод на примере из четырёх файлов (`src/A.kt`, `src/B.kt`, `README.md`, `notes.txt`):

```text
Files and lines by extension:
  kt: 2 files, 4 lines
  md: 1 file, 0 lines
Top 3 longest files:
  src/A.kt (2)
  src/B.kt (2)
  README.md (0)
```

Если путь отсутствует или указывает на файл, программа печатает `Error: '...' does not exist or is not a directory` в stderr и завершается с кодом 1 (при неверном числе аргументов — код 2).

## Эксперимент со сборкой

Команда: `./gradlew build --console=plain`.

**Правка:** в `core/src/main/kotlin/kfd/analyzer/Analyzer.kt` подсчёт `file.useLines { it.count() }` заменён на `file.useLines { lines -> lines.fold(0) { n, _ -> n + 1 } }`. Публичные сигнатуры и результат не изменились.

**Прогноз до правки:** `:core:compileKotlin` выполнится (изменился его исходник), а `:cli:compileKotlin` будет `UP-TO-DATE`: ABI `:core` не поменялся, и Gradle/Kotlin не должны перекомпилировать зависимый модуль.

**Лог после правки (без `clean`):**

```text
> Task :core:compileKotlin            (выполнена)
> Task :cli:compileKotlin UP-TO-DATE
> Task :app:compileKotlin UP-TO-DATE
```

(`:core:test` и `:cli:test` тоже выполнились заново: изменился `core.jar`, который входит в их classpath.)

**Вывод:** прогноз совпал. `:core:compileKotlin` перезапущена из-за изменения входных исходников. `:cli:compileKotlin` пропущена, потому что Kotlin сравнивает ABI-снимок classpath, а он не изменился: тело функции не влияет на публичный интерфейс. Тестовые задачи запускаются заново, так как у них изменился входной classpath (jar), а их результат не кешировался.
