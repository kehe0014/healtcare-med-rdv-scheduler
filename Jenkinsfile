pipeline {
    agent any
    environment {
        NEXUS_URL = 'https://your-nexus-server/repository/your-repo/'
        NEXUS_TOKEN = credentials('nexus-access-token')
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
                sh 'mvn -B clean deploy --settings settings.xml'
            }
        }
    }
}
