def call() {
    stage('downloadNexus') {
        env.TASK = env.STAGE_NAME
        sh 'curl -X GET -u admin:admin "http://localhost:9081/repository/test-repo/com/devopsusach2020/DevOpsUsach2020/0.0.1/DevOpsUsach2020-0.0.1.jar" -O'
    }

    stage('runDownloadedJar') {
        env.TASK = env.STAGE_NAME
        sh 'java -jar DevOpsUsach2020-0.0.1.jar &' 
    }
    stage('rest') {
        env.TASK = env.STAGE_NAME
        sh 'sleep 20'
        sh 'curl -X GET "http://localhost:8888/rest/mscovid/test?msg=testing"'
    }

    stage('nexusCD') {
        env.TASK = env.STAGE_NAME
        nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'test-repo', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: 'C:/Proyectos/ejemplo-gradle/build/libs/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: '1.0.0']]]
    }
}

return this;
