package cs107;

import java.util.ArrayList;

import static cs107.QOISpecification.*;

/**
 * "Quite Ok Image" Encoder
 *
 * @author Hamza REMMAL (hamza.remmal@epfl.ch)
 * @version 1.3
 * @apiNote Second task of the 2022 Mini Project
 * @since 1.0
 */
public final class QOIEncoder {

    /**
     * DO NOT CHANGE THIS, MORE ON THAT IN WEEK 7.
     */
    private QOIEncoder() {
    }

    // ==================================================================================
    // ============================ QUITE OK IMAGE HEADER ===============================
    // ==================================================================================

    /**
     * Generate a "Quite Ok Image" header using the following parameters
     *
     * @param image (Helper.Image) - Image to use
     * @return (byte[]) - Corresponding "Quite Ok Image" Header
     * @throws AssertionError if the colorspace or the number of channels is corrupted or if the image is null.
     *                        (See the "Quite Ok Image" Specification or the handouts of the project for more information)
     */
    public static byte[] qoiHeader(Helper.Image image) {
        assert image.data() != null; //assert image is not null
        assert ((image.channels() == QOISpecification.RGB) || (image.channels() == QOISpecification.RGBA)); //assert that the number of channels encoding the image does not differ from the values of the constants QOISpecification.RGB and QOISpecification.RGBA
        assert (image.color_space() == QOISpecification.sRGB) || (image.color_space() == QOISpecification.ALL); //assert that the value encoding the color space does not differ from the values QOISpecification.sRGB and QOISpecification.ALL

        byte[] header = new byte[14]; //array that is going to contain the header
        byte[] magicNum = QOISpecification.QOI_MAGIC; //array containing the magic number
        for (int i = 0; i < 4; i++) {
            header[i] = magicNum[i]; //adding the magic number to the header array in the first 4 bytes
        }

        int height = (image.data()).length; //height of image
        int width = (image.data())[0].length; //width of image
        byte[] byteHeight = ArrayUtils.fromInt(height); //4 byte array containing height
        byte[] byteWidth = ArrayUtils.fromInt(width); //4 byte array containing width

        for (int i = 4; i < 8; i++) {
            header[i] = byteWidth[i - 4]; //adding the width of the image to the header array to the next 4 bytes
        }
        for (int i = 8; i < 12; i++) {
            header[i] = byteHeight[i - 8]; //adding the height of the image to the header array to the next 4 bytes
        }

        header[12] = image.channels(); //adding the number of channels that the image has to the next byte
        header[13] = image.color_space(); //adding the color-space of the channels that the image has to the next byte

        return header;
    }

    // ==================================================================================
    // ============================ ATOMIC ENCODING METHODS =============================
    // ==================================================================================

    /**
     * Encode the given pixel using the QOI_OP_RGB schema
     *
     * @param pixel (byte[]) - The Pixel to encode
     * @return (byte[]) - Encoding of the pixel using the QOI_OP_RGB schema
     * @throws AssertionError if the pixel's length is not 4
     */
    public static byte[] qoiOpRGB(byte[] pixel) {
        assert pixel.length == 4; //makes sure size of pixel is 4
        byte[] qoiOpRGB = new byte[4]; //array containing the qoiOpRGB
        qoiOpRGB[0] = QOI_OP_RGB_TAG; //first byte of array is used to store the tag
        for (int i = 0; i < 3; i++) {
            qoiOpRGB[i + 1] = pixel[i]; //other 3 bytes of array are used to store the RGB part of the pixel excluding Alpha
        }
        return qoiOpRGB;
    }

    /**
     * Encode the given pixel using the QOI_OP_RGBA schema
     *
     * @param pixel (byte[]) - The pixel to encode
     * @return (byte[]) Encoding of the pixel using the QOI_OP_RGBA schema
     * @throws AssertionError if the pixel's length is not 4
     */
    public static byte[] qoiOpRGBA(byte[] pixel) {
        assert pixel.length == 4; //makes sure size of pixel is 4
        byte[] qoiOpRGBA = new byte[5]; //array containing the qoiOpRGBA
        qoiOpRGBA[0] = QOI_OP_RGBA_TAG; //first byte of array is used to store the tag
        for (int i = 0; i < 4; i++) {
            qoiOpRGBA[i + 1] = pixel[i]; //other 4 bytes of array are used to store the pixel channels in RGBA
        }
        return qoiOpRGBA;
    }

