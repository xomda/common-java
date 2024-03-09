---
outline: deep
---

# Using XOMDA with Gradle

It's easy to add XOMDA to Gradle, this automates a lot and provides a seamless experience.

## Build configuration

First of all, make sure that Gradle knows where to find the plugin. Therefore, you need to configure the plugin
management in your settings.gradle.

**_settings.gradle_**

```groovy
pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}
```

Next, add the Gradle plugin to your individual build files.

**_gradle.build_**

```groovy
plugins {
    id "org.xomda.plugin-gradle" version "latest.release"
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    // The "xomda" configuration lets you add dependencies
    // to your build-templates
    xomda libs.log4j
}

xomda {
    // The "xomda" extensions lets you configure XOMDA
    // (see Plugin configuration below for the available options)
}
```

## Plugin configuration

### Classpath

The packages in which the plugin will look for interfaces, to create proxies from.

```groovy
xomda {
    // org.xomda.model is the default classpath
    // it's the package where the XOMDA object model is found.
    classpath ["org.xomda.model"]
}
```

### Models

By default, the models will automatically be picked up. If you want strict control over which CSV's are used as input,
this can be set by specifying the models.

```groovy
xomda {
    models ["./xomda/Model.csv"]
}
```

### Plugins & Templates

The gradle plugin can be extended in various ways,
even the Templates themselves are plugins and can be injected through config.

Plugins can also be bundled in a _XOMDAModule_, which is actually a container of other plugins.

```groovy
xomda {
    plugins ["org.xomda.core.module.XOMDACore"]
}
```

The XOMDA default plugin (`XOMDACore`) is an example of a module. It contains three plugins:

- one for defining parent/child relationships (`XOMDAReverseEntity`),
- one for resolving references to other XOMDA objects (`XOMDATypeRefs`)
- and one for recursively generating code out of a parsed object model (`XOMDACodeTemplate`)

## Templates

After XOMDA is added to your Gradle build, it needs to be provided with some templates
that will generate your code and/or assets.

All of this template code needs to be placed in a directory called `src/xomda/java` in your project.

Another way of working with templates — and maybe share them across multiple projects — is to
specify them in the XOMDA configuration as plugins ([see above](#plugins-templates)).
