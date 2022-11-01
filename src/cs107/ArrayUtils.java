package cs107;

/**
 * Utility class to manipulate arrays.
 *
 * @author Hamza REMMAL (hamza.remmal@epfl.ch)
 * @version 1.3
 * @apiNote First Task of the 2022 Mini Project
 * @since 1.0
 */
public final class ArrayUtils {

    /**
     * DO NOT CHANGE THIS, MORE ON THAT IN WEEK 7.
     */
    private ArrayUtils() {
    }

    // ==================================================================================
    // =========================== ARRAY EQUALITY METHODS ===============================
    // ==================================================================================

    /**
     * Check if the content of both arrays is the same
     *
     * @param a1 (byte[]) - First array
     * @param a2 (byte[]) - Second array
     * @return (boolean) - true if both arrays have the same content (or both null), false otherwise
     * @throws AssertionError if one of the parameters is null
     */
    public static boolean equals(byte[] a1, byte[] a2) {

        assert !(((a1.length == 0) && (a2.length != 0)) || ((a2.length == 0) && (a1.length != 0))); //if one is null and the other isn't --> assertion error

        if (a1.length != a2.length) { //if not the same size then return false
            return false;
        }

        if (a1.length == 0) { // if both(previously checked that they're the same) null then return true
            return true;
        }

        for (int i = 0; i < a1.length; i++) { //checks every element in table

            if (a1[i] != a2[i]) { //if element not the same then return false
                return false;
            }
        }
        return true; //if all elements are the same then return true
    }

    /**
     * Check if the content of both arrays is the same
     *
     * @param a1 (byte[][]) - First array
     * @param a2 (byte[][]) - Second array
     * @return (boolean) - true if both arrays have the same content (or both null), false otherwise
     * @throws AssertionError if one of the parameters is null
     */
    public static boolean equals(byte[][] a1, byte[][] a2) {
        assert !(((a1.length == 0) && (a2.length != 0)) || ((a2.length == 0) && (a1.length != 0))); //if number of rows in one of the arrays is null and the other isn't then assertion error

        if (a1.length != a2.length) { //if number of rows not the same then return false
            return false;
        }

        if (a1.length == 0) { //if both arrays have 0 rows then return false
            return true;
        }


        for (int i = 0; i < a1.length; i++) { //for every column check the following

            assert !(((a1[i].length == 0) && (a2[i].length != 0)) || ((a2[i].length == 0) && (a1[i].length != 0))); //if number of columns in one of the arrays is null and the other isn't then assertion error

            if (a1[i].length != a2[i].length) { //if number of columns not the same then return false
                return false;
            }

            if (a1[i].length == 0) { //if both arrays have 0 rows then return false
                return true;
            }

            for (int j = 0; j < a1[i].length; j++) { //compare every element in the array
                if (a1[i][j] != a2[i][j]) { //if element not the same then return false
                    return false;
                }
            }
        }
        return true; //if all elements are the same then return true
    }

    // ==================================================================================
    // ============================ ARRAY WRAPPING METHODS ==============================
    // ==================================================================================

    /**
     * Wrap the given value in an array
     *
     * @param value (byte) - value to wrap
     * @return (byte[]) - array with one element (value)
     */
    public static byte[] wrap(byte value) { //puts an integer into a single element int array
        byte[] bytes = {value};
        return bytes;
    }

    // ==================================================================================
    // ========================== INTEGER MANIPULATION METHODS ==========================
    // ==================================================================================

    /**
     * Create an Integer using the given array. The input needs to be considered
     * as "Big Endian"
     * (See handout for the definition of "Big Endian")
     *
     * @param bytes (byte[]) - Array of 4 bytes
     * @return (int) - Integer representation of the array
     * @throws AssertionError if the input is null or the input's length is different from 4
     */
    public static int toInt(byte[] bytes) {
        assert (bytes.length == 4); //make sure the size of the array is 4
        boolean nonnull = false; //boolean that will become true when the for loop finds a non-null element
        for (int i = 0; i < bytes.length; i++) { //checks if any value is non-null
            if (bytes[i] != 0) {
                nonnull = true;
                break;
            }
        }
        assert nonnull; //makes sure that the table is not null
        int element; //temporary variable that holds element i
        int value = 0; //variable that holds the concatenated values
        for (int i = 0; i < 4; i++) {
            element = bytes[i];
            element = element << 32 - (8 * (i + 1)); //shift left to the leftmost 8-bits in a 32-bit number. then shifts left to the second leftmost 8-bits.... doesn't shift
            value = value | element; //does a 'bitwise or' to put together the current 'value' with the new 'element'
        }
        return value;
    }

