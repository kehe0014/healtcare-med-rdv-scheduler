pipeline {
    agent any
    environment {
        ARTIFACTORY_URL = 'https://tdksoft.jfrog.io/artifactory'
        REPO_ID = 'tdk-libs-snapshot'
        REPO_PATH = 'com/tdksoft/appointment'
        ARTIFACTORY_CREDS = credentials('ARTIFACTORY_TOKEN')
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
                        script {
                            // Configure Maven settings with Artifactory credentials
                            sh """
                            cat > settings.xml << 'EOF'
                            <settings>
                                <servers>
                                    <server>
                                        <id>${REPO_ID}</id>
                                        <username>admin</username>
                                        <password>${ARTIFACTORY_CREDS}</password>
                                    </server>
                                </servers>
                            </settings>
                            EOF
                            """
                            
                            // Deploy using Maven with custom settings
                            sh 'mvn -s settings.xml clean deploy'
                        }
                    }
                }
    }
}
