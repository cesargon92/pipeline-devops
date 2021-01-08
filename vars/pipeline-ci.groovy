def call() {
    stage('buildAndTest') {
        env.TASK = env.STAGE_NAME
        sh './gradlew clean build'
    }

    stage('sonar') {
        env.TASK = env.STAGE_NAME
        //corresponde al scanner configurado en global tools Jenkins
        def scannerHome = tool 'sonar-scanner'
        //corresponde a lo configurado en sistema Jenkins
        withSonarQubeEnv('sonar-server') {
            bat "${scannerHome}\\bin\\sonar-scanner -Dsonar.projectKey=ejemplo-gradle -Dsonar.java.binaries=build"
        }
    }

    stage('runJar') {
        env.TASK = env.STAGE_NAME
        sh './gradlew bootRun &'
        sh 'sleep 20'
    }
    stage('rest') {
        env.TASK = env.STAGE_NAME
        sh 'curl -X GET "http://localhost:8888/rest/mscovid/test?msg=testing"'
    }

    stage('nexusCI') {
        env.TASK = env.STAGE_NAME
        nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'test-repo', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: 'C:/Proyectos/ejemplo-gradle/build/libs/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: '0.0.1']]]
    }
}

return this;
