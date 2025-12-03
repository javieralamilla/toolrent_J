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

        stage('Test Backend') {
            agent {
                docker {
                    image 'maven:3.9.6-eclipse-temurin-17'
                    args '-v /root/.m2:/root/.m2'
                }
            }
            steps {
                // CAMBIO AQUÍ: Agregamos "/backend" a la ruta
                dir('TINGESO/backend') {
                    sh 'mvn test'
                }
            }
        }

        stage('Build & Push Backend') {
            steps {
                script {
                    // CAMBIO AQUÍ: También buscamos el Dockerfile dentro de backend
                    dir('TINGESO/backend') {
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
                    // Mantenemos esto igual (a menos que tu frontend también tenga subcarpeta)
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