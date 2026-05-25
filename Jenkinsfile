pipeline {
    agent any

    environment {
        IMAGE_TAG = "${GIT_COMMIT.take(7)}"
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Maven Services') {
            agent {
                docker {
                    image 'maven:3.9.6-eclipse-temurin-21'
                }
            }
            steps {
                sh '''
                    set -e

                    mvn -v
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

                    docker compose down || true
                    docker compose up -d
                '''
            }
        }

        stage('Health Check') {
            steps {
                sh '''
                    echo "Waiting services..."
                    sleep 30

                    curl -f http://localhost:8080/actuator/health || exit 1
                '''
            }
        }
    }

    post {
        success {
            echo "SUCCESS 🚀 ${IMAGE_TAG}"
        }

        failure {
            echo "FAILED ❌ rollback..."

            sh '''
                docker compose down
                docker compose up -d
            '''
        }

        always {
            cleanWs()
        }
    }
}