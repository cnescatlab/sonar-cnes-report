package fr.cnes.sonar.report.utils;

/**
 * Class used to manipulate/encode/validate filenames
 */
public class FileNameUtils {

    private FileNameUtils() {}

    /** Property for regex to identify non valid characters in filename. */
    private static final String FILENAME_INVALID_REGEX = "report.filenames.invalidCharactersRegex";
    /** Property for replacement character in filename. */
    private static final String FILENAME_REPLACE_CHAR = "report.filenames.replacementCharacter";

    /**
     * Replace non valid character for filenames by a generic one
     * @param projectName
     * @return the given string with non valid character replaced by a generic one
     */
    public static String replaceNonValidFileNameCharacter(String filename) {
        String replacementChar = StringManager.getProperty(FILENAME_REPLACE_CHAR);
        String nonValidCharRegex = StringManager.getProperty(FILENAME_INVALID_REGEX);

        return filename.replaceAll(nonValidCharRegex, replacementChar);
    }

}
