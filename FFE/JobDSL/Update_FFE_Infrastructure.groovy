job('update_ffe_infrastructure'){
  description("Update FFE Infrastructure VMs - Auto generated using Job DSL - all manual changes will be overwritten!")
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
      file('ANSIBLE_VAULT_PASSWORD_FILE', 'ansible_vault_file')
      string('OS_PASSWORD','FFE_infra_admin_OpenStack')
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
      name('deployment_id')
      defaultValue('')
      description('ID of deployment')
      trim(true)
    }
    stringParam {
      name('cloud')
      defaultValue('')
      description('Openstack cloud of project')
      trim(true)
    }
    stringParam {
      name('netsim_image_version')
      defaultValue('')
      description('Image version for updated Netsim VM')
      trim(true)
    }
    stringParam {
      name('taf_image_version')
      defaultValue('')
      description('Image version for updated TAF VM')
      trim(true)
    }
    stringParam {
      name('selenium_image_version')
      defaultValue('')
      description('Image version for updated Selenium VM')
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
# update playbook command to include taf, selenium, netsim versions once implemented from infra side
# ansible-playbook ansible/deploy-ffe-infra-vms.yml -e deployment_id=${deployment_id} -e image=${image} -e project=${project}
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
