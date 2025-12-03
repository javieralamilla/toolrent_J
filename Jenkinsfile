pipeline {
    agent any

    environment {
        DOCKER_HUB_USER = 'javieralamilla'
        BACKEND_IMAGE = "${DOCKER_HUB_USER}/toolrent-backend"
        FRONTEND_IMAGE = "${DOCKER_HUB_USER}/toolrent-frontend"
        TAG = 'latest'
    }

    stages {
        stage('Checkout SCM') {
            steps {
                checkout scm
            }
        }

        // --- AQUÍ ESTÁ EL CAMBIO MÁGICO ---
        stage('Test Backend') {
            agent {
                // Usamos una imagen oficial de Maven para hacer el test
                docker {
                    image 'maven:3.9.6-eclipse-temurin-17'
                    args '-v /root/.m2:/root/.m2' // Para que sea rápido
                }
            }
            steps {
                dir('TINGESO') {
                    // Ya no usamos ./mvnw. Usamos el comando 'mvn' oficial
                    sh 'mvn test'
                }
            }
        }
        // ----------------------------------

        stage('Build & Push Backend') {
            steps {
                script {
                    dir('TINGESO') {
                        withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials', passwordVariable: 'DOCKER_PASS', usernameVariable: 'DOCKER_USER')]) {
                            sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
                            sh "docker build -t ${BACKEND_IMAGE}:${TAG} ."
                            sh "docker push ${BACKEND_IMAGE}:${TAG}"
                        }
                    }
                }
            }
        }

        stage('Build & Push Frontend') {
            steps {
                script {
                    dir('frontend_tingeso') {
                        withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials', passwordVariable: 'DOCKER_PASS', usernameVariable: 'DOCKER_USER')]) {
                            sh "docker build -t ${FRONTEND_IMAGE}:${TAG} ."
                            sh "docker push ${FRONTEND_IMAGE}:${TAG}"
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            sh "docker rmi ${BACKEND_IMAGE}:${TAG} || true"
            sh "docker rmi ${FRONTEND_IMAGE}:${TAG} || true"
        }
    }
}