pipelineJob('FFE_Install') {
  description ('FFE Install Job - autogenerated using JobDSL - all manual changes will be overwritten!')
    logRotator {
      numToKeep(100)
    }
    parameters {
      stringParam('jobType', 'install','jobType should be install')
      stringParam('productSet', '','The product set that you want to install in your tenancy \nThe format should be <b>24.07.115</b>')
      stringParam('deployPackage', '','''
   <p>If you <b>want</b> to execute software update, <b>add your packages here</b> in the following format:</p>
<blockquote><p><font color="blue"><b>&lt;deliverable name&gt;::&lt;version&gt;</b></font>, where version is in maven version format or "Latest". There is also an option to add a package that that has not been officially delivered as a complete URL.
  <br><p><font color="blue">Please note: "::Latest" is not supported, you have to specify an actual version of a rpm or a download link"</p>
  <br>ERICrpm::1.2.3
  <br>ERICrpm::https://cifwk-oss.lmera.ericsson.se/static/tmpUploadSnapshot/2015-08-17_14-44-56/ERICapdatamacro_CXP9030537-1.22.9-SNAPSHOT20150817134050.noarch.rpm
  </p></blockquote>

<blockquote><p><b>When adding multiple rpms, use @@ as the separator:</b>
  <br>
  &lt;deliverable name&gt;::&lt;version&gt;<font color="blue"><b>@@</b></font>&lt;deliverable name&gt;::&lt;version&gt;
  <br>ERICrpm::1.0.25@@ERICrpm::1.34.122
  </p></blockquote>''')
      stringParam('enterDeployerVersion', '','')
      stringParam('deployerVersion', '','')
      stringParam('deploymentId','' ,'Deployment ID of the openstack project')
      labelParam('jenkins_agent'){
        defaultValue('')
        description('')
      }
    }
    definition {
      cps {
       script('''pipeline {
   agent {
     node {
       label "$jenkins_agent"
     }
   }
   // The options directive is for configuration that applies to the whole job.
   options {
     buildDiscarder(logRotator(numToKeepStr:'100'))
     timestamps()
     quietPeriod(0)
   }
   // Clean up Workspace at the start of a build
   stages {
     stage('Clean Workspace') {
       steps {
            cleanWs()
       }
     }
     stage('FFE Install') {
       steps {
            sh "wget 'https://arm1s11-eiffel004.eiffel.gic.ericsson.se:8443/nexus/service/local/artifact/maven/redirect?r=releases&g=com.ericsson.de.openstack-kgb&a=openstack-kgb-scripts&p=jar&v=RELEASE' -O openstack-kgb-scripts.jar"
            sh "jar -xvf openstack-kgb-scripts.jar 2>&1 >/dev/null"
            sh "chmod +x job_logic.sh"
            sh "bash job_logic.sh"
       }
     }
   }
   // The post build actions
   post {
     success {
       echo 'Pipeline Successfully Completed'
     }
     failure {
       echo 'Pipeline Failed'
     }
   }
 }''')
     sandbox(true)
      }
    }
}

pipelineJob('FFE_Teardown') {
  description ('FFE Teardown Pipeline - autogenerated using JobDSL - all manual changes will be overwritten!')
    logRotator {
      numToKeep(100)
    }
    parameters {
      stringParam('jobType', 'teardown','jobType should be teardown')
      stringParam('deploymentId','' ,'Deployment ID of the openstack project')
      stringParam('jenkins_agent', '','')
    }
    definition {
      cps {
       script('''pipeline {
   agent {
     node {
       label "$jenkins_agent"
     }
   }
   // The options directive is for configuration that applies to the whole job.
   options {
     buildDiscarder(logRotator(numToKeepStr:'100'))
     timestamps()
     quietPeriod(0)
   }
   stages {
     stage('FFE Teardown') {
       steps {
            sh "wget 'https://arm1s11-eiffel004.eiffel.gic.ericsson.se:8443/nexus/service/local/artifact/maven/redirect?r=releases&g=com.ericsson.de.openstack-kgb&a=openstack-kgb-scripts&p=jar&v=RELEASE' -O openstack-kgb-scripts.jar"
            sh "jar -xvf openstack-kgb-scripts.jar 2>&1 >/dev/null"
            sh "chmod +x job_logic.sh"
            sh "bash job_logic.sh"
       }
     }
   }
   // The post build actions
   post {
     success {
       echo 'Pipeline Successfully Completed'
     }
     failure {
       echo 'Pipeline Failed'
     }
   }
 }''')
      }
    }
}

