
pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'svnsaisathvik/calculator-api'
        IMAGE_TAG = "${BUILD_NUMBER}"
    }

    stages {
        // Stage 1: Get the latest code from GitHub
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        // Stage 2: Build the application
        stage('Build') {
            steps {
                sh './mvnw clean package -DskipTests'
            }
        }

        // Stage 3: Run tests and publish JUnit reports
        stage('Test') {
            steps {
                sh './mvnw test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        // Stage 4: Build and tag the Docker image
        stage('Docker Build') {
            steps {
                sh '''
                    docker build \
                        -t ${DOCKER_IMAGE}:${IMAGE_TAG} \
                        -t ${DOCKER_IMAGE}:latest .
                '''
            }
        }

        // Stage 5: Push images to Docker Hub securely
        stage('Push') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-credentials',
                        usernameVariable: 'DOCKERHUB_USER',
                        passwordVariable: 'DOCKERHUB_TOKEN'
                    )
                ]) {
                    sh '''
                        set +x
                        echo "$DOCKERHUB_TOKEN" | docker login \
                            -u "$DOCKERHUB_USER" --password-stdin

                        docker push ${DOCKER_IMAGE}:${IMAGE_TAG}
                        docker push ${DOCKER_IMAGE}:latest

                        docker logout
                    '''
                }
            }
        }

        // Stage 6: Deploy using Docker Compose and verify health
        stage('Deploy') {
            steps {
                sh '''
                    docker compose pull
                    docker compose up -d --remove-orphans

                    for i in $(seq 1 30); do
                        if curl -fsS \
                            http://localhost:8081/actuator/health; then
                            echo "Application is healthy."
                            exit 0
                        fi
                        sleep 2
                    done

                    echo "Application health check failed."
                    docker compose ps
                    docker compose logs --tail=100
                    exit 1
                '''
            }
        }
    }


    post {
        success {
            echo 'Pipeline completed successfully!'

            emailext(
                to: 'saisathviksvn@gmail.com', // Change to your recipient email
                subject: "SUCCESS: ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}",
                body: """
                    <h2>Pipeline Succeeded!</h2>
                    <p><b>Job:</b> ${env.JOB_NAME}</p>
                    <p><b>Build Number:</b> ${env.BUILD_NUMBER}</p>
                    <p><b>Status:</b> SUCCESS</p>
                    <p><a href="${env.BUILD_URL}">View Build Details</a></p>
                """,
                mimeType: 'text/html'
            )
        }

        failure {
            echo 'Pipeline failed!'

            emailext(
                to: 'saisathviksvn@gmail.com', // Change to your recipient email
                subject: "FAILURE: ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}",
                body: """
                    <h2>Pipeline Failed!</h2>
                    <p><b>Job:</b> ${env.JOB_NAME}</p>
                    <p><b>Build Number:</b> ${env.BUILD_NUMBER}</p>
                    <p><b>Status:</b> FAILURE</p>
                    <p>Please check the console output to identify the error.</p>
                    <p><a href="${env.BUILD_URL}">View Build Details</a></p>
                """,
                mimeType: 'text/html'
            )
        }
    }
}
