pipeline {
    agent any
    environment {
        ARTIFACTORY_URL = 'https://tdksoft.jfrog.io/artifactory'
        REPO_ID = 'tdk-libs-snapshot'
        REPO_PATH = 'com/tdksoft/appointment'
    }
    stages {
        stage('Deploy') {
            steps {
                withCredentials([string(credentialsId: 'jfrog-access-token', variable: 'ARTIFACTORY_TOKEN')]) {
                    script {
                        // Securely create settings.xml
                        writeFile file: 'settings.xml', text: """<settings>
                          <servers>
                            <server>
                              <id>${REPO_ID}</id>
                              <username>admin</username>
                              <password>${ARTIFACTORY_TOKEN}</password>
                            </server>
                          </servers>
                        </settings>"""
                        
                        // Run Maven deploy
                        sh 'mvn -B -s settings.xml clean deploy'
                        
                        // Fallback direct upload
                        sh '''
                        ARTIFACT_PATH=$(find target -name "*.jar" -not -name "*-sources.jar" -not -name "*-javadoc.jar" | head -n 1)
                        curl -H "Authorization: Bearer $ARTIFACTORY_TOKEN" \
                             -X PUT "${ARTIFACTORY_URL}/${REPO_PATH}/$(basename $ARTIFACT_PATH)" \
                             -T "$ARTIFACT_PATH"
                        '''
                    }
                }
            }
        }
    }
}
