pipeline {
    agent any

    environment {
        // Asegúrate de que este usuario sea correcto
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
            steps {
                // CORRECCIÓN AQUÍ: Usamos el nombre real de tu carpeta
                dir('TINGESO') {
                    // Damos permisos y ejecutamos el test
                    sh 'chmod +x mvnw'
                    sh './mvnw test'
                }
            }
        }

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
                    // CORRECCIÓN AQUÍ: Usamos el nombre real de tu carpeta frontend
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
            // Intentamos limpiar, si falla no importa (|| true)
            sh "docker rmi ${BACKEND_IMAGE}:${TAG} || true"
            sh "docker rmi ${FRONTEND_IMAGE}:${TAG} || true"
        }
    }
}