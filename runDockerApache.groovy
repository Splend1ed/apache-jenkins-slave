node("aws-ec2-1-instance"){
    stage("Run Apache container") {
        docker run -itd -p 8181:80 httpd:alpine3.20
    }
}