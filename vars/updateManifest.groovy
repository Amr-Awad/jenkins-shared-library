def call() {
    script {
        def DEPLOYMENT_PATH = "base/deployment.yaml"
        dir('manifests') {
            try {
                git(
                url: "${env.MANIFEST_REPO}",
                credentialsId: "${env.GIT_CREDENTIALS}",
                branch: 'main'
            )

                sh """
                # Update image tag with proper YAML indentation
                sed -i "s|^\\([[:space:]]*image: \\)amrawad12/my-springboot-app:.*|\\1${env.DOCKER_IMAGE}:jenkins_${BUILD_NUMBER}|g" ${DEPLOYMENT_PATH}

                # Verify changes
                echo "Updated ${DEPLOYMENT_PATH}:"
                grep "image:" ${DEPLOYMENT_PATH}

                # Commit and push
                git config user.email "jenkins@example.com"
                git config user.name "Jenkins"
                git add -u
                git commit -m "CI: Update ivolve-app to jenkins_${BUILD_NUMBER}"
                git push origin main

                # Display current deployment.yaml and verify image tag
                echo "Current deployment.yaml:"
                cat ${DEPLOYMENT_PATH}
                echo "\nImage tag verification:"
                grep "image:" ${DEPLOYMENT_PATH} | grep "jenkins_${BUILD_NUMBER}"
            """
        } catch (Exception e) {
                error "Failed to update manifest: ${e.message}"
            }
        }
    }
}
