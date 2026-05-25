pipeline {
    agent any

    environment {
        IMAGE_TAG = "${GIT_COMMIT.take(7)}"
        COMPOSE = "/usr/local/bin/docker-compose"
        GATEWAY_URL = "http://localhost:8080/actuator/health"
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Maven Services') {
            steps {
                sh '''
                    set -e

                    cd eureka-server && mvn clean package -DskipTests
                    cd ../config-server && mvn clean package -DskipTests
                    cd ../demo-service && mvn clean package -DskipTests
                    cd ../api-gateway && mvn clean package -DskipTests
                '''
            }
        }

        stage('Build Docker Images') {
            steps {
                sh '''
                    set -e

                    docker build -t eureka-server:${IMAGE_TAG} ./eureka-server
                    docker build -t config-server:${IMAGE_TAG} ./config-server
                    docker build -t demo-service:${IMAGE_TAG} ./demo-service
                    docker build -t api-gateway:${IMAGE_TAG} ./api-gateway
                '''
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                    set -e

                    echo "Stopping previous containers..."
                    $COMPOSE down || true

                    echo "Starting new version: ${IMAGE_TAG}"
                    $COMPOSE up -d --build
                '''
            }
        }

        stage('Health Check') {
            steps {
                sh '''
                    echo "Waiting services to start..."
                    sleep 20

                    echo "Checking Gateway health..."

                    curl -f $GATEWAY_URL || (
                        echo "Health check failed! Triggering rollback..."
                        exit 1
                    )
                '''
            }
        }
    }

    post {

        success {
            echo "SUCCESS 🚀 Deployment completed - ${IMAGE_TAG}"
        }

        failure {
            echo "FAILED ❌ Rolling back..."

            sh '''
                set +e
                $COMPOSE down
                $COMPOSE up -d
            '''
        }
    }
}