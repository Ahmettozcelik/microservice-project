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
            steps {
                sh '''
                    set -e

                    docker run --rm \
                      -v $WORKSPACE:/app \
                      -w /app \
                      maven:3.9.6-eclipse-temurin-21 \
                      mvn -f eureka-server/pom.xml clean package -DskipTests

                    docker run --rm \
                      -v $WORKSPACE:/app \
                      -w /app \
                      maven:3.9.6-eclipse-temurin-21 \
                      mvn -f config-server/pom.xml clean package -DskipTests

                    docker run --rm \
                      -v $WORKSPACE:/app \
                      -w /app \
                      maven:3.9.6-eclipse-temurin-21 \
                      mvn -f demo-service/pom.xml clean package -DskipTests

                    docker run --rm \
                      -v $WORKSPACE:/app \
                      -w /app \
                      maven:3.9.6-eclipse-temurin-21 \
                      mvn -f api-gateway/pom.xml clean package -DskipTests
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

                    docker-compose down || true
                    docker-compose up -d --build
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
                docker-compose down
                docker-compose up -d
            '''
        }

        always {
            cleanWs()
        }
    }
}