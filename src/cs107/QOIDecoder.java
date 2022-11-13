package cs107;

import static cs107.Helper.Image;

/**
 * "Quite Ok Image" Decoder
 *
 * @author Hamza REMMAL (hamza.remmal@epfl.ch)
 * @version 1.3
 * @apiNote Third task of the 2022 Mini Project
 * @since 1.0
 */
public final class QOIDecoder {

    /**
     * DO NOT CHANGE THIS, MORE ON THAT IN WEEK 7.
     */
    private QOIDecoder() {
    }

    // ==================================================================================
    // =========================== QUITE OK IMAGE HEADER ================================
    // ==================================================================================

    /**
     * Extract useful information from the "Quite Ok Image" header
     *
     * @param header (byte[]) - A "Quite Ok Image" header
     * @return (int[]) - Array such as its content is {width, height, channels, color space}
     * @throws AssertionError See handouts section 6.1
     */
    public static int[] decodeHeader(byte[] header) {

        assert header != null; // check if header is not null
        assert header.length == QOISpecification.HEADER_SIZE; // check if header length conforms to the specification

        // check if first 4 bytes of header is equal to the magic bytes of qoif
        for (int i = 0; i < 4; i++) {
            assert header[i] == QOISpecification.QOI_MAGIC[i];
        }
        assert header[12] == 3 || header[12] == 4; // check that the number of channels is equal to RGB or RGBA
        assert header[13] == 0 || header[13] == 1; // check

        byte[] widthArray = ArrayUtils.extract(header, 4, 4); // created array with width bytes
        byte[] heightArray = ArrayUtils.extract(header, 8, 4); // created array with height bytes
        byte[] channelsArray = ArrayUtils.extract(header, 12, 1); // created array with channel byte
        byte[] colorSpaceArray = ArrayUtils.extract(header, 13, 1); // created array with colorspace byte
        byte[] concatColor = ArrayUtils.concat(new byte[]{0, 0, 0}, colorSpaceArray); // concatenated colorspace array
        // of size 4, as toInt needs a table size of 4
        byte[] concatChannel = ArrayUtils.concat(new byte[]{0, 0, 0}, channelsArray); // concatenated channel array of
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
     *
     * @param buffer   (byte[][]) - Buffer where to store the pixel
     * @param input    (byte[]) - Stream of bytes to read from
     * @param alpha    (byte) - Alpha component of the pixel
     * @param position (int) - Index in the buffer
     * @param idx      (int) - Index in the input
     * @return (int) - The number of consumed bytes
     * @throws AssertionError See handouts section 6.2.1
     */
    public static int decodeQoiOpRGB(byte[][] buffer, byte[] input, byte alpha, int position, int idx) {
        assert buffer != null; // check if buffer is not null
        assert input != null; // check if input is not null
        assert position < buffer.length; // check if position points to a valid location of buffer
        assert idx < input.length; // check if idx contains enough data to recover pixel
        assert input.length >= 3; // check that input contains enough data to recover pixel

        byte[] rgba = new byte[4];

        int count = 0;
        // insert rgb from input into rgba array
        for (int i = idx; i < idx + 3; i++) {
            rgba[i - idx] = input[i]; // take position idx from buffer and put it in position 0 to i < idx + 3 in rgba
            //array
            count++;
        }
        rgba[3] = alpha; // add alpha to position 3

        buffer[position] = rgba; // insert array rgb at location "position"

        return count; // return the count of rgb, which is always 3
    }


    /**
     * Store the pixel in the buffer and return the number of consumed bytes
     *
     * @param buffer   (byte[][]) - Buffer where to store the pixel
     * @param input    (byte[]) - Stream of bytes to read from
     * @param position (int) - Index in the buffer
     * @param idx      (int) - Index in the input
     * @return (int) - The number of consumed bytes
     * @throws AssertionError See handouts section 6.2.2
     */
    public static int decodeQoiOpRGBA(byte[][] buffer, byte[] input, int position, int idx) {
        assert buffer != null; // check buffer is not null
        assert input != null; // check input is not null
        assert position < buffer.length; // check that position points to a valid location of buffer
        assert idx < input.length; // check that idx points to a valid location of buffer
        assert input.length >= 4; // check that input contains enough data to recover pixel

        byte[] rgba = new byte[4];

        int count = 0;
        // insert rgb from input into rgba array
        for (int i = idx; i < idx + 4; i++) {
            rgba[i - idx] = input[i]; // take position idx from buffer and put it in position 0 to i < idx + 4 in rgba
            //array
            count++;
        }

        buffer[position] = rgba; // insert rgb array at location "position"

        return count; // return the count of rgb, which is always 4
    }

    /**
     * Create a new pixel following the "QOI_OP_DIFF" schema.
     *
     * @param previousPixel (byte[]) - The previous pixel
     * @param chunk         (byte) - A "QOI_OP_DIFF" data chunk
     * @return (byte[]) - The newly created pixel
     * @throws AssertionError See handouts section 6.2.4
     */
    public static byte[] decodeQoiOpDiff(byte[] previousPixel, byte chunk) {
        assert previousPixel != null; // check if previous pixel is not null
        assert previousPixel.length == 4; // check if previous pixel is equal to 4
        assert chunk > 63; // check if it's greater than 63 as 01_00_00_00 has a byte value of 63. If the first byte is
        // 1 then it makes the value negative, hence checking for > 63 is all that is needed (hence check tag of chunk
        //has the value QOI_OP_DIFF_TAG

        int r, g, b;
        byte dred = 0, dgreen = 0, dblue = 0;


        r = 0b11 & (chunk >> 4);// >> removes last 4 numbers and 0b11 is the mask to retrieve the first 2 bits (red)
        dred = ((byte) (r - 2)); // dred is r - the offset


        g = 0b11 & (chunk >> 2);// >> removes last 2 numbers and 0b11 is the mask to retrieve the middle 2 bits (green)
        dgreen = ((byte) (g - 2)); // dgreen is g - the offset


        b = 0b11 & chunk;  // >> removes nothing and 0b11 is the mask to retrieve the last 2 bits (blue)
        dblue = ((byte) (b - 2)); // dblue is blue - the offset

        byte[] currentPixel = new byte[4];
        currentPixel[0] = (byte) (previousPixel[0] + dred);
        currentPixel[1] = (byte) (previousPixel[1] + dgreen);
        currentPixel[2] = (byte) (previousPixel[2] + dblue);
        currentPixel[3] = previousPixel[3];

        return currentPixel;

    }

    /**
     * Create a new pixel following the "QOI_OP_LUMA" schema
     *
     * @param previousPixel (byte[]) - The previous pixel
     * @param data          (byte[]) - A "QOI_OP_LUMA" data chunk
     * @return (byte[]) - The newly created pixel
     * @throws AssertionError See handouts section 6.2.5
     */
    public static byte[] decodeQoiOpLuma(byte[] previousPixel, byte[] data) {
        assert (previousPixel != null); //check if previous pixel is not null
        assert (data != null); //check if data is not null
        assert (previousPixel.length == 4); //check the length of previous pixel is 4
        assert (data[0] <= -65); // luma is between -128 and -65 as the tag is 0b10_00_00_00, and it is less than -65 as
        // because the OP_Run_tag has 11 in the beginning, hence -65 is the max

        int r, g, b;
        byte dred = 0, dgreen = 0, dblue = 0;

        //need 6 bits, hence mask is 6 1's, masks it with the first byte and retrieves the 6 green bits
        g = data[0] & 0b11_11_11;
        //offset by 32
        dgreen = ((byte) (g - 32));

        //need first 4 bits from second byte in array as those bits represent red
        r = 0b11_11 & (data[1] >> 4);
        //dred is r + the difference in green - the offset
        dred = ((byte) (r + dgreen - 8));

        //need last 4 bits from second byte array, those last 4 bits represent blue
        b = 0b11_11 & (data[1]);
        //dblue is b + the difference in green - the offset
        dblue = ((byte) (b + dgreen - 8));

        byte[] currentPixel = new byte[4];

        currentPixel[0] = (byte) (previousPixel[0] + dred);
        currentPixel[1] = (byte) (previousPixel[1] + dgreen);
        currentPixel[2] = (byte) (previousPixel[2] + dblue);
        currentPixel[3] = previousPixel[3];

        return currentPixel;
    }

    /**
     * Store the given pixel in the buffer multiple times
     *
     * @param buffer   (byte[][]) - Buffer where to store the pixel
     * @param pixel    (byte[]) - The pixel to store
     * @param chunk    (byte) - a QOI_OP_RUN data chunk
     * @param position (int) - Index in buffer to start writing from
     * @return (int) - number of written pixels in buffer
     * @throws AssertionError See handouts section 6.2.6
     */
    public static int decodeQoiOpRun(byte[][] buffer, byte[] pixel, byte chunk, int position) {
        assert buffer != null; // check buffer is not null
        assert pixel != null; // check pixel is not null
        assert pixel.length == 4;// check that pixel length is 4
        assert (position <= buffer.length) && (position >= 0);//check that position is less than the length of our
        // buffer array and that it is greater than zero

        // gets the last six bits from the binary representation of chunk to get the number of pixels to modify, starting
        // from 0 to nPixels
        byte nPixel = (byte) (chunk & 0b11_11_11);

        // check that buffer can return the length of the pixel, check if position plus the number of pixels to modify
        //is less than the buffer length
        assert position + nPixel <= buffer.length;

        // insert pixel in buffer for nPixel times, starting from 0 to nPixel
        for (int i = position; i <= position + nPixel; i++) {
            buffer[i] = pixel;
        }
        return nPixel;
    }

    // ==================================================================================
    // ========================= GLOBAL DECODING METHODS ================================
    // ==================================================================================

    /**
     * Decode the given data using the "Quite Ok Image" Protocol
     *
     * @param data   (byte[]) - Data to decode
     * @param width  (int) - The width of the expected output
     * @param height (int) - The height of the expected output
     * @return (byte[][]) - Decoded "Quite Ok Image"
     * @throws AssertionError See handouts section 6.3
     */
    public static byte[][] decodeData(byte[] data, int width, int height) {
        assert data != null;  // check if data is not null
        assert width >= 0; // check if width is positive
        assert height >= 0; // check if height is positive

        byte[][] hash = new byte[64][4]; // creating hash table

        byte[][] buffer = new byte[width * height][4]; // create buffer in which we will store the pixels
        byte[] previousPixel = QOISpecification.START_PIXEL;
        int bufferCount = 0;


        for (int i = 0; i < data.length; i++) {

            byte tag = (byte) (data[i] & 0b11_00_00_00);
            boolean isRun = false;

            if (i != 0){
                previousPixel = buffer[bufferCount-1];
            }


            if ((tag == QOISpecification.QOI_OP_RUN_TAG) && (data[i] != QOISpecification.QOI_OP_RGBA_TAG) &&
                    (data[i] != QOISpecification.QOI_OP_RGB_TAG)) {

                bufferCount += (decodeQoiOpRun(buffer, previousPixel, data[i], bufferCount)+1);

                isRun = true;

            } else if ((data[i] == QOISpecification.QOI_OP_RGBA_TAG) && (data[i] != QOISpecification.QOI_OP_RGB_TAG)) {

                byte[] rgba = new byte[4];
                for (int j = 0; j < rgba.length; j++) {
                    rgba[j] = data[i + j + 1];
                }

                buffer[bufferCount] = rgba;
                bufferCount++;
                i = i + 4;

            } else if ((data[i] != QOISpecification.QOI_OP_RGBA_TAG) && (data[i] == QOISpecification.QOI_OP_RGB_TAG)) {

                byte[] rgb = new byte[4];
                for (int j = 0; j < rgb.length-1; j++) {
                    rgb[j] = data[i + j + 1];
                }
                rgb[3] = previousPixel[3];

                buffer[bufferCount] = rgb;
                bufferCount++;
                i = i + 3;


            } else if (tag == QOISpecification.QOI_OP_DIFF_TAG) { // if byte has diff tag, call diff method, which
                // stores the difference plus the previous pixel to obtain a new pixel

                buffer[bufferCount] = decodeQoiOpDiff(previousPixel, data[i]);
                bufferCount++;

            } else if (tag == QOISpecification.QOI_OP_LUMA_TAG) { // if byte has luma tag, call the luma method

                byte[] luma = new byte[2]; // create new array to store previous pixel and current pixel
                luma[0] = data[i];
                luma[1] = data[i+1];
                buffer[bufferCount] = decodeQoiOpLuma(previousPixel, luma);
                bufferCount++;
                i++;

            } else if (tag == QOISpecification.QOI_OP_INDEX_TAG) { // if byte has index tag, call index method,
                // stores pixel from hash into buffer

                byte index = (byte) (data[i] & 0b00_11_11_11);
                buffer[bufferCount] = hash[index];
                bufferCount++;


            }
            // store pixel in hash if the byte doesn't have a run tag (doesn't pass through run)
            if (!isRun) {
                hash[QOISpecification.hash(buffer[bufferCount-1])]= buffer[bufferCount-1];
            }
        }
        assert bufferCount == buffer.length;
        return buffer;
    }

    /**
     * Decode a file using the "Quite Ok Image" Protocol
     *
     * @param content (byte[]) - Content of the file to decode
     * @return (Image) - Decoded image
     * @throws AssertionError if content is null
     */
    public static Image decodeQoiFile(byte[] content) {
        assert content != null; //assert content isn't null

        byte[][] partitionArray = ArrayUtils.partition(content, 14, content.length - 22, 8); //byte table partitionArray containing the partition of array content into smaller segments.
        byte[] eof = partitionArray[2]; //end of file data equals partitionArray[2]
        for (int i = 0; i <= 7; i++) {
            assert eof[i] == QOISpecification.QOI_EOF[i]; //assert eof is equal to the expected eof
        }

        byte[] header = partitionArray[0]; //header equals first row of partitionArray
        byte[] rawData = partitionArray[1]; //rawData equals second row of partition array


        int[] decodeHeader = decodeHeader(header); //transforms encoded header into something readable by us
        int width = decodeHeader[0]; //width equals first element of header
        int height = decodeHeader[1]; //height equals second element of header
        byte numChannels = (byte) decodeHeader[2]; //the number of channels equals third element of header
        byte colorSpace = (byte) decodeHeader[3]; //the color space equals fourth element of header

        byte[][] data = decodeData(rawData, width, height); //making a list of byte pixels
        int[][] decodedPixels = ArrayUtils.channelsToImage(data, height, width); //making a table of int pixels
        return Helper.generateImage(decodedPixels, numChannels, colorSpace); //generating the image using the table of int pixels, the number of channels and the color space

    }

}