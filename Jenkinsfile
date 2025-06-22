pipeline {
    agent { label 'tdk-desk-agent-01' }

    environment {
        GITHUB_USER = 'kehe0014'
        GITHUB_REPO = 'healtcare-med-rdv-scheduler'
        GITHUB_TOKEN = credentials('GITHUB_ACESS_TOKEN') // Jenkins Credentials ID
    }

    stages {
        stage('Checkout') {
            steps {
                git url: "https://github.com/${env.GITHUB_USER}/${env.GITHUB_REPO}.git", branch: 'main'
            }
        }

        stage('Build with Maven') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Publish to GitHub Packages') {
            steps {
                withEnv(["MAVEN_OPTS=-Xmx1024m"]) {
                    sh """
                    mvn deploy \
                        -DaltDeploymentRepository=github::default::https://maven.pkg.github.com/${GITHUB_USER}/${GITHUB_REPO} \
                        -Dusername=${GITHUB_USER} \
                        -Dpassword=${GITHUB_TOKEN}
                    """
                }
            }
        }
    }

    post {
        success {
            echo '✅ Build & Deployment successful.'
        }
        failure {
            echo '❌ Build failed.'
        }
    }
}
