def sendNotification(msg) {
    echo "MAIL => ${msg}"
}

def generateImageTag(commitSha) {
    return commitSha.take(8) + "-" + System.currentTimeMillis()
}

def selectDeployEnv(branch) {
    if (branch == "develop") return ["dev"]
    if (branch == "staging") return ["test"]
    if (branch.startsWith("refs/tags")) return ["test","prod"]
    return []
}

def runCI(Map params) {
    pipeline {
        agent any
        environment {
            REGISTRY = params.registry ?: "demo-registry.local"
            IMAGE_TAG = ""
            DEPLOY_TARGETS = ""
        }

        stages {

            stage('Prepare') {
                steps {
                    script {
                        env.IMAGE_TAG = generateImageTag(params.gitCommitSha)
                        env.DEPLOY_TARGETS = selectDeployEnv(params.serviceBranch).join(',')
                        echo "Generated Image Tag: ${env.IMAGE_TAG}"
                        echo "Deploy Targets: ${env.DEPLOY_TARGETS}"
                    }
                }
            }

            stage('Tag Validation (Only for Tags)') {
                when { expression { params.serviceBranch.contains("refs/tags") } }
                steps {
                    script {
                        def tagName = params.serviceBranch.tokenize('/').last()
                        def pattern = /^v\d+\.\d+\.\d+$/
                        echo "Detected tag: ${tagName}"
                        if (!(tagName ==~ pattern)) error "Tag format must be vMAJOR.MINOR.PATCH"
                    }
                }
            }

            stage('Build Image') {
                steps {
                    echo "Building image ${env.REGISTRY}/apps/${params.serviceName}:${env.IMAGE_TAG}"
                    // sh "docker build -t ${env.REGISTRY}/apps/${params.serviceName}:${env.IMAGE_TAG} ."
                }
            }

            stage('Test') {
                when { expression { !params.skipTests } }
                steps { echo "Running tests for ${params.serviceName}" }
            }

            stage('Push Image') {
                steps { echo "Pushing image ${env.REGISTRY}/apps/${params.serviceName}:${env.IMAGE_TAG}" }
            }
        }

        post {
            success { sendNotification("CI SUCCESS for ${params.serviceName}") }
            failure { sendNotification("CI FAILED for ${params.serviceName}") }
        }
    }
}
