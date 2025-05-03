package net.mizukilab.pit.util;

/**
 * @author Araykal
 * @since 2025/5/4
 */
public class ProgressBar {
    public static String getProgressBar(double currentExp, double nextLevelExp, int barLength) {
/*        if (nextLevelExp <= 0.0) {
            return "&b■".repeat(barLength);
        }*/

        int filledLength = (int) ((currentExp / nextLevelExp) * barLength);
        filledLength = Math.min(filledLength, barLength);

        String progressBar = "&b■".repeat(filledLength) + "&l□".repeat(barLength - filledLength);
        return progressBar;
    }
}
