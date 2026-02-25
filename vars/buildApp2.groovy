def call(Map config = [:]) {
    def appName = config.get('name', 'default-app')
    def version = config.get('version', '1.0.0')

    echo "Building ${appName} version ${version}"

    sh """
        echo Compiling ${appName}...
        # mvn clean package -Dversion=${version}
    """
}
