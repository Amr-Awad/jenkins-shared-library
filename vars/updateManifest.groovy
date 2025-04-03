def call() {
    script {
        dir('manifests') {
            git(
                url: "${env.MANIFEST_REPO}",
                credentialsId: "${env.GIT_CREDENTIALS}",
                branch: 'main'
            )
            
            sh """
                # Update image tag with proper YAML indentation
                sed -i "s|^\\([[:space:]]*image: \\)amrawad12/my-springboot-app:.*|\\1${env.DOCKER_IMAGE}:jenkins_${BUILD_NUMBER}|g" deployment.yaml
                
                # Verify changes
                echo "Updated deployment.yaml:"
                grep "image:" deployment.yaml
                
                # Commit and push
                git config user.email "jenkins@example.com"
                git config user.name "Jenkins"
                git add deployment.yaml
                git commit -m "CI: Update ivolve-app to jenkins_${BUILD_NUMBER}"
                git push origin main

                # Display current deployment.yaml and verify image tag
                echo "Current deployment.yaml:"
                cat deployment.yaml
                echo "\nImage tag verification:"
                grep "image:" deployment.yaml | grep "jenkins_${BUILD_NUMBER}"
            """
        }
    }
}
