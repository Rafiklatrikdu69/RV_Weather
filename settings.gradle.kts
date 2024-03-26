pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter() // Attention : ce référentiel va bientôt fermer
        maven { url = uri("https://jitpack.io") } // Ajoutez cette ligne dans votre settings.gradle
    }
}

rootProject.name = "RV_weather"
include(":app")
