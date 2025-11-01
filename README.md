# FlavorMate

<p align="center">
    <img src="docs/logo_transparent.png" alt="FlavorMate logo" height="64px">
</p>

FlavorMate is your personal, self-hosted, open-source recipe management app, available on iOS, macOS, Android, and as a
web application. You can also build it from source for Linux and Windows. Organize your culinary creations by
categorizing and tagging them to suit your needs. Whether you’re crafting a recipe from scratch or importing one from
the web, FlavorMate makes it easy.

Stuck on what to cook or bake? Let FlavorMate inspire you with the Recipe of the Day or choose a dish at random. For
those following vegetarian or vegan lifestyles, simply set your preference in your profile, and you'll receive recipes
tailored just for you.

> [!TIP]
> This is the repository for the FlavorMate backend migrator, which is written in Kotlin.<br>
> For the backend, please visit [this repository](https://github.com/FlavorMate/flavormate-server).

## Getting Started

<details>
<summary>Docker</summary>

1. Create a `docker-compose.yaml` file (or download one from the [examples](./example))
2. Download the [.env.template](./example/.env.template) file and rename it to `.env`.
3. Enter your details into the `.env` file
4. Start your container with `docker compose up -d --remove-orphans`

</details>

<details>
<summary>Barebone</summary>

You must have these dependencies installed:

- Postgresql
- Java 21

1. Download the latest [FlavorMate-Migrator.jar](https://github.com/FlavorMate/flavormate-migrator/releases).
2. Download the [.env.template](./example/.env.template) file and rename it to `.env`.
3. Enter your details in the `.env` file
4. Start the migrator with `java -jar FlavorMate-Migrator.jar`.

</details>

## Environment Variables

|                  Key                   | Required |                 Description                 |     Example      |
|----------------------------------------|----------|---------------------------------------------|------------------|
| FLAVORMATE_MIGRATOR_DB_SOURCE_HOST     | Yes      | The source (FlavorMate 2) database host     | `localhost`      |
| FLAVORMATE_MIGRATOR_DB_SOURCE_PORT     | Yes      | The source (FlavorMate 2) database port     | `5432`           |
| FLAVORMATE_MIGRATOR_DB_SOURCE_DB       | Yes      | The source (FlavorMate 2) database database | `flavormate_old` |
| FLAVORMATE_MIGRATOR_DB_SOURCE_USERNAME | Yes      | The source (FlavorMate 2) database user     | `username`       |
| FLAVORMATE_MIGRATOR_DB_SOURCE_PASSWORD | Yes      | The source (FlavorMate 2) database password | `Passw0rd!`      |

|          Key           | Required |                 Description                 |     Example      |
|------------------------|----------|---------------------------------------------|------------------|
| FLAVORMATE_DB_HOST     | Yes      | The target (FlavorMate 3) database host     | `localhost`      |
| FLAVORMATE_DB_PORT     | Yes      | The target (FlavorMate 3) database port     | `5432`           |
| FLAVORMATE_DB_DATABASE | Yes      | The target (FlavorMate 3) database database | `flavormate_new` |
| FLAVORMATE_DB_USER     | Yes      | The target (FlavorMate 3) database user     | `username`       |
| FLAVORMATE_DB_PASSWORD | Yes      | The target (FlavorMate 3) database password | `Passw0rd!`      |

|                Key                 | Required |                         Description                          |   Example    | Default |
|------------------------------------|----------|--------------------------------------------------------------|--------------|---------|
| FLAVORMATE_PATHS_FILES             | Yes      | The path where FlavorMate files are located                  | `/opt/data/` |         |
| FLAVORMATE_MIGRATOR_CLEAN_DATABASE | No       | Should the FlavorMate 3 Database be overwritten if existent? | `true`       | `false` |

> [!WARNING]
> `FLAVORMATE_MIGRATOR_CLEAN_DATABASE` will delete all tables beginning with `v3_`.
> These tables are used by FlavorMate 3, but check if you have any other tables beginning with `v3_` before setting this
> to `true`.

