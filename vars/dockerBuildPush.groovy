/* groovylint-disable NestedBlockDepth */
def call(Map params = [:]) {
    script {
        def registryUrl = params.get('registryUrl', 'https://registry.hub.docker.com')
        def containerPort = params.get('containerPort', 8081)
        def hostPort = params.get('hostPort', 8082)
        def tagVersion = "jenkins_${BUILD_NUMBER}"

        docker.withRegistry(env.DOCKERHUB_CREDENTIALS) {
            def image
            try {
                image = docker.build("${env.DOCKER_IMAGE}:${tagVersion}", ".")
                
                // Run the container before pushing the image
                try {
                    /* groovylint-disable-next-line LineLength */
                    sh "docker run -d --name test-container -p ${hostPort}:${containerPort} ${env.DOCKER_IMAGE}:${tagVersion}"
                    
                    timeout(time: 30, unit: 'SECONDS') {
                        waitUntil {
                            def status = sh(
                                script: "docker inspect --format='{{.State.Status}}' test-container",
                                returnStdout: true
                            ).trim()
                            return status == 'running'
                        }
                    }
                    timeout(time: 30, unit: 'SECONDS') {
                        waitUntil {
                            try {
                                sh "curl --fail http://localhost:${hostPort}"
                                return true
                            } catch (Exception e) {
                                return false
                            }
                        }
                    }
                } catch (e) {
                    error "Container validation failed: ${e.message}"
                }

                // Push the image after validation
                try{
                    sh "docker push ${env.DOCKER_IMAGE}:${tagVersion}"
                } catch (e) {
                    error "Docker push failed: ${e.message}"
                }
            } catch (e) {
                error "Docker build, run, or push failed: ${e.message}"
            }
        }
    }
}