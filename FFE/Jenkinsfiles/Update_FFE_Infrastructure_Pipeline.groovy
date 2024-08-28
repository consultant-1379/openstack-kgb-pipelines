def deployment_id, cloud

pipeline {
   agent{
       node{
            label "master"
       }
   }
   parameters{
    string(name: 'project_name', defaultValue: '', description: 'project_name')
    string(name: 'netsim_image_version', defaultValue: '', description: 'Image version for updated Netsim VM')
    string(name: 'taf_image_version', defaultValue: '', description: 'Image version for updated TAF VM')
    string(name: 'selenium_image_version', defaultValue: '', description: 'Image version for updated Selenium VM')
    string(name: 'branch', defaultValue: '', description: 'Branch (defaults to master)')
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
               }
           }
       }
       stage('Update Infrastructure'){
        steps{
            build job: 'update_ffe_infrastructure' , parameters: [
                string(name: 'project_name', value: String.valueOf(project_name)),
                string(name: 'deployment_id', value: deployment_id),
                string(name: 'cloud', value: cloud),
                string(name: 'netsim_image_version', value: String.valueOf(netsim_image_version)),
                string(name: 'taf_image_version', value: String.valueOf(taf_image_version)),
                string(name: 'netsim_image_version', value: String.valueOf(netsim_image_version)),
                string(name: 'branch', value: String.valueOf(branch))
            ]
        }
       }
   }
}
