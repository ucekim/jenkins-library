def call(Map config = [:]) {
    def appName = config.get('name', 'default-app')

    echo "Testing ${appName}"

    sh """
        echo Running unit tests for ${appName}...
        # mvn test
    """
}
