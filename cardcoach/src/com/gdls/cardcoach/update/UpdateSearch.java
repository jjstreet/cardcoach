package com.gdls.cardcoach.update;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateSearch {
	
	private static final Pattern versionRegex = Pattern.compile("^cardcoach setup-(\\d+\\.\\d+\\.\\d+)\\.exe$");
	
	private UpdateSearch() {
		// Class not be instantiated
	}
	
	public static boolean updateExists(String location, String currentVersion) {
		if (!getLatestVersion(location, currentVersion).equals(currentVersion)) {
			return true;
		}
		return false;
	}
	
	public static String getLatestVersion(String location, String currentVersion) {
		File updateFolder = new File(location);
		String newestVersion = currentVersion;
		if (updateFolder.isDirectory() && updateFolder.canRead() && updateFolder.canExecute()) {
			File[] setupFiles = updateFolder.listFiles();
			for (File setupFile : setupFiles) {
				if (setupFile.isFile()) {
					Matcher matcher = versionRegex.matcher(setupFile.getName());
					if (matcher.matches()) {
						if (compareVersions(newestVersion, matcher.group(1)) < 0) {
							newestVersion = matcher.group(1);
						}
					}
				}
			}
		}
		return newestVersion;
	}
	
	private static int[] getVersionParts(String version) {
		String[] versionPartsStr = version.split("\\.");
		int[] versionParts = new int[versionPartsStr.length];
		for (int i=0; i<versionPartsStr.length; i++) {
			versionParts[i] = Integer.parseInt(versionPartsStr[i]);
		}
		return versionParts;
	}
	
	private static int compareVersions(String v1, String v2) {
		int[] vp1 = getVersionParts(v1);
		int[] vp2 = getVersionParts(v2);
		for (int i=0; i<Math.min(vp1.length, vp2.length); i++) {
			if (vp1[i] != vp2[i]) {
				if (vp1[i] > vp2[i]) {
					return 1;
				}
				return -1;
			}
		}
		return 0;
	}
}
