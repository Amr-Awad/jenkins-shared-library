def call() {
    script {
        try {
            echo 'Executing unit tests using Gradle...'
            sh 'chmod +x ./gradlew'
            sh './gradlew test'
            echo 'Unit tests executed successfully.'

            junit 'build/test-results/test/**/*.xml'

            def testResultsFile = 'build/test-results/test/TEST-com.example.demo.MathServiceTest.xml'
            if (fileExists(testResultsFile)) {
                def testResults = readFile(testResultsFile)
                if (testResults.contains('failures="0"') && testResults.contains('errors="0"')) {
                    echo 'All unit tests passed successfully.'
                } else {
                    error 'Some unit tests failed.'
                }
            } else {
                error "Test results file not found: ${testResultsFile}"
            }
        } catch (Exception e) {
            error "Unit tests failed: ${e.message}"
        }
    }
}
