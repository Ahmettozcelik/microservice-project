pipeline {
    agent any

    environment {
        IMAGE_TAG = "latest"
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Maven') {
            steps {
                sh '''
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
                    docker build -t eureka-server ./eureka-server
                    docker build -t config-server ./config-server
                    docker build -t demo-service ./demo-service
                    docker build -t api-gateway ./api-gateway
                '''
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                    docker compose down || true
                    docker compose up -d --build
                '''
            }
        }
    }

    post {
        success {
            echo 'CI/CD SUCCESS 🚀'
        }
        failure {
            echo 'CI/CD FAILED ❌'
        }
    }
}