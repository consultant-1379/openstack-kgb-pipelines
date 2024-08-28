pipeline {
   agent{
       node{
            label "infra_ffe"
       }
   }
   parameters{
    string(name: 'project_name', defaultValue: '', description: 'Enter the OpenStack project name')
    booleanParam(name: 'delete_netsim', defaultValue: false, description: 'Delete Netsim VM and Volume')
    booleanParam(name: 'delete_taf', defaultValue: false, description: 'Delete TAF VM and Volumes')
    booleanParam(name: 'delete_selenium', defaultValue: false, description: 'Delete Selenium VM and Volume')
   }
   options {
       timestamps()
   }
   stages {
       stage('Get Additional Parameters for Pipeline') {
           steps {
               script {
                    // get deployment id from the project name
                    proj_name = String.valueOf(project_name)
                    id_from_project_name = proj_name.split('_').last()
                    deployment_id = "ieatenm" + id_from_project_name.toLowerCase()
                    echo "Deployment ID retrieved from project name " + deployment_id
                    // get cloud from project name
                    cloud = id_from_project_name.substring(1, id_from_project_name.length() - 2).toLowerCase()
                    cloud_url = "https://cloud"+cloud+".athtem.eei.ericsson.se:13000/v3"
                    echo "Cloud URL retrieved from project name " + cloud_url
               }
           }
       }
       stage('Delete Netsim VM'){
           when {
               expression { params.delete_netsim }
           }
        steps{
            sh "cd FFE/Ansible; ansible-playbook -vv -e deployment_id=${deployment_id} -e project_name=${project_name} -e cloud=${cloud_url} teardown_netsim.yml"
        }
      }
      stage('Delete TAF VM'){
           when {
               expression { params.delete_taf }
           }
        steps{
            sh "cd FFE/Ansible; ansible-playbook -vv -e deployment_id=${deployment_id} -e project_name=${project_name} -e cloud=${cloud_url} teardown_taf.yml"
        }
      }
      stage('Delete Selenium VM'){
           when {
               expression { params.delete_selenium }
           }
        steps{
            sh "cd FFE/Ansible; ansible-playbook -vv -e deployment_id=${deployment_id} -e project_name=${project_name} -e cloud=${cloud_url} teardown_selenium.yml"
        }
      }
   }
}
