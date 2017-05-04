def javaEnv() {
    def javaHome = tool 'JDK8u66'
    ["PATH=${env.PATH}:${javaHome}/bin", "JAVA_HOME=${javaHome}"]
}

def notifyFailed() {
    emailext (subject: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
              body: """FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':\n\nCheck console output at ${env.BUILD_URL}""",
              recipientProviders: [[$class: 'CulpritsRecipientProvider'], [$class: 'DevelopersRecipientProvider']])
}

def notifySuccess() {
    if (currentBuild.getPreviousBuild()?.result == 'FAILURE') {
        emailext (subject: "SUCCESS: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                  body: """Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' succeeded:\n\nCheck console output at ${env.BUILD_URL}""",
                  recipientProviders: [[$class: 'CulpritsRecipientProvider'], [$class: 'DevelopersRecipientProvider']])
    }
}

def run(Closure body) {
    try {
        body()
    } catch (e) {
        currentBuild.result = "FAILED"
        notifyFailed()
        throw e
    }
}

// Run the body closure. Then run the cleanup closure (e.g. for closing VPN connections), even in case of error.
def run(Closure body, Closure cleanup) {
    try {
        run(body)
    } finally {
        cleanup()
    }
}

// Wait for the specified url to become available, with a timeout of 5 minutes.
def waitForServer(String url) {
    timeout(5) {
        waitUntil {
            def r1 = sh script: "wget -q ${url} --header='Cookie: ROUTEID=1' --no-check-certificate -O /dev/null", returnStatus: true
            def r2 = sh script: "wget -q ${url} --header='Cookie: ROUTEID=2' --no-check-certificate -O /dev/null", returnStatus: true
            return (r1 == 0 && r2 == 0);
        }
    }
}

// Return the latest published version of a module, with the given baseVersion.
def latestVersion(String module, String baseVersion) {
    withEnv(["BASE_VERSION=${baseVersion}",
             "MODULE=${module}",
             "URL=https://build-inera.nordicmedtest.se/nexus/service/local/lucene/search"]) {
        return sh(script: 'curl -ks "${URL}?a=${MODULE}&v=${BASE_VERSION}&repositoryId=releases" | sed -nr "/^.*latestRelease>([0-9.]+)<.*/{s//\\1/p;q}"',
                  returnStdout: true).trim()
    }
}
