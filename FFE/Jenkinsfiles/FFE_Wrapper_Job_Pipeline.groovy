def DROP, ENM_ISO_VERSION, TEST_PHASE, build_description, te_allure_log

pipeline {
   agent{
       node{
           label String.valueOf(jenkins_agent)
       }
   }
   options {
       timestamps()
       disableConcurrentBuilds()
   }
   stages {
       stage('Get Additional Parameters for Pipeline') {
           when {
               expression { params.jobType != 'teardown' }
           }
           steps {
               script {
                   project_name = job_name
                   TEST_PHASE = "MTE"
                   if(String.valueOf(product_set_version) == "") {
                       product_set_version = sh(script:'''curl -L -s "https://ci-portal.seli.wh.rnd.internal.ericsson.com/getLastGoodProductSetVersion/?productSet=ENM&confidenceLevel=Deploy-vENM-II"''', returnStdout: true)
                   } else {
                       product_set_version = String.valueOf(product_set_version)
                   }
                   echo "Product Set is " + "$product_set_version"
                   drop_values = String.valueOf(product_set_version).tokenize('.')
                   DROP = drop_values[0] + "." + drop_values[1]
                   echo "Drop is " + "$DROP"
                   curl_response = sh(script:'''curl -L -s https://ci-portal.seli.wh.rnd.internal.ericsson.com/api/deployment/deploymentutilities/productSet/ENM/version/''' + "$product_set_version", returnStdout: true)
                   ENM_ISO_VERSION = curl_response.substring(curl_response.lastIndexOf("mediaArtifactVersion")).split('"')[2]
                   echo "ENM ISO Version is " + "$ENM_ISO_VERSION"
                   enm_gui_link = "https://${deploymentId}.athtem.eei.ericsson.se/"
                   if(String.valueOf(enterDeployerVersion) == "No") {
                       deployerVersion = ""
                   } else {
                       deployerVersion = String.valueOf(deployerVersion)
                   }
                   if(String.valueOf(enterProfiles) == "No") {
                       profiles = ""
                   } else {
                       profiles = String.valueOf(profiles)
                   }
                   if(String.valueOf(enterEDPVersion) == "No") {
                       edpVersion = ""
                   } else {
                       edpVersion = String.valueOf(edpVersion)
                   }
               }
           }
       }
      stage('Set build name and description'){
        steps{
            script{
                JOB_TYPE = String.valueOf(params.jobType)
                DEPLOYMENT_ID = String.valueOf(deploymentId)
                DEPLOY_PACKAGE = String.valueOf(deployPackage)
                TAF = params.run_taf ? 'Yes' : 'No'
                PRODUCT_SET = String.valueOf(product_set_version)
                MT_CSV_FILE_URI = String.valueOf(mt_csv_file_uri)
                currentBuild.displayName = String.valueOf(params.jobType) + ' - ' + String.valueOf(deploymentId)
                build_description = """<b>${JOB_TYPE}</b> for deployment <b>${DEPLOYMENT_ID}</b> <br>Product set: ${PRODUCT_SET} <br>deployPackage: ${DEPLOY_PACKAGE} <br> TAF: ${TAF} <br> mt_csv_file_uri: ${MT_CSV_FILE_URI}"""
                currentBuild.description = build_description
            }
        }
       }
      stage('Reinstall Netsim VM') {
          when {
             expression { params.jobType == 'install' && !params.skip_reinstall_netsim_vm }
          }
          steps {
              build job: 'FFE_Infrastructure_Refresh', parameters: [
                  string(name: 'project_name', value: String.valueOf(project_name)),
                  booleanParam(name: 'delete_netsim', value: true)
              ]
          }
      }
      stage('Install, Apply Licenses and Certs Only, Upgrade, or Teardown') {
          parallel {
              stage('Teardown, Install, Add Licenses, Update Trust Profiles') {
                  when {
                      expression { params.jobType == 'install' }
                  }
                  steps {
                      build job: 'FFE_Teardown', parameters: [
                          string(name: 'deploymentId', value: String.valueOf(deploymentId)),
                          string(name: 'enterDeployerVersion', value: String.valueOf(enterDeployerVersion)),
                          string(name: 'deployerVersion', value: String.valueOf(deployerVersion)),
                          string(name: 'jenkins_agent', value: String.valueOf(jenkins_agent))
                      ]
                      build job: 'FFE_Install', parameters: [
                          string(name: 'productSet', value: String.valueOf(product_set_version)),
                          string(name: 'deployPackage', value: String.valueOf(deployPackage)),
                          string(name: 'enterDeployerVersion', value: String.valueOf(enterDeployerVersion)),
                          string(name: 'deployerVersion', value: String.valueOf(deployerVersion)),
                          string(name: 'deploymentId', value: String.valueOf(deploymentId)),
                          string(name: 'jenkins_agent', value: String.valueOf(jenkins_agent))
                      ]
                      build job: 'FFE_Add_Licenses', parameters: [
                          string(name: 'test_phase', value: "$TEST_PHASE"),
                          string(name: 'cluster_id', value: String.valueOf(deploymentId)),
                          string(name: 'mt_utils_version', value: String.valueOf(mt_utils_version)),
                          string(name: 'drop', value: "$DROP"),
                          string(name: 'product_set_version', value: String.valueOf(product_set_version)),
                          string(name: 'deployment_type', value: 'cloud'),
                          string(name: 'jenkins_agent', value: String.valueOf(jenkins_agent))
                      ]
                      build job: 'FFE_Apply_Certs', parameters: [
                          string(name: 'clusterId', value: String.valueOf(deploymentId)),
                          string(name: 'drop', value: "$DROP"),
                          string(name: 'simdep_release', value: String.valueOf(simdep_release)),
                          string(name: 'enm_gui_link', value: "$enm_gui_link"),
                          string(name: 'deployment_type', value: 'Cloud'),
                          string(name: 'MT_utils_version', value: String.valueOf(mt_utils_version)),
                          string(name: 'nodesCleanUp', value: 'NO')
                      ]
                  }
              }
              stage('Apply Licenses and Certs Only'){
                  when {
                    expression { params.jobType == 'apply_licenses_and_certs_only'}
                  }
                  steps{
                    build job: 'FFE_Add_Licenses', parameters: [
                        string(name: 'test_phase', value: "$TEST_PHASE"),
                        string(name: 'cluster_id', value: String.valueOf(deploymentId)),
                        string(name: 'mt_utils_version', value: String.valueOf(mt_utils_version)),
                        string(name: 'drop', value: "$DROP"),
                        string(name: 'product_set_version', value: String.valueOf(product_set_version)),
                        string(name: 'deployment_type', value: 'cloud'),
                        string(name: 'jenkins_agent', value: String.valueOf(jenkins_agent))
                    ]
                    build job: 'FFE_Apply_Certs', parameters: [
                        string(name: 'clusterId', value: String.valueOf(deploymentId)),
                        string(name: 'drop', value: "$DROP"),
                        string(name: 'simdep_release', value: String.valueOf(simdep_release)),
                        string(name: 'enm_gui_link', value: "$enm_gui_link"),
                        string(name: 'deployment_type', value: 'Cloud'),
                        string(name: 'MT_utils_version', value: String.valueOf(mt_utils_version)),
                        string(name: 'nodesCleanUp', value: 'NO')
                    ]
                }
              }
              stage('Upgrade') {
                  when {
                      expression { params.jobType == 'upgrade' }
                  }
                  steps {
                      build job: 'FFE_Upgrade', parameters: [
                          string(name: 'productSet', value: String.valueOf(product_set_version)),
                          string(name: 'deployPackage', value: String.valueOf(deployPackage)),
                          string(name: 'deploymentId', value: String.valueOf(deploymentId)),
                          string(name: 'enterProfiles', value: String.valueOf(enterProfiles)),
                          string(name: 'profiles', value: String.valueOf(profiles)),
                          string(name: 'drop', value: "$DROP"),
                          string(name: 'enterEDPVersion', value: String.valueOf(enterEDPVersion)),
                          string(name: 'edpVersion', value: String.valueOf(edpVersion)),
                          string(name: 'jenkins_agent', value: String.valueOf(jenkins_agent))
                      ]
                  }
              }
              stage('Teardown') {
                  when {
                      expression { params.jobType == 'teardown' }
                  }
                  steps {
                      build job: 'FFE_Teardown', parameters: [
                          string(name: 'deploymentId', value: String.valueOf(deploymentId)),
                          string(name: 'enterDeployerVersion', value: String.valueOf(enterDeployerVersion)),
                          string(name: 'deployerVersion', value: String.valueOf(deployerVersion)),
                          string(name: 'jenkins_agent', value: String.valueOf(jenkins_agent))
                      ]
                  }
              }
          }
      }
      stage('Run TAF if applicable') {
          when {
            expression { ((params.jobType == 'install' || params.jobType == 'apply_licenses_and_certs_only' || params.jobType == 'upgrade') && params.run_taf) || params.jobType == 'taf_only'}
          }
          steps {
              script{
                  taf_job = build job: 'FFE_TAF', parameters: [
                      string(name: 'enmIso', value: "$ENM_ISO_VERSION"),
                      string(name: 'enmDrop', value: "$DROP"),
                      string(name: 'NssProductSetVersion', value: String.valueOf(product_set_version)),
                      string(name: 'hostname', value: String.valueOf(deploymentId)),
                      string(name: 'taf_schedule', value: String.valueOf(taf_schedule)),
                      string(name: 'taf_profiles', value: String.valueOf(taf_profiles)),
                      string(name: 'mt_csv_file_uri', value: String.valueOf(mt_csv_file_uri)),
                      string(name: 'internal_nodes', value: String.valueOf(internal_nodes)),
                      string(name: 'jenkins_agent', value: String.valueOf(jenkins_agent))
                      ]
                    taf_job_num = taf_job.getBuildVariables()["BUILD_NUMBER"]
                    echo "fetching te allure log url...."
                    te_allure_log = sh(script:'''curl -L -s https://meteo.athtem.eei.ericsson.se//get-te-allure-log-api/?build='''+"$taf_job_num", returnStdout: true).replaceAll('"','')
                    echo "TE_ALLURE_LOG_URL: " + "$te_allure_log"
                    currentBuild.description += """<br>Te Allure Log: <a href=${te_allure_log}>${te_allure_log}</a>"""
              }
          }
      }
    }
    post{
       success{
           emailext body: "<b style='color:green'>Build successful: ${deploymentId} - ${params.jobType}</b><br>Project: ${env.JOB_NAME} <br>Build Number: ${env.BUILD_NUMBER} <br> Build URL: ${env.BUILD_URL} <br> Description: " + build_description + "<br> Te Allure Log: ${te_allure_log}",
           mimeType: 'text/html',
           subject: "SUCCESS: ${params.jobType} for ${deploymentId}",
           compressLog: true,
           attachLog: true,
           to: String.valueOf(email_recipients),
           recipientProviders: [requestor()];
       }
       failure{
           emailext body: "<b style='color:red'>Build failed: ${deploymentId} - ${params.jobType}</b><br>Project: ${env.JOB_NAME} <br>Build Number: ${env.BUILD_NUMBER} <br> Build URL: ${env.BUILD_URL} <br> Description: " + build_description + "<br> Te Allure Log: ${te_allure_log}",
           mimeType: 'text/html',
           subject: "FAILED: ${params.jobType} for ${deploymentId}",
           compressLog: true,
           attachLog: true,
           to: String.valueOf(email_recipients),
           recipientProviders: [requestor()];
       }
   }
}

