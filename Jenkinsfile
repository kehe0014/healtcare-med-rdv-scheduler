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
                withCredentials([string(credentialsId: 'jfrog-access-token', variable: 'ARTIFACTORY_TOKEN')]) {
                    script {
                        // Securely create settings.xml
                        writeFile file: 'settings.xml', text: """\
                        <settings>
                          <servers>
                            <server>
                              <id>${REPO_ID}</id>
                              <username>admin</username>
                              <password>${ARTIFACTORY_TOKEN}</password>
                            </server>
                          </servers>
                        </settings>
                        """.stripIndent()
                        
                        // Run Maven deploy
                        sh 'mvn -B -s settings.xml clean deploy'
                    }
                }
            }
        }
    }
}
