pipeline {
    agent any
    environment {
        NEXUS_TOKEN = credentials('jfrog-access-token')
    }
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }
        
        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
        
        stage('Deploy') {
            steps {
                sh 'mvn  clean deploy'
            }
        }
    }
}
