/*
    forma de invocación de método call:
    def ejecucion = load 'script.groovy'
    ejecucion.call()
*/

def call() {
    stage('Compile') {
        env.TASK = env.STAGE_NAME
        sh 'mvn clean compile -e'
    }
    stage('Test') {
        env.TASK = env.STAGE_NAME
        sh 'mvn clean test -e'
    }
    stage('Package') {
        env.TASK = env.STAGE_NAME
        sh 'mvn clean package -e'
    }
    stage('SonarQube analysis') {
        env.TASK = env.STAGE_NAME
        withSonarQubeEnv(installationName: 'sonar-server') {
            sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.7.0.1746:sonar'
        }
    }
    stage('UploadNexus') {
        env.TASK = env.STAGE_NAME
        nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'test-repo', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: 'C:/Proyectos/ejemplo-maven/build/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: '0.0.1']]]
    }
}

return this;
