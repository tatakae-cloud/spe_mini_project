
pipeline {
    agent any

    environment {
        JAVA_HOME = '/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home'
        PATH = "${JAVA_HOME}/bin:/Users/saiganesh/.docker/bin:/opt/homebrew/bin:${env.PATH}"

        DOCKER_IMAGE = 'saiganesh690/calculator-api'
        IMAGE_TAG = "${BUILD_NUMBER}"
    }

    stages {
        // Stage 1: Checkout
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        // Stage 2: Build application
        stage('Build') {
            steps {
                sh '''
                    echo "Checking Java configuration..."
                    echo "JAVA_HOME=$JAVA_HOME"
                    java -version
                    javac -version

                    echo "Building application..."
                    ./mvnw clean package -DskipTests
                '''
            }
        }

        // Stage 3: Run tests
        stage('Test') {
            steps {
                sh './mvnw test'
            }
            post {
                always {
                    junit allowEmptyResults: true,
                          testResults: 'target/surefire-reports/*.xml'
                }
            }
        }

        // Stage 4: Build and push multi-platform Docker images
        stage('Docker Build & Push') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-credentials',
                        usernameVariable: 'DOCKERHUB_USER',
                        passwordVariable: 'DOCKERHUB_TOKEN'
                    )
                ]) {
                    sh '''
                        set -e
                        set +x

                        echo "$DOCKERHUB_TOKEN" | docker login \
                            -u "$DOCKERHUB_USER" --password-stdin

                        docker buildx build \
                            --platform linux/amd64,linux/arm64 \
                            -t ${DOCKER_IMAGE}:${IMAGE_TAG} \
                            -t ${DOCKER_IMAGE}:latest \
                            --push .

                        docker logout
                    '''
                }
            }
        }

        // Stage 5: Deploy using Docker Compose
        stage('Deploy') {
            steps {
                sh '''
                    set -e
                    export IMAGE_TAG=${IMAGE_TAG}

                    echo "Pulling Docker images..."
                    docker compose pull

                    echo "Starting containers..."
                    docker compose up -d --remove-orphans

                    echo "Checking application health..."

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
                to: 'saisathviksvn@gmail.com',
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
                to: 'saisathviksvn@gmail.com',
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
