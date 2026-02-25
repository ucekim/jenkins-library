def call(Map config = [:]) {

    pipeline {
        agent any

        stages {

            stage('Build') {
                steps {
                    sh "echo Building ${config.appName}"
                }
            }

            stage('Test') {
                steps {
                    sh "echo Testing ${config.appName}"
                }
            }

        }
    }
}

