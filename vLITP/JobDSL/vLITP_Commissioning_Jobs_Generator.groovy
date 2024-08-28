job('create_vlitp_infrastructure'){
  description("Create vLITP Infratructure VMs - Auto generated using Job DSL - all manual changes will be overwritten!")
  label('infra_ffe') // not sure about this
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
      description('Name of the OpenStack project where the gateway,netsim, TAF and seleniumhub VMs will be deployed. e.g. EE_FFE_C17B17')
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
      description('''<p>Name of the git branch to use. Defaults to <em>master</em>.</p>
<p>For any other branch e.g. mybranch specify it as <em>mybranch</em> <strong>not</strong> refs/heads/<em>mybranch</em>.</p>''')
      trim(true)
    }
    stringParam {
      name('external_network')
      defaultValue('master')
      description('External network for the FFE gateway e.g. GGN/ECN network')
      trim(true)
    }
  }
  steps {
    shell ('''export OS_IMAGE_API_VERSION=2
export OS_AUTH_URL=https://cloud{cloud}.athtem.eei.ericsson.se:13000
export OS_PROJECT_DOMAIN_ID=default
export OS_REGION_NAME=regionOne
export OS_PROJECT_NAME=vLITP_infra_admin
export OS_PROJECT_DOMAIN_NAME=Default
export OS_USER_DOMAIN_NAME=Default
export OS_IDENTITY_API_VERSION=3
export OS_AUTH_TYPE=password
export OS_NO_CACHE=True
export OS_COMPUTE_API_VERSION=2.latest
export OS_INTERFACE=public
export OS_USERNAME=vLITP_infra_admin
export OS_VOLUME_API_VERSION=3

ansible-playbook ansible/deploy-vlitp-infra-vms.yml -e deployment_id=${deployment_id} -e svc_node_count=12 -e project=${project_name} -e external_network=${external_network}
    ''')
  }
  publishers {
    git{
        pushOnlyIfSuccess(true)
        pushMerge(true)
      branch('origin', '${branch}')
    }
    mailer('thomas.walsh@ericsson.com,simon.a.fagan@ericsson.com', false, false) // different contacts needed
  }
}
