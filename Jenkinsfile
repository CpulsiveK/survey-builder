def appname = 'survery-sphere'
def deploy_group_dev = 'survey-sphere-backend-dev'
def s3_bucket = 'survery-sphere-artifacts'
def s3_filename = 'survey-sphere-src-backend'


pipeline {
  agent any
  tools {
    maven "maven"
    jdk "jdk21"
  }

    stages{

     stage('SonarQube Analysis') {
      steps{
        withSonarQubeEnv('SonarQube') {
          script {
            def mvn = tool 'maven';
            sh "${mvn}/bin/mvn clean verify sonar:sonar -Dmaven.test.skip -Dsonar.projectKey=Amali-Tech_Survey-Sphere-Backend_AY-5ZS0iiBozlGzLDKW- -Dsonar.projectName='Survey-Sphere-Backend'"
          }
        }
      }
    }    

     stage('Prepare to Deploy') {
         when {
            branch 'develop';
         }

       steps {
         withAWS(region:'eu-west-1',credentials:'aws-cred-training-center') {
           script {
             def gitsha = sh(script: 'git log -n1 --format=format:"%H"', returnStdout: true)
             s3_filename = "${s3_filename}-${gitsha}"
             sh """
                 aws deploy push \
                 --application-name ${appname} \
                 --description "This is a revision for the ${appname}-${gitsha}" \
                 --s3-location s3://${s3_bucket}/${s3_filename}.zip \
                 --source .
               """
           }
         }
       }
     }

	 stage('Deploy to Development') {
        when {
            branch 'develop';
         }
       steps {
         withAWS(region:'eu-west-1',credentials:'aws-cred-training-center') {
           script {
             sh """
                 aws deploy create-deployment \
                 --application-name ${appname} \
                 --deployment-config-name CodeDeployDefault.OneAtATime \
                 --deployment-group-name ${deploy_group_dev} \
                 --file-exists-behavior OVERWRITE \
                 --s3-location bucket=${s3_bucket},key=${s3_filename}.zip,bundleType=zip
               """
           }
         }
	   }
	 }

    
    }  
    post {
        always {
            echo 'One way or another, I have finished'
            cleanWs()
        }
        success {
            echo "========pipeline executed successfully ========"
        }
        unstable {
            echo 'I am unstable :/'
        }
        failure {
            echo "========pipeline execution failed========"
        }
        changed {
            echo 'Things were different before...'
        }
    }

}
 