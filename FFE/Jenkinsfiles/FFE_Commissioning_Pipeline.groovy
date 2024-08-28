def node_name, node_ip, project_list, deployment_id, product_set
import groovy.json.JsonSlurper
import java.io.File

def get_first_hostname(cloud){
  def curl_cmd = "curl --header \'Accept: application/json\' -X GET -ks https://meteo.athtem.eei.ericsson.se/get-available-ffe-hostnames-api/?cloud=${cloud}".execute()
  curl_cmd.waitFor()
  def result = curl_cmd.in.text
  def next_available_hostname = new JsonSlurper().parseText(result).Available_Hostnames[0]
  return next_available_hostname
}

pipeline{
   agent{
     node{
       label "${FFE_Commissioning_Agents}"
     }
   }

  stages{
    stage('Getting next available hostname on cloud'){
      steps{
        script{
          available_hostname = get_first_hostname(String.valueOf(cloud))
          deployment_id = available_hostname
          echo "New hostname found: $deployment_id"
        }
      }
    }
    stage('Setting up variables'){
      steps{
        script{
          project_name = project_name + "_" + deployment_id.substring(7, deployment_id.length()).toUpperCase()
          echo "New project name is " + "$project_name"
          echo "Deployment_id is " + "$deployment_id"
          product_set = sh(script:'''curl -L -s "https://ci-portal.seli.wh.rnd.internal.ericsson.com/getLastGoodProductSetVersion/?productSet=ENM&confidenceLevel=Deploy-vENM-II"''', returnStdout: true)
          echo "Product Set is " + "$product_set"
          drop_values = String.valueOf(product_set).tokenize('.')
          DROP = drop_values[0] + "." + drop_values[1]
          echo "Drop is " + "$DROP"
          curl_response = sh(script:'''curl -L -s https://ci-portal.seli.wh.rnd.internal.ericsson.com/api/deployment/deploymentutilities/productSet/ENM/version/''' + "$product_set", returnStdout: true)
          ENM_ISO_VERSION = curl_response.substring(curl_response.lastIndexOf("mediaArtifactVersion")).split('"')[2]
          echo "ENM ISO Version is " + "$ENM_ISO_VERSION"
        }
      }
    }

    stage('Create OS project, setup all DIT documents'){
      steps{
        script{
          echo "Creating Openstack project ..."
        }
        build job : 'create_ffe_environment', parameters: [
          string(name: 'project_name', value: String.valueOf(project_name)),
          string(name: 'deployment_id', value: String.valueOf(deployment_id)),
          string(name: 'cloud', value: String.valueOf(cloud)),
          string(name: 'volumes', value: String.valueOf(volumes)),
          string(name: 'cpu', value: String.valueOf(cpu)),
          string(name: 'ram', value: String.valueOf(ram)),
          string(name: 'storage', value: String.valueOf(storage)),
          string(name: 'contacts', value: String.valueOf(contacts)),
          string(name: 'product_set', value: String.valueOf(product_set))
        ]
      }
    }

    stage('Create DTT deployment'){
      steps{
          build job: 'create_ffe_dtt_entry', parameters: [
            string(name: 'project_name', value: String.valueOf(project_name)),
            string(name: 'deployment_id', value: String.valueOf(deployment_id)),
            string(name: 'requirement_area', value: String.valueOf(requirement_area)),
            string(name: 'program', value: String.valueOf(program)),
            string(name: 'jira', value: String.valueOf(jira_ticket)),
            string(name: 'team_name', value: String.valueOf(team_name)),
            string(name: 'spocs', value: String.valueOf(spocs))
          ]
      }
    }

    stage('Create FFE Infrastructure VMs'){
      steps{
          build job: 'create_ffe_infrastructure', parameters: [
            string(name: 'project_name', value: String.valueOf(project_name)),
            string(name: 'cloud', value: String.valueOf(cloud)),
            string(name: 'deployment_id', value: String.valueOf(deployment_id)),
            string(name: 'branch', value: "master")
          ]
      }
    }

    stage('Update FFE projects list + Run Wrapper Job Rollout'){
      steps{
        script{
          echo "Updating FFE project list and Rolling out wrapper job ..."
        }
        build job: 'FFE_Wrapper_Pipeline_Rollout', parameters: []
      }
    }

    stage('Get Gateway IP and node name'){
      steps{
        script{
          node_name = "${project_name}" + '_gateway'
          curl_response=sh(script: 'curl --header \'Accept: application/json\' -X GET -ks "https://atvdit.athtem.eei.ericsson.se/api/documents/?q=name="' + "$node_name", returnStdout: true)
          node_ip = curl_response.substring(curl_response.indexOf("ip")).split('"')[2]
          echo "node_ip=${node_ip}"
        }
      }
    }

    stage('Setup gateway as a Jenkins agent'){
      steps{
        script{
          echo "Calling job to create Jenkins agent for node IP ${node_ip}"
        }
        build job: 'create_jenkins_agent_setup', parameters: [
          string(name: 'nodeIP', value: node_ip),
          string(name: 'nodeName', value: node_name)
        ]
      }
    }

    stage("Update Jira Ticket"){
      steps{
        build job: 'update_ffe_jira', parameters: [
          string(name: 'status', value: "create"),
          string(name: 'project_name', value: String.valueOf(project_name)),
          string(name: 'jira_ticket', value: String.valueOf(jira_ticket))
        ]
      }
    }

  }

}
