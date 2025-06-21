pipeline {
    agent any

    parameters {
        choice(
            name: 'ENVIRONMENT',
            choices: ['dev', 'staging', 'prod'],
            description: 'Select deployment environment (auto-set based on branch)'
        )
        string(
            name: 'DOCKER_HUB_ID',
            defaultValue: 'tdksoft',
            description: 'Your Docker Hub username'
        )
        string(
            name: 'DOCKER_HUB_REPO',
            defaultValue: '${DOCKER_HUB_ID}/appointment-app',
            description: 'Docker Hub repository name'
        )
    }

    environment {
        JAVA_HOME = tool 'jdk-11'
        // Automatically set environment based on branch
        // AUTO_ENV = determineEnvironment()
        AUTO_ENV = env.BRANCH_NAME == 'develop' || env.BRANCH_NAME.startsWith('feat/') ? 'dev' : 
                    (env.BRANCH_NAME == 'main' ? 'staging' : 'prod')
        DOCKER_IMAGE_NAME = ''
        DOCKER_HUB_PASSWORD = credentials('DOCKER_HUB_PASS')
        BUILD_NUMBER = "${env.BUILD_ID}"
        BRANCH_NAME = "${env.BRANCH_NAME ?: 'develop'}" // Default to develop if not set
        ENVIRONMENT = "${params.ENVIRONMENT ?: AUTO_ENV}" // Default to auto-detected environment
        AUTO_ENV = "${AUTO_ENV ?: 'dev'}" // Fallback to 'dev' if AUTO_ENV is not set
       // JAVA_HOME = tool 'jdk-11'
        AUTO_ENV = ''
    }

    stages {
        stage('Build') {
            steps {
                sh './mvnw clean package'
            }
        }
        
        stage('Test') {
            steps {
                sh './mvnw test'
            }
        }
        
        stage('Deploy') {
            steps {
                script {
                    sh 'docker --version || true' 
                    sh 'mvn --version'
                    sh 'java -version'
                }
            }
        }

        stage('Build with Maven') {
            steps {
                script {
                    sh "./mvnw clean package -Dspring.profiles.active=${env.ENVIRONMENT}"
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    def imageTag = "${env.ENVIRONMENT}-${env.BRANCH_NAME}-${env.BUILD_NUMBER}".toLowerCase()
                    def imageName = "${params.DOCKER_HUB_REPO}:${imageTag}"
                    
                    sh """
                        ./mvnw spring-boot:build-image \
                            -Dspring-boot.build-image.imageName=${imageName} \
                        mvn spring-boot:build-image \\
                            -Dspring-boot.build-image.imageName=${imageName} \\
                            -Dspring.profiles.active=${env.ENVIRONMENT}
                    """
                    
                    env.DOCKER_IMAGE_NAME = imageName
                }
            }
        }

        stage('Push to Docker Hub') {
            when {
                expression { 
                    // Only push to Docker Hub for certain branches
                    return env.BRANCH_NAME == 'develop' || 
                           env.BRANCH_NAME == 'main' || 
                           env.BRANCH_NAME.startsWith('feat/')
                }
            }
            steps {
                script {
                    withCredentials([string(
                        credentialsId: 'DOCKER_HUB_PASS',
                        variable: 'DOCKER_HUB_PASSWORD'
                    )]) {
                        sh """
                            docker login -u ${params.DOCKER_HUB_ID} --password-stdin <<<\${DOCKER_HUB_PASSWORD}
                        """
                    }
                    
                    sh "docker push ${env.DOCKER_IMAGE_NAME}"
                    
                    // For main branch, push to both staging and prod
                    if (env.BRANCH_NAME == 'main') {
                        if (env.ENVIRONMENT == 'staging') {
                            sh "docker tag ${env.DOCKER_IMAGE_NAME} ${params.DOCKER_HUB_REPO}:staging-latest"
                            sh "docker push ${params.DOCKER_HUB_REPO}:staging-latest"
                        } else if (env.ENVIRONMENT == 'prod') { // Changed from 'if' to 'else if' for clarity
                            sh "docker tag ${env.DOCKER_IMAGE_NAME} ${params.DOCKER_HUB_REPO}:prod-latest"
                            sh "docker push ${params.DOCKER_HUB_REPO}:prod-latest"
                        }
                    } else {
                        // For other branches, push as dev-latest
                        sh "docker tag ${env.DOCKER_IMAGE_NAME} ${params.DOCKER_HUB_REPO}:dev-latest"
                        sh "docker push ${params.DOCKER_HUB_REPO}:dev-latest"
                    }
                    
                    echo """
                    =============================================
                    Successfully pushed to Docker Hub:
                    - Image: ${env.DOCKER_IMAGE_NAME}
                    - Environment: ${env.ENVIRONMENT}
                    - Branch: ${env.BRANCH_NAME}
                    =============================================
                    """
                }
            }
        }
        stage('Cleanup') {
            steps {
                script {
                    // Clean up Docker images to save space
                    sh "docker rmi ${env.DOCKER_IMAGE_NAME} || true"
                }
            }
        }
        stage('Notify') {
            steps {
                script {
                    // Notify about the successful build and push
                    echo "Build and push completed successfully for ${env.ENVIRONMENT} environment on branch ${env.BRANCH_NAME}."
                }
            }
        }
    }
}
