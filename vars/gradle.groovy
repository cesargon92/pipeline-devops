def call(List<String> arrStages) {
    println("Cantidad de Stages ingresados: " + arrStages.size())

    boolean buildOk = false;
    boolean runOk = false;

    if (arrStages.size() == 0){
        arrStages = ["Build", "Test", "Sonar", "Run", "Rest", "Nexus"]
    }

    for (String str in arrStages){
        if (str.equalsIgnoreCase("Build")){
            stage('Build') {
                env.TASK = env.STAGE_NAME
                sh './gradlew clean build -x test'
            }
            buildOk = true;
            break;
        }
    }

    for (String str in arrStages){
        if (str.equalsIgnoreCase("Test")){
            if (buildOk){
                stage('Test') {
                    env.TASK = env.STAGE_NAME
                    sh './gradlew clean test -x build'
                }
            } else {
                throw new Exception("Deteniendo pipeline")
            }
            break;
        }
    }

    for (String str in arrStages){
        if (str.equalsIgnoreCase("Sonar")){
            if (buildOk){
                stage('Sonar') {
                    env.TASK = env.STAGE_NAME
                    //corresponde al scanner configurado en global tools Jenkins
                    def scannerHome = tool 'sonar-scanner'
                    //corresponde a lo configurado en sistema Jenkins
                    withSonarQubeEnv('sonar-server') {
                        bat "${scannerHome}\\bin\\sonar-scanner -Dsonar.projectKey=ejemplo-gradle -Dsonar.java.binaries=build"
                    }
                }
            } else {
                throw new Exception("Deteniendo pipeline")
            }
            break;
        }
    }

    for (String str in arrStages){
        if (str.equalsIgnoreCase("Run")){
            stage('Run') {
                env.TASK = env.STAGE_NAME
                sh './gradlew bootRun &'
                sh 'sleep 20'
            }
            runOk = true;
            break;
        }
    }

    for (String str in arrStages){
        if (str.equalsIgnoreCase("Rest") || arrStages.size() == 0){
            if (runOk) {
                stage('Rest') {
                    env.TASK = env.STAGE_NAME
                    sh 'curl -X GET "http://localhost:8888/rest/mscovid/test?msg=testing"'
                }
            } else {
                throw new Exception("Deteniendo pipeline")
            }
            break;
        }
        
    }

    for (String str in arrStages){
        if (str.equalsIgnoreCase("Nexus")){
            if (buildOk){
                stage('Nexus') {
                    env.TASK = env.STAGE_NAME
                    nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'test-repo', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: 'C:/Proyectos/ejemplo-gradle/build/libs/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: '0.0.1']]]
                }
            } else {
                throw new Exception("Deteniendo pipeline")
            }
            break;
        }
    }
}

return this;
