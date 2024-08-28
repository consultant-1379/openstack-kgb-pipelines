listView('FFE_Pipelines') {
     filterBuildQueue()
     filterExecutors()
     jobs {
          regex(/(.*[aAbBcC][0-9]{1,3})$/)
     }
     columns {
          status()
          weather()
          name()
          lastSuccess()
          lastFailure()
          lastDuration()
          buildButton()
     }
}
listView('FFE_Pipeline_Jobs') {
     filterBuildQueue()
     filterExecutors()
     jobs {
           name('FFE_Install')
           name('FFE_Upgrade')
           name('FFE_TAF')
           name('FFE_Apply_Certs')
           name('FFE_Teardown')
           name('FFE_Add_Licenses')
           //name('FFE_Snapshot')
           //name('FFE_Rollback')
     }
     columns {
          status()
          weather()
          name()
          lastSuccess()
          lastFailure()
          lastDuration()
          buildButton()
     }
}
listView('FFE_Admin_Jobs') {
     filterBuildQueue()
     filterExecutors()
     jobs {
          name('create_ffe_infrastructure')
          name('delete_ffe_infrastructure')
          name('update_ffe_jira')
          name('create_ffe_dtt_entry')
          name('create_ffe_environment')
          name('create_jenkins_agent_setup')
          name('delete_ffe_jenkins_agent')
          name('update_ffe_infrastructure')
          name('delete_ffe_infra_vms')
     }
     columns {
          status()
          weather()
          name()
          lastSuccess()
          lastFailure()
          lastDuration()
          buildButton()
     }
}
listView('FFE_Admin_Pipelines') {
     filterBuildQueue()
     filterExecutors()
     jobs {
          name('FFE_Commissioning_Pipeline')
          name('FFE_Decommissioning_Pipeline')
          name('FFE_Infrastructure_Update')
          name('FFE_Infrastructure_Refresh')
     }
     columns {
          status()
          weather()
          name()
          lastSuccess()
          lastFailure()
          lastDuration()
          buildButton()
     }
}
listView('FFE_Pipeline_Rollout') {
     filterBuildQueue()
     filterExecutors()
     jobs {
          regex(/.*_Rollout|.*_Generator$/)
     }
     columns {
          status()
          weather()
          name()
          lastSuccess()
          lastFailure()
          lastDuration()
          buildButton()
     }
}
