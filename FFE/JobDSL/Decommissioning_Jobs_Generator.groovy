job('delete_ffe_dtt_entry') {
  description ('Removes a DTT deployment for an FFE environment - autogenerated using JobDSL - all manual changes will be overwritten!')
    logRotator {
      numToKeep(30)
    }
    parameters {
      stringParam('deployment_id', '','Deployment ID of the project (eg. ieatenmc17b08)')
    }
    steps{
        shell('''echo "Deleting DTT entry for ${deployent_id}..."
        curl -X DELETE "https://meteo.athtem.eei.ericsson.se/delete-ffe-dtt-entry-api/?deployment_id=${deployment_id}" -H "Content-Type: application/json"''')
    }
}

job('delete_ffe_environment'){
  description("Delete Openstack project and DIT documents for FFE environment - Auto generated using Job DSL - all manual changes will be overwritten!")
  label('master')
  concurrentBuild(allowConcurrentBuild = true)
  logRotator{
    numToKeep(30)
  }
  parameters{
    stringParam {
      name('cloud')
      defaultValue('')
      description('The cloud on which the OS project is to be created eg. 17b')
      trim(true)
    }
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
  }
  steps {
    shell ('''echo "Deleting DIT Docs for project ${project_name}..."
     curl -X DELETE "https://meteo.athtem.eei.ericsson.se/delete-ffe-dit-docs-api/?deployment_id=${deployment_id}&cloud=${cloud}" -H "Content-Type: application/json"
     echo "Deleting project ${project_name}..."
     curl -X DELETE "https://meteo.athtem.eei.ericsson.se/delete-ffe-project-api/?cloud=${cloud}&project_name=${project_name}" -H "Content-Type: application/json"
     echo "Done!"
    ''')
  }
  publishers {
    mailer('thomas.walsh@ericsson.com,simon.a.fagan@ericsson.com', false, false)
  }
}

job('delete_ffe_infrastructure'){
  description("Delete FFE Infrastructure VMs - Auto generated using Job DSL - all manual changes will be overwritten!")
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
      name('branch')
      defaultValue('master')
      description('Name of the git branch to use. Defaults to <em>master</em>.')
      trim(true)
    }
  }
  steps {
    shell ('''export OS_IMAGE_API_VERSION=2
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

ansible-playbook ansible/delete-ffe-infra-vms.yml -e deployment_id=${deployment_id} -e project=${project}''')
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

job('delete_ffe_jenkins_agent'){
  description("Deletes a jenkins agent (gateway) of an FFE environment - Auto generated using Job DSL - all manual changes will be overwritten!")
  label('master')
  concurrentBuild(allowConcurrentBuild = true)
  logRotator{
    numToKeep(72)
  }
  wrappers{
    credentialsBinding{
      string("DEPADM100_SECRET","DEPADM100_SECRET")
    }
  }
  parameters{
    stringParam {
      name('nodeName')
      defaultValue('')
      description('''This is the Jenkis slave/node name.
<br>
Example:
<br>
ENM_FFE_C12B23''')
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

build.setDisplayName(hardcoded_param_value)''')
    shell ('''echo "Deleting node: $nodeName"
java -jar /proj/ciexadm200/tools/jcli/jenkins-cli.jar -noCertificateCheck -s https://fem24s11-eiffel004.eiffel.gic.ericsson.se:8443/jenkins -auth depadm100:$DEPADM100_SECRET delete-node $nodeName''')
  }
  publishers {
    mailer('thomas.walsh@ericsson.com,simon.a.fagan@ericsson.com', false, false)
  }
}
