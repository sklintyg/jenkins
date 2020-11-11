def getBuildOpts() {
    return " -DnexusUsername=$NEXUS_USERNAME -DnexusPassword=$NEXUS_PASSWORD" +
        " -DgithubUser=$GITHUB_USERNAME -DgithubPassword=$GITHUB_PASSWORD" +
        " -DineraNexusUsername=$INERA_NEXUS_USERNAME -DineraNexusPassword=$INERA_NEXUS_PASSWORD" +
        " -DineraSonarLogin=$INERA_SONAR_LOGIN"
}

def call(gradleCommand) {
    util.run {
        withEnv(util.javaEnv()) {
            sh "./gradlew " + "--stacktrace " + gradleCommand + getBuildOpts()
        }
    }
}
