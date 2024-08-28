import groovy.json.JsonSlurper
import java.io.File
def deployment_id, cloud

pipeline{
   agent{
     node{
       label "master"
     }
   }

  stages{
    stage('Get Deployment ID'){
        steps{
            script{
                echo "Getting deployment id for project ${project_name} ..."
                deployment_id_value = String.valueOf(project_name).split('_').last()
                deployment_id = "ieatenm"+deployment_id_value.toLowerCase()
                echo "Deployment_id is " + "$deployment_id"
                cloud = deployment_id_value.substring(1, deployment_id_value.length() - 2).toLowerCase()
            }
        }
    }
    stage('Run ENM Teardown job'){
        steps{
            script{
                echo "Running ENM Teardown job ..."
            }
            build job: 'FFE_Teardown', parameters: [
                string(name: 'jobType', value: "teardown"),
                string(name: 'deploymentId', value: String.valueOf(deployment_id)),
                string(name: 'jenkins_agent', value: String.valueOf(project_name)+"_gateway")
            ]
        }
    }

    stage('Remove Jenkins agent'){
        steps{
            script{
                echo "Deleting Jenkins agent for ${project_name}..."
            }
            build job: 'delete_ffe_jenkins_agent', parameters: [
                string(name: 'nodeName', value: String.valueOf(project_name)+"_gateway")
            ]
        }
    }

    stage('Teardown gateway/netsim/TAF/selenium'){
        steps{
            script{
                echo "Running teardown for gateway, netsim, TAF and selenium ..."
            }
            build job: 'delete_ffe_infrastructure', parameters: [
                string(name: 'project_name', value: String.valueOf(project_name)),
                string(name: 'deployment_id', value: String.valueOf(deployment_id)),
                string(name: 'cloud', value: String.valueOf(cloud))
            ]
        }
    }

    stage('Delete DIT documents & remove Openstack project + user(s)'){
        steps{
            script{
                echo "Deleting OS Project and DIT Docs ..."
            }
            build job: "delete_ffe_environment", parameters: [
                string(name: 'project_name', value: String.valueOf(project_name)),
                string(name: 'deployment_id', value: String.valueOf(deployment_id)),
                string(name: 'cloud', value: String.valueOf(cloud))
            ]
        }
    }

    stage('Remove DTT entry'){
        steps{
            script{
                echo "Deleting DTT deployment"
            }
            build job: "delete_ffe_dtt_entry", parameters: [
                string(name: 'deployment_id', value: String.valueOf(deployment_id)),
            ]
        }
    }

    stage('Run wrapper rollout job'){
        steps{
            script{
                echo "Removing wrapper job for " + String.valueOf(project_name)
            }
            build job: "FFE_Wrapper_Pipeline_Rollout", parameters: []
        }
    }

    stage('Update FFE jira'){
        steps{
            script{
                echo "Leaving a deletion comment on the associated jira ..."
            }
            build job: 'update_ffe_jira', parameters: [
                string(name: 'status', value: "delete"),
                string(name: 'project_name', value: String.valueOf(project_name)),
                string(name: 'jira_ticket', value: String.valueOf(jira_ticket))
            ]
        }
    }
  }

}
