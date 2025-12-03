pipeline {
    agent any

    environment {
        // [cite: 147] Configura tu usuario de Docker Hub aquí
        DOCKER_HUB_USER = 'javieralamilla'

        // Nombres de las imágenes según requerimiento
        BACKEND_IMAGE = "${DOCKER_HUB_USER}/toolrent-backend"
        FRONTEND_IMAGE = "${DOCKER_HUB_USER}/toolrent-frontend"

        // Tag para la versión (puedes usar 'latest' o el número de build)
        TAG = 'latest'
    }

    stages {
        // 1. OBTENCIÓN DEL CÓDIGO [cite: 154, 164]
        stage('Checkout SCM') {
            steps {
                // Jenkins clona automáticamente el repo configurado en el Job
                checkout scm
            }
        }

        // 2. PRUEBAS UNITARIAS BACKEND [cite: 154, 176]
        // Requisito: Cobertura > 90%
        stage('Backend Unit Tests') {
            steps {
                dir('backend') { // Entramos a la carpeta del backend
                    // Damos permisos de ejecución al wrapper de Maven
                    sh 'chmod +x mvnw'
                    // Ejecutamos los tests. Si fallan, el pipeline se detiene.
                    sh './mvnw test'
                }
            }
        }

        // 3. CONSTRUCCIÓN Y SUBIDA (BACKEND) [cite: 155, 164]
        stage('Build & Push Backend') {
            steps {
                script {
                    dir('backend') {
                        // Login a DockerHub usando las credenciales configuradas
                        withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials', passwordVariable: 'DOCKER_PASS', usernameVariable: 'DOCKER_USER')]) {
                            sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'

                            // Construir imagen
                            sh "docker build -t ${BACKEND_IMAGE}:${TAG} ."

                            // Subir a DockerHub
                            sh "docker push ${BACKEND_IMAGE}:${TAG}"
                        }
                    }
                }
            }
        }

        // 4. CONSTRUCCIÓN Y SUBIDA (FRONTEND) [cite: 155, 164]
        stage('Build & Push Frontend') {
            steps {
                script {
                    dir('frontend') {
                        withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials', passwordVariable: 'DOCKER_PASS', usernameVariable: 'DOCKER_USER')]) {
                            // Nota: El login ya se hizo arriba, pero por seguridad/sesión se puede repetir o asumir activo.

                            // Construir imagen (Asegúrate que tu Dockerfile de frontend haga el build de React)
                            sh "docker build -t ${FRONTEND_IMAGE}:${TAG} ."

                            // Subir a DockerHub
                            sh "docker push ${FRONTEND_IMAGE}:${TAG}"
                        }
                    }
                }
            }
        }
    }

    // Limpieza post-ejecución para no llenar el disco del servidor Jenkins
    post {
        always {
            sh "docker rmi ${BACKEND_IMAGE}:${TAG} || true"
            sh "docker rmi ${FRONTEND_IMAGE}:${TAG} || true"
        }
    }
}