def deployment_id, cloud

pipeline {
   agent{
       node{
            label "master"
       }
   }
   parameters{
    string(name: 'project_name', defaultValue: '', description: 'Enter the OpenStack project name (e.g. ENM_FFE_C17B01)')
    booleanParam(name: 'delete_netsim', defaultValue: false, description: 'Delete Netsim VM and Volume and recreate them with the latest version')
    booleanParam(name: 'delete_taf', defaultValue: false, description: 'Delete TAF VM and Volumes and recreate them with the latest version')
    booleanParam(name: 'delete_selenium', defaultValue: false, description: 'Delete Selenium VM and Volume and recreate them with the latest version')
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
       stage('Delete specified VMs'){
           steps{
               build job: 'delete_ffe_infra_vms' , parameters: [
                   string(name: 'project_name', value: String.valueOf(project_name)),
                   booleanParam(name: 'delete_netsim', value: Boolean.valueOf(delete_netsim)),
		   booleanParam(name: 'delete_taf', value: Boolean.valueOf(delete_taf)),
		   booleanParam(name: 'delete_selenium', value: Boolean.valueOf(delete_selenium))
              ]
           }
       }
       stage('Recreate deleted VMs as latest version'){
	   steps{
               build job: 'create_ffe_infrastructure' , parameters: [
                   string(name: 'project_name', value: String.valueOf(project_name)),
                   string(name: 'deployment_id', value: deployment_id),
                   string(name: 'cloud', value: cloud)
               ]
           }
       }
   }
}
