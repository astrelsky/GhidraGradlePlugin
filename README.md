This is abandonware. It had been made public and archived for reference. I do not recall how functional it was.
# Usage Example

```gradle
buildscript {
    dependencies {
        classpath files(
            'C:\\Users\\astre\\Documents\\ghidra_plugins\\GhidraPlugin\\build\\libs\\GhidraPlugin.jar'
        )
    }
}

apply plugin: 'java'
apply plugin: 'ghidra'

buildExtension {
    exclude '.vscode'
    exclude 'gradle*'
    exclude '.editorconfig'
}
```
