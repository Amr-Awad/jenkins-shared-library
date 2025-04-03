def call(Map params = [:]) {
    script {
        try {
            def appPort = params.get('appPort', 8081)

            echo "Starting build process..."
            sh './gradlew clean build'

            sh 'pwd'
            sh 'ls -lah build/libs/'  // To list the files and ensure the jar is there
            sh 'ls -lah build/libs/*.jar'  // To list the files and ensure the jar is there

           
            if (!fileExists(env.JAR_FILE)) {
                error "JAR file not found: ${env.JAR_FILE}"
            }

            echo "Starting the JAR file: ${env.JAR_FILE}"
            sh "java -jar ${env.JAR_FILE} & echo \$!"

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

            echo "Terminating java process"
            sh "pkill -f 'java -jar build/libs/demo-0.0.1-SNAPSHOT.jar' || true"
        } catch (Exception e) {
            echo "An error occurred: ${e.message}"
            sh "pkill -f 'java -jar build/libs/demo-0.0.1-SNAPSHOT.jar' || true"
            throw e
        } 
    }
}
