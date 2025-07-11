import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.amazonEC2CloudImage
import jetbrains.buildServer.configs.kotlin.amazonEC2CloudProfile
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.projectFeatures.perforceAdminAccess
import jetbrains.buildServer.configs.kotlin.triggers.perforceShelveTrigger
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.vcs.PerforceVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2025.03"

project {

    vcsRoot(Perforce10128931691665OllvenTestTw449901main)

    buildType(Clean1)

    params {
        param("teamcity.internal.perforce.automaticCloudAgentWorkspaceRemoval.enabled", "true")
        param("teamcity.internal.perforce.agent.removeBuildWorkspace.enabled", "true")
    }

    features {
        perforceAdminAccess {
            id = "PROJECT_EXT_121"
            name = "Perforce Administrator Access"
            port = "10.128.93.169:1665"
            userName = "jetbrains"
            password = "credentialsJSON:44242aff-a88e-4f2e-b149-fa4511df82a6"
            deleteCloudAgentWorkspaces = "true"
        }
        amazonEC2CloudImage {
            id = "PROJECT_EXT_122"
            profileId = "amazon-23"
            agentPoolId = "-2"
            name = "Perforce"
            vpcSubnetId = "subnet-043178c302cabfe37"
            instanceType = "t2.small"
            securityGroups = listOf("sg-072d8bfa0626ea2a6")
            source = Source("ami-093cbb1cb6b948eb4")
        }
        amazonEC2CloudProfile {
            id = "amazon-23"
            name = "Test p4 workspaces cleanup"
            terminateAfterBuild = true
            terminateIdleMinutes = 0
            region = AmazonEC2CloudProfile.Regions.EU_WEST_DUBLIN
            awsConnectionId = "AmazonWebServicesAws_3"
        }
    }
}

object Clean1 : BuildType({
    name = "Clean1"

    vcs {
        root(Perforce10128931691665OllvenTestTw449901main)
    }

    steps {
        maven {
            id = "Maven2"
            goals = "clean test"
            pomLocation = "triangle-checker/pom.xml"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
        script {
            id = "simpleRunner"
            scriptContent = "sllep 120"
        }
    }

    triggers {
        perforceShelveTrigger {
        }
        vcs {
        }
    }

    requirements {
        equals("cloud.amazon.agent-name-prefix", "Perforce")
    }
})

object Perforce10128931691665OllvenTestTw449901main : PerforceVcsRoot({
    name = "perforce://10.128.93.169:1665/Ollven-test-TW-44990-1/main/"
    port = "10.128.93.169:1665"
    mode = stream {
        streamName = "//Ollven-test-TW-44990-1/main"
        enableFeatureBranches = true
        branchSpec = "+:*"
    }
    userName = "jetbrains"
    password = "credentialsJSON:b972f16c-c238-4248-954a-551212d01a19"
    workspaceOptions = """
        Options:        noallwrite clobber nocompress unlocked nomodtime rmdir
        Host:           %teamcity.agent.hostname%
        SubmitOptions:  revertunchanged
        LineEnd:        local
    """.trimIndent()
    deleteCreatedWorkspaces = "true"
})
