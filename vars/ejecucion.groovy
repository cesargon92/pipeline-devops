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
                        String inputParam = params.stage
                        println("Texto ingresado: " + inputParam)
                        String [] splittedParam = inputParam.split(";")
                        for (String str in splittedParam){
                            println("Parametro detectado: " + str)
                        }
                        if(params.selector == 'gradle'){
                            gradle.call()
                        } else {
                            maven.call()
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