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
        AUTO_ENV = ''
    }

    stages {
        stage('Initialize') {
            steps {
                script {
                    env.AUTO_ENV = determineEnvironment()

                    if (params.ENVIRONMENT != env.AUTO_ENV) {
                        env.ENVIRONMENT = env.AUTO_ENV
                        echo "Auto-selected environment: ${env.ENVIRONMENT} based on branch ${env.BRANCH_NAME}"
                    } else {
                        env.ENVIRONMENT = params.ENVIRONMENT
                    }
                    
                    echo "Building for environment: ${env.ENVIRONMENT}"
                    echo "Branch: ${env.BRANCH_NAME}"
                }
            }
        }

        stage('Check Prerequisites') {
            steps {
                script {
                    sh 'docker --version || true' // Added '|| true' to prevent failure if docker isn't found
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

// Helper function to determine environment based on branch
def determineEnvironment() {
    if (env.BRANCH_NAME == 'develop' || env.BRANCH_NAME.startsWith('feat/')) {
        return 'dev'
    } else if (env.BRANCH_NAME == 'main') {
        // For main branch, we'll build both staging and prod in separate runs
        // If the ENVIRONMENT parameter is set, use it; otherwise, default to 'staging'
        return params.ENVIRONMENT ?: 'staging'
    }
    return 'dev' // default fallback for any other branches not explicitly handled
}
