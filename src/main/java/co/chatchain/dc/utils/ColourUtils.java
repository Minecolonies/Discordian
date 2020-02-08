package co.chatchain.dc.utils;

public class ColourUtils
{
    public static String convertRawToString(int rawColour)
    {
        final int red = (rawColour >> 16) & 0xFF;
        final int green = (rawColour >> 8) & 0xFF;
        final int blue = rawColour & 0xFF;

        return String.format("#%02x%02x%02x", red, green, blue);
    }
}
