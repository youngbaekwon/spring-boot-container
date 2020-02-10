pipeline {
  environment {
    PROJECT = "my-bg-lab"
    APP_NAME = "spring-boot-app"
    REPOSITORY_NAME = "spring-boot-container"
    CLUSTER = "jenkins-cluster-1"
    CLUSTER_ZONE = "us-east1-a"
    IMAGE_TAG = "gcr.io/${PROJECT}/${APP_NAME}:${env.BUILD_NUMBER}"
    JENKINS_CRED = "${PROJECT}"
  }
  agent {
    kubernetes {
      label 'spring-boot-app'
      defaultContainer 'jnlp'
      yaml """
apiVersion: v1
kind: Pod
metadata:
labels:
  component: ci
spec:
  # Use service account that can deploy to all namespaces
  serviceAccountName: cd-jenkins
  containers:
  - name: maven
    image: maven:3-alpine
    command:
    - cat
    tty: true 
  - name: gcloud
    image: gcr.io/cloud-builders/gcloud
    command:
    - cat
    tty: true
  - name: kubectl
    image: gcr.io/cloud-builders/kubectl
    command:
    - cat
    tty: true
"""
}
  }
  
  stages {
  
	//Stage 1: Checkout Code from Git
	stage('Application Code Checkout from Git') {
		steps {
			checkout scm
		}
	}
	
	//Stage 2: Build with mvn
	stage('Build with Maven') {
		steps {
			container('maven'){
				dir ("./${REPOSITORY_NAME}") {
					sh ("mvn -Dmaven.test.skip=true clean package")
				}
			}
		}
    }
	
	//Stage 3: Build Docker Image    
    stage('Build Docker Image') {
		steps {
			container('docker'){
				sh("docker build -t ${IMAGE_TAG} .")
			}
		}
    }
	
	//Stage 4: Push the Image to a Docker Registry
    stage('Push Docker Image to Docker Registry') {
		steps {
			container('docker'){
				//withCredentials([[$class: 'UsernamePasswordMultiBinding',
				//credentialsId: env.DOCKER_CREDENTIALS_ID,
				//usernameVariable: 'USERNAME',
				//passwordVariable: 'PASSWORD']]) {
				//	docker.withRegistry(env.DOCEKR_REGISTRY, env.DOCKER_CREDENTIALS_ID) {
						sh("docker push ${IMAGE_TAG}")
				//	}
				//}
			}
		}
    }
	
	//Stage 5: Deploy Application on K8s
    stage('Deploy Application on K8s') {
		steps {
			container('kubectl'){
				withKubeConfig([credentialsId: env.K8s_CREDENTIALS_ID,
				serverUrl: env.K8s_SERVER_URL,
				contextName: env.K8s_CONTEXT_NAME,
				clusterName: env.K8s_CLUSTER_NAME]){
					sh("kubectl apply -f myapp.yml")

				}     
			}
		}
	}
  }
}

