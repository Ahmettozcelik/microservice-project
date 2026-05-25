pipeline {
    agent any

    environment {
        IMAGE_TAG = "latest"
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/Ahmettozcelik/microservice-project.git'
            }
        }

        stage('Build Maven') {
            steps {
                sh 'cd eureka-server && mvn clean package -DskipTests'
                sh 'cd config-server && mvn clean package -DskipTests'
                sh 'cd demo-service && mvn clean package -DskipTests'
                sh 'cd api-gateway && mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Images') {
            steps {
                sh 'docker build -t eureka-server ./eureka-server'
                sh 'docker build -t config-server ./config-server'
                sh 'docker build -t demo-service ./demo-service'
                sh 'docker build -t api-gateway ./api-gateway'
            }
        }

        stage('Stop Old Containers') {
            steps {
                sh 'docker compose down || true'
            }
        }

        stage('Deploy') {
            steps {
                sh 'docker compose up -d --build'
            }
        }
    }
}