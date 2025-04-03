def call() {
    // Ensure SonarQube server is configured in Jenkins
    def sonarServer = 'SonarQube' // Default SonarQube server name in Jenkins
    script {
        // Run SonarQube analysis using Gradle
        withSonarQubeEnv(sonarServer) {
            sh './gradlew sonar'
        }
        // Wait for SonarQube quality gate result
        timeout(time: 3, unit: 'MINUTES') {
            def qualityGate = waitForQualityGate()
            if (qualityGate.status != 'OK') {
                error "Pipeline aborted due to quality gate failure: ${qualityGate.status}"
            }
        }
    }
}