def call(List<String> arrStages) {
    println("Cantidad de Stages ingresados: " + arrStages.size())

    boolean buildOk = false;

    if (arrStages.size() == 0){
        arrStages = ["Compile", "Test", "Package", "SonarQube", "UploadNexus"]
    }

    for (String str in arrStages){
        if (str.equalsIgnoreCase("Compile")){
            stage('Compile') {
                env.TASK = env.STAGE_NAME
                sh 'mvn clean compile -e'
            }
            break;
        }
    }
    
    for (String str in arrStages){
        if (str.equalsIgnoreCase("Test")){
            stage('Test') {
                env.TASK = env.STAGE_NAME
                sh 'mvn clean test -e'
            }
            break;
        }
    }
    
    for (String str in arrStages){
        if (str.equalsIgnoreCase("Build")){
            stage('Package') {
                env.TASK = env.STAGE_NAME
                sh 'mvn clean package -e'
            }
            buildOk = true;
            break;
        }
    }

    for (String str in arrStages){
        if (str.equalsIgnoreCase("SonarQube")){
            if (buildOk){
                stage('SonarQube') {
                    env.TASK = env.STAGE_NAME
                    withSonarQubeEnv(installationName: 'sonar-server') {
                        sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.7.0.1746:sonar'
                    }
                }
            } else {
                throw new Exception("Deteniendo pipeline. No se ha contruido .jar para ejecutar esta tarea")
            }
        }
    }

    for (String str in arrStages){
        if (str.equalsIgnoreCase("UploadNexus")){
            if (buildOk){
                stage('UploadNexus') {
                    env.TASK = env.STAGE_NAME
                    nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'test-repo', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: 'C:/Proyectos/ejemplo-maven/build/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: '0.0.1']]]
                }
            } else {
                throw new Exception("Deteniendo pipeline. No se ha contruido .jar para ejecutar esta tarea")
            }
        }
    }
}

return this;
