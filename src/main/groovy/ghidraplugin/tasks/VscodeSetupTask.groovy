package ghidraplugin.tasks

import groovy.transform.Internal
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import groovy.json.JsonOutput

import ghidraplugin.PluginUtils

class VscodeSetupTask extends DefaultTask {

	VscodeSetupTask() {
		group = 'ghidra'
		description = 'vscode setup task'
	}

	@TaskAction
	void createLaunchSettings() {
		def launchers = [
			"version": "0.2.0",
			"configurations": [
				[
					'type': 'java',
					'name': 'Ghidra Debug',
					'projectName': project.name,
					'request': 'launch',
					'mainClass': 'ghidra.GhidraLauncher',
					'args': 'ghidra.GhidraRun',
					'shortenCommandLine': 'argfile',
					'vmArgs': parseVmargs().join(' ')
				]
			]
		]
		File fd = new File(project.projectDir, ".vscode/launch.json")
		fd.getParentFile().mkdirs()
		fd.write(JsonOutput.prettyPrint(JsonOutput.toJson(launchers)))
	}

	private static String getPlatformArgs() {
		if (System.env['OS'].contains('Windows')) {
			return 'WINDOWS'
		}
		if (System.env['OS'].contains('Mac')) {
			return 'MACOS'
		}
		return 'LINUX'
	}

	def parseVmargs() {
		def platform = getPlatformArgs()
		def ARGS = ~/^VMARGS(?:_$platform)?=(\S+)/
		def args = [
			'-Declipse.project.dir=${workspaceFolder}',
			'-Dghidra.util.swing.timeout.seconds=9999999999999999',
			'-Dghidra.test.property.timeout.disable=true'
		]
		PluginUtils.getGhidraFile('support/launch.properties').eachLine('UTF-8') {
			def match = it =~ ARGS
			if (match.matches()) {
				args.add(match.group(1))
			}
		}
		return args
	}
}
