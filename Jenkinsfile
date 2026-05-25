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
                    echo "=== WORKSPACE STRUCTURE ==="
                    ls -R
                '''
            }
        }

        stage('Build Maven Services') {
            steps {
                sh '''
                    set -e

                    mvn_cmd="mvn -B -DskipTests clean package"

                    docker run --rm \
                      -v $WORKSPACE:/app \
                      -w /app \
                      maven:3.9.6-eclipse-temurin-21 \
                      $mvn_cmd -f eureka-server/pom.xml

                    docker run --rm \
                      -v $WORKSPACE:/app \
                      -w /app \
                      maven:3.9.6-eclipse-temurin-21 \
                      $mvn_cmd -f config-server/pom.xml

                    docker run --rm \
                      -v $WORKSPACE:/app \
                      -w /app \
                      maven:3.9.6-eclipse-temurin-21 \
                      $mvn_cmd -f demo-service/pom.xml

                    docker run --rm \
                      -v $WORKSPACE:/app \
                      -w /app \
                      maven:3.9.6-eclipse-temurin-21 \
                      $mvn_cmd -f api-gateway/pom.xml
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
            echo "SUCCESS 🚀 IMAGE: ${IMAGE_TAG}"
        }

        failure {
            echo "FAILED ❌ rolling back..."

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