    /**
     * Encode the index using the QOI_OP_INDEX schema
     *
     * @param index (byte) - Index of the pixel
     * @return (byte[]) - Encoding of the index using the QOI_OP_INDEX schema
     * @throws AssertionError if the index is outside the range of all possible indices
     */
    public static byte[] qoiOpIndex(byte index) {
        assert index >= 0; //assert index is positive
        assert index < 64; //assert index is smaller than 64

        byte[] qoiOpIndex = new byte[1]; //array that will contain the qoiOpIndex
        byte tag = QOI_OP_INDEX_TAG; //tag
        qoiOpIndex[0] = (byte) (tag | index); //bitwise or to join tag and index into one byte, then put this byte into qoiOpIndex array
        return qoiOpIndex;
    }

    /**
     * Encode the difference between 2 pixels using the QOI_OP_DIFF schema
     *
     * @param diff (byte[]) - The difference between 2 pixels
     * @return (byte[]) - Encoding of the given difference
     * @throws AssertionError if diff doesn't respect the constraints or diff's length is not 3
     *                        (See the handout for the constraints)
     */
    public static byte[] qoiOpDiff(byte[] diff) {
        assert diff != null; //assert input in not null
        assert diff.length == 3; //assert the input is length 3
        for (int i = 0; i < 3; i++) {
            assert ((diff[i] > -3) && (diff[i] < 2)); //assert the diff is between -3 < x < 2
        }

        byte[] qoiOPDiff = new byte[1]; //array containing qoiOpDiff
        byte tag = QOI_OP_DIFF_TAG; //byte containing the tag
        int r = ((int) (diff[0])) + 2; //converts red diff into integer and adds two (as per instructions)
        int g = ((int) (diff[1])) + 2; //converts green diff into integer and adds two (as per instructions)
        int b = ((int) (diff[2])) + 2; //converts blue diff into integer and adds two (as per instructions)


        byte dr = (byte) ((byte) (r) << 4); //converts red diff into byte again and shifts 4 to the left
        byte dg = (byte) ((byte) (g) << 2); //converts green diff into byte again and shifts 2 to the left
        byte db = (byte) (b); //converts blue diff into byte again.

        qoiOPDiff[0] = (byte) (tag | dr | dg | db); //bitwise or to join the tag with all the colour diffs

        return qoiOPDiff;
    }

    /**
     * Encode the difference between 2 pixels using the QOI_OP_LUMA schema
     *
     * @param diff (byte[]) - The difference between 2 pixels
     * @return (byte[]) - Encoding of the given difference
     * @throws AssertionError if diff doesn't respect the constraints
     *                        or diff's length is not 3
     *                        (See the handout for the constraints)
     */
    public static byte[] qoiOpLuma(byte[] diff) {
        assert diff != null; //assert input in not null
        assert diff.length == 3; //assert the input is length 3
        assert ((diff[1] > -33) && (diff[1] < 32)); //assert dg is between -33 < x < 32
        assert ((diff[0] - diff[1]) > -9) && ((diff[0] - diff[1]) < 8); //assert dr' - dg' is between -9 < x < 8
        assert ((diff[2] - diff[1]) > -9) && ((diff[2] - diff[1]) < 8); //assert db' - dg' is between -9 < x < 8

        byte[] qoiOpLuma = new byte[2]; //array that will contain qoiOpLuma
        byte tag = QOI_OP_LUMA_TAG; //byte containing tag

        int g = diff[1] + 32; //int containing difference in green channel and offset by 32
        int rg = (diff[0] - diff[1]) + 8; //int containing subtraction of diff in red and diff in green and offset by 8
        int bg = (diff[2] - diff[1]) + 8; //int containing subtraction of diff in red and diff in green and offset by 8

        byte dg = (byte) g; //byte containing difference in green channel and offset by 32
        byte drdg = (byte) rg; //byte containing subtraction of diff in red and diff in green and offset by 8
        drdg = (byte) (drdg << 4); //push drdg to the left by 4
        byte dbdg = (byte) bg; //byte containing subtraction of diff in red and diff in green and offset by 8

        qoiOpLuma[0] = (byte) (tag | dg); //first byte is bitwise or of tag and diff green
        qoiOpLuma[1] = (byte) (drdg | dbdg); //second byte is bitwise or of shifted drdg and dbdg

        return qoiOpLuma;
    }

