def call(Map config = [:]) {
    def appName = config.get('name', 'default-app')
    def env = config.get('env', 'dev')

    echo "Deploying ${appName} to ${env} environment"

    // Örnek deploy komutu
    sh """
        echo Deploying ${appName} to ${env}...
        # kubectl apply -f k8s/${env}/${appName}.yaml
    """
}
