plugins {
    id("dev.prism")
}

group = "com.portingdeadmods"
version = "1.1.0"

prism {
    curseMaven()
    maven("Leclown", "https://maven.leclowndu93150.dev/releases")
    maven("Xander", "https://maven.isxander.dev/releases")

    publishing {
        changelogFile = "CHANGELOG.md"

        curseforge {
            accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
            projectId = "1167127"
        }

        modrinth {
            accessToken = providers.environmentVariable("MODRINTH_TOKEN")
            projectId = "mrR15dsb"
        }

        dependencies {
            requires("baguettelib")
            requires("yacl")
        }
    }

    metadata {
        modId = "stickit"
        name = "Stick It!"
        description = "Place blocks and items anywhere in the world!"
        license = "MIT"
        author("Leclowndu93150")
        author("Reclipse")
        author("BlueAgent")
    }

    version("1.21.1") {
        parchmentMinecraftVersion = "1.21.1"
        parchmentMappingsVersion = "2024.11.17"

        common {
            modCompileOnly("com.leclowndu93150.baguettelib:baguettelib-1.21.1-common:2.0.7")
            modCompileOnly("dev.isxander:yet-another-config-lib:3.8.1+1.21.1-neoforge")
        }

        fabric {
            loaderVersion = "0.19.2"
            fabricApi("0.116.12+1.21.1")

            dependencies {
                modImplementation("com.leclowndu93150.baguettelib:baguettelib-1.21.1-fabric:2.0.7")
                modCompileOnly("dev.isxander:yet-another-config-lib:3.8.1+1.21.1-fabric")
                modRuntimeOnly("dev.isxander:yet-another-config-lib:3.8.1+1.21.1-fabric")
            }

            publishingDependencies {
                requires("fabric-api")
                optional("modmenu")
            }
        }

        neoforge {
            loaderVersion = "21.1.228"

            dependencies {
                modImplementation("com.leclowndu93150.baguettelib:baguettelib-1.21.1-neoforge:2.0.7")
                modCompileOnly("dev.isxander:yet-another-config-lib:3.8.1+1.21.1-neoforge")
                modRuntimeOnly("dev.isxander:yet-another-config-lib:3.8.1+1.21.1-neoforge")
            }
        }
    }

    version("26.1.2") {
        common {
            modCompileOnly("com.leclowndu93150.baguettelib:baguettelib-26.1.2-common:2.0.7")
            modCompileOnly("dev.isxander:yet-another-config-lib:3.9.7+26.1-neoforge")
        }

        fabric {
            loaderVersion = "0.19.5"
            fabricApi("0.155.3+26.1.2")

            dependencies {
                modImplementation("com.leclowndu93150.baguettelib:baguettelib-26.1.2-fabric:2.0.7")
                modCompileOnly("dev.isxander:yet-another-config-lib:3.9.7+26.1-fabric")
                modRuntimeOnly("dev.isxander:yet-another-config-lib:3.9.7+26.1-fabric")
            }

            publishingDependencies {
                requires("fabric-api")
                optional("modmenu")
            }
        }

        neoforge {
            loaderVersion = "26.1.2.124"

            dependencies {
                modImplementation("com.leclowndu93150.baguettelib:baguettelib-26.1.2-neoforge:2.0.7")
                modCompileOnly("dev.isxander:yet-another-config-lib:3.9.7+26.1-neoforge")
                modRuntimeOnly("dev.isxander:yet-another-config-lib:3.9.7+26.1-neoforge")
            }
        }
    }

    version("26.2") {
        common {
            modCompileOnly("com.leclowndu93150.baguettelib:baguettelib-26.2-common:2.0.7")
            modCompileOnly("dev.isxander:yet-another-config-lib:3.9.7+26.2-neoforge")
        }

        fabric {
            loaderVersion = "0.19.5"
            fabricApi("0.161.0+26.2")

            dependencies {
                modImplementation("com.leclowndu93150.baguettelib:baguettelib-26.2-fabric:2.0.7")
                modCompileOnly("dev.isxander:yet-another-config-lib:3.9.7+26.2-fabric")
                modRuntimeOnly("dev.isxander:yet-another-config-lib:3.9.7+26.2-fabric")
            }

            publishingDependencies {
                requires("fabric-api")
                optional("modmenu")
            }
        }

        neoforge {
            loaderVersion = "26.2.0.93"

            rawProject {
                configurations.configureEach { exclude(mapOf("group" to "curse.maven", "module" to "curios-309927")) }
            }

            dependencies {
                modImplementation("com.leclowndu93150.baguettelib:baguettelib-26.2-neoforge:2.0.7")
                modCompileOnly("dev.isxander:yet-another-config-lib:3.9.7+26.2-neoforge")
                modRuntimeOnly("dev.isxander:yet-another-config-lib:3.9.7+26.2-neoforge")
            }
        }
    }

    version("26.3") {
        common {
            modCompileOnly("com.leclowndu93150.baguettelib:baguettelib-26.3-common:2.0.7")
            modCompileOnly("dev.isxander:yet-another-config-lib:3.9.7+26.3-neoforge")
        }

        fabric {
            loaderVersion = "0.19.5"
            fabricApi("0.165.0+26.3")

            dependencies {
                modImplementation("com.leclowndu93150.baguettelib:baguettelib-26.3-fabric:2.0.7")
                modCompileOnly("dev.isxander:yet-another-config-lib:3.9.7+26.3-fabric")
                modRuntimeOnly("dev.isxander:yet-another-config-lib:3.9.7+26.3-fabric")
            }

            publishingDependencies {
                requires("fabric-api")
                optional("modmenu")
            }
        }

        neoforge {
            loaderVersion = "26.3.0.99"

            dependencies {
                modImplementation("com.leclowndu93150.baguettelib:baguettelib-26.3-neoforge:2.0.7")
                modCompileOnly("dev.isxander:yet-another-config-lib:3.9.7+26.3-neoforge")
                modRuntimeOnly("dev.isxander:yet-another-config-lib:3.9.7+26.3-neoforge")
            }
        }
    }
}
