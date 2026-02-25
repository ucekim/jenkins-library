def selectDeployEnv(branch) {
    if (branch == "develop") return ["dev"]
    if (branch == "staging") return ["test"]
    if (branch.startsWith("refs/tags")) return ["test","prod"]
    return []
}

def runCD(Map params) {
    // Manual Approval (Prod Only)
    if (params.serviceBranch.startsWith("refs/tags") && params.manualApproval == 'true') {
        input message: "Approve production deployment?"
    }

    // Deploy stage
    def targets = selectDeployEnv(params.serviceBranch)
    targets.each { envName ->
        echo "Deploying ${params.serviceName} to ${envName}"
    }

    // Schedule Prod Deploy
    if (params.serviceBranch.startsWith("refs/tags")) {
        echo "Scheduling production deployment..."
    }

    // Post actions
    echo "CD SUCCESS for ${params.serviceName}"
}
