package cs107;

import static cs107.Helper.Image;

/**
 * "Quite Ok Image" Decoder
 * @apiNote Third task of the 2022 Mini Project
 * @author Hamza REMMAL (hamza.remmal@epfl.ch)
 * @version 1.3
 * @since 1.0
 */
public final class QOIDecoder {

    /**
     * DO NOT CHANGE THIS, MORE ON THAT IN WEEK 7.
     */
    private QOIDecoder(){}

    // ==================================================================================
    // =========================== QUITE OK IMAGE HEADER ================================
    // ==================================================================================

    /**
     * Extract useful information from the "Quite Ok Image" header
     * @param header (byte[]) - A "Quite Ok Image" header
     * @return (int[]) - Array such as its content is {width, height, channels, color space}
     * @throws AssertionError See handouts section 6.1
     */
    public static int[] decodeHeader(byte[] header){

        assert header != null; // check if header is not null
        assert header.length == QOISpecification.HEADER_SIZE; // check if header length conforms to the specification

        // check if first 4 bytes of header is equal to the magic bytes of qoif
        for (int i = 0; i < 4 ; i++) {
            assert header[i] == QOISpecification.QOI_MAGIC[i];
        }
        assert header[12] == 3 || header[12] == 4; // check that the number of channels is equal to RGB or RGBA
        assert header[13] == 0 || header[13] == 1; // check

        byte[] widthArray = ArrayUtils.extract(header, 4,4); // created array with width bytes
        byte[] heightArray = ArrayUtils.extract(header, 8, 4); // created array with height bytes
        byte[] channelsArray = ArrayUtils.extract(header, 12, 1); // created array with channel byte
        byte[] colorSpaceArray = ArrayUtils.extract(header, 13, 1); // created array with colorspace byte
        byte[] concatColor = ArrayUtils.concat(new byte[]{0,0,0}, colorSpaceArray); // concatenated colorspace array
        // of size 4, as toInt needs a table size of 4
        byte[] concatChannel = ArrayUtils.concat(new byte[]{0,0,0}, channelsArray); // concatenated channel array of
        // size 4, as toInt needs a table size of 4

        int width = ArrayUtils.toInt(widthArray); // convert widthArray to Int
        int height = ArrayUtils.toInt(heightArray); // convert heightArray to Int
        int channel = ArrayUtils.toInt(concatChannel); // convert concatenated channel array to Int
        int colorSpace = ArrayUtils.toInt(concatColor); // convert concatenated colorspace array to Int

        // insert values for decoded array
        return new int[]{width, height, channel, colorSpace};


    }

    // ==================================================================================
    // =========================== ATOMIC DECODING METHODS ==============================
    // ==================================================================================

    /**
     * Store the pixel in the buffer and return the number of consumed bytes
     * @param buffer (byte[][]) - Buffer where to store the pixel
     * @param input (byte[]) - Stream of bytes to read from
     * @param alpha (byte) - Alpha component of the pixel
     * @param position (int) - Index in the buffer
     * @param idx (int) - Index in the input
     * @return (int) - The number of consumed bytes
     * @throws AssertionError See handouts section 6.2.1
     */
    public static int decodeQoiOpRGB(byte[][] buffer, byte[] input, byte alpha, int position, int idx){
        assert buffer != null;
        assert input != null;
        assert position < buffer.length;
        assert idx < input.length;

        byte[] rgba = new byte[4];

        int count = 0;
        // insert rgb into array
        for (int i = idx; i < idx + 3; i++) {
            rgba[i - idx] = input[i];
            count++;
        }
        rgba[3] = alpha; // add alpha to position 3

        buffer[position] = rgba; // insert array rgb at location "position"

        return count; // return the count of rgb, which is always 3
    }


    /**
     * Store the pixel in the buffer and return the number of consumed bytes
     * @param buffer (byte[][]) - Buffer where to store the pixel
     * @param input (byte[]) - Stream of bytes to read from
     * @param position (int) - Index in the buffer
     * @param idx (int) - Index in the input
     * @return (int) - The number of consumed bytes
     * @throws AssertionError See handouts section 6.2.2
     */
    public static int decodeQoiOpRGBA(byte[][] buffer, byte[] input, int position, int idx){
        assert buffer != null;
        assert input != null;
        assert position < buffer.length;
        assert idx < input.length;

        byte[] rgba = new byte[4];

        int count = 0;
        // insert rgb into array
        for (int i = idx; i < idx + 4; i++) {
            rgba[i - idx] = input[i];
            count++;
        }

        buffer[position] = rgba; // insert array rgb at location "position"

        return count; // return the count of rgb, which is always 4
    }

    /**
     * Create a new pixel following the "QOI_OP_DIFF" schema.
     * @param previousPixel (byte[]) - The previous pixel
     * @param chunk (byte) - A "QOI_OP_DIFF" data chunk
     * @return (byte[]) - The newly created pixel
     * @throws AssertionError See handouts section 6.2.4
     */
    public static byte[] decodeQoiOpDiff(byte[] previousPixel, byte chunk){
//        assert previousPixel != null;
//        assert previousPixel.length == 4;
//        assert chunk > 63; // check if it's greater than 63 as 01_00_00_00 has a byte value of 63. If the first byte is
//        // 1 then it makes the value negative, hence checking for > 63 is all that is needed
//
//        int diff = 128 - (int) chunk;
//
//        for (int i = 0; i < previousPixel.length; i++) {
//            previousPixel[i] += diff;
//
//        }
//        return previousPixel;
        return Helper.fail("Not Implemented");
    }

    /**
     * Create a new pixel following the "QOI_OP_LUMA" schema
     * @param previousPixel (byte[]) - The previous pixel
     * @param data (byte[]) - A "QOI_OP_LUMA" data chunk
     * @return (byte[]) - The newly created pixel
     * @throws AssertionError See handouts section 6.2.5
     */
    public static byte[] decodeQoiOpLuma(byte[] previousPixel, byte[] data){
        return Helper.fail("Not Implemented");
    }

    /**
     * Store the given pixel in the buffer multiple times
     * @param buffer (byte[][]) - Buffer where to store the pixel
     * @param pixel (byte[]) - The pixel to store
     * @param chunk (byte) - a QOI_OP_RUN data chunk
     * @param position (int) - Index in buffer to start writing from
     * @return (int) - number of written pixels in buffer
     * @throws AssertionError See handouts section 6.2.6
     */
    public static int decodeQoiOpRun(byte[][] buffer, byte[] pixel, byte chunk, int position){
        return Helper.fail("Not Implemented");
    }

    // ==================================================================================
    // ========================= GLOBAL DECODING METHODS ================================
    // ==================================================================================

    /**
     * Decode the given data using the "Quite Ok Image" Protocol
     * @param data (byte[]) - Data to decode
     * @param width (int) - The width of the expected output
     * @param height (int) - The height of the expected output
     * @return (byte[][]) - Decoded "Quite Ok Image"
     * @throws AssertionError See handouts section 6.3
     */
    public static byte[][] decodeData(byte[] data, int width, int height){
        return Helper.fail("Not Implemented");
    }

    /**
     * Decode a file using the "Quite Ok Image" Protocol
     * @param content (byte[]) - Content of the file to decode
     * @return (Image) - Decoded image
     * @throws AssertionError if content is null
     */
    public static Image decodeQoiFile(byte[] content){
        return Helper.fail("Not Implemented");
    }

}