pipelineJob('FFE_Upgrade') {
  description ('FFE Upgrade Job - autogenerated using JobDSL - all manual changes will be overwritten!')
    logRotator {
      numToKeep(100)
    }
    parameters {
      stringParam('jobType', 'upgrade','jobType should be upgrade')
      stringParam('productSet', '','The product set that you want to upgrade in your tenancy \nThe format should be <b>24.07.115</b>')
      stringParam('deployPackage', '','''
  <p>If you <b>want</b> to execute software update, <b>add your packages here</b> in the following format:</p>
<blockquote><p><font color="blue"><b>&lt;deliverable name&gt;::&lt;version&gt;</b></font>, where version is in maven version format or "Latest". There is also an option to add a package that that has not been officially delivered as a complete URL.
  <br><p><font color="blue">Please note: "::Latest" is not supported, you have to specify an actual version of a rpm or a download link"</p>
  <br>ERICrpm::1.2.3
  <br>ERICrpm::https://cifwk-oss.lmera.ericsson.se/static/tmpUploadSnapshot/2015-08-17_14-44-56/ERICapdatamacro_CXP9030537-1.22.9-SNAPSHOT20150817134050.noarch.rpm
  </p></blockquote>

<blockquote><p><b>When adding multiple rpms, use @@ as the separator:</b>
  <br>
  &lt;deliverable name&gt;::&lt;version&gt;<font color="blue"><b>@@</b></font>&lt;deliverable name&gt;::&lt;version&gt;
  <br>ERICrpm::1.0.25@@ERICrpm::1.34.122
  </p></blockquote>''')
      labelParam('jenkins_agent'){
        defaultValue('')
        description('')
      }
      stringParam('deploymentId','' ,'Deployment ID of the openstack project')
      stringParam('enterProfiles', '','')
      stringParam('profiles', '','')
      stringParam('enterEDPVersion', '','')
      stringParam('edpVersion', '','')
    }
    definition {
      cps {
       script('''pipeline {
   agent {
     node {
       label "$jenkins_agent"
     }
   }
   // The options directive is for configuration that applies to the whole job.
   options {
     buildDiscarder(logRotator(numToKeepStr:'100'))
     timestamps()
     quietPeriod(0)
   }
   stages {
    stage('FFE Upgrade') {
       steps {
            sh "wget 'https://arm1s11-eiffel004.eiffel.gic.ericsson.se:8443/nexus/service/local/artifact/maven/redirect?r=releases&g=com.ericsson.de.openstack-kgb&a=openstack-kgb-scripts&p=jar&v=RELEASE' -O openstack-kgb-scripts.jar"
            sh "jar -xvf openstack-kgb-scripts.jar 2>&1 >/dev/null"
            sh "chmod +x job_logic.sh"
            sh "bash job_logic.sh"
       }
     }
   }
   // The post build actions
   post {
     success {
       echo 'Pipeline Successfully Completed'
     }
     failure {
       echo 'Pipeline Failed'
     }
   }
 }''')
     sandbox(true)
      }
    }
}

