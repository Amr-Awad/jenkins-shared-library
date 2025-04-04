def call() {
    script {
        dir('manifests') {
            try {
                git(
                url: "${env.MANIFEST_REPO}",
                credentialsId: "${env.GIT_CREDENTIALS}",
                branch: 'main'
            )

                sh """
                # Update image tag with proper YAML indentation
                sed -i "s|^\\([[:space:]]*image: \\)amrawad12/my-springboot-app:.*|\\1${env.DOCKER_IMAGE}:jenkins_${BUILD_NUMBER}|g" base/deployment.yaml

                # Verify changes
                echo "Updated deployment.yaml:"
                grep "image:" deployment.yaml

                # Commit and push
                git config user.email "jenkins@example.com"
                git config user.name "Jenkins"
                git add -u
                git commit -m "CI: Update ivolve-app to jenkins_${BUILD_NUMBER}"
                git push origin main

                # Display current deployment.yaml and verify image tag
                echo "Current deployment.yaml:"
                cat deployment.yaml
                echo "\nImage tag verification:"
                grep "image:" deployment.yaml | grep "jenkins_${BUILD_NUMBER}"
            """
        } catch (Exception e) {
                error "Failed to update manifest: ${e.message}"
            }
        }
    }
}
