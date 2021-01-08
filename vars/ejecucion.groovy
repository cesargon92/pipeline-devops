def call(){
    pipeline {
        agent any
        /*parameters { 
            choice(name: 'selector', choices: ['gradle', 'maven'], description: 'Seleccione')
            string(name: 'stage', defaultValue: '', description: '')
        }*/
        stages {
            stage('Pipeline') {
                steps {
                    script {
                        bat 'set'
                        env.TASK = ''
                        println("Ejecutando pipeline de la rama: " + env.GIT_BRANCH);
                                            
                        if(env.GIT_BRANCH.contains('feature') || env.GIT_BRANCH.contains('develop')){
                            figlet "Pipeline CI"
                            pipeline-ci.call()
                        } else if (env.GIT_BRANCH.contains('release')){
                            figlet "Pipeline CD"
                            pipeline-cd.call()
                        } else {
                            println("Rama sin pipeline asociado: " + env.GIT_BRANCH);
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
