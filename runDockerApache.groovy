node('aws-ec2-1-instance') {
    // Set environment variables
    withEnv([
        'PATH=/usr/bin/'
    ]) {
        def containerId
        try {
            // Stage to run the Apache container
            stage('Run Apache container') {
                containerId = sh(
                    script: 'docker run -itd -p 80:80 httpd:alpine3.20',
                    returnStdout: true
                ).trim()
            }
            // Stage to check Apache logs for 4xx & 5xx errors
            stage('Check Apache logs for 4** & 5** errors') {
                // Make a test request
                sh "curl 0.0.0.0:80/test"
                // Get the container logs
                def log = sh(script: "docker logs ${containerId}", returnStdout: true).trim()
                
                // Check the logs for 4xx errors
                def errors4xx = sh(script: "docker logs ${containerId} | grep 'HTTP/1.1\" 4'", returnStatus: true) == 0
                // Check the logs for 5xx errors
                def errors5xx = sh(script: "docker logs ${containerId} | grep 'HTTP/1.1\" 5'", returnStatus: true) == 0
                
                // Output a message if 4xx errors are found
                if (errors4xx) {
                    echo "4** error is present"
                }
                // Output a message if 5xx errors are found
                if (errors5xx) {
                    echo "5** error is present"
                }
            }
        } finally {
            // Stage to stop and remove the Docker container
            stage('Stop & Remove docker container') {
                sh "docker stop ${containerId}"
                sh "docker rm ${containerId}"
            }
        }
    }
}
