pipeline {
    agent any

    environment {
        IMAGE_TAG = "${env.GIT_COMMIT?.take(7) ?: 'latest'}"
    }

    stages {

        stage('Checkout') {
            steps {
                deleteDir()
                checkout scm

                sh '''
                    echo "=== WORKSPACE ==="
                    ls -R
                '''
            }
        }

        stage('Build Maven') {
            steps {
                sh '''
                    set -e

                    echo "Building all services with local Maven wrapper..."

                    cd eureka-server && ./mvnw clean package -DskipTests && cd ..
                    cd config-server && ./mvnw clean package -DskipTests && cd ..
                    cd demo-service && ./mvnw clean package -DskipTests && cd ..
                    cd api-gateway && ./mvnw clean package -DskipTests && cd ..
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
                    docker compose up -d --build
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
                docker compose down || true
                docker compose up -d
            '''
        }

        always {
            cleanWs()
        }
    }
}