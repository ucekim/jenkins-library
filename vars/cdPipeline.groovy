def selectDeployEnv(branch) {
    if (branch == "develop") return ["dev"]
    if (branch == "staging") return ["test"]
    if (branch.startsWith("refs/tags")) return ["test","prod"]
    return []
}

def runCD(Map params) {
    pipeline {
        agent any

        stages {
            stage('Manual Approval (Prod Only)') {
                when {
                    expression {
                        params.serviceBranch.startsWith("refs/tags") &&
                        params.manualApproval == 'true'
                    }
                }
                steps { input message: "Approve production deployment?" }
            }

            stage('Deploy') {
                when { expression { selectDeployEnv(params.serviceBranch).size() > 0 } }
                steps {
                    script {
                        selectDeployEnv(params.serviceBranch).each { envName ->
                            echo "Deploying ${params.serviceName} to ${envName}"
                        }
                    }
                }
            }

            stage('Schedule Prod Deploy') {
                when { expression { params.serviceBranch.startsWith("refs/tags") } }
                steps { echo "Scheduling production deployment..." }
            }
        }

        post {
            success { echo "CD SUCCESS for ${params.serviceName}" }
            failure { echo "CD FAILED for ${params.serviceName}" }
        }
    }
}