job('FFE_Add_Licenses'){
  description("<h3>Job Description</h3><p>This job is used to install the licenses onto FFE vENM. - Auto generated using Job DSL - all manual changes will be overwritten!</p><h3>For any licences queries, contact Decepticons.</h3>")
  concurrentBuild(allowConcurrentBuild = true)
  logRotator{
    daysToKeep(30)
  }
  wrappers{
    preBuildCleanup{
      cleanupParameter('true')
      timestamps()
      buildName('ClusterID = ${ENV,var="cluster_id"}')
    }
  }
  parameters{
    stringParam {
      name('test_phase')
      defaultValue('')
      description('Test phase being run')
      trim(true)
    }
    stringParam {
      name('cluster_id')
      defaultValue('')
      description('Deployment name of vENM Cloud Deployment to be installed')
      trim(true)
    }
    stringParam {
      name('mt_utils_version')
      defaultValue('')
      description('This is the version of the scripts available in Nexus<br>The version used will be printed in the console output<br><a href="https://arm1s11-eiffel004.eiffel.gic.ericsson.se:8443/nexus/content/repositories/releases/com/ericsson/mtg/utils/">Nexus Link</a>')
      trim(true)
    }
    stringParam {
      name('drop')
      defaultValue('')
      description('Drop for install <br> Exampl: 18.02')
      trim(true)
    }
    stringParam {
      name('product_set_version')
      defaultValue('')
      description('The Product Set version of vENM to upgrade to<br>Example: 18.02.19')
      trim(true)
    }
    stringParam {
      name('deployment_type')
      defaultValue('')
      description('Type of deployment being used')
      trim(true)
    }
    labelParam('jenkins_agent'){
        defaultValue('')
        description('Jenkins agent to run the job, this should be the gateway jenkins agent')
    }
  }
  steps{
    shell ('''#!/bin/bash

gateway_ip=$(curl --header 'Accept: application/json' -X GET -ks "https://atvdit.athtem.eei.ericsson.se/api/documents/?q=name=${NODE_NAME}" | egrep -o "ip.*?" | cut -d '"' -f3)
echo "gateway_ip=${gateway_ip}" >> "${WORKSPACE}"/build.properties

echo "Retrieving Scripts from Nexus"
tarFileName="utils_${mt_utils_version}.tar.gz"
echo "Downloading file - ${tarFileName} - to the workspace"
curl -s --noproxy \\* -L "https://arm1s11-eiffel004.eiffel.gic.ericsson.se:8443/nexus/service/local/artifact/maven/redirect?r=releases&g=com.ericsson.mtg&a=utils&p=tar.gz&v=${mt_utils_version}" -o ${tarFileName}
tar -zxf ${tarFileName}

/bin/bash MTELoopScripts/pipeline_scripts/add_licenses_setup.sh "${WORKSPACE}/parameters.properties" || exit 1
    ''')
    environmentVariables{
      propertiesFile('$WORKSPACE/build.properties')
    }
    exportParametersBuilder{
      filePath("parameters")
      fileFormat('properties')
      keyPattern('')
      useRegexp(false)
    }
    baselineDefinedMessageDispatcher{
      ciArtifacts {
        isoProduct('ENM')
        isoDrop('${drop}')
        isoVersion('${enm_iso_version}')
        jobType("Entry Loop")
        teamName("")
        artifactsClassName("com.ericsson.oss.axis.BaselineDefinedMessageDispatcher\$ArtifactAsIso\$1")
      }
      armId("")
      downloadRepoName("")
      uploadRepoName("")
      httpString("https://arm1s11-eiffel004.eiffel.gic.ericsson.se:8443/nexus/content/groups/public")
      ftpString("")
      nfsString("")
      armName("")
      armPassword("")
      armDescription("")
      sutClusterId("\${cluster_id}")
      citeHostPropertiesFile("")
      testwarePropertiesFile("")
      schedule {
        xml('''<?xml version="1.0" encoding="UTF-8"?>
<schedule xmlns="http://taf.lmera.ericsson.se/schema/te" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://taf.lmera.ericsson.se/schema/te http://taf.lmera.ericsson.se/schema/te/schedule/xml">
<item>
    <name>Install Licenses</name>
<component>com.ericsson.oss.services.lcm:ERICTAFlicensecontrolmonitoringservice_CXP9031454</component>
    <suites>LicenseRollout.xml</suites>
</item>
</schedule>
        ''')
        testPropertiesAsString(null)
        scheduleClassName('com.ericsson.oss.axis.BaselineDefinedMessageDispatcher\$ScheduleAsXml\$1')
        name(null)
        groupId(null)
        artifactId(null)
        version(null)
        scheduleType(null)
        tafScheduleName(null)
        tafScheduleVersion(null)
      }
      tunnellingOn("false")
      tafVersion("\${tafVersion}")
      userDefinedGAVs(null)
      additionalTestProperties('''
          taf.profiles=license_rollout
          tdm.api.host=https://taf-tdm.seli.wh.rnd.internal.ericsson.com/api/
          taf.config.dit.deployment.name=${cluster_id}
          ''')
      breakBuildOnTestsFailure("false")
      ciFwkHost("https://ci-portal.seli.wh.rnd.internal.ericsson.com/")
      tafTestExecutorHostname('${gateway_ip}')
      tafTestExecutorPort("8080")
      globalTestGroups("")
    }
  }
}

