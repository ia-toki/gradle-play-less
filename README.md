# gradle-play-less

[![GitHub Release](https://img.shields.io/github/release/ia-toki/gradle-play-less.svg)](https://github.com/ia-toki/gradle-play-less)
[![Build Status](https://img.shields.io/travis/ia-toki/gradle-play-less/master.svg)](https://travis-ci.org/ia-toki/gradle-play-less)

Gradle plugin for compiling LESS assets in Play framework projects.

## Usage

1. Apply the [Play framework plugin](https://docs.gradle.org/current/userguide/play_plugin.html).
1. Apply this plugin by adding the following to your `build.gradle`:

   ```
   buildscript {
       repositories {
           jcenter()
       }
 
       dependencies {
           classpath 'org.iatoki:gradle-play-less:0.2.0'
       }
   }
 
   apply plugin: 'org.iatoki.play-less'
   ```
   
## Features

This plugin adds a new `less` source set that contains all `*.less` files under `app/assets` directory (directly or indirectly). This plugin also adds a new `:compilePlayBinaryPlayLess` task, which compiles the LESS files into CSS files to the `$buildDir/src/play/binary/lessCss` directory. The produced CSS files will then be included the resulting Play assets jar (produced by the :createPlayBinaryAssetsJar` task).

If [gradle-play-webjars](https://github.com/ia-toki/gradle-play-webjars) plugin is applied, LESS files can reference WebJars files under `lib/` path, similar to what is described in the [Play framework LESS CSS documentation](https://www.playframework.com/documentation/2.5.x/AssetsLess#Using-LESS-with-Bootstrap).

This plugin uses [less4j](https://github.com/SomMeri/less4j) for compiling LESS into CSS.

## Configuration

You can override the default `less` source set, e.g. as follows:

```
model {
    components {
        play {
            sources {
                less {
                    source {
                        srcDir 'app/assets'
                        include '**/*.less'
                        exclude '**/_*.less'
                    }
                }
            }
        }
    }
}
```

If required, you can also add new source sets:

```
model {
    components {
        play {
            sources {
                extraLess(org.iatoki.gradle.play.less.LessSourceSet) {
                    source {
                        srcDir 'app/extra-assets'
                        include '**/*.less'
                    }
                }
            }
        }
    }
}
```

The above will add `:compilePlayBinaryPlayExtraLess` task to compile LESS files in the source set.

## Limitations

Currently, the output CSS files are always compressed, and it is not possible to configure any compilation options.
