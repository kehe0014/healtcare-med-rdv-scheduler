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
                    / Alternative: Direct curl upload if Maven deploy fails
                    sh """
                    ARTIFACT_PATH=\$(find target -name "*.jar" -not -name "*-sources.jar" -not -name "*-javadoc.jar" | head -n 1)
                    curl -H "Authorization: Bearer ${ARTIFACTORY_CREDS}" \
                         -X PUT "${ARTIFACTORY_URL}/${REPO_PATH}/\$(basename \$ARTIFACT_PATH)" \
                         -T \$ARTIFACT_PATH
                    """
            }
        }
    }
}
