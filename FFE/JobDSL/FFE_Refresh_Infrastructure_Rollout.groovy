pipelineJob('delete_ffe_infra_vms') {
  description("Delete specified FFE Infrastructure VMs - Auto generated using Job DSL - all manual changes will be overwritten!")
    logRotator {
        numToKeep(100)
    }
    definition {
        cpsScm {
          scm {
            git {
              branch('master')
              remote {
                credentials('lciadm100_gerrit_ssh')
                url("${GERRIT_MIRROR}/OSS/ENM-Parent/SQ-Gate/com.ericsson.de/openstack-kgb-pipelines")
              }
              extensions {
                cleanBeforeCheckout()
              }
                }
          }
          scriptPath('FFE/Jenkinsfiles/Delete_FFE_Infra_VMs.groovy')
          lightweight(lightweight = true)
        }
    }
}
pipelineJob('FFE_Infrastructure_Refresh') {
  description("Delete specified FFE Infrastructure VMs and recreate them with the latest version - Auto generated using Job DSL - all manual changes will be overwritten!")
    logRotator {
        numToKeep(100)
    }
    definition {
        cpsScm {
          scm {
            git {
              branch('master')
              remote {
                credentials('lciadm100_gerrit_ssh')
                url("${GERRIT_MIRROR}/OSS/ENM-Parent/SQ-Gate/com.ericsson.de/openstack-kgb-pipelines")
              }
              extensions {
                cleanBeforeCheckout()
              }
                }
          }
          scriptPath('FFE/Jenkinsfiles/Refresh_FFE_Infrastructure_Pipeline.groovy')
          lightweight(lightweight = true)
        }
    }
}
