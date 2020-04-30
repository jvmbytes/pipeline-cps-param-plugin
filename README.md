# Parameter Pipeline Plugin

The current official plugin [workflow-cps](https://github.com/jenkinsci/workflow-cps-plugin/) 
does provide a way to retrieve a Jenkinsfile through a SCM, such as Git. 
The goal of this plugin is to provide another way to retrieve Jenkinsfiles via parameter of pipeline.

## How to use the plugin

1. Create a pipeline job
2. Select the `Pipeline script from Parameter` option in the `Pipeline` section

## Releasing
To release simply call the following script:
```
mvn release:prepare release:perform
```

## Contributing

You can contribute to this plugin by retrieving the source and following the [official Jenkins plugin tutorial](https://wiki.jenkins.io/display/JENKINS/Plugin+tutorial) to install, run, test and package it.

## Legal

This project is licensed under the terms of the [MIT license](LICENSE).