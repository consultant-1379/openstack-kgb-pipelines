job('create_ffe_environment'){
  description("Create Openstack project and DIT documents for FFE environment - Auto generated using Job DSL - all manual changes will be overwritten!")
  label('master')
  concurrentBuild(allowConcurrentBuild = true)
  logRotator{
    numToKeep(30)
  }
  parameters{
    stringParam {
      name('project_name')
      defaultValue('')
      description('The name of the Openstack project eg. Project_FFE_C17B08')
      trim(true)
    }
    stringParam {
      name('deployment_id')
      defaultValue('')
      description('The deployment id of the project for DIT eg. ieatenmc17b08')
      trim(true)
    }
    stringParam {
      name('cloud')
      defaultValue('')
      description('The cloud on which the OS project is to be created eg. 17b (also creates northbound network on DIT pod)')
      trim(true)
    }
    stringParam {
      name('volumes')
      defaultValue('')
      description('Number of volumes required for Openstack project')
      trim(true)
    }
    stringParam {
      name('cpu')
      defaultValue('')
      description('Number of vCPUs required for Openstack project')
      trim(true)
    }
    stringParam {
      name('ram')
      defaultValue('')
      description('RAM (in MB) required for Openstack project eg. 532340 MB')
      trim(true)
    }
    stringParam {
      name('storage')
      defaultValue('')
      description('Cinder storage (in GB) required for Openstack project eg. 2000 GB')
      trim(true)
    }
    stringParam {
      name('contacts')
      defaultValue('')
      description('Contacts for Openstack project (comma separated string of email addresses)')
      trim(true)
    }
    stringParam {
      name('product_set')
      defaultValue('')
      description('Product set used for creating FFE DIT documents')
      trim(true)
    }
  }
  steps {
    shell ('''curl -X POST -H 'Content-Type: application/json' "https://meteo.athtem.eei.ericsson.se/create-ffe-project-api/?cloud=${cloud}&project_name=${project_name}&contact=${contacts}&deployment_id=${deployment_id}&cpu=${cpu}&ram=${ram}&storage=${storage}&volumes=${volumes}"
curl -X POST -H 'Content-Type: application/json' "https://meteo.athtem.eei.ericsson.se/create-ffe-dit-docs-api/?deployment_id=${deployment_id}&project_name=${project_name}&cloud=${cloud}&product_set=${product_set}"
''')
  }
  publishers {
    mailer('thomas.walsh@ericsson.com,simon.a.fagan@ericsson.com', false, false)
  }
}

job('create_ffe_infrastructure'){
  description("Create FFE Infrtructure VMs - Auto generated using Job DSL - all manual changes will be overwritten!")
  label('infra_ffe')
  concurrentBuild(allowConcurrentBuild = true)
  logRotator{
    numToKeep(30)
  }
  scm {
    git {
      remote{
        url('${GERRIT_CENTRAL}/OSS/ENM-Parent/SQ-Gate/com.ericsson.de/infra-ffe')
        credentials('infra-ffe-git')
      }
      branch('${branch}')
    }
  }
  wrappers{
    credentialsBinding{
      file("ANSIBLE_VAULT_PASSWORD_FILE", "ANSIBLE_VAULT_PASSWORD_FILE")
      string("OS_PASSWORD","FFE_infra_admin_OpenStack")
    }
  }
  parameters{
    stringParam {
      name('project_name')
      defaultValue('')
      description('The name of the Openstack project')
      trim(true)
    }
    stringParam {
      name('cloud')
      defaultValue('')
      description('The name of the Openstack cloud')
      trim(true)
    }
    stringParam {
      name('deployment_id')
      defaultValue('')
      description('The deployment id of the project for DIT')
      trim(true)
    }
    stringParam {
      name('gateway_image_version')
      defaultValue('')
      description('Name of gateway_image_version')
      trim(true)
    }
    stringParam {
      name('netsim_image_version')
      defaultValue('')
      description('Name of netsim_image_version')
      trim(true)
    }
    stringParam {
      name('taf_image_version')
      defaultValue('')
      description('Name of taf_image_version')
      trim(true)
    }
    stringParam {
      name('selenium_image_version')
      defaultValue('')
      description('Name of selenium_image_version')
      trim(true)
    }
    stringParam {
      name('branch')
      defaultValue('master')
      description('Name of the git branch to use. Defaults to <em>master</em>.')
      trim(true)
    }
  }
  steps {
    shell ('''
export OS_IMAGE_API_VERSION=2
export OS_AUTH_URL=https://cloud${cloud}.athtem.eei.ericsson.se:13000
export OS_PROJECT_DOMAIN_ID=default
export OS_REGION_NAME=regionOne
export OS_PROJECT_NAME=FFE_infra_admin
export OS_PROJECT_DOMAIN_NAME=Default
export OS_USER_DOMAIN_NAME=Default
export OS_IDENTITY_API_VERSION=3
export OS_AUTH_TYPE=password
export OS_NO_CACHE=True
export OS_COMPUTE_API_VERSION=2.latest
export OS_INTERFACE=public
export OS_USERNAME=FFE_infra_admin
export OS_VOLUME_API_VERSION=3
ansible-playbook ansible/deploy-ffe-infra-vms.yml -e deployment_id=${deployment_id} -e image=${image} -e project=${project_name}
    ''')
  }
  publishers {
    git{
        pushOnlyIfSuccess(true)
        pushMerge(true)
      branch('origin', '${branch}')
    }
    mailer('thomas.walsh@ericsson.com,simon.a.fagan@ericsson.com', false, false)
  }
}