    /**
     * Separate the Integer (word) to 4 bytes. The Memory layout of this integer is "Big Endian"
     * (See handout for the definition of "Big Endian")
     *
     * @param value (int) - The integer
     * @return (byte[]) - Big Endian representation of the integer
     */
    public static byte[] fromInt(int value) {
        return Helper.fail("Not Implemented");
    }

    // ==================================================================================
    // ========================== ARRAY CONCATENATION METHODS ===========================
    // ==================================================================================

    /**
     * Concatenate a given sequence of bytes and stores them in an array
     *
     * @param bytes (byte ...) - Sequence of bytes to store in the array
     * @return (byte[]) - Array representation of the sequence
     * @throws AssertionError if the input is null
     */
    public static byte[] concat(byte... bytes) {
        return Helper.fail("Not Implemented");
    }

    /**
     * Concatenate a given sequence of arrays into one array
     *
     * @param tabs (byte[] ...) - Sequence of arrays
     * @return (byte[]) - Array representation of the sequence
     * @throws AssertionError if the input is null
     *                        or one of the inner arrays of input is null.
     */
    public static byte[] concat(byte[]... tabs) {
        return Helper.fail("Not Implemented");
    }

    // ==================================================================================
    // =========================== ARRAY EXTRACTION METHODS =============================
    // ==================================================================================

    /**
     * Extract an array from another array
     *
     * @param input  (byte[]) - Array to extract from
     * @param start  (int) - Index in the input array to start the extract from
     * @param length (int) - The number of bytes to extract
     * @return (byte[]) - The extracted array
     * @throws AssertionError if the input is null or start and length are invalid.
     *                        start + length should also be smaller than the input's length
     */
    public static byte[] extract(byte[] input, int start, int length) {
        return Helper.fail("Not Implemented");
    }

    /**
     * Create a partition of the input array.
     * (See handout for more information on how this method works)
     *
     * @param input (byte[]) - The original array
     * @param sizes (int ...) - Sizes of the partitions
     * @return (byte[][]) - Array of input's partitions.
     * The order of the partition is the same as the order in sizes
     * @throws AssertionError if one of the parameters is null
     *                        or the sum of the elements in sizes is different from the input's length
     */
    public static byte[][] partition(byte[] input, int... sizes) {
        return Helper.fail("Not Implemented");
    }

    // ==================================================================================
    // ============================== ARRAY FORMATTING METHODS ==========================
    // ==================================================================================

    /**
     * Format a 2-dim integer array
     * where each dimension is a direction in the image to
     * a 2-dim byte array where the first dimension is the pixel
     * and the second dimension is the channel.
     * See handouts for more information on the format.
     *
     * @param input (int[][]) - image data
     * @return (byte [][]) - formatted image data
     * @throws AssertionError if the input is null
     *                        or one of the inner arrays of input is null
     */
    public static byte[][] imageToChannels(int[][] input) {
        return Helper.fail("Not Implemented");
    }

    /**
     * Format a 2-dim byte array where the first dimension is the pixel
     * and the second is the channel to a 2-dim int array where the first
     * dimension is the height and the second is the width
     *
     * @param input  (byte[][]) : linear representation of the image
     * @param height (int) - Height of the resulting image
     * @param width  (int) - Width of the resulting image
     * @return (int[][]) - the image data
     * @throws AssertionError if the input is null
     *                        or one of the inner arrays of input is null
     *                        or input's length differs from width * height
     *                        or height is invalid
     *                        or width is invalid
     */
    public static int[][] channelsToImage(byte[][] input, int height, int width) {
        return Helper.fail("Not Implemented");
    }

}