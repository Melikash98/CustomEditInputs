Add it in your root settings.gradle at the end of repositories:
dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}
Step 2. Add the dependency
  dependencies {
	        implementation 'com.github.Melikash98:CustomEditInputs:1.0.0'
	}
