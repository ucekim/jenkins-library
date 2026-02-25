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

// Shared library fonksiyonu sadece logic çalıştıracak
def runCI(Map params) {
    stage('Prepare') {
        script {
            env.IMAGE_TAG = generateImageTag(params.gitCommitSha)
            env.DEPLOY_TARGETS = selectDeployEnv(params.serviceBranch).join(',')
            echo "Generated Image Tag: ${env.IMAGE_TAG}"
            echo "Deploy Targets: ${env.DEPLOY_TARGETS}"
        }
    }

    if (params.serviceBranch.contains("refs/tags")) {
        stage('Tag Validation (Only for Tags)') {
            script {
                def tagName = params.serviceBranch.tokenize('/').last()
                def pattern = /^v\d+\.\d+\.\d+$/
                echo "Detected tag: ${tagName}"
                if (!(tagName ==~ pattern)) error "Tag format must be vMAJOR.MINOR.PATCH"
            }
        }
    }

    stage('Build Image') {
        echo "Building image ${params.registry}/apps/${params.serviceName}:${env.IMAGE_TAG}"
        // sh "docker build -t ${params.registry}/apps/${params.serviceName}:${env.IMAGE_TAG} ."
    }

    stage('Test') {
        if (!params.skipTests) {
            echo "Running tests for ${params.serviceName}"
        }
    }

    stage('Push Image') {
        echo "Pushing image ${params.registry}/apps/${params.serviceName}:${env.IMAGE_TAG}"
    }
}
