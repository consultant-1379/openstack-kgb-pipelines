job('FFE_Commissioning_Pipeline_Rollout'){
  label("${FFE_Commissioning_Agents}")
  concurrentBuild(allowConcurrentBuild = true)
  logRotator {
    numToKeep(30)
  }
  parameters {
    stringParam('FFE_Commissioning_Agents', "${FFE_Commissioning_Agents}",'The agents configured to run the commissioning jobs')
  }
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
  steps {
      dsl {
        text(readFileFromWorkspace('FFE/JobDSL/Create_FFE_Commissioning_Pipeline.groovy'))
    }
  }
}

job('FFE_Commissioning_Jobs_Rollout'){
  label("${FFE_Commissioning_Jobs_Agents}")
  concurrentBuild(allowConcurrentBuild = true)
  logRotator {
    numToKeep(30)
  }
  parameters {
    stringParam('FFE_Commissioning_Jobs_Agents', "${FFE_Commissioning_Jobs_Agents}",'The agents configured to create the commissioning jobs on')
  }
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
  steps {
      dsl {
        text(readFileFromWorkspace('FFE/JobDSL/Commissioning_Jobs_Generator.groovy'))
    }
  }
}

job('FFE_Decommissioning_Pipeline_Rollout'){
  label("${FFE_Decommissioning_Agents}")
  concurrentBuild(allowConcurrentBuild = true)
  logRotator {
    numToKeep(30)
  }
  parameters {
    stringParam('FFE_Decommissioning_Agents', "${FFE_Decommissioning_Agents}",'The agents configured to run the decommissioning jobs')
  }
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
  steps {
      dsl {
        text(readFileFromWorkspace('FFE/JobDSL/Create_FFE_Decommissioning_Pipeline.groovy'))
    }
  }
}

job('FFE_Decommissioning_Jobs_Rollout'){
  label("${FFE_Decommissioning_Jobs_Agents}")
  concurrentBuild(allowConcurrentBuild = true)
  logRotator {
    numToKeep(30)
  }
  parameters {
    stringParam('FFE_Decommissioning_Jobs_Agents', "${FFE_Decommissioning_Jobs_Agents}",'The agents configured to create the decommissioning jobs on')
  }
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
  steps {
      dsl {
        text(readFileFromWorkspace('FFE/JobDSL/Decommissioning_Jobs_Generator.groovy'))
    }
  }
}

job('FFE_ENM_Pipeline_Rollout'){
  label("${FFE_ENM_Agents}")
  concurrentBuild(allowConcurrentBuild = true)
  logRotator {
    numToKeep(30)
  }
  parameters {
    stringParam('FFE_ENM_Agents', "${FFE_ENM_Agents}",'The agents configured to run the ENM jobs')
    stringParam('Apply_Certs_Agents', "${Apply_Certs_Agents}",'The agents configured to run the Apply Certs job')
  }
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
  steps {
      dsl {
        text(readFileFromWorkspace('FFE/JobDSL/FFE_Pipeline_Jobs_Rollout.groovy'))
    }
  }
}

job('FFE_Update_Infrastructure_Pipeline_Rollout'){
  label("${FFE_Update_Infrastructure_Agents}")
  concurrentBuild(allowConcurrentBuild = true)
  logRotator {
    numToKeep(30)
  }
  parameters {
    stringParam('FFE_Update_Infrastructure_Agents', "${FFE_Update_Infrastructure_Agents}",'The agents configured to run the Update Infrastructure jobs')
  }
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
  steps {
      dsl {
        text(readFileFromWorkspace('FFE/JobDSL/Update_FFE_Infrastructure.groovy'))
    }
  }
}

job('FFE_Wrapper_Pipeline_Rollout'){
  label("${FFE_Wrapper_Pipeline_Agents}")
  concurrentBuild(allowConcurrentBuild = true)
  logRotator {
    numToKeep(30)
  }
  parameters {
    stringParam('FFE_Wrapper_Pipeline_Agents', "${FFE_Wrapper_Pipeline_Agents}",'The agents configured to run the Wrapper Pipeline jobs')
  }
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
  steps {
      dsl {
        text(readFileFromWorkspace('FFE/JobDSL/FFE_Wrapper_Jobs_Rollout.groovy'))
        removeAction('DELETE')
    }
  }
}

job('FFE_TAF_Job_Rollout'){
  concurrentBuild(allowConcurrentBuild = true)
  logRotator {
    numToKeep(30)
  }
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
  steps {
      dsl {
        text(readFileFromWorkspace('FFE/JobDSL/FFE_TAF_Job_Rollout.groovy'))
    }
  }
}

job('FFE_Refresh_Infrastructure_Pipeline_Rollout'){
  label("${FFE_Refresh_Infrastructure_Agents}")
  concurrentBuild(allowConcurrentBuild = true)
  logRotator {
    numToKeep(30)
  }
  parameters {
    stringParam('FFE_Refresh_Infrastructure_Agents', "${FFE_Refresh_Infrastructure_Agents}",'The agents configured to run the Refresh Infrastructure jobs')
  }
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
  steps {
      dsl {
        text(readFileFromWorkspace('FFE/JobDSL/FFE_Refresh_Infrastructure_Rollout.groovy'))
    }
  }
}

job('FFE_Views_Rollout'){
  concurrentBuild(allowConcurrentBuild = true)
  logRotator {
    numToKeep(30)
  }
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
  steps {
      dsl {
        text(readFileFromWorkspace('FFE/JobDSL/FFE_Views_Rollout.groovy'))
    }
  }
}
