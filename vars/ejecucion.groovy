def call(){
    pipeline {
        agent any
        parameters { choice(name: 'selector', choices: ['gradle', 'maven'], description: 'Seleccione') }
        stages {
            stage('Pipeline') {
                steps {
                    script {
                        env.TASK = ''
                        if(params.selector == 'gradle'){
                            def ejecucion = load 'gradle.groovy'
                            ejecucion.call()
                        } else {
                            def ejecucion = load 'maven.groovy'
                            ejecucion.call()
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