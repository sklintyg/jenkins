def getBuildOpts() {
    return " -DnexusUsername=$NEXUS_USERNAME -DnexusPassword=$NEXUS_PASSWORD" +
        " -DgithubUser=$GITHUB_USERNAME -DgithubPassword=$GITHUB_PASSWORD"
}

def call(gradleCommand) {
    util.run {
        withEnv(util.javaEnv11()) {
            sh "./gradlew " + "--stacktrace " + gradleCommand + getBuildOpts()
        }
    }
}