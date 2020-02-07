pipeline {

  environment {
    registry = "https://gcr.io/rugged-precept-258803/spring-boot-container-test"
    dockerImage = ""
  }

  agent any

  stages {

    stage('Checkout Source') {
      steps {
        git 'https://github.com/youngbaekwon/spring-boot-container.git'
      }
    }

    stage('Build image') {
      steps{
        script {
          dockerImage = docker.build registry + ":$BUILD_NUMBER"
        }
      }
    }

    stage('Push Image') {
      steps{
        script {
          docker.withRegistry( "" ) {
            dockerImage.push()
          }
        }
      }
    }

    stage('Deploy App') {
      steps {
        script {
          kubernetesDeploy(configs: "myapp.yaml", kubeconfigId: "mykubeconfig")
        }
      }
    }

  }

}
