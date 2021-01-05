def call(){
    pipeline {
        agent any
        parameters { 
            choice(name: 'selector', choices: ['gradle', 'maven'], description: 'Seleccione')
            string(name: 'stage', defaultValue: '', description: '')
        }
        stages {
            stage('Pipeline') {
                steps {
                    script {
                        env.TASK = ''
                        String inputParam = params.stage;
                        inputParam.replaceAll(" ", "");
                        println("Texto ingresado: " + inputParam);
                        println("Longitud String: " + inputParam.size());
                        String [] splittedParam = new String[] { }
                        if (inputParam.contains(";")){
                            splittedParam = inputParam.split(";");
                        }
                        
                        if(params.selector == 'gradle'){
                            gradle.call(splittedParam)
                        } else {
                            maven.call(splittedParam)
                        }
                    }
                }
            }
        }

        post{
            success {
                slackSend color: 'good', message: "[Cesar Gonzalez][${env.JOB_NAME}][${params.selector}] Ejecucion exitosa", tokenCredentialId: 'slack-token'
            }
            failure {
                slackSend color: 'danger', message: "[Cesar Gonzalez][${env.JOB_NAME}][${params.selector}] Ejecucion fallida en stage [${env.TASK}]", tokenCredentialId: 'slack-token'
            }
        }
    }
}

return this;