    /**
     * Encode the number of similar pixels using the QOI_OP_RUN schema
     *
     * @param count (byte) - Number of similar pixels
     * @return (byte[]) - Encoding of count
     * @throws AssertionError if count is not between 0 (exclusive) and 63 (exclusive)
     */
    public static byte[] qoiOpRun(byte count) {
        assert ((count < 63) && (count > 0)); //assert count is between 0 < x < 63

        byte tag = QOI_OP_RUN_TAG; //byte containing tag
        byte[] qoiOpRun = new byte[1]; //byte array containing qoiOpRun
        count = (byte) (((int) count) - 1); // offset count by -1
        qoiOpRun[0] = (byte) (tag | count); //make qoiOpRun be equal to the bitwise or of tag and count
        return qoiOpRun;
    }

    // ==================================================================================
    // ============================== GLOBAL ENCODING METHODS  ==========================
    // ==================================================================================

    /**
     * Encode the given image using the "Quite Ok Image" Protocol
     * (See handout for more information about the "Quite Ok Image" protocol)
     *
     * @param image (byte[][]) - Formatted image to encode
     * @return (byte[]) - "Quite Ok Image" representation of the image
     */
    public static byte[] encodeData(byte[][] image) {

        assert image != null;
        //assert picture != null DON'T KNOW WHAT PICTURE IS
        for (int i = 0; i < image.length; i++) {
            assert image[i].length == 4; //assert all pixels are size 4
        }


        byte[] prevPixel = START_PIXEL; //first pixel is always constant (as per instructions)
        byte[][] hash = new byte[64][4]; //definition of hash table
        int count = 0; //used for qoiOpRun
        boolean isRun; //boolean that will become true if opRun is used for a pixel and will cause the hash not to be saved into the hash table
        ArrayList<Byte> encodedata = new ArrayList<>();//arrayList where the encoded data is going to be stored


        for (int i = 0; i < image.length; i++) { //for every pixel


            /*===============================
            ======== previous pixel =========
            =================================*/

            if (i != 0) { //if not the first pixel (keeps value from constant before)
                prevPixel = image[i - 1]; //prevPixel becomes pixel from last repetition
            }

            /*===============================
            ======== hash index =========
            =================================*/

            byte pixHash = hash(image[i]); //produces a hash value from a pixel
            isRun = false; //restart run check boolean

            /*================================
            ==difference between two pixels ==
            =================================*/

            byte[] diff = new byte[4]; //def of array where diff is stored
            byte[] diffLuma = new byte[3]; //def of array where diffLuma is stored
            for (int j = 0; j < 4; j++) { //for every element of diff, diff[j] equals pixel channel j minus prevPixel channel j
                diff[j] = (byte) (image[i][j] - prevPixel[j]);
            }
            if (diff[3] == 0) { //if difference in alpha channel is 0
                for (int j = 0; j < 3; j++) {
                    diffLuma[j] = diff[j]; //shorter version of diff, excluding diff in alpha
                }
            }

          /*===========================
            ======== qoiOpRun =========
            ===========================*/

            if (ArrayUtils.equals(image[i], prevPixel)) { //if pixel equals previous pixel
                ++count; //increase count by one
                if (((count == 62) || (i == image.length - 1))||((!ArrayUtils.equals(image[i], image[i + 1])) && (count != 0))) {
                    //if count equals 62 or pixel is the last pixel or (pixel isn't equal to next pixel and count is not 0)
                    encodedata.add(qoiOpRun((byte) count)[0]); //add one byte of qoiOpRun to encode data
                    count = 0; //reset count
                }
                isRun = true; //set run to true to avoid saving hash index to hash table

            }

            /*===========================
            ======== qoiOpIndex =========
            ===========================*/

            else if (ArrayUtils.equals(hash[pixHash], image[i])) { //else if pixel equals pixel stored at the indicated index in the hash table
                encodedata.add((qoiOpIndex(pixHash))[0]); //add one byte of qoiOpIndex to encode data
            }



        /*===========================
         ======== qoiOpDiff =========
        ===========================*/

            else if ((diff[3] == 0) && (((diff[0]) > -3) && ((diff[0]) < 2)) && (((diff[1]) > -3) && ((diff[1]) < 2)) &&
                    (((diff[2]) > -3) && ((diff[2]) < 2))) {
                //else if diff in alpha is 0, and diffs in other channels are between -3 < x < 2
                encodedata.add(qoiOpDiff(diffLuma)[0]); //add one byte of qoiOpDiff to encode data (I used diffLuma because the alpha diff isn't stored in the byte as it is 0)
            }


            /*===============================
            ========    qoiOpLuma   =========
            =================================*/

            else if ((diff[3] == 0) && ((diffLuma[1] > -33) && (diffLuma[1] < 32)) && ((diff[0] - diff[1]) > -9) &&
                    ((diff[0] - diff[1]) < 8) && (((diff[2] - diff[1]) > -9) && ((diff[2] - diff[1]) < 8))) {
                //else if diff in alpha = 0, and diff green is between -33 < x < 32, and difference between diff green and red(blue) is between -9 < x < 8
                encodedata.add(qoiOpLuma(diffLuma)[0]); //add two bytes of qoiOpLuma to encode data
                encodedata.add(qoiOpLuma(diffLuma)[1]);

            }

            /*===============================
            ========    qoiOpRGB   =========
            =================================*/
            else if ((diff[3] == 0)) { //else if diff alpha = 0
                for (int j = 0; j < 4; j++) {
                    encodedata.add(qoiOpRGB(image[i])[j]); //add 4 bytes of qoiOpRGB to encode data
                }
            }

            /*===============================
            ========    qoiOpRGBA   =========
            =================================*/

            else { //else if diff alpha is not 0
                for (int j = 0; j < 5; j++) {
                    encodedata.add(qoiOpRGBA(image[i])[j]); //add 5 bytes of qoiOpRGBA to encode data
                }
            }

             /*===============================
            ====== adding pixel to hash ======
            =================================*/

            if ((!(ArrayUtils.equals(hash[pixHash], image[i]))) && (!isRun)) { //if the pixel isn't stored in the hash table yet, and process OpRun hasn't happened, then save pixel in the hash table at position pixHash
                hash[pixHash] = image[i];
            }

        }

        byte[] encodeData = new byte[encodedata.size()]; //translate the arraylist into an array in order to return the array
        for (int z = 0; z < encodedata.size(); z++) {
            encodeData[z] = encodedata.get(z);
        }
        encodedata.clear();
        return encodeData;
    }

    /**
     * Creates the representation in memory of the "Quite Ok Image" file.
     *
     * @param image (Helper.Image) - Image to encode
     * @return (byte[]) - Binary representation of the "Quite Ok File" of the image
     * @throws AssertionError if the image is null
     * @apiNote THE FILE IS NOT CREATED YET, THIS IS JUST ITS REPRESENTATION.
     * TO CREATE THE FILE, YOU'LL NEED TO CALL Helper::write
     */
    public static byte[] qoiFile(Helper.Image image) {
        assert image != null; //assert image is not null

        byte[] header = qoiHeader(image);
        byte[][] imageToChannels = ArrayUtils.imageToChannels(image.data());
        byte[] encodeData = encodeData(imageToChannels);
        byte[] eof = QOISpecification.QOI_EOF;

        byte[] qoiFile = ArrayUtils.concat(header, encodeData, eof);

        return qoiFile;
    }

}