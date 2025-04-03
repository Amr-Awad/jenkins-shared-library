def call(Map params = [:]) {
    script {
        try {
            def appPort = params.get('appPort', 8081)

            echo "Starting build process..."
            sh './gradlew clean build'

            sh 'pwd'
           
            if (!fileExists(env.JAR_FILE)) {
                error "JAR file not found: ${env.JAR_FILE}"
            }

            echo "Starting the JAR file: ${env.JAR_FILE}"
            def pid = sh(script: "java -jar ${env.JAR_FILE} & echo \$!", returnStdout: true).trim()

            echo "Waiting for the application to start..."
            timeout(time: 15, unit: 'SECONDS') {
                waitUntil {
                    try {
                        sh "curl --fail http://localhost:${appPort}"
                        return true
                    } catch (Exception e) {
                        return false
                    }
                }
            }

            echo "Application is running successfully."
        } catch (Exception e) {
            echo "An error occurred: ${e.message}"
            throw e
        } finally {
            echo "Cleaning up..."
            if (pid) {
                echo "Terminating process with PID: ${pid}"
                sh "kill ${pid} || true"
            }
        }
    }
}