job('FFE_Apply_Certs') {
    label("${Apply_Certs_Agents}")
    description ('''<p style="color:blue;">This job basically imports external CA certs and updates trust profile on ENM (both cloud and pyshical).
This is mainly a ENM activity needs to be run after Intial Install and there is no interaction with netsim or netsim nodes <br>
<br>
</p>
<p style="color:green;">

The job needs to <b> wait 30 min</b> to allow all ENM services to "load" the trust. <br>
After "INFO: Trust Profile is successfully updated." there will be 30 minutes sleep.



</p>
<h2>
<a href="https://confluence-oss.seli.wh.rnd.internal.ericsson.com/display/PDUCD/nssSingleApplyCertsOnENM">LINK TO <b><font color="red">User Guide</font></b></a>
</h2>''')
    concurrentBuild(allowConcurrentBuild = true)
    logRotator {
        numToKeep(150)
    }
    wrappers{
        preBuildCleanup()
        timestamps()
    }
    parameters {
        stringParam('clusterId', '','''<h1 style="color:blue;">This parameter refers to cluster id of the deployment on which you want to run update certficates</h1>
    <h1 style="color:blue;">Example: 327 for pshysical servers</h1>
    <h1 style="color:blue;">Example: ieatenmc6b05 for openstack </h1>''')
        stringParam('drop', '','''<h1 style="color:blue;">Please mention NSS drop </h1>
    <h1 style="color:blue;">Example:18.14</h1>''')
        stringParam('simdep_release', '1.5.773','''<h1 style="color:red;">TLS Simdep version for >= NSS-23.06: 1.5.773 ( With CRL Feature)</h1>
<h1 style="color:red;">TLS Simdep version for < NSS-23.06 : 1.5.744  ( Without CRL Feature)</h1>
<h6 style="color:red;">While downgrading from 1.5.773 to 1.5.744 remove the NSSCA (ENM_ExtCA3) from ENM trust-store manually and then run the job with proper simdep version</h6>
<h6>
  <a href="https://confluence-oss.seli.wh.rnd.internal.ericsson.com/display/PDUCD/nssSingleApplyCertsOnENM#nssSingleApplyCertsOnENM-ManuallyremovingNSSCAcertfromENM:">LINK TO <b><font color="red">removal</font></b></a>
   </h6>''')
        choiceParam('deployment_type', ['Cloud'],'''Please mention the type of environment''')
        stringParam('enm_gui_link', '', '''<h1 style="color:blue;">This parameter refers to ENM GUI LINK of the deployment on which you want to run update certficates</h1>
<h1 style="color:blue;">Example: https://ieatENM5418-1.athtem.eei.ericsson.se/</h1>''')
        // original example uses extensible choice, this appears to do the same thing
        activeChoiceParam('MT_utils_version') {
            description('<h1 style="color:red;">Customers should ignore this parameter</h1>')
            choiceType('SINGLE_SELECT')
            groovyScript {
                script('''def xml = new XmlSlurper().parse("https://arm1s11-eiffel004.eiffel.gic.ericsson.se:8443/nexus/content/repositories/releases/com/ericsson/mtg/utils/maven-metadata.xml")
def list = []

xml.versioning.versions.version.each
{
 list.add(it)
}
list.add("RELEASE")
return list.reverse().subList(0, 10)''')
            }
        }
        choiceParam('nodesCleanUp', ['NO', 'YES'],'''<h1 style="color:red;">Customers should set this parameter to YES only if NSS 15K or 2K network is on the servers linked to ENM</h1>''')

    }
    steps{
        envInjectBuilder {
            propertiesFilePath('')
            propertiesContent('TRUST_PROFILE_LOG="/var/tmp/trustProfile.log"')
        }
        shell('''#curl -o ERICTAFenmnisimdep_CXP9031884.jar https://arm1s11-eiffel004.eiffel.gic.ericsson.se:8443/nexus/content/groups/public/com/ericsson/ci/simnet/ERICTAFenmnisimdep_CXP9031884/${simdep_release}/ERICTAFenmnisimdep_CXP9031884-${simdep_release}.jar
#nssDropAsAnInteger=$(echo ${simdep_release//./})
#if [[ ${nssDropAsAnInteger} -le 15652 ]]
#	then
 #    curl -o   ERICTAFenmnisimdep_CXP9031884.jar https://arm1s11-eiffel004.eiffel.gic.ericsson.se:8443/nexus/content/groups/public/com/ericsson/ci/simnet/ERICTAFenmnisimdep_CXP9031884/1.5.652/ERICTAFenmnisimdep_CXP9031884-1.5.652.jar
#else
 #curl -o   ERICTAFenmnisimdep_CXP9031884.jar https://arm1s11-eiffel004.eiffel.gic.ericsson.se:8443/nexus/content/groups/public/com/ericsson/ci/simnet/ERICTAFenmnisimdep_CXP9031884/${simdep_release}/ERICTAFenmnisimdep_CXP9031884-${simdep_release}.jar
#fi
#mkdir -p ERICTAFenmnisimdep_CXP9031884/src/main/resources;
#unzip ERICTAFenmnisimdep_CXP9031884.jar -d ERICTAFenmnisimdep_CXP9031884/src/main/resources
#chmod -R 755 ./*


#if [[ $nodesCleanUp == "YES" ]]
#then
#sh $WORKSPACE/ERICTAFenmnisimdep_CXP9031884/src/main/resources/scripts/simdep/ext/jenkins/updateTrustONENM.sh $clusterId $drop $simdep_release $deployment_type $enm_gui_link
#else
#sh $WORKSPACE/ERICTAFenmnisimdep_CXP9031884/src/main/resources/scripts/simdep/ext/jenkins/updateTrustONENM_MTV.sh $clusterId $drop $simdep_release $deployment_type $enm_gui_link
#fi

echo "###########################################################################################"

wget -O NSS_UpdateCertsOnENM.sh https://arm901-eiffel004.athtem.eei.ericsson.se:8443/nexus/content/repositories/nss-releases/com/ericsson/nss/NSS_UpdateCertsOnENM/1.0.1/NSS_UpdateCertsOnENM-1.0.1.sh

sh NSS_UpdateCertsOnENM.sh''')
        shell('''#!/bin/sh
curl -O "https://arm901-eiffel004.athtem.eei.ericsson.se:8443/nexus/content/repositories/nss-releases/com/ericsson/nss/scripts/jq/1.0.1/jq-1.0.1.tar"  ; tar -xvf jq-1.0.1.tar ; chmod +x ./jq
if [[ $2 == "yes" ]]
then
map_generator=`/netsim/inst/netsim_pipe<<EOF
.generateNetworkMap
.server stop all
EOF`
else
map_generator=`/netsim/inst/netsim_pipe<<EOF
.generateNetworkMap
EOF`
fi
echo "$map_generator"
for node in $(echo $1 | sed "s/,/ /g")
do
        Nodedetails=`./jq  --raw-output '.networkMap[] | select(.["name"]=="'$node'") ' /netsim/netsimdir/networkMap.json `
            Simname=`echo $Nodedetails | awk -F'Simulation": "' '{print $2}'| tr -d '" }'`
                echo "${node} ${Simname}"
check_output=`/netsim/inst/netsim_pipe<<MML
              .open $Simname
              .select $node
              .start
MML`
                echo "$check_output"
            done''')
    }
}