job('create_ffe_dtt_entry') {
  description ('Creates a DTT deployment for an FFE environment - autogenerated using JobDSL - all manual changes will be overwritten!')
    logRotator {
      numToKeep(100)
    }
    wrappers{
        preBuildCleanup()
        timestamps()
    }
    parameters {
      stringParam('project_name', '','Name of the Openstack project')
      stringParam('deployment_id', '','Deployment ID of the project (eg. ieatenmc17b08)')
      stringParam('program','' ,'Program to which the environment belongs (needed for DTT)')
      stringParam('requirement_area','' ,'Requirement area to which the project belongs')
      stringParam('jira','' ,'ID of jira ticket/issue associated with environment')
      stringParam('team_name','' ,'Team using the deployment')
      stringParam('spocs','' ,'Point of contact for the project (SIGNUM)')
    }
    steps{
        shell('''echo "Creating DTT entry for project ${project_name}"
        ra_api=${requirement_area// /%20}
        program_api=${program// /%20}
        team_api=${team_name// /%20}
        curl -X POST "https://meteo.athtem.eei.ericsson.se/create-ffe-dtt-entry-api/?deployment_id=${deployment_id}&project_name=${project_name}&jira_ticket=${jira}&ra=${ra_api}&program=${program_api}&team_name=${team_api}&spocs=${spocs}" -H "Content-Type: application/json"''')
    }
}

job('create_jenkins_agent_setup'){
  description("Add the gateway as a jenkins agent - all manual changes will be overwritten!")
  label('master')
  concurrentBuild(allowConcurrentBuild = true)
  logRotator{
    numToKeep(72)
  }
  wrappers{
    preBuildCleanup{
      cleanupParameter('true')
    }
    credentialsBinding{
      string("DEPADM100_SECRET", "DEPADM100_SECRET")
    }
  }
  parameters{
    stringParam {
      name('nodeIP')
      defaultValue('')
      description('The Gateway IP Address')
      trim(true)
    }
    stringParam {
      name('nodeName')
      defaultValue('')
      description('Name of node')
      trim(true)
    }
  }
  steps {
    systemGroovyCommand('''import hudson.model.*
import hudson.util.*

// get current thread / Executor
def thr = Thread.currentThread()
// get current build
def build = thr?.executable

def hardcoded_param = "nodeName"
def resolver = build.buildVariableResolver
def hardcoded_param_value = resolver.resolve(hardcoded_param)

build.setDisplayName(hardcoded_param_value)
''')
    shell ('''echo "<?xml version="\\""1.0"\\"" encoding="\\""UTF-8"\\""?> <slave>   <name>$nodeName</name>   <description>Agent for vENM use</description>   <remoteFS>/home/lciadm100/jenkins</remoteFS>   <numExecutors>3</numExecutors>   <mode>EXCLUSIVE</mode>   <retentionStrategy class="\\""hudson.slaves.RetentionStrategy$Always"\\""/>   <launcher class="\\""hudson.plugins.sshslaves.SSHLauncher"\\"" plugin="\\""ssh-slaves@1.6"\\"">     <host>$nodeIP</host>     <port>22</port>   <credentialsId>7718fc2b-7626-4608-8849-e5b7aff719f7</credentialsId>   </launcher>   <label>$nodeName</label>   <nodeProperties/> </slave>"  > tmp.xml
java -jar /proj/ciexadm200/tools/jcli/jenkins-cli.jar -noCertificateCheck -s https://fem24s11-eiffel004.eiffel.gic.ericsson.se:8443/jenkins -auth depadm100:$DEPADM100_SECRET create-node < tmp.xml
rm -rf tmp.xml

echo "NOTE: to run a jenkins job on this node you should change the "Restrict where this project can be run" setting in your jenkins job config to $nodeName"
''')
  }
  publishers {
    mailer('thomas.walsh@ericsson.com,simon.a.fagan@ericsson.com', false, false)
  }
}

job('update_ffe_jira') {
  description ('Updates a Jira ticket with info of a new project or a notification for the deletion of a cluster - autogenerated using JobDSL - all manual changes will be overwritten!')
    logRotator {
      numToKeep(100)
    }
    wrappers{
        preBuildCleanup()
        timestamps()
    }
    parameters {
      choiceParam('status', ['create', 'delete'], 'State if the project is being created or deleted')
      stringParam('project_name', '','Name of Openmstack project')
      stringParam('jira_ticket','' ,'Ticket to update with project status (Ex. EESS-99999)')
    }
    steps{
        shell('''echo "Updating jira ${jira_ticket}..."
        curl "https://meteo.athtem.eei.ericsson.se/update-ffe-jira-api/?status=${status}&project_name=${project_name}&jira_ticket=${jira_ticket}"''')
    }
}
