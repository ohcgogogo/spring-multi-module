// Declarative Pipeline
// https://velog.io/@seunghyeon/Jenkins-%EC%84%A0%EC%96%B8%EC%A0%81Declarative-%ED%8C%8C%EC%9D%B4%ED%94%84%EB%9D%BC%EC%9D%B8
pipeline {
    agent any

    environment {
        CHECKOUT_DIRECTORY = 'eureka'
        DOCKER_REGISTRY_CREDENTIALS = credentials('docker-registry')
    }

    stages{
        stage('Checkout'){
            steps {
                cleanWs()
                checkout([$class: 'GitSCM',
                          branches: [[name: '*/ben-develop']],
                          extensions: [
                                  [$class: 'SparseCheckoutPaths',
                                   sparseCheckoutPaths:[[$class:'SparseCheckoutPath', path:"${CHECKOUT_DIRECTORY}"]]]
                          ],
                          doGenerateSubmoduleConfigurations: false,
                          submoduleCfg: [],
                          userRemoteConfigs: [[url: 'http://gitea.local.in/local/demo.git']]],
                )
                sh "ls -ltr"
            }
        }
        stage('Build') {
            steps {
                dir("${CHECKOUT_DIRECTORY}") {
                    sh 'chmod +x ./gradlew'
                    sh './gradlew clean build --console plain'
                    sh 'ls -al ./build'
                }
            }
            post {
                success {
                    echo 'gradle build success'
                }
                failure {
                    echo 'gradle build failed'
                }
            }
        }
        stage('Dockerizing') {
            steps{
                sh 'echo " Image Bulid Start"'
                sh 'docker login my.http-registry.io:5000 -u $DOCKER_REGISTRY_CREDENTIALS_USR -p $DOCKER_REGISTRY_CREDENTIALS_PSW'
                sh 'docker build -t my.http-registry.io:5000/mas-eureka-service:0.1.0 . --no-cache'
                sh 'docker push my.http-registry.io:5000/mas-eureka-service:0.1.0'
            }
        }
    }
}